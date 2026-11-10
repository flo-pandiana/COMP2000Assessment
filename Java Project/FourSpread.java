import java.util.List;
import java.util.stream.Collectors;

public class FourSpread implements SpreadPatternStrategy {
    @Override
    public List<Grid.Position> getSpreadPositions(Grid<Cell> grid, int row, int column) {
        return grid.neighbourPositions(row, column)
                .stream()
                .filter(neighbour -> neighbour.getRow() == row || neighbour.getColumn() == column)
                .collect(Collectors.toList());
    }
}
