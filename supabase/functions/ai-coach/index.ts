type AiCoachAction = "explain_answer" | "quiz_summary" | "study_plan";

type AiCoachRequest =
  | {
      action: "explain_answer";
      payload: ExplainAnswerPayload;
    }
  | {
      action: "quiz_summary";
      payload: QuizSummaryPayload;
    }
  | {
      action: "study_plan";
      payload: StudyPlanPayload;
    };

interface ExplainAnswerPayload {
  studyPathId: string;
  topic: string;
  difficulty: string;
  question: string;
  selectedAnswer: string;
  correctAnswer: string;
  staticExplanation: string;
}

interface QuizSummaryPayload {
  studyPathId: string;
  score: number;
  totalQuestions: number;
  incorrectTopics: string[];
  weakTopics: string[];
}

interface StudyPlanPayload {
  studyPathId: string;
  goal: string;
  days: number;
  level: "beginner" | "intermediate" | "advanced";
}

interface ExplainAnswerResult {
  title: string;
  conciseExplanation: string;
  whyCorrectAnswerWorks: string;
  studyTip: string;
  recommendedTopic: string;
}

interface QuizSummaryResult {
  summary: string;
  recommendedFocus: string[];
  nextSessionSuggestion: string;
  encouragement: string;
}

interface StudyPlanResult {
  title: string;
  days: StudyPlanDay[];
}

interface StudyPlanDay {
  day: number;
  focus: string;
  tasks: string[];
}

type AiCoachResult = ExplainAnswerResult | QuizSummaryResult | StudyPlanResult;

interface AiCoachSuccessResponse {
  action: AiCoachAction;
  result: AiCoachResult;
}

interface AiCoachErrorResponse {
  error: string;
  message: string;
}

interface PromptConfig {
  system: string;
  user: string;
  responseFormat: unknown;
  maxOutputTokens: number;
}

const textField = { type: "string", minLength: 1, maxLength: 600 };
const shortTextField = { type: "string", minLength: 1, maxLength: 220 };

const responseFormats: Record<AiCoachAction, unknown> = {
  explain_answer: {
    type: "json_schema",
    name: "explain_answer_response",
    strict: true,
    schema: {
      type: "object",
      additionalProperties: false,
      required: [
        "title",
        "conciseExplanation",
        "whyCorrectAnswerWorks",
        "studyTip",
        "recommendedTopic",
      ],
      properties: {
        title: shortTextField,
        conciseExplanation: textField,
        whyCorrectAnswerWorks: textField,
        studyTip: textField,
        recommendedTopic: shortTextField,
      },
    },
  },
  quiz_summary: {
    type: "json_schema",
    name: "quiz_summary_response",
    strict: true,
    schema: {
      type: "object",
      additionalProperties: false,
      required: [
        "summary",
        "recommendedFocus",
        "nextSessionSuggestion",
        "encouragement",
      ],
      properties: {
        summary: textField,
        recommendedFocus: {
          type: "array",
          minItems: 1,
          maxItems: 3,
          items: shortTextField,
        },
        nextSessionSuggestion: textField,
        encouragement: textField,
      },
    },
  },
  study_plan: {
    type: "json_schema",
    name: "study_plan_response",
    strict: true,
    schema: {
      type: "object",
      additionalProperties: false,
      required: ["title", "days"],
      properties: {
        title: shortTextField,
        days: {
          type: "array",
          minItems: 1,
          maxItems: 7,
          items: {
            type: "object",
            additionalProperties: false,
            required: ["day", "focus", "tasks"],
            properties: {
              day: { type: "integer", minimum: 1, maximum: 7 },
              focus: shortTextField,
              tasks: {
                type: "array",
                minItems: 2,
                maxItems: 3,
                items: shortTextField,
              },
            },
          },
        },
      },
    },
  },
};

const globalPromptRules = [
  "Use only the supplied LearnLift AI study context.",
  "Keep the answer concise, practical, friendly, and beginner/intermediate friendly.",
  "Do not invent facts beyond the provided context.",
  "Do not promise guaranteed job, interview, exam, certification, or career success.",
  "Avoid medical, legal, financial, therapy, safety-critical, or other high-stakes advice.",
  "Return one valid JSON object only.",
  "Do not include markdown, code fences, prose, commentary, labels, or explanations outside the JSON object.",
  "Do not wrap the JSON in ```json or any other fence.",
  "Use double quotes for all JSON keys and string values.",
  "Do not include trailing comments.",
  "Keep every field concise.",
  "Output must match the exact schema.",
].join("\n");

