import { buildPrompt } from "./promptTemplates.ts";
import {
  isExplainAnswerResult,
  isQuizSummaryResult,
  isStudyPlanResult,
} from "./responseSchemas.ts";
import type {
  AiCoachAction,
  AiCoachErrorResponse,
  AiCoachRequest,
  AiCoachResult,
  AiCoachSuccessResponse,
  ExplainAnswerPayload,
  QuizSummaryPayload,
  StudyPlanPayload,
} from "./types.ts";

const openAiResponsesUrl = "https://api.openai.com/v1/responses";
const defaultModel = "gpt-4.1-mini";
const defaultMaxInputChars = 6000;
const allowedActions: AiCoachAction[] = ["explain_answer", "quiz_summary", "study_plan"];
const rateLimitWindowMs = 60 * 60 * 1000;
const rateLimitByAction: Record<AiCoachAction, number> = {
  explain_answer: 30,
  quiz_summary: 15,
  study_plan: 5,
};
const rateLimitStore = new Map<string, { count: number; resetAt: number }>();

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, x-client-info, apikey, content-type",
  "Access-Control-Allow-Methods": "POST",
};

Deno.serve(async (request: Request): Promise<Response> => {
  const timestamp = new Date().toISOString();

  if (request.method !== "POST") {
    return jsonError("METHOD_NOT_ALLOWED", "Only POST requests are supported.", 405);
  }

  const contentType = request.headers.get("content-type") ?? "";
  if (!contentType.toLowerCase().includes("application/json")) {
    return jsonError("UNSUPPORTED_CONTENT_TYPE", "Content-Type must be application/json.", 400);
  }

  let body: unknown;
  try {
    body = await request.json();
  } catch {
    return jsonError("INVALID_JSON", "Request body must be valid JSON.", 400);
  }

  const validation = validateRequestBody(body, getMaxInputChars());
  if (!validation.ok) {
    return jsonError(validation.error, validation.message, 400);
  }

  const clientKey = getClientKey(request, validation.request.action);
  if (!consumeRateLimit(clientKey, validation.request.action)) {
    return jsonError("RATE_LIMITED", "Too many AI Coach requests. Try again later.", 429);
  }

  debugLog({
    action: validation.request.action,
    inputSize: JSON.stringify(validation.request.payload).length,
    success: null,
    timestamp,
  });

  try {
    const result = await callOpenAi(validation.request);
    debugLog({
      action: validation.request.action,
      inputSize: JSON.stringify(validation.request.payload).length,
      success: true,
      timestamp: new Date().toISOString(),
    });

    return jsonResponse({
      action: validation.request.action,
      result,
    });
  } catch (error) {
    debugLog({
      action: validation.request.action,
      inputSize: JSON.stringify(validation.request.payload).length,
      success: false,
      timestamp: new Date().toISOString(),
    });

    if (error instanceof AiResponseParseError) {
      return jsonError("AI_RESPONSE_PARSE_ERROR", "AI Coach returned an invalid response.", 502);
    }

    if (error instanceof MissingConfigurationError) {
      return jsonError("AI_PROXY_CONFIGURATION_ERROR", error.message, 500);
    }

    return jsonError("AI_PROVIDER_ERROR", "AI Coach is temporarily unavailable.", 502);
  }
});

