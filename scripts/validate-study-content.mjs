import { readFile, readdir } from "node:fs/promises";
import { join } from "node:path";

const contentDir = join(process.cwd(), "app", "src", "main", "assets", "study_content");
const allowedDifficulties = new Set(["easy", "medium", "hard"]);
const requiredRootFields = ["pathId", "flashcards", "quizQuestions"];
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
  "english-vocabulary-speaking": { flashcards: 80, quizQuestions: 60, isPremium: false, isComingSoon: false },
  "job-interview-prep": { flashcards: 80, quizQuestions: 60, isPremium: false, isComingSoon: false },
  "it-qa-interview-prep": { flashcards: 60, quizQuestions: 50, isPremium: false, isComingSoon: false },
  "sql-interview-prep": { flashcards: 30, quizQuestions: 25, isPremium: true, isComingSoon: false, freePreviewCount: 5 },
  "qa-advanced": { flashcards: 30, quizQuestions: 25, isPremium: true, isComingSoon: false, freePreviewCount: 5 },
  "automation-testing-basics": { flashcards: 30, quizQuestions: 25, isPremium: true, isComingSoon: false, freePreviewCount: 5 },
  "python-basics": { flashcards: 0, quizQuestions: 0, isPremium: true, isComingSoon: true },
  "javascript-basics": { flashcards: 0, quizQuestions: 0, isPremium: true, isComingSoon: true },
  "business-english": { flashcards: 0, quizQuestions: 0, isPremium: true, isComingSoon: true },
  "technical-interview-prep": { flashcards: 0, quizQuestions: 0, isPremium: true, isComingSoon: true },
};

const files = (await readdir(contentDir)).filter((file) => file.endsWith(".json")).sort();
const errors = [];
const warnings = [];
const results = [];
const seenPathIds = new Set();
const globalIds = new Map();
const globalFlashcardQuestions = new Map();
const globalQuizQuestions = new Map();

const normalizeText = (value) =>
  String(value ?? "")
    .toLowerCase()
    .replace(/[^\p{L}\p{N}]+/gu, " ")
    .trim()
    .replace(/\s+/g, " ");

const isBlank = (value) => typeof value !== "string" || value.trim().length === 0;

function addDuplicate(map, key, location, label) {
  const existing = map.get(key);
  if (existing) {
    errors.push(`${location}: duplicate ${label}; first seen at ${existing}`);
  } else {
    map.set(key, location);
  }
}

function countDifficulty(items) {
  return items.reduce(
    (counts, item) => {
      counts[item.difficulty] = (counts[item.difficulty] ?? 0) + 1;
      return counts;
    },
    { easy: 0, medium: 0, hard: 0 },
  );
}

function topTopics(items) {
  const counts = new Map();
  for (const item of items) counts.set(item.topic, (counts.get(item.topic) ?? 0) + 1);
  return [...counts.entries()].sort((a, b) => b[1] - a[1]).slice(0, 6);
}

