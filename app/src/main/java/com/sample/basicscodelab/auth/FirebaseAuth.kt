//package com.sample.basicscodelab.auth
//
//import android.util.Log
//import android.widget.Toast
//import com.google.firebase.auth.FirebaseAuth
//
//class FirebaseAuth {
//    private lateinit var auth: FirebaseAuth
//    fun signIn(email: String, password: String) {
//
//        // [START sign_in_with_email]
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    val user = auth.currentUser
////                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
////                    Toast.makeText(baseContext, "Authentication failed.",
////                        Toast.LENGTH_SHORT).show()
////                    updateUI(null)
//                }
//            }
//        // [END sign_in_with_email]
//    }
//
//    companion object {
//        private const val TAG = "FirebaseAuth"
//    }
//}