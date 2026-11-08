import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ForestFireSimulation {
    private Grid<Cell> grid;
    private Grid<Float> heatMap;
    private float ignitionThreshold;
    private WeatherManager weatherManager = new WeatherManager();
    private SpreadPatternStrategy spreadPattern = new EightSpread();
    private List<Firefighter> firefighters = new ArrayList<>();
    private List<SimulationObserver> observers = new ArrayList<>();

    public ForestFireSimulation(int rows, int columns, Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null");
        }

        grid = new Grid<Cell>(rows, columns);
        initializeGrid(random);

        heatMap = new Grid<Float>(rows, columns);
        heatMap.fill(0f);

        ignitionThreshold = 100f;
    }

    private void initializeGrid(Random random) {
        for (int row = 0; row < grid.getRows(); row++) {
            for (int column = 0; column < grid.getColumns(); column++) {
                int terrainNumber = random.nextInt(100);
                Terrain terrain;

                if (terrainNumber < 60) {
                    int age = random.nextInt(11); // age 0 to 10
                    int height = 3 + random.nextInt(8); // height 3 to 10
                    int fuel = 50 + random.nextInt(40); // fuel 50 to 89
                    int burnRate = 2 + random.nextInt(2); // burnRate 2 to 3
                    float moisture = 0.3f + random.nextFloat() * 0.3f; // moisture 0.3 to 0.6

                    terrain = new Tree(age, fuel, burnRate, moisture, height);
                } else if (terrainNumber < 90) {
                    int age = random.nextInt(11); // age 0 to 10
                    int density = 1 + random.nextInt(4); // height 1 to 4
                    int fuel = 120 + random.nextInt(60); // fuel 120 to 179
                    int burnRate = 4 + random.nextInt(2); // burnRate 4 to 5
                    float moisture = 0.1f + random.nextFloat() * 0.2f; // moisture 0.1 to 0.3

                    terrain = new Grass(age, fuel, burnRate, moisture, density);

                } else {
                    float coolingStrength = 0.1f + random.nextFloat() * 0.2f; // coolingStrength 0.1 to 0.3
                    terrain = new River(coolingStrength);
                }

                grid.setCell(row, column, new Cell(terrain));
            }
        }
    }

    public Grid<Cell> getGrid() {
        return grid;
    }

    public WeatherManager getWeatherManager() {
        return this.weatherManager;
    }

    public void addWeather(String weather) {
        weatherManager.createWeather(weather);

        notifyObservers("Weather applied: " + weather);
    }

    public void addWeather(String weather, String direction) {
        weatherManager.createWeather(weather, direction);

        notifyObservers("Weather applied: " + weather + " going " + direction);
    }

    public void addObserver(SimulationObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SimulationObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(String message) {
        for (SimulationObserver observer : observers) {
            observer.onSimulationChanged(message);
        }
    }

    public List<Firefighter> getFirefighters() {
        return Collections.unmodifiableList(firefighters);
    }

    public void addFirefighter(int row, int column) {
        if (!grid.isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                    "Invalid grid position: (" + row + ", " + column + ")");
        }

        firefighters.add(new Firefighter(row, column));

        notifyObservers("Firefighter placed at row " + row + ", column " + column);
    }

    private void moveFirefighters() {
        for (Firefighter firefighter : firefighters) {
            firefighter.act(this);
        }
    }

    public void setSpreadPattern(SpreadPatternStrategy spreadPattern) {
        if (spreadPattern == null) {
            throw new IllegalArgumentException("Spread pattern cannot be null");
        }

        this.spreadPattern = spreadPattern;
    }

    public float getIgnitionThreshold() {
        return ignitionThreshold;
    }

    public void igniteCell(int row, int column, int intensity) {
        if (intensity <= 0) {
            throw new IllegalArgumentException(
                    "Fire intensity must be greater than zero");
        }

        Cell cell = grid.getCell(row, column);

        if (cell == null) {
            throw new IllegalStateException(
                    "The selected Grid position has no this Cell coordinate");
        }

        System.out.println("MANUAL IGNITE at row=" + row + " column=" + column);
        cell.ignite(intensity);

        notifyObservers("Fire started at row " + row + ", column " + column);
    }

    public void addHeat(int row, int column, float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Heat amount cannot be negative");
        }

        float currentHeat = heatMap.getCell(row, column);

        heatMap.setCell(row, column, currentHeat + amount);
    }

    public void removeHeat(int row, int column, float amount) {
        if (amount < 0) {
            throw new IllegalArgumentException(
                    "Heat amount cannot be negative");
        }

        float currentHeat = heatMap.getCell(row, column);
        float newHeat = Math.max(0f, currentHeat - amount);

        heatMap.setCell(row, column, newHeat);
    }

    public boolean hasReachedIgnitionThreshold(int row, int column) {
        return heatMap.getCell(row, column) >= ignitionThreshold;
    }

    public void update() {
        weatherManager.update(this);
        spreadFires();
        applyRiverCooling();
        moveFirefighters();
        updateCells();
        igniteHeatedCells();
        evolveTerrain();

        notifyObservers(null);
    }

    public void addRain(int strength) {
        grid.stream().forEach(entry -> {
            Cell cell = entry.getValue();

            if (cell.isBurning() && Math.random() < 0.3) {
                cell.extinguish();
                removeHeat(entry.getRow(), entry.getColumn(), strength);
            } else if (!cell.isBurning()) {
                removeHeat(entry.getRow(), entry.getColumn(), strength / 2f);
            }
        });
    }

    public void addHeatwave(int strength) {
        grid.stream()
                .filter(entry -> entry.getValue().isBurning())
                .forEach(entry -> {
                    for (int rowOffset = -2; rowOffset <= 2; rowOffset++) {
                        for (int columnOffset = -2; columnOffset <= 2; columnOffset++) {
                            int targetRow = entry.getRow() + rowOffset;
                            int targetColumn = entry.getColumn() + columnOffset;

                            if (!grid.isInBounds(targetRow, targetColumn)) {
                                continue;
                            }

                            Cell target = grid.getCell(targetRow, targetColumn);

                            if (target.canBurn() && !target.isBurning() && Math.random() < 0.03) {
                                igniteCell(targetRow, targetColumn, strength);
                            }
                        }
                    }
                });
    }

    public void addLightning(int strength, int strikesPerUpdate) {
        for (int i = 0; i < strikesPerUpdate; i++) {
            int row = (int) (Math.random() * grid.getRows());
            int column = (int) (Math.random() * grid.getColumns());

            Cell cell = grid.getCell(row, column);

            if (cell.canBurn() && !cell.isBurning()) {
                igniteCell(row, column, strength);
            }
        }
    }

    public void addWind(int direction, int strength) {
        GridIterator<Grid.Entry<Cell>> iterator = grid.createIterator();

        while (iterator.hasNext()) {
            Grid.Entry<Cell> entry = iterator.next();
            int row = entry.getRow();
            int column = entry.getColumn();
            Cell cell = entry.getValue();
            if (!cell.isBurning()) {
                continue;
            }

            int targetRow = row;
            int targetColumn = column;
            int oppositeRow = row;
            int oppositeColumn = column;

            switch (direction) {
                case 1:
                    targetColumn = column + 1;
                    oppositeColumn = column - 1;
                    break;
                case 2:
                    targetRow = row + 1;
                    oppositeRow = row - 1;
                    break;
                case 3:
                    targetColumn = column - 1;
                    oppositeColumn = column + 1;
                    break;
                default:
                    targetRow = row - 1;
                    oppositeRow = row + 1;
                    break;
            }

            if (grid.isInBounds(targetRow, targetColumn)) {
                addHeat(targetRow, targetColumn, strength);
            }

            if (grid.isInBounds(oppositeRow, oppositeColumn)) {
                removeHeat(oppositeRow, oppositeColumn, 200f);
            }
        }
    }

    private void igniteHeatedCells() {
        GridIterator<Grid.Entry<Cell>> iterator = grid.createIterator();

        while (iterator.hasNext()) {
            Grid.Entry<Cell> entry = iterator.next();
            int row = entry.getRow();
            int column = entry.getColumn();
            if (!hasReachedIgnitionThreshold(row, column)) {
                continue;
            }

            Cell cell = entry.getValue();

            if (!cell.canBurn()) {
                heatMap.setCell(row, column, 0f);
                continue;
            }

            if (cell.getTerrain().isBurnedOut()) {
                heatMap.setCell(row, column, 0f);
                continue;
            }

            if (cell.isBurning()) {
                continue;
            }

            int intensity = (int) Math.ceil(ignitionThreshold / 10f);

            System.out.println("AUTO IGNITE at row=" + row + " column=" + column);
            cell.ignite(intensity);

            if (cell.isBurning()) {
                removeHeat(row, column, ignitionThreshold);
            }
        }
    }

    private void updateCells() {
        grid.stream()
                .map(entry -> entry.getValue())
                .forEach(Cell::updateBurningState);
    }

    private void spreadFires() {
        GridIterator<Grid.Entry<Cell>> iterator = grid.createIterator();

        while (iterator.hasNext()) {
            Grid.Entry<Cell> entry = iterator.next();
            int row = entry.getRow();
            int column = entry.getColumn();

            Cell sourceCell = entry.getValue();

            if (!sourceCell.isBurning()) {
                continue;
            }

            try {
                int fireIntensity = sourceCell.getFire().getIntensity();

                float spreadHeat = fireIntensity;
                Terrain terrain = sourceCell.getTerrain();

                if (terrain instanceof Vegetation) {
                    Vegetation vegetation = (Vegetation) terrain;

                    spreadHeat = vegetation.calculateSpreadHeat(fireIntensity);
                }

                for (Grid.Position neighbour : spreadPattern.getSpreadPositions(grid, row, column)) {

                    addHeat(neighbour.getRow(), neighbour.getColumn(), spreadHeat);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Can't burn on cell (" + row + ", " + column + ")");
            }
        }
    }

    private void applyRiverCooling() {
        grid.stream()
                .filter(entry -> entry.getValue().getTerrain() instanceof River)
                .forEach(entry -> {
                    int row = entry.getRow();
                    int column = entry.getColumn();
                    float cooling = ((River) entry.getValue().getTerrain()).getCoolingStrength();

                    removeHeat(row, column, cooling);

                    for (Grid.Position neighbour : grid.neighbourPositions(row, column)) {
                        removeHeat(neighbour.getRow(), neighbour.getColumn(), cooling / 2.0f);
                    }
                });
    }

    private void evolveTerrain() {
        grid.stream()
                .map(entry -> entry.getValue())
                .filter(cell -> cell != null && !cell.isBurning() && cell.getTerrain() != null)
                .forEach(cell -> cell.getTerrain().update());
    }
}