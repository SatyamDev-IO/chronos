import React, { useState } from "react";
import axios from "axios";

export default function Register({ goToLogin }) {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const register = async () => {
        try {
            await axios.post("http://localhost:3092/register", {
                username,
                password,
                role: "USER",
            });

            alert("Registered successfully");
            goToLogin();
        } catch (err) {
            alert("Registration failed");
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h2>Register</h2>

            <input
                placeholder="Username"
                onChange={(e) => setUsername(e.target.value)}
            />
            <br /><br />

            <input
                type="password"
                placeholder="Password"
                onChange={(e) => setPassword(e.target.value)}
            />
            <br /><br />

            <button onClick={register}>Register</button>

            <p>
                Already have an account?{" "}
                <button onClick={goToLogin}>Login</button>
            </p>
        </div>
    );
}