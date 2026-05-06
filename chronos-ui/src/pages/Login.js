import React, { useState } from "react";
import axios from "axios";

export default function Login({ setAuth, goToRegister }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const login = async () => {
        try {
            const res = await axios.post(
                `http://localhost:3092/signin?username=${username}&password=${password}`
            );

            localStorage.setItem("token", res.data);
            setAuth(true);
        } catch (err) {
            console.error(err);
            alert("Login failed");
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h2>Login</h2>

            <input
                placeholder="Username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <br /><br />

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <br /><br />

            <button onClick={login}>Login</button>

            <br /><br />

            <p>
                Don't have an account?{" "}
                <button onClick={goToRegister}>Register</button>
            </p>
        </div>
    );
}