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
                val events = result.map { it.toObject(Event::class.java) }
                onResult(events)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    fun subscribeToEvent(eventId: String, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val subscription = hashMapOf(
            "userId" to userId,
            "eventId" to eventId
        )
        db.collection("subscriptions").add(subscription)
            .addOnSuccessListener {
                db.collection("events").document(eventId).update("attendees", FieldValue.increment(1))
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    /*fun unsubscribeFromEvent(eventId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)
        db.collection("subscriptions").whereEqualTo("userId", userId).whereEqualTo("eventId", eventId).get()
            .addOnSuccessListener { result ->
                val subscriptionId = result.firstOrNull()?.id ?: return callback(false)
                db.collection("subscriptions").document(subscriptionId).delete()
                    .addOnSuccessListener {
                        db.collection("events").document(eventId).update("attendees", FieldValue.increment(-1))
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            }
            .addOnFailureListener {
                callback(false)
            }
    }*/

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
