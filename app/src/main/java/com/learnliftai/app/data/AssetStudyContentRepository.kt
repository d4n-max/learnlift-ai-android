package com.learnliftai.app.data

import android.content.Context
import com.learnliftai.app.domain.model.Flashcard
import com.learnliftai.app.domain.model.QuizOption
import com.learnliftai.app.domain.model.QuizQuestion
import com.learnliftai.app.domain.model.StudyContent
import org.json.JSONArray
import org.json.JSONObject

object AssetStudyContentRepository {
    fun loadStudyContent(context: Context, pathId: String): StudyContent? {
        return runCatching {
            val fileName = "study_content/$pathId.json"
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            parseStudyContent(JSONObject(json))
        }.getOrNull()
    }

    private fun parseStudyContent(json: JSONObject): StudyContent {
        return StudyContent(
            pathId = json.getString("pathId"),
            flashcards = json.getJSONArray("flashcards").mapObjects { flashcardJson ->
                Flashcard(
                    id = flashcardJson.getString("id"),
                    pathId = flashcardJson.getString("pathId"),
                    question = flashcardJson.getString("question"),
                    answer = flashcardJson.getString("answer"),
                    topic = flashcardJson.getString("topic"),
                    difficulty = flashcardJson.getString("difficulty")
                )
            },
            quizQuestions = json.getJSONArray("quizQuestions").mapObjects { questionJson ->
                QuizQuestion(
                    id = questionJson.getString("id"),
                    pathId = questionJson.getString("pathId"),
                    question = questionJson.getString("question"),
                    options = questionJson.getJSONArray("options").mapObjects { optionJson ->
                        QuizOption(
                            id = optionJson.getString("id"),
                            text = optionJson.getString("text")
                        )
                    },
                    correctAnswerId = questionJson.getString("correctAnswerId"),
                    explanation = questionJson.getString("explanation"),
                    topic = questionJson.getString("topic"),
                    difficulty = questionJson.getString("difficulty")
                )
            }
        )
    }

    private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> {
        return List(length()) { index -> transform(getJSONObject(index)) }
    }
}
