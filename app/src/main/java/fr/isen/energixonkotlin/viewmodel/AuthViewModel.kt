package fr.isen.energixonkotlin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import fr.isen.energixonkotlin.model.UserModel

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val firestore = Firebase.firestore

    fun login(context: Context, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }

    fun signup(context: Context, email : String, name : String, password : String, onResult: (Boolean, String?) -> Unit) {

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            onResult(false, "Certaines cases sont vides")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val userId = it.result?.user?.uid
                    val userModel = UserModel(name, email, userId!!)
                    firestore.collection("users")
                        .document()
                        .set(userModel)
                        .addOnCompleteListener { dbTask ->
                            if(dbTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, "Something went wrong")
                            }
                    }
                } else {
                    onResult(false, it.exception?.localizedMessage)
                }
            }
    }
}