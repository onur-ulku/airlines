import React from "react";
import { useAuth } from "../../context/AuthContext";

export default function Header() {
  const { isAuthenticated, role, logout } = useAuth();
  return (
    <header className="header">
      <div className="header-title">Airlines Route Planner</div>
      <div className="header-actions">
        {isAuthenticated && (
          <>
            <span className="header-role">Role: {role}</span>
            <button className="btn btn-secondary" onClick={logout}>Logout</button>
          </>
        )}
      </div>
    </header>
  );
}
