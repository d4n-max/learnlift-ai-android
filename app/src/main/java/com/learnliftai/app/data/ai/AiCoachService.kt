package com.learnliftai.app.data.ai

import com.learnliftai.app.BuildConfig
import android.util.Log
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
                .put("studyPathTitle", request.studyPathTitle)
                .put("score", request.score)
                .put("totalQuestions", request.totalQuestions)
                .put("numberCorrect", request.numberCorrect)
                .put("numberWrong", request.numberWrong)
                .put("incorrectTopics", JSONArray(request.incorrectTopics))
                .put("weakTopics", JSONArray(request.weakTopics))
                .put("wrongQuestions", request.wrongQuestions.toJsonArray())
                .put("difficultySummary", request.difficultySummary),
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
                .put("studyPathTitle", request.studyPathTitle)
                .put("onboardingGoal", request.onboardingGoal)
                .put("dailyStudyMinutes", request.dailyStudyMinutes)
                .put("weakTopics", JSONArray(request.weakTopics))
                .put("dueSmartReviewCount", request.dueSmartReviewCount)
                .put("recentQuizSummary", request.recentQuizSummary)
                .put("planState", request.planState)
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
        logEndpoint(action)
        if (!isConfiguredEndpoint()) {
            debugLog("AI Coach endpoint is not configured for action=$action")
            return@withContext AiCoachResult.Error(AiCoachUnavailableWithFallbackMessage)
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
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }
            debugLog("Sending action=$action")

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
            debugLog(
                "HTTP status=$statusCode action=$action responsePreview=${responseBody.sanitizedPreview()}"
            )

            if (statusCode !in 200..299) {
                debugLog(
                    "Backend error action=$action code=${backendErrorCode(responseBody) ?: "unknown"}"
                )
                return@withContext AiCoachResult.Error(friendlyMessageFromError(responseBody))
            }

            val responseJson = JSONObject(responseBody)
            val wrapperAction = responseJson.optString("action")
            val resultJson = responseJson.optJSONObject("result")
            if (resultJson == null) {
                debugLog("Parsing failure action=$action reason=missing_result")
                return@withContext AiCoachResult.Error(AiCoachUnavailableWithFallbackMessage)
            }
            if (wrapperAction.isNotBlank() && wrapperAction != action) {
                debugLog("Parsing warning action=$action wrapperAction=$wrapperAction")
            }

            val parsed = parser(resultJson)
            debugLog("Parsing success action=$action")
            AiCoachResult.Success(parsed)
        }.getOrElse { error ->
            debugLog("Parsing/request failure action=$action reason=${error.javaClass.simpleName}")
            AiCoachResult.Error(AiCoachUnavailableWithFallbackMessage)
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
                error == "AI_RESPONSE_PARSE_ERROR" ||
                error == "AI_PROXY_CONFIGURATION_ERROR" ||
                error == "OPENAI_INSUFFICIENT_QUOTA" ||
                error == "OPENAI_INVALID_API_KEY" ||
                error == "OPENAI_MODEL_NOT_FOUND" ||
                error == "OPENAI_RATE_LIMIT_EXCEEDED" ||
                message.contains("insufficient_quota", ignoreCase = true) ||
                message.contains("invalid_api_key", ignoreCase = true) ||
                message.contains("model_not_found", ignoreCase = true) ||
                message.contains("rate_limit_exceeded", ignoreCase = true) ||
                message.contains("temporarily unavailable", ignoreCase = true)
            ) {
                AiCoachUnavailableWithFallbackMessage
            } else {
                AiCoachUnavailableMessage
            }
        }.getOrDefault(AiCoachUnavailableMessage)
    }

    private fun backendErrorCode(responseBody: String): String? {
        return runCatching {
            JSONObject(responseBody).optString("error").takeIf { it.isNotBlank() }
        }.getOrNull()
    }

    private fun logEndpoint(action: String) {
        if (!BuildConfig.DEBUG) return
        runCatching {
            val url = URL(endpointUrl)
            debugLog("Configured endpoint action=$action host=${url.host} path=${url.path}")
        }.getOrElse {
            debugLog("Configured endpoint action=$action invalid_url")
        }
    }

    private fun debugLog(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(LogTag, message)
        }
    }

    private fun String.sanitizedPreview(maxChars: Int = 500): String {
        return replace(Regex("sk-[A-Za-z0-9_-]+"), "sk-REDACTED")
            .replace(Regex("\\s+"), " ")
            .trim()
            .take(maxChars)
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

    private fun List<WrongQuestionSample>.toJsonArray(): JSONArray {
        return JSONArray(map { sample ->
            JSONObject()
                .put("question", sample.question)
                .put("selectedAnswer", sample.selectedAnswer)
                .put("correctAnswer", sample.correctAnswer)
                .put("topic", sample.topic)
                .put("difficulty", sample.difficulty)
        })
    }

    private companion object {
        const val LogTag = "LearnLiftAiCoach"
        const val RequestTimeoutMillis = 12_000
    }
}
