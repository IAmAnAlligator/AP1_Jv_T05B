// Базовый URL для эндпоинтов, связанных с играми
const API_URL = "http://localhost:8080/games";

// Универсальный вывод данных
function show(data) {
  document.getElementById("output").textContent =
    JSON.stringify(data, null, 2);
}

// Загрузка информации о текущем пользователе
async function aboutMe() {
  try {
    const data = await authorizedFetch("http://localhost:8080/auth/me");
    show(data);
  } catch (_) {}
}

// Загрузка списка завершённых игр пользователя
async function loadFinishedGames() {
  try {
    const data = await authorizedFetch(`${API_URL}/finished`);
    show(data);
  } catch (_) {}
}

// Загрузка таблицы лидеров
async function loadTopPlayers() {
  try {
    const data = await authorizedFetch(`${API_URL}/leaderboard`);
    show(data);
  } catch (_) {}
}

// Создание новой игры
async function createGame() {
  const vsComputer = document.getElementById("vsComputer").value === "true";

  try {
    const data = await authorizedFetch(API_URL, {
      method: "POST",
      body: JSON.stringify({ vsComputer })
    });

    if (data.id) {
      window.location.href =
        `game-view.html?gameId=${data.id}&vsComputer=${vsComputer}`;
    }
  } catch (_) {}
}

// Загрузка списка доступных игр (без авто-logout)
async function loadAvailableGames() {
  const res = await fetch(`${API_URL}/available`, {
    headers: authHeaders()
  });

  show(await res.json());
}

// Подключение к существующей игре
async function joinGame() {
  const gameId = document.getElementById("joinGameId").value.trim();

  if (!gameId) {
    alert("Enter game ID");
    return;
  }

  try {
    const data = await authorizedFetch(`${API_URL}/join`, {
      method: "POST",
      body: JSON.stringify({ gameId })
    });

    if (data.id) {
      window.location.href = `game-view.html?gameId=${data.id}`;
    } else {
      show(data);
    }
  } catch (_) {}
}
