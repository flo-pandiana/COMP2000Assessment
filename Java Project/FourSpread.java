import java.util.ArrayList;
import java.util.List;

public class FourSpread implements SpreadPatternStrategy {
    @Override
    public List<Grid.Position> getSpreadPositions(Grid<Cell> grid, int row, int column) {
        List<Grid.Position> positions = new ArrayList<Grid.Position>();

        for (Grid.Position neighbour : grid.neighbourPositions(row, column)) {
            if (neighbour.getRow() == row || neighbour.getColumn() == column) {
                positions.add(neighbour);
            }
        }

        return positions;
    }
}
