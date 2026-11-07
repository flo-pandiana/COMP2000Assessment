import java.util.List;

public class EightSpread implements SpreadPatternStrategy {
    @Override
    public List<Grid.Position> getSpreadPositions(Grid<Cell> grid, int row, int column) {
        return grid.neighbourPositions(row, column);
    }
}
