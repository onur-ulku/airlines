export interface Location {
  id?: number;
  name: string;
  country: string;
  city: string;
  code: string;
}

export type TransportationType = "FLIGHT" | "BUS" | "SUBWAY" | "UBER";

export interface Transportation {
  id?: number;
  origin: Location;
  destination: Location;
  type: TransportationType;
  operatingDays: number[];
}

export interface RouteSegment {
  transportationId: number;
  originId: number;
  destinationId: number;
  type: TransportationType;
}

export interface Route {
  segments: RouteSegment[];
}

export type UserRole = "ADMIN" | "AGENCY";
