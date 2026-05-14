import type {
  AiCoachAction,
  ExplainAnswerPayload,
  QuizSummaryPayload,
  StudyPlanPayload,
} from "./types.ts";
import { responseFormats } from "./responseSchemas.ts";

interface PromptConfig {
  system: string;
  user: string;
  responseFormat: unknown;
  maxOutputTokens: number;
}

const globalRules = [
  "Use only the supplied LearnLift AI study context.",
  "Keep the answer concise, practical, friendly, and beginner/intermediate friendly.",
  "Do not invent facts beyond the provided context.",
  "Do not promise guaranteed job, interview, exam, certification, or career success.",
  "Avoid medical, legal, financial, therapy, safety-critical, or other high-stakes advice.",
  "Return JSON only. Do not include markdown.",
].join("\n");

export function buildPrompt(action: AiCoachAction, payload: unknown): PromptConfig {
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
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalRules}`,
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
    ].join("\n"),
    responseFormat: responseFormats.explain_answer,
    maxOutputTokens: 500,
  };
}

function buildQuizSummaryPrompt(payload: QuizSummaryPayload): PromptConfig {
  return {
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalRules}`,
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
    ].join("\n"),
    responseFormat: responseFormats.quiz_summary,
    maxOutputTokens: 450,
  };
}

function buildStudyPlanPrompt(payload: StudyPlanPayload): PromptConfig {
  return {
    system: `You are LearnLift AI Coach, a concise educational assistant for a study app.\n${globalRules}`,
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
    ].join("\n"),
    responseFormat: responseFormats.study_plan,
    maxOutputTokens: 900,
  };
}
