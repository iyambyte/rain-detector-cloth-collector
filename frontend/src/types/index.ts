export type OperatingMode = 'AUTO' | 'MANUAL';
export type ClothesPosition = 'OUTSIDE' | 'MOVING_INSIDE' | 'INDOOR' | 'MOVING_OUTSIDE' | 'PARTIAL';
export type MotorStatus = 'RUNNING' | 'STOPPED' | 'ERROR';
export type DeviceStatus = 'ONLINE' | 'OFFLINE';

export interface DeviceState {
  deviceId: number;
  rainDetected: boolean;
  clothesPosition: ClothesPosition;
  motorStatus: MotorStatus;
  mode: OperatingMode;
  deviceStatus: DeviceStatus;
  lastUpdated: string;
}

export interface SystemEvent {
  id: number;
  eventType: string;
  description: string;
  createdAt: string;
}
