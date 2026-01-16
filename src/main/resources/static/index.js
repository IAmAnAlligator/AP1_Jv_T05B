// Базовый URL для auth-эндпоинтов бэкенда
const API_URL = "http://localhost:8080/auth";

// Токены текущей сессии (в памяти страницы)
let accessToken = null;
let refreshToken = null;

// Вывод ответа сервера в <pre id="output">
function show(data) {
  document.getElementById("output").textContent =
    JSON.stringify(data, null, 2);
}

// Считывание логина и пароля из input-полей формы
function getCredentials() {
  return {
    login: document.getElementById("username").value,
    password: document.getElementById("password").value
  };
}

// Регистрация нового пользователя
async function register() {
  // Отправляем POST-запрос с логином и паролем
  const res = await fetch(`${API_URL}/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(getCredentials())
  });

  // Показываем ответ сервера (успех или ошибка)
  show(await res.json());
}

// Авторизация пользователя
async function login() {
  // Отправляем логин и пароль на сервер
  const res = await fetch(`${API_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(getCredentials())
  });

  // Парсим JSON-ответ
  const data = await res.json();

  // Если логин успешен и сервер вернул accessToken
  if (res.ok && data.accessToken) {
    // Сохраняем токены в переменные
    accessToken = data.accessToken;
    refreshToken = data.refreshToken;

    // Сохраняем токены в localStorage,
    // чтобы они были доступны после перезагрузки страницы
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    // Переходим на страницу игры
    window.location.href = "game.html";
  } else {
    // В случае ошибки показываем ответ сервера
    show(data);
  }
}
