package com.example.unievents.data

import com.google.firebase.auth.FirebaseAuth
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
                        id = document.id
                    }
                }
                onResult(events)
            }
            .addOnFailureListener { e ->
                println("Error fetching events: ${e.message}")
                onResult(emptyList())
            }
    }



    fun getUserSubscriptions(onResult: (List<Event>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(emptyList())
        db.collection("subscriptions").whereEqualTo("userId", userId).get()
            .addOnSuccessListener { result ->
                val eventIds = result.map { it.getString("eventId") ?: "" }
                db.collection("events").whereIn("id", eventIds).get()
                    .addOnSuccessListener { eventResult ->
                        val events = eventResult.map { it.toObject(Event::class.java) }
                        onResult(events)
                    }
                    .addOnFailureListener {
                        onResult(emptyList())
                    }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    private fun getCurrentUser(): User {
        val currentUser = auth.currentUser ?: return User()
        return User(
            id = currentUser.uid,
            name = currentUser.displayName ?: "",
            email = currentUser.email ?: "",
            role = "user"  // ou "admin", dependendo do usuário autenticado
        )
    }
}
