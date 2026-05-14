package com.learnliftai.app.data.ai

import com.learnliftai.app.BuildConfig
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AiCoachService(
    private val endpointUrl: String = BuildConfig.SUPABASE_AI_COACH_URL
) {
    suspend fun explainAnswer(request: ExplainAnswerRequest): AiCoachResult<ExplainAnswerResponse> {
        return postAiCoachRequest(
            action = "explain_answer",
            payload = JSONObject()
                .put("studyPathId", request.studyPathId)
                .put("topic", request.topic)
                .put("difficulty", request.difficulty)
                .put("question", request.question)
                .put("selectedAnswer", request.selectedAnswer)
                .put("correctAnswer", request.correctAnswer)
                .put("staticExplanation", request.staticExplanation),
            parser = { result ->
                ExplainAnswerResponse(
                    title = result.getString("title"),
                    conciseExplanation = result.getString("conciseExplanation"),
                    whyCorrectAnswerWorks = result.getString("whyCorrectAnswerWorks"),
                    studyTip = result.getString("studyTip"),
                    recommendedTopic = result.getString("recommendedTopic")
                )
            }
        )
    }

    suspend fun quizSummary(request: QuizSummaryRequest): AiCoachResult<QuizSummaryResponse> {
        return postAiCoachRequest(
            action = "quiz_summary",
            payload = JSONObject()
                .put("studyPathId", request.studyPathId)
                .put("score", request.score)
                .put("totalQuestions", request.totalQuestions)
                .put("incorrectTopics", JSONArray(request.incorrectTopics))
                .put("weakTopics", JSONArray(request.weakTopics)),
            parser = { result ->
                QuizSummaryResponse(
                    summary = result.getString("summary"),
                    recommendedFocus = result.getJSONArray("recommendedFocus").toStringList(),
                    nextSessionSuggestion = result.getString("nextSessionSuggestion"),
                    encouragement = result.getString("encouragement")
                )
            }
        )
    }

    suspend fun studyPlan(request: StudyPlanRequest): AiCoachResult<StudyPlanResponse> {
        return postAiCoachRequest(
            action = "study_plan",
            payload = JSONObject()
                .put("studyPathId", request.studyPathId)
                .put("goal", request.goal)
                .put("days", request.days)
                .put("level", request.level),
            parser = { result ->
                StudyPlanResponse(
                    title = result.getString("title"),
                    days = result.getJSONArray("days").toStudyPlanDays()
                )
            }
        )
    }

    private suspend fun <T> postAiCoachRequest(
        action: String,
        payload: JSONObject,
        parser: (JSONObject) -> T
    ): AiCoachResult<T> = withContext(Dispatchers.IO) {
        if (!isConfiguredEndpoint()) {
            return@withContext AiCoachResult.Error()
        }

        runCatching {
            val body = JSONObject()
                .put("action", action)
                .put("payload", payload)
                .toString()
            val connection = (URL(endpointUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = RequestTimeoutMillis
                readTimeout = RequestTimeoutMillis
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(body)
            }

            val statusCode = connection.responseCode
            val responseBody = if (statusCode in 200..299) {
                connection.inputStream.bufferedReader().use(BufferedReader::readText)
            } else {
                connection.errorStream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
            }
            connection.disconnect()

            if (statusCode !in 200..299) {
                return@withContext AiCoachResult.Error(friendlyMessageFromError(responseBody))
            }

            val responseJson = JSONObject(responseBody)
            val resultJson = responseJson.getJSONObject("result")
            AiCoachResult.Success(parser(resultJson))
        }.getOrElse {
            AiCoachResult.Error()
        }
    }

    private fun isConfiguredEndpoint(): Boolean {
        return endpointUrl.startsWith("https://") &&
            !endpointUrl.contains("YOUR_PROJECT_REF") &&
            endpointUrl.endsWith("/functions/v1/ai-coach")
    }

    private fun friendlyMessageFromError(responseBody: String): String {
        return runCatching {
            val errorJson = JSONObject(responseBody)
            val error = errorJson.optString("error")
            val message = errorJson.optString("message")
            if (
                error == "AI_PROVIDER_ERROR" ||
                message.contains("insufficient_quota", ignoreCase = true) ||
                message.contains("temporarily unavailable", ignoreCase = true)
            ) {
                AiCoachUnavailableWithFallbackMessage
            } else {
                AiCoachUnavailableMessage
            }
        }.getOrDefault(AiCoachUnavailableMessage)
    }

    private fun JSONArray.toStringList(): List<String> {
        return (0 until length()).mapNotNull { index -> optString(index).takeIf { it.isNotBlank() } }
    }

    private fun JSONArray.toStudyPlanDays(): List<StudyPlanDay> {
        return (0 until length()).map { index ->
            val day = getJSONObject(index)
            StudyPlanDay(
                day = day.getInt("day"),
                focus = day.getString("focus"),
                tasks = day.getJSONArray("tasks").toStringList()
            )
        }
    }

    private companion object {
        const val RequestTimeoutMillis = 12_000
    }
}
