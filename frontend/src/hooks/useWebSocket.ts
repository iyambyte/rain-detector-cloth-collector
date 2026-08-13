import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import type { DeviceState, SystemEvent } from '../types';

export const useWebSocket = (deviceId: number) => {
  const [deviceState, setDeviceState] = useState<DeviceState | null>(null);
  const [events, setEvents] = useState<SystemEvent[]>([]);

  useEffect(() => {
    // Uses a relative path. Vite proxy or production server routes it correctly.
    const socket = new SockJS('/ws-endpoint');
    const client = new Client({
      webSocketFactory: () => socket,
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log('Connected to WebSocket');
      
      // Subscribe to device state updates
      client.subscribe(`/topic/device/${deviceId}/state`, (message) => {
        if (message.body) {
          const state = JSON.parse(message.body) as DeviceState;
          setDeviceState(state);
        }
      });

      // Subscribe to system events for history/toasts
      client.subscribe('/topic/events', (message) => {
        if (message.body) {
          const event = JSON.parse(message.body) as SystemEvent;
          setEvents((prev) => [event, ...prev]);
        }
      });
    };

    client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      console.error('Additional details: ' + frame.body);
    };

    client.activate();

    return () => {
      client.deactivate();
    };
  }, [deviceId]);

  return { deviceState, events };
};
