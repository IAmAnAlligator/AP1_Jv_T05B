const AUTH_URL = "http://localhost:8080/auth";

// Проверка авторизации при загрузке страницы
(function ensureAuth() {
  const token = localStorage.getItem("accessToken");
  if (!token) {
    window.location.href = "index.html";
  }
})();

// Заголовки с актуальным accessToken
function authHeaders() {
  const token = localStorage.getItem("accessToken");

  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${token}`
  };
}

// Универсальный fetch с авторизацией
async function authorizedFetch(url, options = {}, retry = true) {
  const res = await fetch(url, {
    ...options,
    headers: {
      ...authHeaders(),
      ...(options.headers || {})
    }
  });

  let body = null;
  try {
    body = await res.json();
  } catch (_) {}

  if (res.status === 401 && retry) {

    // пробуем обновить оба токена
    if (await refreshTokens()) {
    console.log("access + refresh tokens refreshed, retrying request");
      return authorizedFetch(url, options, false);
    }

    // выходим
    logout(true);
    throw { status: 401, body };
  }

  if (!res.ok) {
    throw { status: res.status, body };
  }

  return body;
}


// Выход из системы
function logout(auto = false) {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");

  if (auto) {
    alert("Session expired. Please login again.");
  }

  window.location.href = "index.html";
}

async function refreshTokens() {
  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) return false;

  try {

    const res = await fetch(`${AUTH_URL}/token/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ refreshToken })
    });


    if (!res.ok) {
      console.log("Failed to refresh tokens");
      return false;
    }

    const data = await res.json();

    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("refreshToken", data.refreshToken);

    return true;
  } catch (err) {
    console.error("Error refreshing tokens:", err);
    return false;
  }
}


