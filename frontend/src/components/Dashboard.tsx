import { useEffect, useState } from 'react';
import { useWebSocket } from '../hooks/useWebSocket';
import api from '../api/axios';
import { CloudRain, Sun, Square, FastForward, RotateCcw, Power } from 'lucide-react';
import type { DeviceState } from '../types';

export const Dashboard = () => {
  const deviceId = 1; // Default device
  const { deviceState, events } = useWebSocket(deviceId);
  const [initialState, setInitialState] = useState<DeviceState | null>(null);

  useEffect(() => {
    // Fetch initial state before websocket sends updates
    api.get(`/devices/${deviceId}`)
      .then(res => setInitialState(res.data))
      .catch(err => console.error(err));
  }, []);

  const state = deviceState || initialState;

  if (!state) return <div className="flex h-screen items-center justify-center">Loading...</div>;

  const handleCommand = async (command: string) => {
    try {
      await api.post(`/devices/${deviceId}/commands`, { command });
    } catch (e: any) {
      alert(e.response?.data || "Error sending command");
    }
  };

  const handleModeChange = async (mode: string) => {
    try {
      await api.put(`/devices/${deviceId}/mode`, { mode });
    } catch (e: any) {
      alert("Error changing mode");
    }
  };

  const simulateRain = async (detected: boolean) => {
    try {
      await api.post(`/simulation/rain`, { deviceId, detected });
    } catch (e: any) {
      alert("Error simulating rain");
    }
  };

  return (
    <div className="min-h-screen bg-slate-100 p-8 font-sans">
      <div className="max-w-5xl mx-auto space-y-6">
        
        <header className="flex justify-between items-center bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div>
            <h1 className="text-2xl font-bold text-slate-800 tracking-tight">Cloth Collector</h1>
            <p className="text-sm text-slate-500 mt-1">Device ID: {deviceId}</p>
          </div>
          <div className="flex items-center gap-3">
            <span className={`flex items-center gap-2 px-3 py-1.5 rounded-full text-sm font-medium ${state.deviceStatus === 'ONLINE' ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
              <Power size={16} /> {state.deviceStatus}
            </span>
          </div>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          {/* Status Cards */}
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 flex flex-col items-center justify-center text-center">
            <div className={`p-4 rounded-full mb-4 ${state.rainDetected ? 'bg-blue-100 text-blue-600' : 'bg-orange-100 text-orange-500'}`}>
              {state.rainDetected ? <CloudRain size={32} /> : <Sun size={32} />}
            </div>
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider">Weather Status</h2>
            <p className="text-xl font-bold text-slate-800 mt-1">{state.rainDetected ? 'Rain Detected' : 'Clear Sky'}</p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 flex flex-col items-center justify-center text-center">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-2">Clothes Position</h2>
            <p className={`text-2xl font-bold ${
              state.clothesPosition === 'OUTSIDE' ? 'text-green-600' : 
              state.clothesPosition === 'INDOOR' ? 'text-slate-700' : 
              state.clothesPosition === 'PARTIAL' ? 'text-orange-500' : 'text-blue-600 animate-pulse'
            }`}>
              {state.clothesPosition.replace('_', ' ')}
            </p>
          </div>

          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 flex flex-col items-center justify-center text-center">
            <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-2">Motor Status</h2>
            <p className={`text-xl font-bold ${state.motorStatus === 'RUNNING' ? 'text-blue-600 animate-pulse' : 'text-slate-600'}`}>
              {state.motorStatus}
            </p>
          </div>
        </div>

        {/* Controls */}
        <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-bold text-slate-800">Control Panel</h2>
            <div className="flex bg-slate-100 rounded-lg p-1">
              <button 
                onClick={() => handleModeChange('AUTO')}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${state.mode === 'AUTO' ? 'bg-white shadow-sm text-blue-600' : 'text-slate-600 hover:text-slate-900'}`}
              >AUTO</button>
              <button 
                onClick={() => handleModeChange('MANUAL')}
                className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${state.mode === 'MANUAL' ? 'bg-white shadow-sm text-blue-600' : 'text-slate-600 hover:text-slate-900'}`}
              >MANUAL</button>
            </div>
          </div>

          {state.mode === 'MANUAL' ? (
            <div className="grid grid-cols-3 gap-4">
              <button onClick={() => handleCommand('EXTEND')} className="flex flex-col items-center justify-center p-4 bg-slate-50 border border-slate-200 rounded-xl hover:bg-slate-100 hover:border-slate-300 transition-all text-slate-700">
                <FastForward size={24} className="mb-2" /> Extend
              </button>
              <button onClick={() => handleCommand('STOP')} className="flex flex-col items-center justify-center p-4 bg-red-50 border border-red-200 rounded-xl hover:bg-red-100 hover:border-red-300 transition-all text-red-700">
                <Square size={24} className="mb-2" /> Stop
              </button>
              <button onClick={() => handleCommand('RETRACT')} className="flex flex-col items-center justify-center p-4 bg-slate-50 border border-slate-200 rounded-xl hover:bg-slate-100 hover:border-slate-300 transition-all text-slate-700">
                <RotateCcw size={24} className="mb-2" /> Retract
              </button>
            </div>
          ) : (
            <div className="bg-blue-50 border border-blue-100 rounded-xl p-6 text-center">
              <p className="text-blue-800">System is in <strong>AUTO</strong> mode. Controls are disabled. The system will automatically retract clothes when rain is detected.</p>
            </div>
          )}
        </div>

        {/* Rain Simulator */}
        <div className="bg-slate-800 p-6 rounded-2xl shadow-sm border border-slate-700 text-white flex justify-between items-center">
          <div>
            <h2 className="text-lg font-bold">Rain Simulation</h2>
            <p className="text-slate-400 text-sm mt-1">Trigger rain events to test the AUTO mode</p>
          </div>
          <div className="flex gap-3">
            <button 
              onClick={() => simulateRain(true)}
              className="bg-blue-600 hover:bg-blue-500 px-5 py-2.5 rounded-lg font-medium transition-colors"
            >Simulate Rain</button>
            <button 
              onClick={() => simulateRain(false)}
              className="bg-slate-700 hover:bg-slate-600 px-5 py-2.5 rounded-lg font-medium transition-colors"
            >Stop Rain</button>
          </div>
        </div>

        {/* Live Event Log */}
        {events.length > 0 && (
          <div className="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
            <h2 className="text-lg font-bold text-slate-800 mb-4">Live Event Log</h2>
            <div className="space-y-3">
              {events.slice(0, 5).map((e, i) => (
                <div key={i} className="flex items-start gap-4 p-3 bg-slate-50 rounded-lg">
                  <span className="text-xs font-mono text-slate-500 mt-1">{new Date(e.createdAt).toLocaleTimeString()}</span>
                  <div>
                    <p className="text-sm font-bold text-slate-700">{e.eventType.replace('_', ' ')}</p>
                    <p className="text-sm text-slate-600">{e.description}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