function buildPrompt(action: AiCoachAction, payload: unknown): PromptConfig {
  switch (action) {
    case "explain_answer":
      return buildExplainAnswerPrompt(payload as ExplainAnswerPayload);
    case "quiz_summary":
      return buildQuizSummaryPrompt(payload as QuizSummaryPayload);
    case "study_plan":
      return buildStudyPlanPrompt(payload as StudyPlanPayload);
  }
}

function buildExplainAnswerPrompt(payload: ExplainAnswerPayload): PromptConfig {
  return {
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalPromptRules}`,
    user: [
      `Study path ID: ${payload.studyPathId}`,
      `Topic: ${payload.topic}`,
      `Difficulty: ${payload.difficulty}`,
      "",
      "Question:",
      payload.question,
      "",
      "Learner selected:",
      payload.selectedAnswer,
      "",
      "Correct answer:",
      payload.correctAnswer,
      "",
      "Static explanation from the app:",
      payload.staticExplanation,
      "",
      "Explain why the correct answer works and why the selected answer may be wrong if applicable.",
      "Include one practical study tip and one recommended topic.",
      "Required JSON keys: title, conciseExplanation, whyCorrectAnswerWorks, studyTip, recommendedTopic.",
      "Return ONLY valid minified JSON matching those keys.",
    ].join("\n"),
    responseFormat: responseFormats.explain_answer,
    maxOutputTokens: 800,
  };
}

function buildQuizSummaryPrompt(payload: QuizSummaryPayload): PromptConfig {
  return {
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalPromptRules}`,
    user: [
      `Study path ID: ${payload.studyPathId}`,
      `Score: ${payload.score} out of ${payload.totalQuestions}`,
      `Incorrect topics: ${payload.incorrectTopics.join(", ") || "none provided"}`,
      `Weak topics: ${payload.weakTopics.join(", ") || "none provided"}`,
      "",
      "Write a concise quiz review.",
      "Recommend up to three focus topics.",
      "Suggest one realistic next study session.",
      "Be encouraging but realistic.",
      "Required JSON keys: summary, recommendedFocus, nextSessionSuggestion, encouragement.",
      "recommendedFocus must be an array of one to three short strings.",
      "Return ONLY valid minified JSON matching those keys.",
    ].join("\n"),
    responseFormat: responseFormats.quiz_summary,
    maxOutputTokens: 800,
  };
}

