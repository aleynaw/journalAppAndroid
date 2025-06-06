package com.example.journalappmcl.model

import com.example.journalappmcl.R
import com.example.journalappmcl.notification.NotificationManager

object QuestionRepository {
    fun getFollowupQuestions(): List<Question> = listOf(
        Question( //0
            text = "Hi again! Let's take a moment and explore what is happening for you right now.",
            type = QuestionType.Statement
        ),
        Question( //1
            text = "Are you still craving?",
            type = QuestionType.YesNo(yesNextIndex = 2, noNextIndex = 10, yesFunc = NotificationManager::schedule20minNotification)
        ),
        Question( //2
            text = "Please tell us about your current situation.",
            type = QuestionType.MultiText(
                subQuestions = listOf(
                    "Where are you?",
                    "What are you doing?",
                    "Are you alone or with others?"
                )
            )
        ),
        Question( //3
            text = "How intense is your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 4,
                mildNextIndex = 5
            )
        ),
        Question( //4
            text = "What makes your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 6
            )
        ),
        Question( //5
            text = "What makes your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 6
            )
        ),
        Question( //6
            text = "Does it feel like things are happening for you with a greater speed? Or does it feel like things are slowing down and the moment never passes? Or do you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
        Question( //7
            text = "Which image below best represents your attentional state?",
            type = QuestionType.ImageOptions(
                drawableIds = listOf(
                    R.drawable.busy_mind,
                    R.drawable.calm_mind,
                    R.drawable.tunnel_vision
                )
            )
        ),
        Question( //8
            text = "Please describe any emotions that are standing out to you.",
            type = QuestionType.Text(
                NextIndex = 9
            )
        ),
        Question( //9
            text = "Are you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What are you doing to control it?" ,
                NextIndex = 11
            )
        ),
        Question( //10
            text = "How did the craving stop? How did you know it was over?",
            type = QuestionType.Text(
                NextIndex=11
            )
        ),
        Question( //11
            text = "Thank you for your time! We will check in later.",
            type = QuestionType.EndLoop
        )
    )
    fun getInitialQuestions(): List<Question> = listOf(
        Question( //0
            text = "Hi there! Let's take a moment and explore what is happening for you right now.",
            type = QuestionType.Statement
        ),
        Question( //1
            text = "Please tell us about your current situation.",
            type = QuestionType.MultiText(
                subQuestions = listOf(
                    "Where are you?",
                    "What are you doing?",
                    "Are you alone or with others?"
                )
            )
        ),
        Question( //2
            text = "Are you physically hungry?",
            type = QuestionType.YesNo(yesNextIndex = 3, noNextIndex = 3)
        ),
        Question( //3
            text = "When was the last time you ate (approximately)?",
            type = QuestionType.Option(
                options = listOf(
                    "Less than an hour ago",
                    "1-3 hours ago",
                    "More than 3 hours ago"
                )
            )
        ),
        Question( //4
            text = "Are you craving food right now?",
            type = QuestionType.YesNo(yesNextIndex = 5, noNextIndex = 16, yesFunc = NotificationManager::schedule20minNotification)
        ),
        Question( //5
            text = "How do you know that you are craving?",
            type = QuestionType.Text(
                NextIndex=6
            )
        ),
        Question( //6
            text = "Are you craving any specific foods?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What food(s) are you craving?",
                NextIndex=7
            )
        ),
        Question( //7
            text = "How intense is your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 8,
                mildNextIndex = 9
            )
        ),
        Question( //8
            text = "What makes your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 10
            )
        ),
        Question( //9
            text = "What makes your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 10
            )
        ),
        Question( //10
            text = "Does it feel like things are happening for you with a greater speed? Or does it feel like things are slowing down and the moment never passes? Or do you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
        Question( //1
            text = "Which image below best represents your attentional state?",
            type = QuestionType.ImageOptions(
                drawableIds = listOf(
                    R.drawable.busy_mind,
                    R.drawable.calm_mind,
                    R.drawable.tunnel_vision
                )
            )
        ),
        Question( //12
            text = "Please describe any emotions that are standing out to you.",
            type = QuestionType.Text(
                NextIndex = 13
            )
        ),
        Question( //13
            text = "Are you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "What are you doing to control it?",
                NextIndex = 14
            )
        ),
        Question( //14
            text = "Do you remember what elicited the craving?",
            type = QuestionType.YesNo(yesNextIndex = 15, noNextIndex = 28)
        ),
        Question( //15
            text = "What elicited the craving? (select all that apply, and describe further in text)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "Mood",
                    "Hunger",
                    "Social Context",
                    "Spontaneous Thought",
                    "Other",
                ),
                NextIndex = 28
            )
        ),
        Question( //16
            text = "Did you have a craving experience recently?",
            type = QuestionType.YesNo(yesNextIndex = 17, noNextIndex = 28)
        ),
        Question( //17
            text = "Please tell us about your past situation.",
            type = QuestionType.MultiText(
                subQuestions = listOf(
                    "Where are you?",
                    "What are you doing?",
                    "Are you alone or with others?"
                )
            )
        ),
        Question( //18
            text = "How did you know that you were craving?",
            type = QuestionType.Text(
                NextIndex = 19
            )
        ),
        Question( //19
            text = "Were you craving any specific foods?",
            type = QuestionType.Text(
                NextIndex = 20
            )
        ),
        Question( //20
            text = "How intense was your craving?",
            type = QuestionType.Slider(
                range = 0f..10f,
                step = 1f,
                intenseNextIndex = 21,
                mildNextIndex = 22
            )
        ),
        Question( //21
            text = "What made your craving feel intense?",
            type = QuestionType.Text(
                NextIndex = 23,
            )
        ),
        Question( //22
            text = "What made your craving feel mild?",
            type = QuestionType.Text(
                NextIndex = 23
            )
        ),
        Question( //23
            text = "Did it feel like things were happening for you with a greater speed? Or did it feel like things were slowing down and the moment never passed? Or did you not notice any change at all?",
            type = QuestionType.Option(
                options = listOf(
                    "Speeding up",
                    "Slowing down",
                    "No change"
                )
            )
        ),
        Question( //24
            text = "What was your attention like while you were craving? Choose the image that best represents it.",
            type = QuestionType.ImageOptions(
                drawableIds = listOf(
                    R.drawable.busy_mind,
                    R.drawable.calm_mind,
                    R.drawable.tunnel_vision
                )
            )
        ),
        Question( //25
            text = "Please describe any emotions that you were experiencing while craving",
            type = QuestionType.Text(
                NextIndex = 26
            )
        ),
        Question( //26
            text = "Were you trying to control the craving?",
            type = QuestionType.ConditionalText(
                followUpPrompt = "How did you control the craving?",
                NextIndex = 27
            )
        ),
        Question( //27
            text = "What elicited the craving? (select all that apply, and describe further in text)",
            type = QuestionType.MultiQ(
                options = listOf(
                    "Mood",
                    "Hunger",
                    "Social Context",
                    "Spontaneous Thought",
                    "Other"
                ),
                NextIndex = 28
            )
        ),
        Question( //28
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
        "Please rate the intensity of your craving from mild to very strong.", // intensity
        "", // intense feeling
        "", // mild feeling
        "Sometimes the flow of time may feel accelerated or slowed down without an obvious explanation." +
                "Do you notice any change in your experience of time, or does it feel like nothing has changed at all?", // time perception
        "", // attention state
        "", // emotions
        "", // control craving
        "", // remember elicitor
        "", // what elicited
        "", // did you see/smell/think
        "", // recent craving
        "Please describe how you noticed that you were craving", // how did you know
        "", // specific foods
        "Please rate the intensity of your craving from mild to very strong.", // intensity
        "", // intense feeling
        "", // mild feeling
        "Sometimes the flow of time may feel accelerated or slowed without an obvious explanation. Did you notice a" +
                "change in your experience of time, or did it feel like nothing has changed at all?", // time perception
        "", // attention state
        "", // emotions
        "", // control craving
        "", // what elicited
        "", // did you see/smell/think
        ""  // end loop
    )

    fun getFollowupInfoMessages(): List<String> = listOf(
        "", // statement 0
        "", // situation 1
        "", // cuur sit 2
        "Please rate the intensity of your craving from mild to very strong.", // intensity 3
        "", // intense 4
        "", // mild 5
        "Sometimes the flow of time may feel accelerated or slowed down without an obvious explanation." +
                "Do you notice any change in your experience of time, or does it feel like nothing has changed at all?", // time perception 6
        "", // attention state 7
        "", // emotions 8
        "", // control craving 9
        "", // how stop 10
        ""  // end loop 11
    )
}
