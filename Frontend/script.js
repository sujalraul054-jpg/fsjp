/* ==========================================================
   Serial Position Effect — full app script
   --------------------------------------------------------
   - Fetches a fresh random 10-word list from the backend every
     time a test starts (GET /api/words/random).
   - Presents words one at a time at a user-configurable
     interval (Settings), with no pause/skip controls.
   - Submits recall to the backend, renders results including
     the semantic-associations and semantic-relations sections.
   - Lets the user browse past attempts and clear all history.
   - Light/dark theme, persisted in localStorage.
   ========================================================== */

const BACKEND_BASE = "http://localhost:8080/api";
const RECALL_URL = `${BACKEND_BASE}/recall`;
const AGGREGATE_URL = `${BACKEND_BASE}/recall/aggregate`;
const HISTORY_URL = `${BACKEND_BASE}/recall/history`;

const THEME_KEY = "spe-theme";
const INTERVAL_KEY = "spe-interval-seconds";

let currentWords = [];
let chartInstance = null;

// ---------- DOM references ----------
const appBarEl = document.getElementById("app-bar");

const screens = {
  landing: document.getElementById("screen-landing"),
  test: document.getElementById("screen-test"),
  recall: document.getElementById("screen-recall"),
  results: document.getElementById("screen-results"),
  graph: document.getElementById("screen-graph"),
  history: document.getElementById("screen-history"),
};

const wordDisplayEl = document.getElementById("word-display");
const progressDisplayEl = document.getElementById("progress-display");
const landingErrorEl = document.getElementById("landing-error");

const recallInputEl = document.getElementById("recall-input");
const recallErrorEl = document.getElementById("recall-error");
const relatedInputEl = document.getElementById("related-input");

const statGridEl = document.getElementById("stat-grid");
const resultsTableBodyEl = document.getElementById("results-table-body");
const semanticSectionEl = document.getElementById("semantic-section");
const semanticListEl = document.getElementById("semantic-list");
const relationsSectionEl = document.getElementById("relations-section");
const relationsListEl = document.getElementById("relations-list");
const inputNotesEl = document.getElementById("input-notes");

const graphAttemptsNoteEl = document.getElementById("graph-attempts-note");
const graphErrorEl = document.getElementById("graph-error");

const historyListEl = document.getElementById("history-list");
const historyErrorEl = document.getElementById("history-error");
const historyDetailEl = document.getElementById("history-detail");
const historyDetailStatsEl = document.getElementById("history-detail-stats");
const historyDetailTableBodyEl = document.getElementById("history-detail-table-body");

const settingsOverlayEl = document.getElementById("settings-overlay");
const themePickerEl = document.getElementById("theme-picker");
const intervalSliderEl = document.getElementById("interval-slider");
const intervalValueEl = document.getElementById("interval-value");
const clearStatusEl = document.getElementById("settings-clear-status");

// ---------- Screen switching ----------
function showScreen(name) {
  Object.values(screens).forEach((el) => el.classList.remove("is-active"));
  screens[name].classList.add("is-active");
  // Hide the app bar (and its Settings button) during word presentation,
  // matching the spec's "no pause/skip controls during presentation" rule.
  appBarEl.classList.toggle("is-hidden", name === "test");
}

// ---------- Settings: theme ----------
function getStoredThemePreference() {
  return localStorage.getItem(THEME_KEY) || "system";
}

function resolveTheme(preference) {
  if (preference === "system") {
    return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  }
  return preference;
}

function applyTheme() {
  const preference = getStoredThemePreference();
  document.documentElement.setAttribute("data-theme", resolveTheme(preference));
  document.querySelectorAll(".segmented__option").forEach((btn) => {
    btn.classList.toggle("is-active", btn.dataset.themeChoice === preference);
  });
}

window.matchMedia("(prefers-color-scheme: dark)").addEventListener("change", () => {
  if (getStoredThemePreference() === "system") {
    applyTheme();
  }
});

