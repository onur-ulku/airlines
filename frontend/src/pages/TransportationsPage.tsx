import React, { useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";
import type { Location, Transportation, TransportationType } from "../types";

const types: TransportationType[] = ["FLIGHT", "BUS", "SUBWAY", "UBER"];
const days = [1, 2, 3, 4, 5, 6, 7];

export default function TransportationsPage() {
  const { credentials } = useAuth();
  const [locations, setLocations] = useState<Location[]>([]);
  const [transportations, setTransportations] = useState<Transportation[]>([]);
  const [editing, setEditing] = useState<Transportation | null>(null);
  const [form, setForm] = useState({ originId: 0, destinationId: 0, type: "FLIGHT" as TransportationType, operatingDays: [] as number[] });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const [locs, trans] = await Promise.all([api.getLocations(credentials), api.getTransportations(credentials)]);
      setLocations(locs);
      setTransportations(trans);
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const toggleDay = (d: number) => setForm((p) => ({ ...p, operatingDays: p.operatingDays.includes(d) ? p.operatingDays.filter((x) => x !== d) : [...p.operatingDays, d] }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.originId || !form.destinationId) {
      setError("Origin and destination required");
      return;
    }
    setError(null);
    const payload: Transportation = {
      origin: { id: form.originId, name: "", country: "", city: "", code: "" },
      destination: { id: form.destinationId, name: "", country: "", city: "", code: "" },
      type: form.type,
      operatingDays: form.operatingDays
    };
    try {
      if (editing?.id) {
        const u = await api.updateTransportation(credentials, editing.id, payload);
        setTransportations((p) => p.map((t) => (t.id === u.id ? u : t)));
      } else {
        const c = await api.createTransportation(credentials, payload);
        setTransportations((p) => [...p, c]);
      }
      setEditing(null);
      setForm({ originId: 0, destinationId: 0, type: "FLIGHT", operatingDays: [] });
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed");
    }
  };

  const startEdit = (t: Transportation) => {
    setEditing(t);
    setForm({
      originId: t.origin?.id ?? 0,
      destinationId: t.destination?.id ?? 0,
      type: t.type,
      operatingDays: Array.isArray(t.operatingDays) ? [...t.operatingDays] : []
    });
  };

  const handleDelete = async (id?: number) => {
    if (!id || !window.confirm("Delete?")) return;
    try {
      await api.deleteTransportation(credentials, id);
      setTransportations((p) => p.filter((t) => t.id !== id));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed");
    }
  };

  const locName = (id?: number) => locations.find((l) => l.id === id)?.name ?? id;

  return (
    <div className="page">
      <h1>Transportations</h1>
      {error && <div className="error-text">{error}</div>}
      {loading && <div>Loading...</div>}
      <div className="grid-two">
        <div>
          <h2>List</h2>
          <table className="table">
            <thead>
              <tr><th>ID</th><th>Origin</th><th>Destination</th><th>Type</th><th>Days</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {transportations.map((t) => (
                <tr key={t.id}>
                  <td>{t.id}</td><td>{locName(t.origin?.id)}</td><td>{locName(t.destination?.id)}</td><td>{t.type}</td>
                  <td>{(t.operatingDays ?? []).join(", ")}</td>
                  <td>
                    <button className="btn btn-secondary btn-sm" onClick={() => startEdit(t)}>Edit</button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(t.id)}>Delete</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div>
          <h2>{editing ? "Edit" : "Create"}</h2>
          <form className="form" onSubmit={handleSubmit}>
            <label>Origin
              <select value={form.originId} onChange={(e) => setForm((p) => ({ ...p, originId: Number(e.target.value) }))}>
                <option value={0}>Select</option>
                {locations.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
              </select>
            </label>
            <label>Destination
              <select value={form.destinationId} onChange={(e) => setForm((p) => ({ ...p, destinationId: Number(e.target.value) }))}>
                <option value={0}>Select</option>
                {locations.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
              </select>
            </label>
            <label>Type
              <select value={form.type} onChange={(e) => setForm((p) => ({ ...p, type: e.target.value as TransportationType }))}>
                {types.map((t) => <option key={t} value={t}>{t}</option>)}
              </select>
            </label>
            <div className="days-selector">Operating Days
              <div className="days-chips">
                {days.map((d) => (
                  <button key={d} type="button" className={form.operatingDays.includes(d) ? "chip chip-selected" : "chip"} onClick={() => toggleDay(d)}>
                    {["Mon","Tue","Wed","Thu","Fri","Sat","Sun"][d - 1]}
                  </button>
                ))}
              </div>
            </div>
            <div className="form-actions">
              <button className="btn btn-primary" type="submit">{editing ? "Update" : "Create"}</button>
              {editing && <button type="button" className="btn btn-secondary" onClick={() => { setEditing(null); setForm({ originId: 0, destinationId: 0, type: "FLIGHT", operatingDays: [] }); }}>Cancel</button>}
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
