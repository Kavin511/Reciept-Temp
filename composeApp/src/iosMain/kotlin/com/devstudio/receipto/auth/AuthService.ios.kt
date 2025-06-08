package com.devstudio.receipto.auth

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRAuthDataResult
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.GoogleSignIn.GIDConfiguration // Will be needed if configured here, but usually in AppDelegate
import cocoapods.GoogleSignIn.GIDSignIn
import cocoapods.GoogleSignIn.GIDSignInResult
import cocoapods.FirebaseCore.FIRApp // For clientID, if configuring GIDSignIn here.
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class, ExperimentalCoroutinesApi::class)
actual class AuthService actual constructor() { // No constructor context needed for basic FIRAuth, GIDSignIn uses shared instance

    private val firAuth: FIRAuth = FIRAuth.auth()!!

    // Helper to get top UIViewController for GIDSignIn
    private fun getPresentingViewController(): platform.UIKit.UIViewController? {
        var viewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (viewController?.presentedViewController != null) {
            viewController = viewController.presentedViewController
        }
        return viewController
    }

    actual fun observeAuthState(): Flow<PlatformUser?> = callbackFlow {
        val handle = firAuth.addStateDidChangeListener { _, user ->
            trySend(user?.toPlatformUser()).isSuccess
        }
        awaitClose { firAuth.removeStateDidChangeListener(handle) }
    }

    actual suspend fun signInWithGoogle(): AuthResult = suspendCancellableCoroutine { continuation ->
        val presentingViewController = getPresentingViewController()
        if (presentingViewController == null) {
            continuation.resume(AuthResult.Error("Could not get presenting UIViewController for Google Sign-In"))
            return@suspendCancellableCoroutine
        }

        // Crucial: GIDSignIn.sharedInstance.configuration must be set before calling signIn.
        // This is typically done in AppDelegate using the clientID from GoogleService-Info.plist.
        // Example:
        // guard let clientID = FIRApp.defaultApp()?.options.clientID else { return }
        // let config = GIDConfiguration(clientID: clientID)
        // GIDSignIn.sharedInstance.configuration = config
        // If not configured, GIDSignIn.sharedInstance will be null or operations will fail.
        // We assume this is done in native iOS setup (e.g., AppDelegate).

        GIDSignIn.sharedInstance.signInWithPresentingViewController(presentingViewController) { gidSignInResult, nsError ->
            if (nsError != null) {
                if (nsError.code.toInt() == cocoapods.GoogleSignInSDK.kGIDSignInErrorCodeCancelled) {
                    continuation.resume(AuthResult.Cancelled)
                } else {
                    continuation.resume(AuthResult.Error("Google Sign-In failed: ${nsError.localizedDescription} (Code: ${nsError.code})"))
                }
                return@signInWithPresentingViewController
            }

            gidSignInResult?.user?.idToken?.tokenString?.let { idToken ->
                 val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, null)
                 firAuth.signInWithCredential(credential) { firAuthDataResult, firError ->
                    if (firError != null) {
                        continuation.resume(AuthResult.Error("Firebase Sign-In failed: ${firError.localizedDescription}"))
                    } else if (firAuthDataResult?.user != null) {
                        continuation.resume(AuthResult.Success(firAuthDataResult.user.toPlatformUser()))
                    } else {
                        continuation.resume(AuthResult.Error("Firebase Sign-In failed: User data is null"))
                    }
                }
            } ?: run {
                continuation.resume(AuthResult.Error("Google Sign-In failed: ID token is null"))
            }
        }
    }

    actual suspend fun signOut(): AuthResult = suspendCancellableCoroutine { continuation ->
        try {
            firAuth.signOut(null) // signOut can throw an NSError, but typically doesn't for simple sign-out. NSError* is nullable.
            GIDSignIn.sharedInstance.signOut() // Sign out from Google
            continuation.resume(AuthResult.Success(null))
        } catch (e: Exception) {
             continuation.resume(AuthResult.Error("Sign out failed: ${e.message}"))
        }
    }

    actual suspend fun deleteAccount(): AuthResult = suspendCancellableCoroutine { continuation ->
        val currentUser = firAuth.currentUser
        if (currentUser == null) {
            continuation.resume(AuthResult.Error("No user signed in to delete."))
            return@suspendCancellableCoroutine
        }

        currentUser.deleteWithCompletion { nsError ->
            if (nsError != null) {
                continuation.resume(AuthResult.Error("Delete account failed: ${nsError.localizedDescription} (Re-authentication might be required)"))
            } else {
                // Also sign out from GID after successful Firebase deletion
                GIDSignIn.sharedInstance.signOut()
                continuation.resume(AuthResult.Success(null))
            }
        }
    }

    actual fun getCurrentUser(): PlatformUser? {
        return firAuth.currentUser?.toPlatformUser()
    }

    private fun FIRUser.toPlatformUser(): PlatformUser {
        return PlatformUser(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL?.absoluteString
        )
    }
}
