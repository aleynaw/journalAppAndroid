package com.example.journalappmcl.model

import android.content.Context
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KFunction1

@Serializable
sealed class QuestionType {
    @Serializable
    object Statement           : QuestionType()
    @Serializable
    data class Text(
        val NextIndex: Int?
    )               : QuestionType()
    @Serializable
    data class YesNo(
        val yesNextIndex: Int?,
        val noNextIndex: Int?,
        @Transient
        val yesFunc: KFunction1<Context, Unit>? = null
    ) : QuestionType()
    @Serializable
    data class Option(
        val options: List<String>
    ) : QuestionType()
    @Serializable
    data class Slider(
        @Serializable(with = FloatRangeSerializer::class)
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
        val options: List<String>,
        val NextIndex: Int
    ) : QuestionType()
    @Serializable
    data class MultiText(
        val subQuestions: List<String>
    ) : QuestionType()
    @Serializable
    data class ConditionalText(
        val followUpPrompt: String,
        val NextIndex: Int?
    ) : QuestionType()
    @Serializable
    object EndLoop            : QuestionType()
}

@Serializable
data class Question(
    val text: String,
    val type: QuestionType
)

class FloatRangeSerializer : KSerializer<ClosedFloatingPointRange<Float>> {
    override val descriptor = PrimitiveSerialDescriptor("FloatRange", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ClosedFloatingPointRange<Float>) {
        encoder.encodeString("${value.start},${value.endInclusive}")
    }

    override fun deserialize(decoder: Decoder): ClosedFloatingPointRange<Float> {
        val parts = decoder.decodeString().split(",")
        return parts[0].toFloat()..parts[1].toFloat()
    }
}