themePickerEl.addEventListener("click", (event) => {
  const btn = event.target.closest(".segmented__option");
  if (!btn) return;
  localStorage.setItem(THEME_KEY, btn.dataset.themeChoice);
  applyTheme();
});

// ---------- Settings: word display interval ----------
function getStoredIntervalSeconds() {
  const stored = parseFloat(localStorage.getItem(INTERVAL_KEY));
  return Number.isFinite(stored) ? stored : 1;
}

function applyIntervalDisplay() {
  const seconds = getStoredIntervalSeconds();
  intervalSliderEl.value = String(seconds);
  intervalValueEl.textContent = `${seconds.toFixed(1)}s`;
}

intervalSliderEl.addEventListener("input", () => {
  const seconds = parseFloat(intervalSliderEl.value);
  intervalValueEl.textContent = `${seconds.toFixed(1)}s`;
  localStorage.setItem(INTERVAL_KEY, String(seconds));
});

// ---------- Settings modal open/close ----------
document.getElementById("btn-settings").addEventListener("click", () => {
  applyTheme();
  applyIntervalDisplay();
  clearStatusEl.hidden = true;
  settingsOverlayEl.hidden = false;
});
document.getElementById("btn-settings-close").addEventListener("click", () => {
  settingsOverlayEl.hidden = true;
});
settingsOverlayEl.addEventListener("click", (event) => {
  if (event.target === settingsOverlayEl) {
    settingsOverlayEl.hidden = true;
  }
});

// ---------- Clear all history (shared by Settings + History screen) ----------
async function clearAllHistory(statusEl) {
  const confirmed = window.confirm("This permanently deletes every stored attempt. Continue?");
  if (!confirmed) return;

  try {
    const response = await fetch(HISTORY_URL, { method: "DELETE" });
    if (!response.ok && response.status !== 204) {
      throw new Error(`Server responded with status ${response.status}.`);
    }
    if (statusEl) {
      statusEl.textContent = "All attempt history cleared.";
      statusEl.hidden = false;
    }
    if (screens.history.classList.contains("is-active")) {
      loadAndShowHistory();
    }
  } catch (err) {
    const message = err instanceof TypeError
      ? "Couldn't reach the backend. Make sure it's running at http://localhost:8080."
      : `Something went wrong: ${err.message}`;
    if (statusEl) {
      statusEl.textContent = message;
      statusEl.hidden = false;
    }
  }
}

document.getElementById("btn-clear-history-settings").addEventListener("click", () => {
  clearAllHistory(clearStatusEl);
});
document.getElementById("btn-clear-history-page").addEventListener("click", () => {
  clearAllHistory(historyErrorEl);
});

// ---------- Word fetching + presentation ----------
async function fetchRandomWords(count) {
  const response = await fetch(`${BACKEND_BASE}/words/random?count=${count}`);
  if (!response.ok) {
    throw new Error(`Server responded with status ${response.status}.`);
  }
  return response.json();
}

async function handleStartTest() {
  landingErrorEl.hidden = true;
  const startBtn = document.getElementById("btn-start");
  startBtn.disabled = true;
  startBtn.textContent = "Loading words...";

  try {
    currentWords = await fetchRandomWords(10);
    beginWordPresentation(currentWords);
  } catch (err) {
    landingErrorEl.textContent = err instanceof TypeError
      ? "Couldn't reach the backend. Make sure it's running at http://localhost:8080."
      : `Something went wrong: ${err.message}`;
    landingErrorEl.hidden = false;
  } finally {
    startBtn.disabled = false;
    startBtn.textContent = "Start Test";
  }
}

function beginWordPresentation(words) {
  showScreen("test");
  const intervalMs = getStoredIntervalSeconds() * 1000;

  let index = 0;
  renderWord(words, index);

  const timer = setInterval(() => {
    index += 1;
    if (index >= words.length) {
      clearInterval(timer);
      setTimeout(() => showScreen("recall"), 300);
      return;
    }
    renderWord(words, index);
  }, intervalMs);
}

