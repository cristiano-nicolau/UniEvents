package com.example.unievents.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.ByteArrayOutputStream
import java.util.*

class TicketRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun generateTicket(eventId: String, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val ticketId = UUID.randomUUID().toString()
        val qrCodeBitmap = generateQrCode(ticketId)
        val qrCodeBase64 = bitmapToBase64(qrCodeBitmap)
        val ticket = Ticket(
            id = ticketId,
            eventId = eventId,
            userId = userId,
            qrCode = qrCodeBase64
        )

        val ticketData = mapOf(
            "id" to ticket.id,
            "eventId" to ticket.eventId,
            "userId" to ticket.userId,
            "status" to ticket.status,
            "qrCode" to ticket.qrCode
        )

        db.collection("tickets").document(ticketId).set(ticketData)
            .addOnSuccessListener {
                // adiciona o id do user aos atendentes do evento
                db.collection("events").document(eventId).update("attendees", FieldValue.arrayUnion(userId))
                db.collection("events").document(eventId).update("attendeesCount", FieldValue.increment(1))
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun getTicket(eventId: String, onResult: (Ticket?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("tickets")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("userId", userId)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.documents[0]
                    val ticket = document.toObject(Ticket::class.java)
                    onResult(ticket)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun unsubscribeFromEvent(eventId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        db.collection("tickets").whereEqualTo("eventId", eventId).whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val ticketId = result.firstOrNull()?.id ?: return@addOnSuccessListener callback(false)
                db.collection("tickets").document(ticketId).delete()
                    .addOnSuccessListener {
                        db.collection("events").document(eventId).update("attendees", FieldValue.arrayRemove(userId))
                        db.collection("events").document(eventId).update("attendeesCount", FieldValue.increment(-1))
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun subscribedOnEventOrNot(eventId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        db.collection("tickets").whereEqualTo("eventId", eventId).whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                callback(!result.documents.isEmpty())
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUserSubscriptions(callback: (List<Event>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(emptyList())
        db.collection("tickets").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val tickets = result.toObjects(Ticket::class.java)
                val eventIds = tickets.map { it.eventId }
                if (eventIds.isNotEmpty()) {
                    fetchEventsByIds(eventIds, callback)

                } else {
                    callback(emptyList())
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    private fun fetchEventsByIds(eventIds: List<String>, callback: (List<Event>) -> Unit) {
        db.collection("events").whereIn(FieldPath.documentId(), eventIds).get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { document ->
                    document.toObject(Event::class.java)?.apply {
                        id = document.id
                    }
                }
                EventRepository().fetchOrganizers(events, callback)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun getTicketsForEvent(eventId: String, onResult: (List<Ticket>) -> Unit) {
        db.collection("tickets")
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { result ->
                val tickets = result.documents.mapNotNull { it.toObject(Ticket::class.java) }
                fetchEmailsForTickets(tickets, onResult)

            }
            .addOnFailureListener { e ->
                println("Erro ao buscar tickets: ${e.message}")
                onResult(emptyList())
            }
    }

    private fun fetchEmailsForTickets(tickets: List<Ticket>, onResult: (List<Ticket>) -> Unit) {
        val userIds = tickets.map { it.userId }
        db.collection("users").whereIn(FieldPath.documentId(), userIds).get()
            .addOnSuccessListener { result ->
                val emails = result.documents.associateBy({ it.id }, { it.getString("email") })
                tickets.forEach { ticket ->
                    // adiciona uma variavel email ao ticket
                    val email = emails[ticket.userId]
                    ticket.email = email ?: "unknown"
                }
                onResult(tickets)
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar emails: ${e.message}")
                onResult(tickets)
            }
    }

    fun validateTicket(ticketId: String, onResult: (Boolean) -> Unit) {
        db.collection("tickets").document(ticketId).get()
            .addOnSuccessListener { document ->
                val ticket = document.toObject(Ticket::class.java) ?: return@addOnSuccessListener onResult(false)
                if (ticket.status == "unused") {
                    db.runTransaction { transaction ->
                        val eventRef = db.collection("events").document(ticket.eventId)
                        val eventSnapshot = transaction.get(eventRef)
                        transaction.update(document.reference, "status", "using")
                    }.addOnSuccessListener {
                        onResult(true)
                    }.addOnFailureListener {
                        onResult(false)
                    }
                } else if (ticket.status == "using") {
                    db.runTransaction { transaction ->
                        val eventRef = db.collection("events").document(ticket.eventId)
                        val eventSnapshot = transaction.get(eventRef)
                        transaction.update(document.reference, "status", "used")
                    }.addOnSuccessListener {
                        onResult(true)
                    }.addOnFailureListener {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }

            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    private fun generateQrCode(ticketId: String): Bitmap {
        val size = 512 // Specify the size of the QR code
        val hints = mapOf(
            EncodeHintType.MARGIN to 1
        ) // Reduce the margin for the QR code
        val bitMatrix = MultiFormatWriter().encode(ticketId, BarcodeFormat.QR_CODE, size, size, hints)
        return BarcodeEncoder().createBitmap(bitMatrix)
    }

    private fun bitmapToBase64(qrCode: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        qrCode.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }



}
