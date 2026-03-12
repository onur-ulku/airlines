import React, { createContext, useContext, useState } from "react";
import type { Credentials } from "../api/client";
import type { UserRole } from "../types";
import { api } from "../api/client";

interface AuthState {
  isAuthenticated: boolean;
  role: UserRole | null;
  credentials: Credentials | null;
}

interface AuthContextValue extends AuthState {
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [state, setState] = useState<AuthState>({
    isAuthenticated: false,
    role: null,
    credentials: null
  });

  const login = async (username: string, password: string) => {
    const creds: Credentials = { username, password };
    const role = await api.loginAndDetectRole(creds);
    setState({ isAuthenticated: true, role, credentials: creds });
  };

  const logout = () => setState({ isAuthenticated: false, role: null, credentials: null });

  return (
    <AuthContext.Provider value={{ ...state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
};