function renderWord(words, index) {
  wordDisplayEl.textContent = words[index];

  // Restart the CSS animation on every word — remove the class, force a
  // reflow, then re-add it, or the browser just no-ops on an unchanged class.
  wordDisplayEl.classList.remove("is-animating");
  void wordDisplayEl.offsetWidth;
  wordDisplayEl.classList.add("is-animating");

  const percentDone = ((index + 1) / words.length) * 100;
  progressDisplayEl.innerHTML = `
    <span class="stimulus-progress-label">Word ${index + 1} of ${words.length}</span>
    <span class="stimulus-progress-track">
      <span class="stimulus-progress-fill" style="width: ${percentDone}%"></span>
    </span>
  `;
}

// ---------- Recall parsing ----------
function parseRecallInput(raw) {
  return raw
    .split(/[\s,]+/)
    .map((w) => w.trim())
    .filter((w) => w.length > 0);
}

// ---------- Backend response mapping ----------
function mapBackendResult(result) {
  return {
    totalWords: result.totalWords,
    totalRecalled: result.totalRecalled,
    overallPct: result.recallPercentage,
    primacyPct: result.primacyScore,
    middlePct: result.middleScore,
    recencyPct: result.recencyScore,
    positionResults: result.positionResults,
    semanticAssociations: result.semanticAssociations,
    duplicateWords: result.duplicateWords,
    unknownWords: result.unknownWords,
    associationReference: result.associationReference,
  };
}

// ---------- Recall screen error helper ----------
function showRecallError(message) {
  recallErrorEl.textContent = message;
  recallErrorEl.hidden = false;
}
function hideRecallError() {
  recallErrorEl.hidden = true;
}

