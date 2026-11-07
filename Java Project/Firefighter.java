public class Firefighter {
    public static final int NORMAL_SPEED = 1;
    public static final float COOLING = 60f;

    private int row;
    private int column;

    public Firefighter(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public void act(ForestFireSimulation simulation) {
        Grid<Cell> grid = simulation.getGrid();

        int targetRow = -1;
        int targetColumn = -1;
        int bestDistance = Integer.MAX_VALUE;

        GridIterator<Grid.Entry<Cell>> iterator = grid.createIterator();

        while (iterator.hasNext()) {
            Grid.Entry<Cell> entry = iterator.next();
            Cell cell = entry.getValue();

            if (cell == null || !cell.isBurning()) {
                continue;
            }

            int r = entry.getRow();
            int c = entry.getColumn();
            int distance = Math.max(Math.abs(r - row), Math.abs(c - column));

            if (distance < bestDistance) {
                bestDistance = distance;
                targetRow = r;
                targetColumn = c;
            }
        }

        if (targetRow == -1) {
            return;
        }

        // Speed depends on the weather
        WeatherManager weatherManager = simulation.getWeatherManager();
        int speed = NORMAL_SPEED;

        if (weatherManager.hasActiveWeather(Heatwave.class)) {
            speed = 0;
        } else if (weatherManager.hasActiveWeather(Rain.class)) {
            speed = 2;
        }

        // Move towards the fire, diagonals allowed
        for (int step = 0; step < speed; step++) {
            int distance = Math.max(Math.abs(targetRow - row), Math.abs(targetColumn - column));

            if (distance <= 1) {
                break;
            }

            row += Integer.signum(targetRow - row);
            column += Integer.signum(targetColumn - column);
        }

        // Fight fires on own cell and neighbours
        Cell ownCell = grid.getCell(row, column);

        if (ownCell != null && ownCell.isBurning()) {
            ownCell.extinguish();
        }
        simulation.removeHeat(row, column, COOLING);

        for (Grid.Position neighbour : grid.neighbourPositions(row, column)) {
            int r = neighbour.getRow();
            int c = neighbour.getColumn();
            Cell cell = grid.getCell(r, c);

            if (cell != null && cell.isBurning()) {
                cell.extinguish();
            }
            simulation.removeHeat(r, c, COOLING);
        }
    }
}
