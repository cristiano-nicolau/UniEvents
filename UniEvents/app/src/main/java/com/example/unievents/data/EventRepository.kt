package com.example.unievents.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class EventRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /*fun createEvent(event: Event, onResult: (Boolean) -> Unit) {
        val eventId = db.collection("events").document().id
        event.id = eventId
        event.organizer = getCurrentUser()  // Adicione o organizador atual
        db.collection("events").document(eventId).set(event)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }*/

    fun getEvents(onResult: (List<Event>) -> Unit) {
        db.collection("events").get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { document ->
                    document.toObject(Event::class.java)?.apply {
                        id = document.id // Define o ID do documento como o ID do evento
                    }
                }
                onResult(events)
            }
            .addOnFailureListener { e ->
                println("Error fetching events: ${e.message}")
                onResult(emptyList())
            }
    }

    fun getEvent(eventId: String, onResult: (Event?) -> Unit) {
        db.collection("events").whereEqualTo(FieldPath.documentId(), eventId).get()
            .addOnSuccessListener { result ->
                val event = result.documents.firstOrNull()?.toObject(Event::class.java)
                onResult(event)
            }
            .addOnFailureListener { e ->
                println("Error fetching event: ${e.message}")
                onResult(null)
            }
    }
}
