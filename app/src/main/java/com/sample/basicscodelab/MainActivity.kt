package com.sample.basicscodelab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.basicscodelab.repository.SubscriptionDataRepository
import com.sample.basicscodelab.ui.UserProfileActivity
import com.sample.basicscodelab.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var repo: SubscriptionDataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize Firebase Auth
        super.onCreate(savedInstanceState)
        repo = SubscriptionDataRepository(this)
        auth = Firebase.auth
        setContent {
            BasicsCodelabTheme {
                MyApp()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.w(TAG, "Current user is null")
//            reload();
        }
    }

    @Composable
    private fun MyApp() {
        var shouldShowOnboarding by rememberSaveable { mutableStateOf(true) }

        if (shouldShowOnboarding) {
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
        } else {
//        Greetings()
            RegisterOrSignIn()
        }
    }

    @Composable
    private fun OnboardingScreen(onContinueClicked: () -> Unit) {

        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome to the the new Subscription Platform!")
                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = onContinueClicked
                ) {
                    Text(text = "Continue")
                }
            }
        }
    }

//@Composable
//private fun Greetings(names: List<String> = List(100) { "$it" }) {
//    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
//        items(items = names) { name ->
//            Greeting(name = name)
//        }
//    }
//}

    @Composable
    private fun RegisterOrSignIn() {
        var userEmail by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable {
            mutableStateOf("")
        }
        val showRegistrationForm = rememberSaveable { mutableStateOf(false) }

        Surface {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    showRegistrationForm.value = true
                }) {
                    Text(text = "Register")
                }

            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = userEmail,
                    onValueChange = { userEmail = it.trim() },
                    label = { Text(text = "Email") },
                    placeholder = { Text(text = "example@gmail.com") }
                )
                TextField(
                    value = password,
                    onValueChange = { password = it.trim() },
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Password") }
                )

                Button(onClick = {
                    signIn(userEmail, password)
                }) {
                    Text(text = "Log in")
                }
            }
            if (showRegistrationForm.value) {
                RegistrationForm()
            }

        }
    }

    @Composable
    fun RegistrationForm() {
        Surface() {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var text by rememberSaveable { mutableStateOf("") }
                var password by rememberSaveable {
                    mutableStateOf("")
                }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(text = "Email") },
                    placeholder = { Text(text = "example@gmail.com") }
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Password") }
                )

                Button(onClick = {
                    createAccount(text, password)
                }) {
                    Text(text = "Submit")
                }
            }

        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun RegisterPreview() {
        BasicsCodelabTheme {
            RegisterOrSignIn()
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun OnboardingPreview() {
        BasicsCodelabTheme {
            OnboardingScreen(onContinueClicked = {})
        }
    }

    @Preview(showBackground = true, widthDp = 320, heightDp = 320)
    @Composable
    fun RegistrationFormPreview() {
        BasicsCodelabTheme {
            RegistrationForm()
        }
    }

    // Load Registration form when showRegistrationForm is set to true


    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        // [END sign_in_with_email]
    }

    companion object {
        private const val TAG: String = "MainActivity"
    }

}


//@Composable
//fun Greeting(name: String) {
//    val expanded = remember {
//        mutableStateOf(value = false)
//    }
//    val extraPadding by animateDpAsState(
//        if (expanded.value) 48.dp else 0.dp,
//        animationSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        )
//    )
//
//    Surface(
//        color = MaterialTheme.colors.primary,
//        modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
//    ) {
//        Row(modifier = Modifier.padding(24.dp)) {
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(bottom = extraPadding.coerceAtLeast(0.dp))
//            ) {
//                Text(text = "Hello, ")
//                Text(text = name, style = MaterialTheme.typography.h4)
//            }
//            IconButton(onClick = { expanded.value = !expanded.value }) {
//                Icon(
//                    imageVector = if (expanded.value) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
//                    contentDescription = if (expanded.value) {
//                        stringResource(id = R.string.show_less)
//                    } else {
//                        stringResource(id = R.string.show_more)
//                    }
//                )
//
//            }
//
//        }
//
//    }
//}

//@Preview(showBackground = true, name = "Default preview")
//@Composable
//fun DefaultPreview() {
//    BasicsCodelabTheme {
//        Greetings()
//    }
//}
