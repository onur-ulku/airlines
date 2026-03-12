import React from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

export default function Sidebar() {
  const { role } = useAuth();
  const isAdmin = role === "ADMIN";
  return (
    <aside className="sidebar">
      <nav>
        <ul>
          {isAdmin && (
            <>
              <li><NavLink to="/locations" className={({ isActive }) => (isActive ? "sidebar-link active" : "sidebar-link")}>Locations</NavLink></li>
              <li><NavLink to="/transportations" className={({ isActive }) => (isActive ? "sidebar-link active" : "sidebar-link")}>Transportations</NavLink></li>
            </>
          )}
          <li><NavLink to="/routes" className={({ isActive }) => (isActive ? "sidebar-link active" : "sidebar-link")}>Routes</NavLink></li>
        </ul>
      </nav>
    </aside>
  );
}
