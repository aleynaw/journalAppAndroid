package com.example.journalappmcl.model

import com.example.journalappmcl.R

object QuestionRepository {
    fun getInitialQuestions(): List<Question> = listOf(
        Question(
            text = "Hi there! Let’s take a moment and explore what is happening for you right now.",
            type = QuestionType.Statement
        ),
        Question(
            text = "Please tell us about your current situation.",
            type = QuestionType.MultiText(
                subQuestions = listOf(
                    "Where are you?",
                    "What are you doing?",
                    "Are you alone or with others?"
                )
            )
        ),
        Question(
            text = "Are you physically hungry?",
            type = QuestionType.YesNo(yesNextIndex = 3, noNextIndex = 3)
        ),
        // … continue porting each Question exactly as in Swift …
        Question(
            text = "Which image below best represents your attentional state?",
            type = QuestionType.ImageOptions(
                drawableIds = listOf(
                    R.drawable.busy_mind,
                    R.drawable.calm_mind,
                    R.drawable.tunnel_vision
                )
            )
        ),
        // final end‐loop question
        Question(
            text = "Thank you for your time! We will check in later.",
            type = QuestionType.EndLoop
        )
    )

    fun getInitialInfoMessages(): List<String> = listOf(
        "", // statement
        "", // situation
        "", // hungry
        // … map each index to the matching infoMessages entry …
        "Please describe how you notice that you are craving", // index 5
        // …
        ""  // index 29
    )
}