async function callOpenAi(request: AiCoachRequest): Promise<AiCoachResult> {
  const apiKey = Deno.env.get("OPENAI_API_KEY");
  if (!apiKey) {
    throw new MissingConfigurationError("OPENAI_API_KEY is not configured for the AI proxy.");
  }

  const model = Deno.env.get("OPENAI_MODEL")?.trim() || defaultModel;
  const prompt = buildPrompt(request.action, request.payload);

  const providerResponse = await fetch(openAiResponsesUrl, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${apiKey}`,
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      model,
      input: [
        {
          role: "system",
          content: [{ type: "input_text", text: prompt.system }],
        },
        {
          role: "user",
          content: [{ type: "input_text", text: prompt.user }],
        },
      ],
      max_output_tokens: prompt.maxOutputTokens,
      store: false,
      text: {
        format: prompt.responseFormat,
      },
    }),
  });

  if (!providerResponse.ok) {
    throw new Error(`OpenAI provider returned ${providerResponse.status}.`);
  }

  const providerJson = await providerResponse.json();
  const outputText = extractOutputText(providerJson);
  if (!outputText) {
    throw new AiResponseParseError();
  }

  let parsed: unknown;
  try {
    parsed = JSON.parse(outputText);
  } catch {
    throw new AiResponseParseError();
  }

  if (request.action === "explain_answer" && isExplainAnswerResult(parsed)) {
    return parsed;
  }

  if (request.action === "quiz_summary" && isQuizSummaryResult(parsed)) {
    return parsed;
  }

  if (request.action === "study_plan" && isStudyPlanResult(parsed, request.payload.days)) {
    return parsed;
  }

  throw new AiResponseParseError();
}

function validateRequestBody(
  body: unknown,
  maxInputChars: number,
): { ok: true; request: AiCoachRequest } | { ok: false; error: string; message: string } {
  if (!isObject(body)) {
    return validationError("INVALID_BODY", "Request body must be a JSON object.");
  }

  const action = body.action;
  if (!isAiCoachAction(action)) {
    return validationError("INVALID_ACTION", "Action must be explain_answer, quiz_summary, or study_plan.");
  }

  const payload = body.payload;
  if (!isObject(payload)) {
    return validationError("INVALID_PAYLOAD", "Payload must be a JSON object.");
  }

  const inputSize = JSON.stringify(payload).length;
  if (inputSize > maxInputChars) {
    return validationError("INPUT_TOO_LARGE", `Payload must be ${maxInputChars} characters or less.`);
  }

  if (action === "explain_answer") {
    const result = validateExplainAnswerPayload(payload);
    return result.ok ? { ok: true, request: { action, payload: result.payload } } : result;
  }

  if (action === "quiz_summary") {
    const result = validateQuizSummaryPayload(payload);
    return result.ok ? { ok: true, request: { action, payload: result.payload } } : result;
  }

  const result = validateStudyPlanPayload(payload);
  return result.ok ? { ok: true, request: { action, payload: result.payload } } : result;
}

function validateExplainAnswerPayload(
  payload: Record<string, unknown>,
): { ok: true; payload: ExplainAnswerPayload } | { ok: false; error: string; message: string } {
  const studyPathId = readString(payload, "studyPathId", 80);
  const topic = readString(payload, "topic", 120);
  const difficulty = readString(payload, "difficulty", 40);
  const question = readString(payload, "question", 800);
  const selectedAnswer = readString(payload, "selectedAnswer", 400);
  const correctAnswer = readString(payload, "correctAnswer", 400);
  const staticExplanation = readString(payload, "staticExplanation", 1000);

  if (!studyPathId || !topic || !difficulty || !question || !selectedAnswer || !correctAnswer || !staticExplanation) {
    return validationError("INVALID_EXPLAIN_ANSWER_PAYLOAD", "Explain answer payload has missing or invalid fields.");
  }

  return {
    ok: true,
    payload: {
      studyPathId,
      topic,
      difficulty,
      question,
      selectedAnswer,
      correctAnswer,
      staticExplanation,
    },
  };
}

function validateQuizSummaryPayload(
  payload: Record<string, unknown>,
): { ok: true; payload: QuizSummaryPayload } | { ok: false; error: string; message: string } {
  const studyPathId = readString(payload, "studyPathId", 80);
  const score = readInteger(payload.score);
  const totalQuestions = readInteger(payload.totalQuestions);
  const incorrectTopics = readStringArray(payload.incorrectTopics, 5, 120);
  const weakTopics = readStringArray(payload.weakTopics, 5, 120);

  if (!studyPathId || score === null || totalQuestions === null || !incorrectTopics || !weakTopics) {
    return validationError("INVALID_QUIZ_SUMMARY_PAYLOAD", "Quiz summary payload has missing or invalid fields.");
  }

  if (totalQuestions < 1 || score < 0 || score > totalQuestions) {
    return validationError("INVALID_QUIZ_SCORE", "Score must be between 0 and totalQuestions.");
  }

  return {
    ok: true,
    payload: {
      studyPathId,
      score,
      totalQuestions,
      incorrectTopics,
      weakTopics,
    },
  };
}

function validateStudyPlanPayload(
  payload: Record<string, unknown>,
): { ok: true; payload: StudyPlanPayload } | { ok: false; error: string; message: string } {
  const studyPathId = readString(payload, "studyPathId", 80);
  const goal = readString(payload, "goal", 300);
  const requestedDays = readInteger(payload.days);
  const level = readString(payload, "level", 40);

  if (!studyPathId || !goal || requestedDays === null || !level) {
    return validationError("INVALID_STUDY_PLAN_PAYLOAD", "Study plan payload has missing or invalid fields.");
  }

  if (requestedDays < 1) {
    return validationError("INVALID_STUDY_PLAN_DAYS", "Study plan days must be at least 1.");
  }

  if (!["beginner", "intermediate", "advanced"].includes(level)) {
    return validationError("INVALID_STUDY_PLAN_LEVEL", "Level must be beginner, intermediate, or advanced.");
  }

  return {
    ok: true,
    payload: {
      studyPathId,
      goal,
      days: Math.min(requestedDays, 7),
      level: level as StudyPlanPayload["level"],
    },
  };
}

function extractOutputText(providerJson: unknown): string | null {
  if (!isObject(providerJson)) {
    return null;
  }

  if (typeof providerJson.output_text === "string") {
    return providerJson.output_text;
  }

  const output = providerJson.output;
  if (!Array.isArray(output)) {
    return null;
  }

  const chunks: string[] = [];
  for (const item of output) {
    if (!isObject(item) || !Array.isArray(item.content)) {
      continue;
    }
    for (const content of item.content) {
      if (isObject(content) && typeof content.text === "string") {
        chunks.push(content.text);
      }
    }
  }

  return chunks.length > 0 ? chunks.join("").trim() : null;
}

function readString(payload: Record<string, unknown>, key: string, maxLength: number): string | null {
  const value = payload[key];
  if (typeof value !== "string") {
    return null;
  }
  const trimmed = value.trim();
  if (trimmed.length === 0 || trimmed.length > maxLength) {
    return null;
  }
  return trimmed;
}

function readStringArray(value: unknown, maxItems: number, maxItemLength: number): string[] | null {
  if (!Array.isArray(value) || value.length > maxItems) {
    return null;
  }

  const cleaned = value.map((item) => {
    if (typeof item !== "string") {
      return null;
    }
    const trimmed = item.trim();
    return trimmed.length > 0 && trimmed.length <= maxItemLength ? trimmed : null;
  });

  if (cleaned.some((item) => item === null)) {
    return null;
  }

  return [...new Set(cleaned as string[])].slice(0, maxItems);
}

function readInteger(value: unknown): number | null {
  return typeof value === "number" && Number.isInteger(value) ? value : null;
}

function isAiCoachAction(value: unknown): value is AiCoachAction {
  return typeof value === "string" && allowedActions.includes(value as AiCoachAction);
}

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
}

function getMaxInputChars(): number {
  const configured = Number(Deno.env.get("AI_PROXY_MAX_INPUT_CHARS"));
  return Number.isInteger(configured) && configured > 0 ? configured : defaultMaxInputChars;
}

function getClientKey(request: Request, action: AiCoachAction): string {
  const forwardedFor = request.headers.get("x-forwarded-for")?.split(",")[0]?.trim();
  const clientId = request.headers.get("x-learnlift-client-id")?.trim();
  return `${action}:${clientId || forwardedFor || "anonymous"}`;
}

function consumeRateLimit(key: string, action: AiCoachAction): boolean {
  const now = Date.now();
  const existing = rateLimitStore.get(key);

  if (!existing || existing.resetAt <= now) {
    rateLimitStore.set(key, { count: 1, resetAt: now + rateLimitWindowMs });
    return true;
  }

  if (existing.count >= rateLimitByAction[action]) {
    return false;
  }

  existing.count += 1;
  return true;
}

function debugLog(metadata: {
  action: AiCoachAction;
  inputSize: number;
  success: boolean | null;
  timestamp: string;
}) {
  if (Deno.env.get("AI_PROXY_ENABLE_DEBUG_LOGS") !== "true") {
    return;
  }
  console.log(JSON.stringify(metadata));
}

function jsonResponse(body: AiCoachSuccessResponse, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json",
    },
  });
}

function jsonError(error: string, message: string, status: number): Response {
  const body: AiCoachErrorResponse = { error, message };
  return new Response(JSON.stringify(body), {
    status,
    headers: {
      ...corsHeaders,
      "Content-Type": "application/json",
    },
  });
}

function validationError(error: string, message: string) {
  return { ok: false as const, error, message };
}

class AiResponseParseError extends Error {}

class MissingConfigurationError extends Error {}
