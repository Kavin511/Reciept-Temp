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
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)