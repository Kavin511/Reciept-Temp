package com.devstudio.receipto

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.File
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ReceiptRepository {
    val firestore = Firebase.firestore
    private val storage = Firebase.storage
    private val receiptsCollection = firestore.collection("receipts")

    fun getReceiptsFlow(): Flow<List<Receipt>> = flow {
        receiptsCollection.orderBy("createdAt", Direction.DESCENDING)
            .get().documents.map { doc -> Receipt() }
    }


    suspend fun addReceipt(receipt: Receipt): Result<String> {
        // This function currently saves receipt data to Firebase Firestore, acting as the primary data store.
        // TODO: When subscription feature is added, include logic here to push data to the new cloud server if the user is subscribed.
        return try {
            val docRef = withContext(Dispatchers.IO) { receiptsCollection.add(receipt) }
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReceipt(receipt: Receipt): Result<Unit> {
        // This function currently updates receipt data in Firebase Firestore.
        // TODO: When subscription feature is added, include logic here to update data in the new cloud server if the user is subscribed.
        return try {
            withContext(Dispatchers.IO) { receiptsCollection.document(receipt.id).set(receipt) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReceipt(receiptId: String): Result<Unit> {
        // This function currently deletes receipt data from Firebase Firestore.
        // TODO: When subscription feature is added, include logic here to delete data from the new cloud server if the user is subscribed.
        return try {
            receiptsCollection.document(receiptId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadImage(imageBytes: File): Result<String> {
        // This function currently uploads image data to Firebase Storage.
        // TODO: When subscription feature is added, include logic here to upload/sync image data to the new cloud server if the user is subscribed.
        return try {
            val imageRef = storage.reference.child("receipts/${Uuid.random()}.jpg")
            val uploadTask = imageRef.putFile(imageBytes)
            Result.success("")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}