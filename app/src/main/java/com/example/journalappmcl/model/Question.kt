package com.example.journalappmcl.model

sealed class QuestionType {
    object Statement           : QuestionType()
    object Text                : QuestionType()
    data class YesNo(
        val yesNextIndex: Int?,
        val noNextIndex: Int?
    ) : QuestionType()
    data class Option(
        val options: List<String>
    ) : QuestionType()
    data class Slider(
        val range: ClosedFloatingPointRange<Float>,
        val step: Float,
        val intenseNextIndex: Int?,
        val mildNextIndex: Int?
    ) : QuestionType()
    data class ImageOptions(
        val drawableIds: List<Int>            // e.g. R.drawable.busy_mind
    ) : QuestionType()
    object TimeSelect         : QuestionType()
    data class MultiQ(
        val options: List<String>
    ) : QuestionType()
    data class MultiText(
        val subQuestions: List<String>
    ) : QuestionType()
    data class ConditionalText(
        val followUpPrompt: String
    ) : QuestionType()
    object EndLoop            : QuestionType()
}

data class Question(
    val text: String,
    val type: QuestionType
)
