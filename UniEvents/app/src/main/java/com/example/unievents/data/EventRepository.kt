package com.example.unievents.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class EventRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    fun createEvent(event: Event, onResult: (Boolean) -> Unit) {
        if (event.name.isEmpty() || event.description.isEmpty() || event.date.isEmpty() || event.time.isEmpty() || event.capacity <= 0 || event.location.isEmpty() || event.latitude.isNaN() || event.longitude.isNaN()) {
            onResult(false)
            return
        }
        val eventId = db.collection("events").document().id
        event.id = eventId
        event
        // Define o organizador do evento com o ID do usuário atual
        db.collection("events").document(eventId).set(event)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }

}
    fun getEvents(onResult: (List<Event>) -> Unit) {
        db.collection("events").get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { document ->
                    document.toObject(Event::class.java)?.apply {
                        id = document.id // Define o ID do documento como o ID do evento
                    }
                }
                fetchOrganizers(events, onResult)
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

    private fun fetchOrganizers(events: List<Event>, onResult: (List<Event>) -> Unit) {
        val eventsWithOrganizers = mutableListOf<Event>()
        var completedRequests = 0

        for (event in events) {
            db.collection("users").document(event.organizer).get()
                .addOnSuccessListener { document ->
                    event.organizer = document.getString("name") ?: ""
                    eventsWithOrganizers.add(event)
                    completedRequests++

                    if (completedRequests == events.size) {
                        onResult(eventsWithOrganizers)
                    }
                }
                .addOnFailureListener { e ->
                    println("Error fetching organizer: ${e.message}")
                    completedRequests++

                    if (completedRequests == events.size) {
                        onResult(eventsWithOrganizers)
                    }
                }
        }

        if (events.isEmpty()) {
            onResult(eventsWithOrganizers)
        }
    }
}
