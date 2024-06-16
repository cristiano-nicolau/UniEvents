package com.example.unievents.data

import com.google.firebase.auth.FirebaseAuth
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
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
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