for (const file of files) {
  const fullPath = join(contentDir, file);
  let content;

  try {
    content = JSON.parse(await readFile(fullPath, "utf8"));
  } catch (error) {
    errors.push(`${file}: invalid JSON (${error.message})`);
    continue;
  }

  for (const field of requiredRootFields) {
    if (content[field] === undefined || content[field] === null) {
      errors.push(`${file}: missing root field ${field}`);
    }
  }

  const expectedPathId = file.replace(/\.json$/, "");
  if (content.pathId !== expectedPathId) {
    errors.push(`${file}: pathId "${content.pathId}" must match file name "${expectedPathId}"`);
  }

  const expectedMinimum = minimums[content.pathId];
  const localIds = new Set();
  const localFlashcardQuestions = new Map();
  const localQuizQuestions = new Map();

  if (!expectedMinimum) {
    errors.push(`${file}: unexpected pathId "${content.pathId}"`);
    continue;
  }
  seenPathIds.add(content.pathId);

  if (expectedMinimum.isComingSoon) {
    errors.push(`${file}: coming soon path "${content.pathId}" should not have a content file yet`);
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
    const location = `${file}:${item.id ?? "(missing id)"}`;

    if (!isBlank(item.id)) {
      if (localIds.has(item.id)) errors.push(`${location}: duplicate id within pack`);
      localIds.add(item.id);
      addDuplicate(globalIds, item.id, location, `global id "${item.id}"`);
    }

    if (item.pathId !== content.pathId) {
      errors.push(`${location}: has pathId "${item.pathId}", expected "${content.pathId}"`);
    }

    if (!allowedDifficulties.has(item.difficulty)) {
      errors.push(`${location}: invalid difficulty "${item.difficulty}"`);
    }

    if (isBlank(item.topic)) {
      errors.push(`${location}: missing useful topic`);
    }
  }

  for (const flashcard of content.flashcards) {
    const location = `${file}:${flashcard.id ?? "(missing id)"}`;
    for (const field of requiredFlashcardFields) {
      if (isBlank(flashcard[field])) errors.push(`${location}: flashcard missing ${field}`);
    }

    const questionKey = normalizeText(flashcard.question);
    if (questionKey) {
      addDuplicate(localFlashcardQuestions, questionKey, location, "flashcard question text within pack");
      addDuplicate(globalFlashcardQuestions, questionKey, location, "flashcard question text across packs");
    }
  }

  for (const question of content.quizQuestions) {
    const location = `${file}:${question.id ?? "(missing id)"}`;
    for (const field of requiredQuizFields) {
      if (field === "options") continue;
      if (isBlank(question[field])) errors.push(`${location}: quiz question missing ${field}`);
    }

    const questionKey = normalizeText(question.question);
    if (questionKey) {
      addDuplicate(localQuizQuestions, questionKey, location, "quiz question text within pack");
      addDuplicate(globalQuizQuestions, questionKey, location, "quiz question text across packs");
    }

    if (!Array.isArray(question.options)) {
      errors.push(`${location}: options must be an array`);
      continue;
    }

    if (question.options.length !== 4) {
      errors.push(`${location}: quiz question must have exactly 4 options, found ${question.options.length}`);
    }

    const optionIds = question.options.map((option) => option.id);
    const correctMatches = optionIds.filter((id) => id === question.correctAnswerId).length;
    if (correctMatches !== 1) {
      errors.push(`${location}: correctAnswerId "${question.correctAnswerId}" must match exactly one option`);
    }

    if (new Set(optionIds).size !== optionIds.length) {
      errors.push(`${location}: duplicate option ids`);
    }

    for (const option of question.options) {
      if (isBlank(option.id) || isBlank(option.text)) {
        errors.push(`${location}: option missing id or text`);
      }
    }
  }

  const combinedTopics = new Set([...content.flashcards, ...content.quizQuestions].map((item) => item.topic).filter(Boolean));
  if (combinedTopics.size < 10) {
    warnings.push(`${file}: only ${combinedTopics.size} topics; target is at least 10 for complete packs`);
  }

  const flashcardDifficulty = countDifficulty(content.flashcards);
  const quizDifficulty = countDifficulty(content.quizQuestions);
  if (content.flashcards.length > 0 && flashcardDifficulty.hard === 0) {
    warnings.push(`${file}: flashcards have no hard items`);
  }
  if (content.quizQuestions.length > 0 && quizDifficulty.hard === 0) {
    warnings.push(`${file}: quiz questions have no hard items`);
  }

  results.push({
    file,
    pathId: content.pathId,
    isPremium: expectedMinimum.isPremium,
    isComingSoon: expectedMinimum.isComingSoon,
    freePreviewCount: expectedMinimum.freePreviewCount ?? 0,
    flashcards: content.flashcards.length,
    quizQuestions: content.quizQuestions.length,
    topics: combinedTopics.size,
    flashcardDifficulty,
    quizDifficulty,
    topTopics: topTopics([...content.flashcards, ...content.quizQuestions]).map(([topic, count]) => `${topic} (${count})`).join(", "),
  });
}

for (const [pathId, metadata] of Object.entries(minimums)) {
  if (!metadata.isComingSoon && !seenPathIds.has(pathId)) {
    errors.push(`${pathId}: expected local content file`);
  }
  if (metadata.isPremium && !metadata.isComingSoon && (!metadata.freePreviewCount || metadata.freePreviewCount < 1)) {
    errors.push(`${pathId}: premium pack must define a positive freePreviewCount`);
  }
  if (!metadata.isPremium && metadata.freePreviewCount) {
    errors.push(`${pathId}: free path should not define a premium preview count`);
  }
}

console.table(results);

if (warnings.length > 0) {
  console.warn(`\nWarnings:\n${warnings.join("\n")}`);
}

if (errors.length > 0) {
  console.error(`\nErrors:\n${errors.join("\n")}`);
  process.exitCode = 1;
}
