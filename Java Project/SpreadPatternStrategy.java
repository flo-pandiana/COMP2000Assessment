import java.util.List;

public interface SpreadPatternStrategy {
    // Returns the grid positions that receive heat from a burning cell at (row, column)
    List<Grid.Position> getSpreadPositions(Grid<Cell> grid, int row, int column);
}
