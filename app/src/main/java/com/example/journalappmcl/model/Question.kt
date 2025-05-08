package com.example.journalappmcl.model

import kotlinx.serialization.Serializable

@Serializable
sealed class QuestionType {
    @Serializable
    object Statement           : QuestionType()
    @Serializable
    object Text                : QuestionType()
    @Serializable
    data class YesNo(
        val yesNextIndex: Int?,
        val noNextIndex: Int?
    ) : QuestionType()
    @Serializable
    data class Option(
        val options: List<String>
    ) : QuestionType()
    @Serializable
    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val step: Float,
        val intenseNextIndex: Int?,
        val mildNextIndex: Int?
    ) : QuestionType()
    @Serializable
    data class ImageOptions(
        val drawableIds: List<Int>            // e.g. R.drawable.busy_mind
    ) : QuestionType()
    @Serializable
    object TimeSelect         : QuestionType()
    @Serializable
    data class MultiQ(
        val options: List<String>
    ) : QuestionType()
    @Serializable
    data class MultiText(
        val subQuestions: List<String>
    ) : QuestionType()
    @Serializable
    data class ConditionalText(
        val followUpPrompt: String
    ) : QuestionType()
    @Serializable
    object EndLoop            : QuestionType()
}

@Serializable
data class Question(
    val text: String,
    val type: QuestionType
)
