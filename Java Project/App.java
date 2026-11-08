import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

public class App {
    private JFrame frame;
    private ForestFireSimulation simulation;
    private PaintPanel paintPanel;
    private Timer timer;

    private JTextField rowsField;
    private JTextField columnsField;
    private JButton startPauseButton;
    private JButton addFirefighterButton;
    private JButton createMapButton;
    private JButton applyWeatherButton;
    private JLabel statusLabel;
    private StatisticsPanel statisticsPanel;
    private JComboBox<String> weatherBox;
    private JComboBox<String> windDirectionBox;
    private JComboBox<String> spreadBox;

    public App() {
        simulation = new ForestFireSimulation(50, 50, new Random());
        paintPanel = new PaintPanel(simulation);
        statisticsPanel = new StatisticsPanel(simulation);
        frame = new JFrame("Forest Fire Simulation");
        rowsField = new JTextField("50", 4);
        columnsField = new JTextField("50", 4);
        startPauseButton = new JButton("Start");
        addFirefighterButton = new JButton("Add Firefighter");
        statusLabel = new JLabel("Paused");
        createMapButton = new JButton("Create New Map");

        weatherBox = new JComboBox<String>(
                new String[] {
                        "None",
                        "Rain",
                        "Heatwave",
                        "Lightning",
                        "Wind"
                });

        windDirectionBox = new JComboBox<String>(
                new String[] {
                        "North",
                        "East",
                        "South",
                        "West"
                });
        windDirectionBox.setEnabled(false);

        spreadBox = new JComboBox<String>(
                new String[] {
                        "8 Neighbours",
                        "4 Neighbours"
                });

        applyWeatherButton = new JButton("Apply Weather");

        timer = new Timer(
                500,
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        simulation.update();
                    }
                });

        configureFrame();
        configureActions();
        attachObservers(simulation);
    }

    // Registers everything that needs to react to the simulation changing
    private void attachObservers(ForestFireSimulation target) {
        target.addObserver(paintPanel);
        target.addObserver(statisticsPanel);
        target.addObserver(
                new SimulationObserver() {
                    @Override
                    public void onSimulationChanged(String message) {
                        if (message != null) {
                            statusLabel.setText(message);
                        }
                    }
                });
    }

    private void configureFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new BorderLayout(8, 8));

        JPanel mapControlPanel = new JPanel(new FlowLayout());

        mapControlPanel.add(new JLabel("Rows:"));
        mapControlPanel.add(rowsField);

        mapControlPanel.add(new JLabel("Columns:"));
        mapControlPanel.add(columnsField);

        mapControlPanel.add(createMapButton);
        mapControlPanel.add(startPauseButton);
        mapControlPanel.add(addFirefighterButton);

        JPanel weatherControlPanel = new JPanel(new FlowLayout());

        weatherControlPanel.add(new JLabel("Weather:"));
        weatherControlPanel.add(weatherBox);
        weatherControlPanel.add(new JLabel("Direction:"));
        weatherControlPanel.add(windDirectionBox);
        weatherControlPanel.add(applyWeatherButton);
        weatherControlPanel.add(new JLabel("Spread:"));
        weatherControlPanel.add(spreadBox);

        JPanel controlPanel = new JPanel(new GridLayout(2, 1));
        controlPanel.add(mapControlPanel);
        controlPanel.add(weatherControlPanel);

        JScrollPane scrollPane = new JScrollPane(paintPanel);

        scrollPane.setPreferredSize(new Dimension(650, 650));

        frame.add(controlPanel, BorderLayout.NORTH);

        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(statusLabel);
        bottomPanel.add(statisticsPanel);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void configureActions() {
        startPauseButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        toggleSimulation();
                    }
                });

        addFirefighterButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        addRandomFirefighter();
                    }
                });

        createMapButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        createNewMap();
                    }
                });

        weatherBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        String selectedWeather = (String) weatherBox
                                .getSelectedItem();

                        windDirectionBox.setEnabled(
                                "Wind".equals(selectedWeather));
                    }
                });

        spreadBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        applySpreadPattern();
                    }
                });

        applyWeatherButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(
                            ActionEvent event) {

                        applySelectedWeather();
                    }
                });

        paintPanel.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mousePressed(
                            MouseEvent event) {

                        if (SwingUtilities.isRightMouseButton(event)) {
                            placeFirefighter(event);
                        } else {
                            igniteClickedCell(event);
                        }
                    }
                });
    }

    private void toggleSimulation() {
        if (timer.isRunning()) {
            timer.stop();
            startPauseButton.setText("Start");
            statusLabel.setText("Paused");
        } else {
            timer.start();
            startPauseButton.setText("Pause");
            statusLabel.setText("Running");
        }
    }

    private void createNewMap() {
        boolean timerWasRunning = timer.isRunning();

        timer.stop();

        try {
            int rows = Integer.parseInt(rowsField.getText().trim());

            int columns = Integer.parseInt(columnsField.getText().trim());

            if (rows > 100 || columns > 100) {
                throw new IllegalArgumentException(
                        "Rows and columns cannot exceed 100.");
            }

            ForestFireSimulation newSimulation = new ForestFireSimulation(rows, columns, new Random());

            simulation = newSimulation;

            paintPanel.setSimulation(newSimulation);
            statisticsPanel.setSimulation(newSimulation);
            attachObservers(newSimulation);

            applySpreadPattern();

            weatherBox.setSelectedItem("None");

            frame.pack();

            statusLabel.setText(
                    "New map created: " + rows + " x " + columns);

        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Rows and columns must be whole numbers.",
                    "Invalid input",
                    JOptionPane.ERROR_MESSAGE);

        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    frame,
                    exception.getMessage(),
                    "Invalid map size",
                    JOptionPane.ERROR_MESSAGE);

        } finally {
            if (timerWasRunning) {
                timer.start();
            }
        }
    }

    private void applySpreadPattern() {
        String selectedPattern = (String) spreadBox.getSelectedItem();

        if ("4 Neighbours".equals(selectedPattern)) {
            simulation.setSpreadPattern(new FourSpread());
        } else {
            simulation.setSpreadPattern(new EightSpread());
        }

        statusLabel.setText("Fire spread pattern: " + selectedPattern);
    }

    private void applySelectedWeather() {
        String selectedWeather = (String) this.weatherBox.getSelectedItem();
        String selectedDirection = (String) this.windDirectionBox.getSelectedItem();

        if ("Wind".equals(selectedWeather)) {
            this.simulation.addWeather(selectedWeather, selectedDirection);
        } else {
            this.simulation.addWeather(selectedWeather);
        }
    }

    private void addRandomFirefighter() {
        Random random = new Random();

        int row = random.nextInt(simulation.getGrid().getRows());
        int column = random.nextInt(simulation.getGrid().getColumns());

        simulation.addFirefighter(row, column);
    }

    private void placeFirefighter(
            MouseEvent event) {

        Grid.Position position = paintPanel.getGridPositionAt(event.getX(), event.getY());

        if (position == null) {
            return;
        }

        int row = position.getRow();
        int column = position.getColumn();

        simulation.addFirefighter(row, column);
    }

    private void igniteClickedCell(
            MouseEvent event) {

        if (event.getButton() != MouseEvent.BUTTON1) {
            return;
        }

        Grid.Position position = paintPanel.getGridPositionAt(event.getX(), event.getY());

        if (position == null) {
            return;
        }

        int row = position.getRow();
        int column = position.getColumn();

        Cell cell = simulation.getGrid().getCell(row, column);

        if (cell.isBurning()) {
            statusLabel.setText(
                    "This Cell is already burning.");

            return;
        }

        if (cell.getTerrain().isBurnedOut()) {
            statusLabel.setText(
                    "This Cell has already burned out.");

            return;
        }

        simulation.igniteCell(
                row,
                column,
                10);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        App app = new App();
                        app.showApp();
                    }
                });
    }

    private void showApp() {
        frame.setVisible(true);
    }
}
