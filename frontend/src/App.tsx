import React from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout/Layout";
import LoginPage from "./pages/LoginPage";
import LocationsPage from "./pages/LocationsPage";
import TransportationsPage from "./pages/TransportationsPage";
import RoutesPage from "./pages/RoutesPage";
import { useAuth } from "./context/AuthContext";

const AppRoutes = () => {
  const { isAuthenticated, role } = useAuth();
  const isAdmin = role === "ADMIN";

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  return (
    <Layout>
      <Routes>
        {isAdmin && <Route path="/locations" element={<LocationsPage />} />}
        {isAdmin && <Route path="/transportations" element={<TransportationsPage />} />}
        <Route path="/routes" element={<RoutesPage />} />
        <Route path="/" element={<Navigate to="/routes" replace />} />
        <Route path="*" element={<Navigate to="/routes" replace />} />
      </Routes>
    </Layout>
  );
};

export default function App() {
  return <AppRoutes />;
}
