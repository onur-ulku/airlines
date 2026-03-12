import React, { useEffect, useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../context/AuthContext";
import type { Location, Route } from "../types";

export default function RoutesPage() {
  const { credentials } = useAuth();
  const [locations, setLocations] = useState<Location[]>([]);
  const [originId, setOriginId] = useState<number>(0);
  const [destinationId, setDestinationId] = useState<number>(0);
  const [date, setDate] = useState("");
  const [routes, setRoutes] = useState<Route[]>([]);
  const [selected, setSelected] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api.getLocations(credentials).then(setLocations).catch((e) => setError(e instanceof Error ? e.message : "Failed"));
  }, []);

  const search = async () => {
    if (!originId || !destinationId || !date) {
      setError("Origin, destination and date required");
      return;
    }
    setError(null);
    setLoading(true);
    setSelected(null);
    try {
      setRoutes(await api.getRoutes(credentials, originId, destinationId, date));
    } catch (e) {
      setError(e instanceof Error ? e.message : "Failed");
    } finally {
      setLoading(false);
    }
  };

  const locName = (id: number) => locations.find((l) => l.id === id)?.name ?? id;
  const selectedRoute = selected != null ? routes[selected] : null;

  return (
    <div className="page routes-page">
      <h1>Routes</h1>
      {error && <div className="error-text">{error}</div>}
      <div className="routes-filters">
        <label>Origin
          <select value={originId} onChange={(e) => setOriginId(Number(e.target.value))}>
            <option value={0}>Select</option>
            {locations.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
          </select>
        </label>
        <label>Destination
          <select value={destinationId} onChange={(e) => setDestinationId(Number(e.target.value))}>
            <option value={0}>Select</option>
            {locations.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
          </select>
        </label>
        <label>Date
          <input
            type="date"
            value={date}
            min={new Date().toISOString().slice(0, 10)}
            onChange={(e) => setDate(e.target.value)}
          />
        </label>
        <button className="btn btn-primary" onClick={search}>Search</button>
      </div>
      {loading && <div>Loading...</div>}
      <div className="routes-layout">
        <div className="routes-list">
          <h2>Available Routes</h2>
          {!loading && routes.length === 0 && <div>No routes. Try different parameters.</div>}
          <ul>
            {routes.map((r, i) => (
              <li key={i} className={i === selected ? "route-item selected" : "route-item"} onClick={() => setSelected(i)}>
                {r.segments.map((s) => `${s.type} (${locName(s.originId)} → ${locName(s.destinationId)})`).join(" ➜ ")}
              </li>
            ))}
          </ul>
        </div>
        <div className="routes-details">
          <h2>Route Details</h2>
          {selectedRoute ? (
            <ol>
              {selectedRoute.segments.map((s, i) => (
                <li key={i}><strong>{s.type}</strong> — {locName(s.originId)} → {locName(s.destinationId)}</li>
              ))}
            </ol>
          ) : (
            <div>Select a route.</div>
          )}
        </div>
      </div>
    </div>
  );
}
