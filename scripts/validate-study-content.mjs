import { readFile, readdir } from "node:fs/promises";
import { join } from "node:path";

const contentDir = join(process.cwd(), "app", "src", "main", "assets", "study_content");
const requiredFlashcardFields = ["id", "pathId", "question", "answer", "topic", "difficulty"];
const requiredQuizFields = [
  "id",
  "pathId",
  "question",
  "options",
  "correctAnswerId",
  "explanation",
  "topic",
  "difficulty",
];

const minimums = {
  "english-vocabulary-speaking": { flashcards: 80, quizQuestions: 60 },
  "job-interview-prep": { flashcards: 80, quizQuestions: 60 },
  "it-qa-interview-prep": { flashcards: 60, quizQuestions: 50 },
};

const files = (await readdir(contentDir)).filter((file) => file.endsWith(".json"));
const errors = [];
const results = [];

for (const file of files) {
  const fullPath = join(contentDir, file);
  const content = JSON.parse(await readFile(fullPath, "utf8"));
  const expectedMinimum = minimums[content.pathId];
  const allIds = new Set();

  if (!expectedMinimum) {
    errors.push(`${file}: unexpected pathId "${content.pathId}"`);
    continue;
  }

  if (!Array.isArray(content.flashcards)) {
    errors.push(`${file}: flashcards must be an array`);
    continue;
  }

  if (!Array.isArray(content.quizQuestions)) {
    errors.push(`${file}: quizQuestions must be an array`);
    continue;
  }

  if (content.flashcards.length < expectedMinimum.flashcards) {
    errors.push(`${file}: expected at least ${expectedMinimum.flashcards} flashcards, found ${content.flashcards.length}`);
  }

  if (content.quizQuestions.length < expectedMinimum.quizQuestions) {
    errors.push(`${file}: expected at least ${expectedMinimum.quizQuestions} quiz questions, found ${content.quizQuestions.length}`);
  }

  for (const item of [...content.flashcards, ...content.quizQuestions]) {
    if (allIds.has(item.id)) {
      errors.push(`${file}: duplicate id "${item.id}"`);
    }
    allIds.add(item.id);

    if (item.pathId !== content.pathId) {
      errors.push(`${file}: ${item.id} has pathId "${item.pathId}", expected "${content.pathId}"`);
    }
  }

  for (const flashcard of content.flashcards) {
    for (const field of requiredFlashcardFields) {
      if (!flashcard[field]) {
        errors.push(`${file}: flashcard ${flashcard.id ?? "(missing id)"} missing ${field}`);
      }
    }
  }

  for (const question of content.quizQuestions) {
    for (const field of requiredQuizFields) {
      if (!question[field]) {
        errors.push(`${file}: quiz question ${question.id ?? "(missing id)"} missing ${field}`);
      }
    }

    if (!Array.isArray(question.options) || question.options.length < 2) {
      errors.push(`${file}: quiz question ${question.id} must have at least two options`);
      continue;
    }

    const optionIds = question.options.map((option) => option.id);
    if (!optionIds.includes(question.correctAnswerId)) {
      errors.push(`${file}: quiz question ${question.id} correctAnswerId "${question.correctAnswerId}" is not in options`);
    }

    if (new Set(optionIds).size !== optionIds.length) {
      errors.push(`${file}: quiz question ${question.id} has duplicate option ids`);
    }

    for (const option of question.options) {
      if (!option.id || !option.text) {
        errors.push(`${file}: quiz question ${question.id} has option missing id or text`);
      }
    }
  }

  results.push({
    file,
    pathId: content.pathId,
    flashcards: content.flashcards.length,
    quizQuestions: content.quizQuestions.length,
  });
}

console.table(results);

if (errors.length > 0) {
  console.error(errors.join("\n"));
  process.exitCode = 1;
}
