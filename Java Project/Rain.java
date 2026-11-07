public class Rain extends Weather {

	public Rain(int strength, int duration, int[][] location) {
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

			if (cell.isBurning() && Math.random() < 0.3) {
				cell.extinguish();
				simulation.removeHeat(row, column, strength);
			} else if (!cell.isBurning()) {
				simulation.removeHeat(row, column, strength / 2f);
			}
		}
	}
}