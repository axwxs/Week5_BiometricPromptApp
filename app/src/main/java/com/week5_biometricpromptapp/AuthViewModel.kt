import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun onAuthenticationSuccess() {
        viewModelScope.launch {
            _authState.value = AuthState.AUTHENTICATED
            _errorMessage.value = null
        }
    }

    fun onAuthenticationError(message: String) {
        viewModelScope.launch {
            _authState.value = AuthState.ERROR
            _errorMessage.value = message
        }
    }

    fun onAuthenticationFailed() {
        viewModelScope.launch {
            _authState.value = AuthState.UNAUTHENTICATED
            _errorMessage.value = "Authentication failed. Please try again."
        }
    }

    fun clearError() {
        viewModelScope.launch {
            _errorMessage.value = null
            _authState.value = AuthState.UNAUTHENTICATED
        }
    }

    fun logout() {
        viewModelScope.launch {
            _authState.value = AuthState.UNAUTHENTICATED
            _errorMessage.value = null
        }
    }
}

enum class AuthState {
    UNAUTHENTICATED,
    AUTHENTICATED,
    ERROR
}