// ---------- Results rendering ----------
function renderResults(scored) {
  const notes = [];
  if (scored.duplicateWords && scored.duplicateWords.length > 0) {
    notes.push(`You entered "${scored.duplicateWords.join('", "')}" more than once — duplicates were only counted once.`);
  }
  if (scored.unknownWords && scored.unknownWords.length > 0) {
    notes.push(`"${scored.unknownWords.join('", "')}" wasn't on the original list.`);
  }
  if (notes.length > 0) {
    inputNotesEl.innerHTML = notes.map((n) => `<p>${n}</p>`).join("");
    inputNotesEl.hidden = false;
  } else {
    inputNotesEl.hidden = true;
  }

  statGridEl.innerHTML = "";
  const stats = [
    [`${scored.totalRecalled} / ${scored.totalWords}`, "Words recalled"],
    [`${scored.overallPct}%`, "Overall recall"],
    [`${scored.primacyPct}%`, "Primacy recall"],
    [`${scored.middlePct}%`, "Middle recall"],
    [`${scored.recencyPct}%`, "Recency recall"],
  ];
  stats.forEach(([value, label]) => {
    const el = document.createElement("div");
    el.className = "stat";
    el.innerHTML = `<span class="stat-value">${value}</span><span class="stat-label">${label}</span>`;
    statGridEl.appendChild(el);
  });

  resultsTableBodyEl.innerHTML = "";
  scored.positionResults.forEach((r) => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${r.position}</td>
      <td>${r.word}</td>
      <td class="${r.recalled ? "hit" : "miss"}">${r.recalled ? "✓ Yes" : "✗ No"}</td>
    `;
    resultsTableBodyEl.appendChild(row);
  });

  const associations = scored.semanticAssociations || [];
  if (associations.length === 0) {
    semanticSectionEl.hidden = true;
  } else {
    semanticListEl.innerHTML = "";
    associations.forEach((assoc) => {
      const li = document.createElement("li");
      li.textContent = `${assoc.originalWord} → ${assoc.associatedWord}`;
      semanticListEl.appendChild(li);
    });
    semanticSectionEl.hidden = false;
  }

  const references = scored.associationReference || [];
  if (references.length === 0) {
    relationsSectionEl.hidden = true;
  } else {
    relationsListEl.innerHTML = "";
    references.forEach((ref) => {
      const li = document.createElement("li");
      li.textContent = `${ref.word} — commonly associated with: ${ref.knownAssociations.join(", ")}`;
      relationsListEl.appendChild(li);
    });
    relationsSectionEl.hidden = false;
  }
}

// ---------- Graph screen error helper ----------
function showGraphError(message) {
  graphErrorEl.textContent = message;
  graphErrorEl.hidden = false;
}
function hideGraphError() {
  graphErrorEl.hidden = true;
}

// ---------- Aggregate graph ----------
async function loadAndShowGraph() {
  hideGraphError();
  graphAttemptsNoteEl.textContent = "Loading...";

  try {
    const response = await fetch(AGGREGATE_URL);
    if (!response.ok) {
      throw new Error(`Server responded with status ${response.status}.`);
    }

    const stats = await response.json();

    if (!stats.positions || stats.positions.length === 0) {
      graphAttemptsNoteEl.textContent = "No attempts stored yet — submit a recall first.";
    } else {
      graphAttemptsNoteEl.textContent =
        `Aggregated recall percentage by position, across ${stats.totalAttempts} ` +
        `attempt${stats.totalAttempts === 1 ? "" : "s"} stored so far.`;
      renderChart(stats.positions);
    }

    showScreen("graph");
  } catch (err) {
    if (err instanceof TypeError) {
      showGraphError("Couldn't reach the backend. Make sure it's running at http://localhost:8080.");
    } else {
      showGraphError(`Something went wrong: ${err.message}`);
    }
    showScreen("graph");
  }
}

function renderChart(positions) {
  const ctx = document.getElementById("spe-chart").getContext("2d");

  if (chartInstance) {
    chartInstance.destroy();
  }

  chartInstance = new Chart(ctx, {
    type: "line",
    data: {
      labels: positions.map((p) => p.position),
      datasets: [{
        label: "Recall %",
        data: positions.map((p) => p.recallPercentage),
        borderColor: "#2F6F63",
        backgroundColor: "rgba(47, 111, 99, 0.15)",
        borderWidth: 2,
        pointBackgroundColor: "#C98A3E",
        pointRadius: 5,
        pointHoverRadius: 7,
        tension: 0.3,
        fill: true,
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        title: {
          display: true,
          text: "Serial Position Effect — Recall by Position",
        },
        tooltip: {
          callbacks: {
            label: (context) => `${context.parsed.y}% recalled`,
          },
        },
      },
      scales: {
        x: {
          title: { display: true, text: "Position in Word List" },
        },
        y: {
          title: { display: true, text: "Recall Percentage" },
          min: 0,
          max: 100,
        },
      },
    },
  });
}

// ---------- History ----------
async function loadAndShowHistory() {
  historyErrorEl.hidden = true;
  historyDetailEl.hidden = true;
  historyListEl.innerHTML = '<li class="history-empty">Loading...</li>';

  try {
    const response = await fetch(HISTORY_URL);
    if (!response.ok) {
      throw new Error(`Server responded with status ${response.status}.`);
    }
    const attempts = await response.json();

    if (attempts.length === 0) {
      historyListEl.innerHTML = '<li class="history-empty">No attempts stored yet.</li>';
    } else {
      historyListEl.innerHTML = "";
      attempts.forEach((attempt) => {
        const li = document.createElement("li");
        li.className = "history-row";
        const date = new Date(attempt.timestamp).toLocaleString();
        li.innerHTML = `
          <span class="history-row__date">${date}</span>
          <span class="history-row__stat">${attempt.totalRecalled}/${attempt.totalWords} recalled (${attempt.recallPercentage}%)</span>
        `;
        li.addEventListener("click", () => showHistoryDetail(attempt.id, li));
        historyListEl.appendChild(li);
      });
    }
    showScreen("history");
  } catch (err) {
    historyErrorEl.textContent = err instanceof TypeError
      ? "Couldn't reach the backend. Make sure it's running at http://localhost:8080."
      : `Something went wrong: ${err.message}`;
    historyErrorEl.hidden = false;
    historyListEl.innerHTML = "";
    showScreen("history");
  }
}

async function showHistoryDetail(id, rowEl) {
  document.querySelectorAll(".history-row").forEach((el) => el.classList.remove("is-selected"));
  rowEl.classList.add("is-selected");

  try {
    const response = await fetch(`${HISTORY_URL}/${id}`);
    if (!response.ok) {
      throw new Error(`Server responded with status ${response.status}.`);
    }
    const detail = await response.json();

    historyDetailStatsEl.innerHTML = "";
    const stats = [
      [`${detail.totalRecalled} / ${detail.totalWords}`, "Words recalled"],
      [`${detail.recallPercentage}%`, "Overall recall"],
    ];
    stats.forEach(([value, label]) => {
      const el = document.createElement("div");
      el.className = "stat";
      el.innerHTML = `<span class="stat-value">${value}</span><span class="stat-label">${label}</span>`;
      historyDetailStatsEl.appendChild(el);
    });

    historyDetailTableBodyEl.innerHTML = "";
    detail.positionResults.forEach((r) => {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td>${r.position}</td>
        <td>${r.word}</td>
        <td class="${r.recalled ? "hit" : "miss"}">${r.recalled ? "✓ Yes" : "✗ No"}</td>
      `;
      historyDetailTableBodyEl.appendChild(row);
    });

    historyDetailEl.hidden = false;
  } catch (err) {
    historyErrorEl.textContent = `Couldn't load that attempt: ${err.message}`;
    historyErrorEl.hidden = false;
  }
}

