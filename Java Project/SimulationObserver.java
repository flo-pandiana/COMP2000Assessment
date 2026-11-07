public interface SimulationObserver {
    // Called whenever the simulation changes. The message describes the event, or is null for a normal tick.
    void onSimulationChanged(String message);
}
