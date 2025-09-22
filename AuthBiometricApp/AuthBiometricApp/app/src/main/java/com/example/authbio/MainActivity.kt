package com.example.authbio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var biometricEnabled by remember { mutableStateOf(getPrefs(context).getBoolean("biometric_enabled", false)) }
    var status by remember { mutableStateOf("Signed in as: " + (Firebase.auth.currentUser?.email ?: "Unknown")) }

    LaunchedEffect(Unit) {
        if (biometricEnabled && BiometricUtils.isBiometricAvailable(context)) {
            authenticateWithBiometrics(
                title = context.getString(R.string.biometric_title),
                subtitle = context.getString(R.string.biometric_subtitle),
                negative = context.getString(R.string.biometric_negative),
                onSuccess = { status = "Biometric unlocked" },
                onFailure = { status = "Biometric failed/cancelled" }
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(status)
        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = biometricEnabled,
                onCheckedChange = {
                    biometricEnabled = it
                    getPrefs(context).edit().putBoolean("biometric_enabled", it).apply()
                }
            )
            Spacer(Modifier.width(12.dp))
            Text("Enable biometric unlock")
        }

        Spacer(Modifier.height(32.dp))

        Button(onClick = {
            Firebase.auth.signOut()
            status = "Signed out"
        }) {
            Text("Sign out")
        }
    }
}

private fun authenticateWithBiometrics(
    title: String,
    subtitle: String,
    negative: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val activity = androidx.activity.compose.LocalContext.current as ComponentActivity
    val executor = ContextCompat.getMainExecutor(activity)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(title)
        .setSubtitle(subtitle)
        .setNegativeButtonText(negative)
        .build()
    val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            onSuccess()
        }
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            onFailure()
        }
        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            onFailure()
        }
    })
    biometricPrompt.authenticate(promptInfo)
}

fun getPrefs(context: android.content.Context) =
    context.getSharedPreferences("authbio_prefs", android.content.Context.MODE_PRIVATE)