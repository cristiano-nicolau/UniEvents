package com.example.unievents.data

import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun registerUser(navController: NavController, email: String, password: String, role: String, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    val user = hashMapOf(
                        "email" to email,
                        "role" to role
                    )
                    db.collection("users").document(userId).set(user)
                        .addOnSuccessListener {
                            // Automatically log in the user after registration
                            loginUser(navController, email, password) { loginSuccess ->
                                onResult(loginSuccess)
                            }
                        }
                        .addOnFailureListener {
                            onResult(false)
                        }
                } else {
                    onResult(false)
                }
            }
    }

    fun loginUser(navController: NavController, email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    db.collection("users").document(userId).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val role = document.getString("role")
                                if (role == "admin") {
                                    navController.navigate("adminHome")
                                } else {
                                    navController.navigate("userHome")
                                }
                                onResult(true)
                            } else {
                                onResult(false)
                            }
                        }
                        .addOnFailureListener {
                            onResult(false)
                        }
                } else {
                    onResult(false)
                }
            }
    }
}
