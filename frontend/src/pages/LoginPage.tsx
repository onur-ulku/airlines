import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await login(username, password);
      navigate("/routes");
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Login</h1>
        <form onSubmit={handleSubmit} className="form">
          <label>Username <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} /></label>
          <label>Password <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} /></label>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary" type="submit" disabled={loading}>{loading ? "Logging in..." : "Login"}</button>
          <div className="hint">admin / admin123 veya agency / agency123</div>
        </form>
      </div>
    </div>
  );
}
