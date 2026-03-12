import React, { useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";
import type { Location } from "../types";

const empty: Location = { name: "", country: "", city: "", code: "" };

export default function LocationsPage() {
  const { credentials } = useAuth();
  const [locations, setLocations] = useState<Location[]>([]);
  const [editing, setEditing] = useState<Location | null>(null);
  const [form, setForm] = useState<Location>(empty);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      setLocations(await api.getLocations(credentials));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to load");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  useEffect(() => {
    if (!error) return;
    const t = setTimeout(() => setError(null), 2500);
    return () => clearTimeout(t);
  }, [error]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      if (editing?.id) {
        const u = await api.updateLocation(credentials, editing.id, form);
        setLocations((p) => p.map((l) => (l.id === u.id ? u : l)));
      } else {
        const c = await api.createLocation(credentials, form);
        setLocations((p) => [...p, c]);
      }
      setEditing(null);
      setForm(empty);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to save");
    }
  };

  const startEdit = (l: Location) => {
    setError(null);
    setEditing(l);
    setForm({ name: l.name, country: l.country, city: l.city, code: l.code });
  };

  const handleDelete = async (id?: number) => {
    if (!id || !window.confirm("Delete?")) return;
    setError(null);
    try {
      await api.deleteLocation(credentials, id);
      setLocations((p) => p.filter((l) => l.id !== id));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed to delete");
    }
  };

  return (
    <div className="page">
      <h1>Locations</h1>
      {error && <div className="error-text">{error}</div>}
      {loading && <div>Loading...</div>}
      <div className="grid-two">
        <div>
          <h2>List</h2>
          <table className="table">
            <thead>
              <tr><th>ID</th><th>Name</th><th>Country</th><th>City</th><th>Code</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {locations.map((l) => (
                <tr key={l.id}>
                  <td>{l.id}</td><td>{l.name}</td><td>{l.country}</td><td>{l.city}</td><td>{l.code}</td>
                  <td>
                    <button className="btn btn-secondary btn-sm" onClick={() => startEdit(l)}>Edit</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(l.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div>
          <h2>{editing ? "Edit" : "Create"}</h2>
          <form className="form" onSubmit={handleSubmit}>
            <label>Name <input value={form.name} onChange={(e) => setForm((p) => ({ ...p, name: e.target.value }))} /></label>
            <label>Country <input value={form.country} onChange={(e) => setForm((p) => ({ ...p, country: e.target.value }))} /></label>
            <label>City <input value={form.city} onChange={(e) => setForm((p) => ({ ...p, city: e.target.value }))} /></label>
            <label>Code <input value={form.code} onChange={(e) => setForm((p) => ({ ...p, code: e.target.value }))} /></label>
            <div className="form-actions">
              <button className="btn btn-primary" type="submit">{editing ? "Update" : "Create"}</button>
              {editing && <button type="button" className="btn btn-secondary" onClick={() => { setEditing(null); setForm(empty); }}>Cancel</button>}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
