package com.example.journalappmcl.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class QuestionResponse(
    val questionText: String,
    val type: QuestionType,
    val answer: String,
    val sliderValue: Float?      = null,
    val imageIndex: Int?         = null,
    @Contextual
    val timestamp: Instant       = Instant.now()
)
