package com.example.task61d.llm
import com.example.task61d.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
object LlmClient {
    private val API_KEY: String get() = BuildConfig.GROQ_API_KEY
    private const val MODEL = "llama-3.1-8b-instant"
    private const val ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"

    data class LlmResponse(val prompt: String, val text: String, val usedFallback: Boolean)

    sealed class Result {
        data class Success(val data: LlmResponse) : Result()
        data class Failure(val prompt: String, val message: String) : Result()
    }
    suspend fun generate(prompt: String): Result = withContext(Dispatchers.IO) {
        if (API_KEY.isBlank()) {
            return@withContext Result.Success(
                LlmResponse(prompt, localFallback(prompt), usedFallback = true)
            )
        }
        try {
            val url = URL(ENDPOINT)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 15_000
                readTimeout = 20_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Authorization", "Bearer $API_KEY")
            }
            val body = JSONObject().apply {
                put("model", MODEL)
                put("messages", JSONArray().put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                }))
                put("max_tokens", 300)
                put("temperature", 0.7)
            }
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val raw = stream.bufferedReader().use { it.readText() }
            if (code !in 200..299) {
                return@withContext Result.Failure(prompt, "HTTP $code: ${raw.take(200)}")
            }
            val text = JSONObject(raw)
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?: return@withContext Result.Failure(prompt, "Empty response from model")
            Result.Success(LlmResponse(prompt, text.trim(), usedFallback = false))
        } catch (e: Exception) {
            Result.Success(
                LlmResponse(prompt, localFallback(prompt), usedFallback = true)
            )
        }
    }
    private fun localFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            "hint" in lower -> "Hint: Re-read the question carefully. Eliminate the option " +
                    "that is clearly incorrect, then compare the remaining choices against the " +
                    "definition you learned for this topic."
            "incorrect" in lower -> "This answer is incorrect because it does not match the " +
                    "formal definition of the concept. Review the key differences between the " +
                    "options and focus on the precise meaning of each term."
            "explain" in lower || "why" in lower -> "This answer is correct because it best " +
                    "matches the formal definition of the concept. The other options are " +
                    "either too narrow, too broad, or describe a different idea entirely."
            "summary" in lower || "summarise" in lower -> "Summary: Focus on the core concepts, " +
                    "common pitfalls, and worked examples. Add a Groq API key in local.properties " +
                    "to get live AI-powered summaries."
            else -> "Here is a thoughtful response for your prompt. " +
                    "Add a Groq API key in local.properties to get live AI responses."
        }
    }
}
