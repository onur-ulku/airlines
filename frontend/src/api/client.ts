import type { Location, Transportation, Route, UserRole } from "../types";

const API_BASE_URL = "http://localhost:8080";

export interface Credentials {
  username: string;
  password: string;
}

function authHeader(credentials: Credentials | null): HeadersInit {
  const headers: HeadersInit = { "Content-Type": "application/json" };
  if (credentials) {
    headers["Authorization"] = `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`;
  }
  return headers;
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const text = await res.text();
    let message = res.statusText;
    try {
      const json = JSON.parse(text) as { message?: string };
      if (json.message) message = json.message;
    } catch {
      if (text) message = text;
    }
    throw new Error(message);
  }
  if (res.status === 204) return {} as T;
  return res.json() as Promise<T>;
}

export const api = {
  async loginAndDetectRole(credentials: Credentials): Promise<UserRole> {
    const res = await fetch(`${API_BASE_URL}/api/transportations`, {
      headers: authHeader(credentials)
    });
    if (res.status === 200) return "ADMIN";
    if (res.status === 403) return "AGENCY";
    if (res.status === 401) throw new Error("Invalid credentials");
    throw new Error(`Unexpected status: ${res.status}`);
  },
  async getLocations(credentials: Credentials | null): Promise<Location[]> {
    return handleResponse(await fetch(`${API_BASE_URL}/api/locations`, { headers: authHeader(credentials) }));
  },
  async createLocation(credentials: Credentials | null, location: Location): Promise<Location> {
    const res = await fetch(`${API_BASE_URL}/api/locations`, {
      method: "POST",
      headers: authHeader(credentials),
      body: JSON.stringify(location)
    });
    return handleResponse(res);
  },
  async updateLocation(credentials: Credentials | null, id: number, location: Location): Promise<Location> {
    return handleResponse(
      await fetch(`${API_BASE_URL}/api/locations/${id}`, {
        method: "PUT",
        headers: authHeader(credentials),
        body: JSON.stringify(location)
      })
    );
  },
  async deleteLocation(credentials: Credentials | null, id: number): Promise<void> {
    await handleResponse(
      await fetch(`${API_BASE_URL}/api/locations/${id}`, {
        method: "DELETE",
        headers: authHeader(credentials)
      })
    );
  },
  async getTransportations(credentials: Credentials | null): Promise<Transportation[]> {
    return handleResponse(await fetch(`${API_BASE_URL}/api/transportations`, { headers: authHeader(credentials) }));
  },
  async createTransportation(credentials: Credentials | null, t: Transportation): Promise<Transportation> {
    const res = await fetch(`${API_BASE_URL}/api/transportations`, {
      method: "POST",
      headers: authHeader(credentials),
      body: JSON.stringify(t)
    });
    return handleResponse(res);
  },
  async updateTransportation(credentials: Credentials | null, id: number, t: Transportation): Promise<Transportation> {
    return handleResponse(
      await fetch(`${API_BASE_URL}/api/transportations/${id}`, {
        method: "PUT",
        headers: authHeader(credentials),
        body: JSON.stringify(t)
      })
    );
  },
  async deleteTransportation(credentials: Credentials | null, id: number): Promise<void> {
    await handleResponse(
      await fetch(`${API_BASE_URL}/api/transportations/${id}`, {
        method: "DELETE",
        headers: authHeader(credentials)
      })
    );
  },
  async getRoutes(credentials: Credentials | null, originId: number, destinationId: number, date: string): Promise<Route[]> {
    const params = new URLSearchParams({ originId: String(originId), destinationId: String(destinationId), date });
    return handleResponse(await fetch(`${API_BASE_URL}/api/routes?${params}`, { headers: authHeader(credentials) }));
  }
};
