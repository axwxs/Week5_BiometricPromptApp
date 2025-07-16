package com.week5_biometricpromptapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import BiometricAuthManager
import AuthViewModel
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.Color


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BiometricAuthTheme {
                BiometricAuthApp()
            }
        }
    }
}

@Composable
fun BiometricAuthApp() {
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    when (authState) {
        AuthState.UNAUTHENTICATED -> {
            LoginScreen(
                onAuthenticationRequest = { biometricManager ->
                    biometricManager.authenticate(
                        title = "Biometric Authentication",
                        subtitle = "Use your fingerprint authenticate",
                        negativeButtonText = "Cancel",
                        onSuccess = { viewModel.onAuthenticationSuccess() },
                        onError = { error -> viewModel.onAuthenticationError(error) },
                        onFailed = { viewModel.onAuthenticationFailed() }
                    )
                },
                errorMessage = errorMessage,
                onErrorDismissed = { viewModel.clearError() }
            )
        }
        AuthState.AUTHENTICATED -> {
            AuthenticatedScreen(
                onLogout = { viewModel.logout() }
            )
        }
        AuthState.ERROR -> {
            LoginScreen(
                onAuthenticationRequest = { biometricManager ->
                    biometricManager.authenticate(
                        title = "Biometric Authentication",
                        subtitle = "Use your fingerprint to authenticate",
                        negativeButtonText = "Cancel",
                        onSuccess = { viewModel.onAuthenticationSuccess() },
                        onError = { error -> viewModel.onAuthenticationError(error) },
                        onFailed = { viewModel.onAuthenticationFailed() }
                    )
                },
                errorMessage = errorMessage,
                onErrorDismissed = { viewModel.clearError() }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onAuthenticationRequest: (BiometricAuthManager) -> Unit,
    errorMessage: String?,
    onErrorDismissed: () -> Unit
) {
    val context = LocalContext.current
    val biometricManager = remember {
        BiometricAuthManager(context as FragmentActivity)
    }
    val biometricStatus = remember { biometricManager.isBiometricAvailable() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Lock",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Secure App",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Please authenticate to continue",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (biometricStatus) {
            BiometricAuthStatus.READY -> {
                Button(
                    onClick = { onAuthenticationRequest(biometricManager) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Person Icon",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Login With Biometrics")
                }
            }
            BiometricAuthStatus.NOT_AVAILABLE -> {
                Text(
                    text = "Biometric authentication is not available on this device",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            BiometricAuthStatus.TEMPORARY_NOT_AVAILABLE -> {
                Text(
                    text = "Biometric authentication is temporarily unavailable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            BiometricAuthStatus.AVAILABLE_BUT_NOT_ENROLLED -> {
                Text(
                    text = "No biometric credentials enrolled. Please set up biometrics in device settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }

        errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onErrorDismissed
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun AuthenticatedScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Authenticated",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Welcome!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You have been successfully authenticated",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout", color = Color.Black)
        }
    }
}

@Composable
fun BiometricAuthTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}