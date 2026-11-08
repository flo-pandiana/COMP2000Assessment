import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.util.stream.Collector;

// Shows live counts for the simulation. It updates itself whenever the simulation notifies it.
public class StatisticsPanel extends JPanel implements SimulationObserver {
    private ForestFireSimulation simulation;
    private JLabel statsLabel;

    public StatisticsPanel(ForestFireSimulation simulation) {
        super(new FlowLayout(FlowLayout.LEFT));

        statsLabel = new JLabel();
        add(statsLabel);

        setSimulation(simulation);
    }

    public void setSimulation(ForestFireSimulation simulation) {
        if (simulation == null) {
            throw new IllegalArgumentException(
                    "Simulation cannot be null");
        }

        this.simulation = simulation;
        refresh();
    }

    @Override
    public void onSimulationChanged(String message) {
        refresh();
    }

    private void refresh() {
        int[] counts = simulation.getGrid()
                .stream()
                .parallel()
                .map(entry -> entry.getValue())
                .collect(cellCounter());

        statsLabel.setText("Burning: " + counts[0]
                + "   |   Burned out: " + counts[1]
                + "   |   Firefighters: " + simulation.getFirefighters().size()
                + "   |   Weather: " + simulation.getWeatherManager().describeActiveWeather());
    }

    private Collector<Cell, int[], int[]> cellCounter() {
        return Collector.of(
                () -> new int[2],
                (partial, cell) -> {
                    if (cell.isBurning()) {
                        partial[0]++;
                    } else if (cell.isPermanentlyBurnedOut()) {
                        partial[1]++;
                    }
                },
                (left, right) -> {
                    left[0] += right[0];
                    left[1] += right[1];
                    return left;
                });
    }
}
