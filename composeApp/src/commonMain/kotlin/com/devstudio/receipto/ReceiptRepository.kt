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
        return try {
            val docRef = withContext(Dispatchers.IO) { receiptsCollection.add(receipt) }
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateReceipt(receipt: Receipt): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) { receiptsCollection.document(receipt.id).set(receipt) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReceipt(receiptId: String): Result<Unit> {
        return try {
            receiptsCollection.document(receiptId).delete()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun uploadImage(imageBytes: ByteArray): Result<String> { // Changed parameter
        return try {
            val fileName = "receipts/${Uuid.random()}.jpg"
            val imageRef = storage.reference(fileName)
            imageRef.putFile(createTempFileFromByteArray(imageBytes))
            val downloadUrl = imageRef.getDownloadUrl() // getDownloadUrl is suspend fun
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
expect fun createTempFileFromByteArray(fileByteArray: ByteArray): File
