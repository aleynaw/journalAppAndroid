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
    val infoMsgs by vm.infoMessages.collectAsState()
    val idx by vm.currentIndex.collectAsState()

    // Current question & info
    val q = questions.getOrNull(idx) ?: return
    val info = infoMsgs.getOrNull(idx) ?: ""

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Question text at the top
            Text(
                text = q.text,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 24.dp)
            )

            // Center content - takes most of the space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // ─── Input Control by QuestionType ────────────────
                when (q.type) {
                    is QuestionType.Statement -> {
                        Text(
                            "We invite you to approach your experience with curiosity and without any pressure or judgment. To do this, we will guide you through some questions. There is no right or wrong way to answer.",
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    is QuestionType.Text -> {
                        OutlinedTextField(
                            value = vm.textAnswer,
                            onValueChange = { vm.textAnswer = it },
                            placeholder = { Text("Please describe your experience without any judgment.") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        )
                    }

                    is QuestionType.YesNo -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Button(
                                onClick = { vm.yesNoAnswer = true
                                        q.type.yesFunc?.let { it() }
                                          },
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vm.yesNoAnswer == true) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                )
                            ) {
                                Text(
                                    "Yes",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }

                            Button(
                                onClick = { vm.yesNoAnswer = false },
                                shape = MaterialTheme.shapes.large,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (vm.yesNoAnswer == false) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                                )
                            ) {
                                Text(
                                    "No",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    is QuestionType.Option -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        ) {
                            (q.type as QuestionType.Option).options.forEach { opt ->
                                Button(
                                    onClick = { vm.optionAnswer = opt },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.large,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (vm.optionAnswer == opt) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        ) {
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
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
                        // Centered time picker placeholder
                        Text(
                            "Time picker goes here",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )
                    }

                    is QuestionType.MultiQ -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        ) {
                            (q.type as QuestionType.MultiQ).options.forEach { opt ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
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
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        ) {
                            val subs = (q.type as QuestionType.MultiText).subQuestions
                            subs.forEachIndexed { i, prompt ->
                                Text(
                                    prompt,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = vm.multiTextAnswers.getOrNull(i).orEmpty(),
                                    onValueChange = { new ->
                                        val list = vm.multiTextAnswers.toMutableList()
                                        if (i < list.size) list[i] = new else list.add(new)
                                        vm.multiTextAnswers = list
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }

                    is QuestionType.ConditionalText -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Button(
                                    onClick = { vm.conditionalYesNo = true },
                                    shape = MaterialTheme.shapes.large
                                ) { Text("Yes") }
                                Button(
                                    onClick = { vm.conditionalYesNo = false },
                                    shape = MaterialTheme.shapes.large
                                ) { Text("No") }
                            }
                            if (vm.conditionalYesNo == true) {
                                Text(
                                    (q.type as QuestionType.ConditionalText).followUpPrompt,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = vm.conditionalText,
                                    onValueChange = { vm.conditionalText = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    is QuestionType.EndLoop -> {
                        Text(
                            text = q.text,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(horizontal = 16.dp)
                        )
                        LaunchedEffect(Unit) {
                            vm.uploadResponsesToGlobus(context)
                            vm.setCompleted()
                        }
                    }
                }
            }

            // Next button at the bottom
            if (q.type !is QuestionType.EndLoop) {
                Button(
                    onClick = { vm.onNext() },
                    enabled = vm.isAnswerValid(),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.padding(vertical = 24.dp)
                ) {
                    Text(
                        "Next",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                }
                // Optional progress indicator
                LinearProgressIndicator(
                    progress = (idx.toFloat() / (questions.size - 1)),
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}
