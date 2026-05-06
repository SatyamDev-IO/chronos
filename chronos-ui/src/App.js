import React, { useState } from "react";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";

function App() {
  const [auth, setAuth] = useState(!!localStorage.getItem("token"));
  const [page, setPage] = useState("login");

  if (auth) return <Dashboard />;

  if (page === "register") {
    return <Register goToLogin={() => setPage("login")} />;
  }

  return (
    <Login
      setAuth={setAuth}
      goToRegister={() => setPage("register")}
    />
  );
}

export default App;