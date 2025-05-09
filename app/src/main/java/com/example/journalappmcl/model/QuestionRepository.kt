package com.example.journalappmcl.model

import com.example.journalappmcl.R
import com.example.journalappmcl.notification.NotificationManager

object QuestionRepository {
    fun getFollowupQuestions(): List<Question> = listOf(
        Question(
            text = "Hi again! Let's take a moment and explore what is happening for you right now. We invite you to approach your experience with curiosity and without any pressure or judgment. By judgment, we mean, for example, whether craving is good or bad. The goal is to explore more deeply how you experience your craving. To do this, we will guide you through some different questions. There is no right or wrong way to answer.",
            type = QuestionType.Statement
        ),
        Question(
            text = "Are you still craving?",
            type = QuestionType.YesNo(yesNextIndex = 2, noNextIndex = 10, yesFunc = NotificationManager::schedule20minNotification)
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
            text = "How intense is your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 4,
                mildNextIndex = 5
            )
        ),
        Question(
            text = "What makes your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 6
            )
        ),
        Question(
            text = "What makes your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 6
            )
        ),
        Question(
            text = "Does it feel like things are happening for you with a greater speed? Or does it feel like things are slowing down and the moment never passes? Or do you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
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
        Question(
            text = "Please describe any emotions that are standing out to you.",
            type = QuestionType.Text(
                NextIndex = 9
            )
        ),
        Question(
            text = "Are you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What are you doing to control it?"
            )
        ),
        Question(
            text = "Please tell us about your current situation.",
            type = QuestionType.MultiText(
                subQuestions = listOf(
                    "How did the craving stop?",
                    "What are you doing?",
                    "Are you alone or with others?"
                )
            )
        ),
        Question(
            text = "Thank you for your time! We will check in later.",
            type = QuestionType.EndLoop
        )
    )
    fun getInitialQuestions(): List<Question> = listOf(
        Question(
            text = "Hi there! Let's take a moment and explore what is happening for you right now.",
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
        Question(
            text = "When was the last time you ate (approximately)?",
            type = QuestionType.Option(
                options = listOf(
                    "Less than an hour ago",
                    "1-3 hours ago",
                    "More than 3 hours ago"
                )
            )
        ),
        Question(
            text = "Are you craving food right now?",
            type = QuestionType.YesNo(yesNextIndex = 5, noNextIndex = 17, yesFunc = NotificationManager::schedule20minNotification)
        ),
        Question(
            text = "How do you know that you are craving?",
            type = QuestionType.Text(
                NextIndex=6
            )
        ),
        Question(
            text = "Are you craving any specific foods?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What food(s) are you craving?"
            )
        ),
        Question(
            text = "How intense is your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 8,
                mildNextIndex = 9
            )
        ),
        Question(
            text = "What makes your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 10
            )
        ),
        Question(
            text = "What makes your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 10
            )
        ),
        Question(
            text = "Does it feel like things are happening for you with a greater speed? Or does it feel like things are slowing down and the moment never passes? Or do you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
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
        Question(
            text = "Please describe any emotions that are standing out to you.",
            type = QuestionType.Text(
                NextIndex = 13
            )
        ),
        Question(
            text = "Are you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What are you doing to control it?"
            )
        ),
        Question(
            text = "Do you remember what elicited the craving?",
            type = QuestionType.YesNo(yesNextIndex = 15, noNextIndex = 29)
        ),
        Question(
            text = "What elicited the craving? (select all that apply, and describe further in text)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "Mood",
                    "Hunger",
                    "Social Context",
                    "Other",
                ),
                NextIndex = 16
            )
        ),
        Question(
            text = "Did you... (select all that apply)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "See the food",
                    "Smell the food",
                    "Think about the food"
                ),
                NextIndex = 29
            )
        ),
        Question(
            text = "Did you have a craving experience recently?",
            type = QuestionType.YesNo(yesNextIndex = 18, noNextIndex = 29)
        ),
        Question(
            text = "How did you know that you were craving?",
            type = QuestionType.Text(
                NextIndex = 19
            )
        ),
        Question(
            text = "Were you craving any specific foods?",
            type = QuestionType.Text(
                NextIndex = 20
            )
        ),
        Question(
            text = "How intense was your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 21,
                mildNextIndex = 22
            )
        ),
        Question(
            text = "What made your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 23,
            )
        ),
        Question(
            text = "What made your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 23
            )
        ),
        Question(
            text = "Did it feel like things were happening for you with a greater speed? Or did it feel like things were slowing down and the moment never passed? Or did you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
        Question(
            text = "What was your attention like while you were craving? Choose the image that best represents it.",
            type = QuestionType.ImageOptions(
                drawableIds = listOf(
                    R.drawable.busy_mind,
                    R.drawable.calm_mind,
                    R.drawable.tunnel_vision
                )
            )
        ),
        Question(
            text = "Please describe any emotions that you were experiencing while craving",
            type = QuestionType.Text(
                NextIndex = 26
            )
        ),
        Question(
            text = "Were you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "How did you control the craving?"
            )
        ),
        Question(
            text = "What elicited the craving? (select all that apply, and describe further in text)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "Mood",
                    "Hunger",
                    "Social Context",
                    "Other"
                ),
                NextIndex = 28
            )
        ),
        Question(
            text = "Did you... (select all that apply)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "See the food",
                    "Smell the food",
                    "Think about the food"
                ),
                NextIndex = 29
            )
        ),
        Question(
            text = "Thank you for your time! We will check in later.",
            type = QuestionType.EndLoop
        )
    )

    fun getInitialInfoMessages(): List<String> = listOf(
        "", // statement
        "", // situation
        "", // hungry
        "", // last time ate
        "", // craving now
        "Please describe how you notice that you are craving", // how do you know
        "", // specific foods
        "", // intensity
        "", // intense feeling
        "", // mild feeling
        "", // time perception
        "", // attention state
        "", // emotions
        "", // control craving
        "", // remember elicitor
        "", // what elicited
        "", // did you see/smell/think
        "", // recent craving
        "", // how did you know
        "", // specific foods
        "", // intensity
        "", // intense feeling
        "", // mild feeling
        "", // time perception
        "", // attention state
        "", // emotions
        "", // control craving
        "", // what elicited
        "", // did you see/smell/think
        ""  // end loop
    )
}
