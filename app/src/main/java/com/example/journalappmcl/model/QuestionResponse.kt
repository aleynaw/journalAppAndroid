package com.example.journalappmcl.model

import java.time.Instant

data class QuestionResponse(
    val questionText: String,
    val type: QuestionType,
    val answer: String,
    val sliderValue: Float?      = null,
    val imageIndex: Int?         = null,
    val timestamp: Instant       = Instant.now()
)
