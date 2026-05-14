import type {
  AiCoachAction,
  ExplainAnswerResult,
  QuizSummaryResult,
  StudyPlanResult,
} from "./types.ts";

const textField = { type: "string", minLength: 1, maxLength: 600 };
const shortTextField = { type: "string", minLength: 1, maxLength: 220 };

export const responseFormats: Record<AiCoachAction, unknown> = {
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

export function isExplainAnswerResult(value: unknown): value is ExplainAnswerResult {
  const result = value as ExplainAnswerResult;
  return isObject(value) &&
    isNonEmptyString(result.title) &&
    isNonEmptyString(result.conciseExplanation) &&
    isNonEmptyString(result.whyCorrectAnswerWorks) &&
    isNonEmptyString(result.studyTip) &&
    isNonEmptyString(result.recommendedTopic);
}

export function isQuizSummaryResult(value: unknown): value is QuizSummaryResult {
  const result = value as QuizSummaryResult;
  return isObject(value) &&
    isNonEmptyString(result.summary) &&
    isStringArray(result.recommendedFocus, 1, 3) &&
    isNonEmptyString(result.nextSessionSuggestion) &&
    isNonEmptyString(result.encouragement);
}

export function isStudyPlanResult(value: unknown, expectedDays: number): value is StudyPlanResult {
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

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === "object" && value !== null && !Array.isArray(value);
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
