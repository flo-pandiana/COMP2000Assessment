import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;

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
        Grid<Cell> grid = simulation.getGrid();

        int burning = 0;
        int burnedOut = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                Cell cell = grid.getCell(row, column);

                if (cell.isBurning()) {
                    burning++;
                } else if (cell.isPermanentlyBurnedOut()) {
                    burnedOut++;
                }
            }
        }

        statsLabel.setText("Burning: " + burning
                + "   |   Burned out: " + burnedOut
                + "   |   Firefighters: " + simulation.getFirefighters().size());
    }
}
