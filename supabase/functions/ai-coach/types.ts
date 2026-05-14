export type AiCoachAction = "explain_answer" | "quiz_summary" | "study_plan";

export type AiCoachRequest =
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

export interface ExplainAnswerPayload {
  studyPathId: string;
  topic: string;
  difficulty: string;
  question: string;
  selectedAnswer: string;
  correctAnswer: string;
  staticExplanation: string;
}

export interface QuizSummaryPayload {
  studyPathId: string;
  score: number;
  totalQuestions: number;
  incorrectTopics: string[];
  weakTopics: string[];
}

export interface StudyPlanPayload {
  studyPathId: string;
  goal: string;
  days: number;
  level: "beginner" | "intermediate" | "advanced";
}

export interface ExplainAnswerResult {
  title: string;
  conciseExplanation: string;
  whyCorrectAnswerWorks: string;
  studyTip: string;
  recommendedTopic: string;
}

export interface QuizSummaryResult {
  summary: string;
  recommendedFocus: string[];
  nextSessionSuggestion: string;
  encouragement: string;
}

export interface StudyPlanResult {
  title: string;
  days: StudyPlanDay[];
}

export interface StudyPlanDay {
  day: number;
  focus: string;
  tasks: string[];
}

export type AiCoachResult = ExplainAnswerResult | QuizSummaryResult | StudyPlanResult;

export interface AiCoachSuccessResponse {
  action: AiCoachAction;
  result: AiCoachResult;
}

export interface AiCoachErrorResponse {
  error: string;
  message: string;
}