function buildStudyPlanPrompt(payload: StudyPlanPayload): PromptConfig {
  return {
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalPromptRules}`,
    user: [
      `Study path ID: ${payload.studyPathId}`,
      `Goal: ${payload.goal}`,
      `Days: ${payload.days}`,
      `Level: ${payload.level}`,
      "",
      `Create exactly ${payload.days} days of study tasks.`,
      "Each day must have one focus and two to three short, actionable tasks.",
      "Keep the plan realistic and grounded in the study path and goal.",
      "Required JSON keys: title, days. Each day requires day, focus, tasks.",
      "Return ONLY valid minified JSON matching those keys.",
    ].join("\n"),
    responseFormat: responseFormats.study_plan,
    maxOutputTokens: 1400,
  };
}

function isExplainAnswerResult(value: unknown): value is ExplainAnswerResult {
  const result = value as ExplainAnswerResult;
  return isObject(value) &&
    isNonEmptyString(result.title) &&
    isNonEmptyString(result.conciseExplanation) &&
    isNonEmptyString(result.whyCorrectAnswerWorks) &&
    isNonEmptyString(result.studyTip) &&
    isNonEmptyString(result.recommendedTopic);
}

function isQuizSummaryResult(value: unknown): value is QuizSummaryResult {
  const result = value as QuizSummaryResult;
  return isObject(value) &&
    isNonEmptyString(result.summary) &&
    isStringArray(result.recommendedFocus, 1, 3) &&
    isNonEmptyString(result.nextSessionSuggestion) &&
    isNonEmptyString(result.encouragement);
}

function isStudyPlanResult(value: unknown, expectedDays: number): value is StudyPlanResult {
  const result = value as StudyPlanResult;
  return isObject(value) &&
    isNonEmptyString(result.title) &&
    Array.isArray(result.days) &&
    result.days.length === expectedDays &&
    result.days.every((day, index) =>
      isObject(day) &&
      day.day === index + 1 &&
      isNonEmptyString(day.focus) &&
      isStringArray(day.tasks, 2, 3)
    );
}

function isNonEmptyString(value: unknown): value is string {
  return typeof value === "string" && value.trim().length > 0;
}

function isStringArray(value: unknown, minItems: number, maxItems: number): value is string[] {
  return Array.isArray(value) &&
    value.length >= minItems &&
    value.length <= maxItems &&
    value.every(isNonEmptyString);
}

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
    if (error instanceof AiResponseParseError) {
      debugLog({
        action: validation.request.action,
        inputSize: JSON.stringify(validation.request.payload).length,
        success: false,
        timestamp: new Date().toISOString(),
        error: "AI_RESPONSE_PARSE_ERROR",
        outputPreview: error.outputPreview,
      });
      return jsonError("AI_RESPONSE_PARSE_ERROR", "AI Coach returned an invalid response.", 502);
    }

    if (error instanceof MissingConfigurationError) {
      debugLog({
        action: validation.request.action,
        inputSize: JSON.stringify(validation.request.payload).length,
        success: false,
        timestamp: new Date().toISOString(),
        error: "AI_PROXY_CONFIGURATION_ERROR",
      });
      return jsonError("AI_PROXY_CONFIGURATION_ERROR", "AI Coach is temporarily unavailable.", 500);
    }

    if (error instanceof AiProviderError) {
      debugLog({
        action: validation.request.action,
        inputSize: JSON.stringify(validation.request.payload).length,
        success: false,
        timestamp: new Date().toISOString(),
        error: error.publicCode,
      });
      return jsonError(error.publicCode, "AI Coach is temporarily unavailable.", error.status);
    }

    debugLog({
      action: validation.request.action,
      inputSize: JSON.stringify(validation.request.payload).length,
      success: false,
      timestamp: new Date().toISOString(),
      error: "AI_PROVIDER_ERROR",
    });
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
    throw await AiProviderError.fromResponse(providerResponse);
  }

  const providerJson = await providerResponse.json();
  const outputText = extractOutputText(providerJson);
  if (!outputText) {
    throw new AiResponseParseError();
  }

  const parsed = parseAiJson(outputText);
  if (!parsed.ok) {
    debugParse(request.action, model, outputText, "parse_error");
    throw new AiResponseParseError(sanitizePreview(outputText));
  }

  const normalized = normalizeAiResult(request, parsed.value);
  if (normalized) {
    debugParse(request.action, model, outputText, normalized.usedDefaults ? "fallback_defaults" : parsed.path);
    return normalized.result;
  }

  debugParse(request.action, model, outputText, "parse_error");
  throw new AiResponseParseError(sanitizePreview(outputText));
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

type ParsePath = "direct_json" | "stripped_code_fence" | "extracted_object";

function parseAiJson(text: string): { ok: true; value: unknown; path: ParsePath } | { ok: false } {
  const candidates: Array<{ text: string | null; path: ParsePath }> = [
    { text: text.trim(), path: "direct_json" },
    { text: stripMarkdownCodeFence(text).trim(), path: "stripped_code_fence" },
    { text: extractFirstJsonObject(text), path: "extracted_object" },
  ];
  const seen = new Set<string>();

  for (const candidate of candidates) {
    const candidateText = candidate.text?.trim();
    if (!candidateText || seen.has(candidateText)) {
      continue;
    }
    seen.add(candidateText);
    try {
      return { ok: true, value: JSON.parse(candidateText), path: candidate.path };
    } catch {
      // Try the next candidate below.
    }
  }

  return { ok: false };
}

function stripMarkdownCodeFence(text: string): string {
  const trimmed = text.trim();
  return trimmed
    .replace(/^```(?:json)?\s*/i, "")
    .replace(/\s*```\s*$/i, "")
    .trim();
}

function extractFirstJsonObject(text: string): string | null {
  const start = text.indexOf("{");
  if (start === -1) {
    return null;
  }

  let depth = 0;
  let inString = false;
  let escaped = false;

  for (let index = start; index < text.length; index += 1) {
    const char = text[index];

    if (inString) {
      if (escaped) {
        escaped = false;
      } else if (char === "\\") {
        escaped = true;
      } else if (char === "\"") {
        inString = false;
      }
      continue;
    }

    if (char === "\"") {
      inString = true;
      continue;
    }

    if (char === "{") {
      depth += 1;
    } else if (char === "}") {
      depth -= 1;
      if (depth === 0) {
        return text.slice(start, index + 1);
      }
    }
  }

  return null;
}

function normalizeAiResult(request: AiCoachRequest, parsed: unknown): { result: AiCoachResult; usedDefaults: boolean } | null {
  const candidate = unwrapResultObject(parsed);

  if (request.action === "explain_answer") {
    const normalized = normalizeExplainAnswerResult(candidate, request.payload);
    return normalized && isExplainAnswerResult(normalized.result) ? normalized : null;
  }

  if (request.action === "quiz_summary") {
    const normalized = normalizeQuizSummaryResult(candidate, request.payload);
    return normalized && isQuizSummaryResult(normalized.result) ? normalized : null;
  }

  const normalized = normalizeStudyPlanResult(candidate, request.payload);
  return normalized && isStudyPlanResult(normalized.result, request.payload.days) ? normalized : null;
}

function unwrapResultObject(value: unknown): unknown {
  if (!isObject(value)) {
    return value;
  }

  if (isObject(value.result)) {
    return value.result;
  }

  return value;
}

function normalizeExplainAnswerResult(value: unknown, payload: ExplainAnswerPayload) {
  if (!isObject(value)) {
    return null;
  }

  const title = readOutputString(value.title);
  const conciseExplanation = readOutputString(value.conciseExplanation);
  const whyCorrectAnswerWorks = readOutputString(value.whyCorrectAnswerWorks);
  const studyTip = readOutputString(value.studyTip);
  const recommendedTopic = readOutputString(value.recommendedTopic);

  return {
    result: {
      title: title ?? "AI Coach Explanation",
      conciseExplanation: conciseExplanation ??
        payload.staticExplanation ??
        "Review the local explanation and focus on the core concept.",
      whyCorrectAnswerWorks: whyCorrectAnswerWorks ??
        "The correct answer matches the key concept in this question.",
      studyTip: studyTip ?? "Review this topic and try one similar question.",
      recommendedTopic: recommendedTopic ?? payload.topic ?? "Review topic",
    },
    usedDefaults: !title || !conciseExplanation || !whyCorrectAnswerWorks || !studyTip || !recommendedTopic,
  };
}

function normalizeQuizSummaryResult(value: unknown, payload: QuizSummaryPayload) {
  if (!isObject(value)) {
    return null;
  }

  const summary = readOutputString(value.summary);
  const nextSessionSuggestion = readOutputString(value.nextSessionSuggestion);
  const encouragement = readOutputString(value.encouragement);
  const recommendedFocus = readOutputStringArray(value.recommendedFocus, 3);
  const fallbackFocus = payload.weakTopics.length > 0 ? payload.weakTopics.slice(0, 3) : ["Review missed topics"];
  return {
    result: {
      summary: summary ??
        `You scored ${payload.score} out of ${payload.totalQuestions}. Review the topics that felt least certain.`,
      recommendedFocus: recommendedFocus.length > 0 ? recommendedFocus : fallbackFocus,
      nextSessionSuggestion: nextSessionSuggestion ??
        "Review the recommended focus topics, then try one short quiz.",
      encouragement: encouragement ??
        "Good progress. A focused review round can make the next session clearer.",
    },
    usedDefaults: !summary || recommendedFocus.length === 0 || !nextSessionSuggestion || !encouragement,
  };
}

function normalizeStudyPlanResult(value: unknown, payload: StudyPlanPayload) {
  if (!isObject(value)) {
    return null;
  }

  const title = readOutputString(value.title) ?? `${payload.days}-Day Study Plan`;
  const sourceDays = Array.isArray(value.days) ? value.days : [];

  const days = sourceDays.map((item) => {
    if (!isObject(item)) {
      return null;
    }

    const day = typeof item.day === "number" && Number.isInteger(item.day) ? item.day : null;
    const focus = readOutputString(item.focus);
    const tasks = readOutputStringArray(item.tasks, 3);
    if (day === null || !focus || tasks.length < 2) {
      return null;
    }

    return {
      day,
      focus,
      tasks: tasks.slice(0, 3),
    };
  });

  const validDays = days.filter((day): day is { day: number; focus: string; tasks: string[] } => day !== null);
  const finalDays = validDays.length === payload.days ? validDays : buildFallbackStudyPlan(payload);

  return {
    result: {
      title,
      days: finalDays,
    },
    usedDefaults: !readOutputString(value.title) || validDays.length !== payload.days,
  };
}

function buildFallbackStudyPlan(payload: StudyPlanPayload) {
  return Array.from({ length: payload.days }, (_, index) => ({
    day: index + 1,
    focus: index === 0 ? "Review the basics" : `Practice session ${index + 1}`,
    tasks: [
      `Review flashcards for ${payload.studyPathId}`,
      "Take one short quiz or practice set",
      "Note one topic to revisit tomorrow",
    ],
  }));
}

function readOutputString(value: unknown): string | null {
  if (typeof value !== "string") {
    return null;
  }
  const trimmed = value.trim();
  return trimmed.length > 0 ? trimmed : null;
}

function readOutputStringArray(value: unknown, maxItems: number): string[] {
  if (typeof value === "string") {
    return value.split(",").map((item) => item.trim()).filter(Boolean).slice(0, maxItems);
  }

  if (!Array.isArray(value)) {
    return [];
  }

  return value
    .map(readOutputString)
    .filter((item): item is string => item !== null)
    .slice(0, maxItems);
}

function sanitizePreview(text: string): string {
  return text
    .replace(/sk-[A-Za-z0-9_-]+/g, "sk-REDACTED")
    .replace(/\s+/g, " ")
    .trim()
    .slice(0, 300);
}

function debugParse(action: AiCoachAction, model: string, rawOutput: string, parsePath: string) {
  debugLog({
    action,
    inputSize: 0,
    success: parsePath !== "parse_error",
    timestamp: new Date().toISOString(),
    model,
    parsePath,
    outputPreview: sanitizePreview(rawOutput),
  });
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
  model?: string;
  error?: string;
  parsePath?: string;
  outputPreview?: string;
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

class AiResponseParseError extends Error {
  constructor(readonly outputPreview?: string) {
    super("AI_RESPONSE_PARSE_ERROR");
  }
}

class MissingConfigurationError extends Error {}

class AiProviderError extends Error {
  constructor(
    readonly publicCode: string,
    readonly status: number,
  ) {
    super(publicCode);
  }

  static async fromResponse(response: Response): Promise<AiProviderError> {
    const providerCode = await readProviderErrorCode(response);
    const publicCode = normalizeProviderErrorCode(providerCode, response.status);
    const status = response.status === 429 ? 429 : 502;
    return new AiProviderError(publicCode, status);
  }
}

async function readProviderErrorCode(response: Response): Promise<string | null> {
  try {
    const body = await response.json();
    if (isObject(body.error)) {
      const code = body.error.code;
      const type = body.error.type;
      if (typeof code === "string" && code.trim().length > 0) {
        return code.trim();
      }
      if (typeof type === "string" && type.trim().length > 0) {
        return type.trim();
      }
    }
  } catch {
    // Ignore provider body parsing failures and return a stable public error below.
  }
  return null;
}

function normalizeProviderErrorCode(providerCode: string | null, status: number): string {
  const normalized = providerCode?.toLowerCase();
  if (normalized === "insufficient_quota") {
    return "OPENAI_INSUFFICIENT_QUOTA";
  }
  if (normalized === "invalid_api_key") {
    return "OPENAI_INVALID_API_KEY";
  }
  if (normalized === "model_not_found") {
    return "OPENAI_MODEL_NOT_FOUND";
  }
  if (normalized === "rate_limit_exceeded" || status === 429) {
    return "OPENAI_RATE_LIMIT_EXCEEDED";
  }
  return "AI_PROVIDER_ERROR";
}
