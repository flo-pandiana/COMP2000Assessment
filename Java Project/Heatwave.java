public class Heatwave extends Weather {

	public Heatwave(int strength, int duration, int[][] location) {
		super(strength, duration, location);
	}

	@Override
	public void affectSimulation(ForestFireSimulation simulation) {
		Grid<Cell> grid = simulation.getGrid();

		GridIterator<Grid.Entry<Cell>> iterator = grid.createIterator();

		while (iterator.hasNext()) {
			Grid.Entry<Cell> entry = iterator.next();
			int row = entry.getRow();
			int column = entry.getColumn();
			Cell cell = entry.getValue();

			if (!cell.isBurning()) {
				continue;
			}

			for (int rowOffset = -2; rowOffset <= 2; rowOffset++) {
				for (int columnOffset = -2; columnOffset <= 2; columnOffset++) {

					int targetRow = row + rowOffset;
					int targetColumn = column + columnOffset;

					if (!grid.isInBounds(targetRow, targetColumn)) {
						continue;
					}

					Cell target = grid.getCell(targetRow, targetColumn);

					if (target.canBurn() && !target.isBurning() && Math.random() < 0.03) {
						simulation.igniteCell(targetRow, targetColumn, strength);
					}
				}
			}
		}
	}
}