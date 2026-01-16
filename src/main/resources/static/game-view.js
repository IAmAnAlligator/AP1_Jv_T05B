const API_URL = "http://localhost:8080/games";

// Получаем параметры из URL
const params = new URLSearchParams(window.location.search);
const gameId = params.get("gameId");
const vsComputer = params.get("vsComputer")?.toLowerCase() === "true";

if (!gameId) {
  alert("Game ID not provided");
  window.location.href = "game.html";
}

let currentGame = null;
let myMark = 1; // X по умолчанию

// Переход назад
function back() {
  window.location.href = "game.html";
}

// Загрузка состояния игры
async function loadGame() {
  try {
    currentGame = await authorizedFetch(`${API_URL}/${gameId}`);
    renderGame(currentGame);
  } catch (e) {
    alert(JSON.stringify(e.body, null, 2));
  }
}

// Можно ли ходить
function canPlayerMove(game, myMark) {
  if (vsComputer) {
    return game.status === "PLAYER_TURN";
  } else {
    return (
      (game.status === "PLAYER_X_TURN" && myMark === 1) ||
      (game.status === "PLAYER_O_TURN" && myMark === -1)
    );
  }
}

// Отрисовка поля
function renderGame(game) {
  document.getElementById("gameInfo").textContent =
    `Game: ${game.id} | Status: ${game.status}`;

  const boardEl = document.getElementById("board");
  boardEl.innerHTML = "";

  if (game.status === "WAITING") {
    document.getElementById("status").textContent =
      "Waiting for second player...";
    return;
  }

  if (!vsComputer) {
    if (game.status === "PLAYER_X_TURN") myMark = 1;
    else if (game.status === "PLAYER_O_TURN") myMark = -1;
  }

  const canMove = canPlayerMove(game, myMark);

  for (let r = 0; r < 3; r++) {
    for (let c = 0; c < 3; c++) {
      const cell = document.createElement("div");
      cell.className = "cell";

      const value = game.field[r][c];
      if (value === 1) {
        cell.textContent = "X";
        cell.classList.add("disabled");
      } else if (value === -1) {
        cell.textContent = "O";
        cell.classList.add("disabled");
      } else if (canMove) {
        cell.onclick = () => makeMove(r, c);
      } else {
        cell.classList.add("disabled");
      }

      boardEl.appendChild(cell);
    }
  }

  document.getElementById("status").textContent =
    `Status: ${game.status}`;
}

// Ход игрока
async function makeMove(row, col) {
  if (!currentGame || currentGame.field[row][col] !== 0) return;

  const newField = currentGame.field.map(r => [...r]);
  newField[row][col] = myMark;

  try {
    currentGame = await authorizedFetch(`${API_URL}/${gameId}`, {
      method: "PUT",
      body: JSON.stringify({ updatedField: newField })
    });
    renderGame(currentGame);
  } catch (e) {
    alert(JSON.stringify(e.body, null, 2));
  }
}

// Автообновление PvP
let gameInterval = null;

if (!vsComputer) {
  gameInterval = setInterval(() => {
    if (currentGame) loadGame();
  }, 1000);
}

window.addEventListener("beforeunload", () => {
  if (gameInterval) clearInterval(gameInterval);
});

// Старт
loadGame();
