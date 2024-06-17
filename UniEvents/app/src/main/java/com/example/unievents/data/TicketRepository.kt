package com.example.unievents.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class TicketRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun generateTicket(eventId: String, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val ticketId = UUID.randomUUID().toString()
        val qrCode = generateQrCode(ticketId)
        val ticket = Ticket(
            id = ticketId,
            eventId = eventId,
            userId = userId,
            qrCode = qrCode
        )

        db.collection("tickets").document(ticketId).set(ticket)
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
                callback(events)
            }
            .addOnFailureListener {
                callback(emptyList())
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
                        val attendeesCount = eventSnapshot.getLong("attendeesCount") ?: 0
                        transaction.update(eventRef, "attendeesCount", attendeesCount + 1)
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

    private fun generateQrCode(ticketId: String): String {
        // Implement your QR code generation logic here.
        // For simplicity, we'll return the ticketId itself.
        return ticketId
    }
}