// ---------- Event wiring ----------
document.getElementById("btn-start").addEventListener("click", handleStartTest);
document.getElementById("btn-view-history-landing").addEventListener("click", loadAndShowHistory);
document.getElementById("btn-view-history").addEventListener("click", loadAndShowHistory);
document.getElementById("btn-history-back").addEventListener("click", () => showScreen("landing"));

const submitRecallBtn = document.getElementById("btn-submit-recall");

submitRecallBtn.addEventListener("click", async () => {
  const raw = recallInputEl.value;
  const recalledWords = parseRecallInput(raw);

  if (recalledWords.length === 0) {
    showRecallError("Please enter at least one word before submitting.");
    return;
  }
  hideRecallError();

  const relatedWords = parseRecallInput(relatedInputEl.value);

  submitRecallBtn.disabled = true;
  submitRecallBtn.textContent = "Scoring...";

  try {
    const response = await fetch(RECALL_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        originalWords: currentWords,
        recalledWords: recalledWords,
        relatedWords: relatedWords,
      }),
    });

    if (!response.ok) {
      const errorBody = await response.json().catch(() => null);
      const message = errorBody && errorBody.message
        ? errorBody.message
        : `Server responded with status ${response.status}.`;
      throw new Error(message);
    }

    const result = await response.json();
    renderResults(mapBackendResult(result));
    showScreen("results");
  } catch (err) {
    if (err instanceof TypeError) {
      showRecallError("Couldn't reach the backend. Make sure it's running at http://localhost:8080.");
    } else {
      showRecallError(`Something went wrong: ${err.message}`);
    }
  } finally {
    submitRecallBtn.disabled = false;
    submitRecallBtn.textContent = "Submit Recall";
  }
});

document.getElementById("btn-restart").addEventListener("click", () => {
  recallInputEl.value = "";
  relatedInputEl.value = "";
  showScreen("landing");
});

document.getElementById("btn-view-graph").addEventListener("click", loadAndShowGraph);

document.getElementById("btn-graph-back").addEventListener("click", () => {
  showScreen("results");
});

// ---------- Init ----------
applyTheme();
applyIntervalDisplay();
