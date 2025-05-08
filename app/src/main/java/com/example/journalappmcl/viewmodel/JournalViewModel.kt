package com.example.journalappmcl.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.journalappmcl.GlobusUploader
import com.example.journalappmcl.UserPreferences
import com.example.journalappmcl.model.Question
import com.example.journalappmcl.model.QuestionRepository
import com.example.journalappmcl.model.QuestionResponse
import com.example.journalappmcl.model.QuestionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class JournalViewModel : ViewModel() {
    private val json = Json {
        prettyPrint = true
        serializersModule = SerializersModule {
            contextual(Instant::class, InstantSerializer)
        }
    }

    // ─── Backing Flows for questions, info messages, current index, and responses ───
    private val _questions = MutableStateFlow(QuestionRepository.getInitialQuestions())
    val questions: StateFlow<List<Question>> = _questions

    private val _infoMessages = MutableStateFlow(QuestionRepository.getInitialInfoMessages())
    val infoMessages: StateFlow<List<String>> = _infoMessages

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _responses = MutableStateFlow<List<QuestionResponse>>(emptyList())
    val responses: StateFlow<List<QuestionResponse>> = _responses

    private var _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted

    // ─── UI State for the "answer" controls ───────────────────────────────────────────
    var textAnswer by mutableStateOf("")
    var yesNoAnswer by mutableStateOf<Boolean?>(null)
    var optionAnswer by mutableStateOf<String?>(null)
    var sliderValue by mutableStateOf(10f)
    var selectedImageIndex by mutableStateOf<Int?>(null)
    var multiSelections by mutableStateOf(setOf<String>())
    var multiTextAnswers by mutableStateOf(listOf<String>())
    var conditionalYesNo by mutableStateOf<Boolean?>(null)
    var conditionalText by mutableStateOf("")

    init {
        _isCompleted.value = false
    }

    fun setCompleted() {
        _isCompleted.value = true
    }

    fun resetState() {
        if (_isCompleted.value) {
            _questions.value = QuestionRepository.getInitialQuestions()
            _infoMessages.value = QuestionRepository.getInitialInfoMessages()
            _currentIndex.value = 0
            _responses.value = emptyList()
            resetAnswerState()
            _isCompleted.value = false
        }
    }

    // ─── Called by the "Next" button ───────────────────────────────────────────────────
    fun onNext() {
        saveCurrentResponse()
        advanceIndex()
        resetAnswerState()
    }

    // ─── Enable/disable the Next button ───────────────────────────────────────────────
    fun isAnswerValid(): Boolean {
        val q = questions.value[_currentIndex.value]
        return when (q.type) {
            is QuestionType.Statement    -> true
            is QuestionType.Text         -> textAnswer.isNotBlank()
            is QuestionType.YesNo        -> yesNoAnswer != null
            is QuestionType.Option       -> optionAnswer != null
            is QuestionType.Slider       -> true
            is QuestionType.ImageOptions -> selectedImageIndex != null
            is QuestionType.TimeSelect   -> true
            is QuestionType.MultiQ       -> multiSelections.isNotEmpty() && textAnswer.isNotBlank()
            is QuestionType.MultiText    -> {
                val subs = (q.type as QuestionType.MultiText).subQuestions
                multiTextAnswers.size == subs.size && multiTextAnswers.all { it.isNotBlank() }
            }
            is QuestionType.ConditionalText -> (conditionalYesNo == false) ||
                    (conditionalYesNo == true && conditionalText.isNotBlank())
            is QuestionType.EndLoop       -> true
        }
    }

    // ─── Persist the user's answer into the responses list ────────────────────────────
    private fun saveCurrentResponse() {
        val idx = _currentIndex.value
        val q = questions.value[idx]

        val answerStr = when (q.type) {
            is QuestionType.YesNo        -> if (yesNoAnswer == true) "Yes" else "No"
            is QuestionType.Option       -> optionAnswer.orEmpty()
            is QuestionType.Slider       -> sliderValue.toString()
            is QuestionType.MultiQ       -> multiSelections.joinToString()
            is QuestionType.MultiText    -> multiTextAnswers.joinToString(" | ")
            is QuestionType.ConditionalText ->
                if (conditionalYesNo == true) "Yes: $conditionalText"
                else "No"
            is QuestionType.ImageOptions -> selectedImageIndex?.toString().orEmpty()
            else                         -> textAnswer
        }

        val resp = QuestionResponse(
            questionText = q.text,
            type         = q.type,
            answer       = answerStr,
            sliderValue  = if (q.type is QuestionType.Slider) sliderValue else null,
            imageIndex   = if (q.type is QuestionType.ImageOptions) selectedImageIndex else null
        )
        _responses.value = _responses.value + resp
    }

    // ─── Figure out which question to show next (yesNextIndex/noNextIndex/etc) ─────
    private fun advanceIndex() {
        val idx = _currentIndex.value
        val q   = questions.value[idx]

        _currentIndex.value = when (q.type) {
            is QuestionType.YesNo -> {
                if (yesNoAnswer == true) (q.type as QuestionType.YesNo).yesNextIndex ?: idx + 1
                else                          (q.type as QuestionType.YesNo).noNextIndex  ?: idx + 1
            }
            is QuestionType.Slider -> {
                val s = q.type as QuestionType.Slider
                if (sliderValue >= (s.range.start + s.range.endInclusive) / 2) s.intenseNextIndex ?: idx + 1
                else                                                             s.mildNextIndex   ?: idx + 1
            }
            is QuestionType.MultiQ -> questions.value.indexOfFirst { it.type is QuestionType.EndLoop }
            is QuestionType.Text -> (q.type as QuestionType.Text).NextIndex!!
            else -> idx + 1
        }
        println("NEXT INDEX:")
        println(_currentIndex.value)
    }

    // ─── After moving on, clear out old answers ───────────────────────────────────────
    private fun resetAnswerState() {
        textAnswer         = ""
        yesNoAnswer        = null
        optionAnswer       = null
        sliderValue        = 10f
        selectedImageIndex = null
        multiSelections    = emptySet()
        multiTextAnswers   = emptyList()
        conditionalYesNo   = null
        conditionalText    = ""
    }

    fun uploadResponsesToGlobus(context: Context) {
        try {
            val jsonString = json.encodeToString(_responses.value)
            Log.d("JournalViewModel", "Serialized responses: $jsonString")
            
            // Get the access token from SharedPreferences
            val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
            val accessToken = prefs.getString("access_token", null)
            Log.d("JournalViewModel", "Retrieved access token: ${accessToken?.take(10)}...")
            
            // Get the user ID
            val userPreferences = UserPreferences(context)
            val userId = userPreferences.userId
            if (userId == null) {
                Log.e("JournalViewModel", "No user ID found")
                return
            }
            
            if (accessToken != null) {
                val uploader = GlobusUploader()
                uploader.uploadResponses(
                    responsesJson = jsonString,
                    baseUrl = "https://g-4e0411.88cee.8443.data.globus.org",
                    collectionPath = "CognitionUnderTension",
                    accessToken = accessToken,
                    userId = userId
                )
                resetState()
            } else {
                Log.e("JournalViewModel", "No access token found in auth_prefs")
            }
        } catch (e: Exception) {
            Log.e("JournalViewModel", "Failed to upload responses", e)
        }
    }
}

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}
