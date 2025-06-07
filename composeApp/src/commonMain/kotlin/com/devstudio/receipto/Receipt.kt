package com.devstudio.receipto

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class Receipt @OptIn(ExperimentalTime::class) constructor(
    val id: String = "",
    val name: String = "",
    val amount: Double = 0.0,
    val date: String = "",
    val reminderDate: String = "",
    val reason: String = "",
    val imageUrl: String = "",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    @kotlinx.serialization.Transient // Exclude from serialization
    var newImageByteArray: ByteArray? = null
) {
    // Custom equals and hashCode if newImageByteArray should not affect them
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Receipt

        if (id != other.id) return false
        if (name != other.name) return false
        if (amount != other.amount) return false
        if (date != other.date) return false
        if (reminderDate != other.reminderDate) return false
        if (reason != other.reason) return false
        if (imageUrl != other.imageUrl) return false
        if (createdAt != other.createdAt) return false
        // Not comparing newImageByteArray as it's transient and for UI/upload purposes

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + reminderDate.hashCode()
        result = 31 * result + reason.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + createdAt.hashCode()
        // Not including newImageByteArray
        return result
    }
}