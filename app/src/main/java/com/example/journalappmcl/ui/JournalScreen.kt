package com.example.journalappmcl.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.journalappmcl.model.Question
import com.example.journalappmcl.model.QuestionType
import com.example.journalappmcl.viewmodel.JournalViewModel
import com.example.journalappmcl.GlobusUploader
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
//import kotlinx.coroutines.flow.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(vm: JournalViewModel = viewModel()) {
    val context = LocalContext.current
    // Observe state from the ViewModel
    val questions by vm.questions.collectAsState()
    val infoMsgs  by vm.infoMessages.collectAsState()
    val idx       by vm.currentIndex.collectAsState()

    // Current question & info
    val q    = questions.getOrNull(idx) ?: return
    val info = infoMsgs.getOrNull(idx) ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ─── Question Text + Info Button ───────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = q.text,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            // show info icon only on certain indices and not for statements
            val infoIndices = setOf(7,10,18,20,25)
            if (q !is QuestionType.Statement && idx in infoIndices) {
                IconButton(onClick = { /* TODO: show AlertDialog with info */ }) {
                    Icon(Icons.Default.Info, contentDescription = "Info")
                }
            }
        }

        // ─── Input Control by QuestionType ────────────────
        when (q.type) {
            is QuestionType.Statement -> {
                Text(
                    "We invite you to approach your experience with curiosity…",
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is QuestionType.Text -> {
                OutlinedTextField(
                    value = vm.textAnswer,
                    onValueChange = { vm.textAnswer = it },
                    placeholder = { Text("Type your answer here…") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is QuestionType.YesNo -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { vm.yesNoAnswer = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vm.yesNoAnswer == true) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                        )
                    ) { Text("Yes") }
                    Button(
                        onClick = { vm.yesNoAnswer = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (vm.yesNoAnswer == false) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary
                        )
                    ) { Text("No") }
                }
            }

            is QuestionType.Option -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (q.type as QuestionType.Option).options.forEach { opt ->
                        Button(
                            onClick = { vm.optionAnswer = opt },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (vm.optionAnswer == opt) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.secondary
                            )
                        ) { Text(opt) }
                    }
                    if (vm.optionAnswer == "Other") {
                        OutlinedTextField(
                            value = vm.textAnswer,
                            onValueChange = { vm.textAnswer = it },
                            placeholder = { Text("Please specify…") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            is QuestionType.Slider -> {
                val slider = q.type as QuestionType.Slider
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Intensity", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = vm.sliderValue,
                        onValueChange = { vm.sliderValue = it },
                        valueRange = slider.range,
                        steps = ((slider.range.endInclusive - slider.range.start) / slider.step).toInt() - 1
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mild"); Text("Very Strong")
                    }
                    Text("Value: ${vm.sliderValue.toInt()}", style = MaterialTheme.typography.bodySmall)
                }
            }

            is QuestionType.ImageOptions -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    (q.type as QuestionType.ImageOptions).drawableIds.forEachIndexed { i, resId ->
                        Image(
                            painter = rememberAsyncImagePainter(resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .border(
                                    width = if (vm.selectedImageIndex == i) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                .clickable { vm.selectedImageIndex = i }
                        )
                    }
                }
            }

            is QuestionType.TimeSelect -> {
                // TODO: show a TimePickerDialog and set vm.textAnswer or another state
                Text("Time picker goes here", style = MaterialTheme.typography.bodyMedium)
            }

            is QuestionType.MultiQ -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (q.type as QuestionType.MultiQ).options.forEach { opt ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = vm.multiSelections.contains(opt),
                                onCheckedChange = {
                                    vm.multiSelections = if (it) vm.multiSelections + opt
                                    else vm.multiSelections - opt
                                }
                            )
                            Text(opt)
                        }
                    }
                    OutlinedTextField(
                        value = vm.textAnswer,
                        onValueChange = { vm.textAnswer = it },
                        placeholder = { Text("Please describe further…") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is QuestionType.MultiText -> {
                val subs = (q.type as QuestionType.MultiText).subQuestions
                subs.forEachIndexed { i, prompt ->
                    Text(prompt)
                    OutlinedTextField(
                        value = vm.multiTextAnswers.getOrNull(i).orEmpty(),
                        onValueChange = { new ->
                            val list = vm.multiTextAnswers.toMutableList()
                            if (i < list.size) list[i] = new else list.add(new)
                            vm.multiTextAnswers = list
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            is QuestionType.ConditionalText -> {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { vm.conditionalYesNo = true }) { Text("Yes") }
                    Button(onClick = { vm.conditionalYesNo = false }) { Text("No") }
                }
                if (vm.conditionalYesNo == true) {
                    Text((q.type as QuestionType.ConditionalText).followUpPrompt)
                    OutlinedTextField(
                        value = vm.conditionalText,
                        onValueChange = { vm.conditionalText = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is QuestionType.EndLoop -> {
                Text(
                    text = q.text,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                LaunchedEffect(Unit) {
                    vm.uploadResponsesToGlobus(context)
                    vm.setCompleted()
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ─── Next Button ─────────────────────────────────
        if (q.type !is QuestionType.EndLoop) {
            Button(
                onClick = { vm.onNext() },
                enabled = vm.isAnswerValid(),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Next")
            }
        }
    }
}
