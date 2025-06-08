package com.devstudio.receipto.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.devstudio.receipto.R // Assuming R.string.default_web_client_id exists

actual class AuthService actual constructor(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Must exist in strings.xml
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    actual companion object {
        // This method would be called by the Android Composable/Activity to get the intent
        fun getGoogleSignInIntent(context: Context): Intent {
            // Ensure this GSO is configured the same way as the one for the client instance
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            return GoogleSignIn.getClient(context, gso).signInIntent
        }

        // This method would be called by the Android Composable/Activity with the result from the intent
        suspend fun handleGoogleSignInResult(intent: Intent?, authServiceInstance: AuthService): AuthResult {
            return try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    ?: return AuthResult.Error("Google Sign-In failed: Account is null after result.")

                val idToken = account.idToken
                if (idToken == null) {
                    // Check if there's an existing signed-in account (e.g., if user re-opens app)
                    val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(authServiceInstance.context)
                    if (lastSignedInAccount?.idToken != null) {
                         authServiceInstance.firebaseSignInWithGoogle(lastSignedInAccount.idToken!!)
                    } else {
                        AuthResult.Error("Google Sign-In failed: ID token is null and no last signed-in account found.")
                    }
                } else {
                    authServiceInstance.firebaseSignInWithGoogle(idToken)
                }

            } catch (e: ApiException) {
                if (e.statusCode == com.google.android.gms.common.api.CommonStatusCodes.SIGN_IN_CANCELLED) {
                    AuthResult.Cancelled
                } else {
                    AuthResult.Error("Google Sign-In failed: ${e.localizedMessage} (Code: ${e.statusCode})")
                }
            } catch (e: Exception) {
                 AuthResult.Error("Google Sign-In failed: ${e.localizedMessage}")
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun observeAuthState(): Flow<PlatformUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toPlatformUser())
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose { firebaseAuth.removeAuthStateListener(authStateListener) }
    }

    // Internal function called by handleGoogleSignInResult via the authServiceInstance
    internal suspend fun firebaseSignInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user?.toPlatformUser()
            if (user != null) {
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Firebase Sign-In failed: User is null after credential sign-in.")
            }
        } catch (e: Exception) {
            AuthResult.Error("Firebase Sign-In with Google credential failed: ${e.localizedMessage}")
        }
    }

    actual suspend fun signInWithGoogle(): AuthResult {
        // This method is not meant to be called directly from commonMain in this pattern.
        // The Android UI (Activity/Composable) must initiate the Google Sign-In flow
        // using AuthService.getGoogleSignInIntent() and then pass the result to
        // AuthService.handleGoogleSignInResult().
        // This function serves to satisfy the expect contract but indicates an incorrect usage if called.
        return AuthResult.Error("Platform-specific UI interaction required. Use static methods getGoogleSignInIntent and handleGoogleSignInResult on Android.")
    }

    actual suspend fun signOut(): AuthResult {
        return try {
            firebaseAuth.signOut()
            googleSignInClient.signOut().await()
            AuthResult.Success(null)
        } catch (e: Exception) {
            AuthResult.Error("Sign out failed: ${e.localizedMessage}")
        }
    }

    actual suspend fun deleteAccount(): AuthResult {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            return AuthResult.Error("No user logged in to delete.")
        }
        return try {
            currentUser.delete().await()
            // Signing out the Google client after account deletion is good practice,
            // though Firebase deletion should revoke tokens.
            googleSignInClient.signOut().await()
            AuthResult.Success(null) // User is null after deletion
        } catch (e: Exception) {
            // Firebase often requires recent sign-in for sensitive operations like delete.
            // This error message should guide the user or trigger re-authentication logic.
            AuthResult.Error("Delete account failed: ${e.localizedMessage}. Re-authentication might be required.")
        }
    }

    actual fun getCurrentUser(): PlatformUser? {
        return firebaseAuth.currentUser?.toPlatformUser()
    }

    private fun com.google.firebase.auth.FirebaseUser.toPlatformUser(): PlatformUser {
        return PlatformUser(
            uid = this.uid,
            email = this.email,
            displayName = this.displayName,
            photoUrl = this.photoUrl?.toString()
        )
    }
}
