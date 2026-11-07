public interface CellState {
    CellState UNBURNT = new Unburnt();
    CellState BURNING = new Burning();
    CellState BURNED_OUT = new BurnedOut();

    boolean isBurning();

    boolean isBurnedOut();

    boolean canBurn(Cell cell);

    void ignite(Cell cell, int intensity);

    void extinguish(Cell cell);

    void update(Cell cell);

    class Unburnt implements CellState {
        @Override
        public boolean isBurning() {
            return false;
        }

        @Override
        public boolean isBurnedOut() {
            return false;
        }

        @Override
        public boolean canBurn(Cell cell) {
            return cell.hasTerrain() && cell.getTerrain().canBurn();
        }

        @Override
        public void ignite(Cell cell, int intensity) {
            if (!cell.hasTerrain()) {
                throw new IllegalStateException("Attempted to ignite a cell with no terrain.");
            }

            cell.setFire(new Fire(intensity));
            cell.getTerrain().ignite();
            cell.setState(BURNING);
        }

        @Override
        public void extinguish(Cell cell) {
        }

        @Override
        public void update(Cell cell) {
        }
    }

    class Burning extends Unburnt {
        private static final int BURN_DURATION = 12;

        @Override
        public boolean isBurning() {
            return true;
        }

        @Override
        public void extinguish(Cell cell) {
            cell.setFire(null);
            cell.setState(BURNED_OUT);
        }

        @Override
        public void update(Cell cell) {
            if (!cell.hasTerrain() || !cell.getTerrain().canBurn()) {
                return;
            }

            Fire fire = cell.getFire();
            cell.getTerrain().burn(fire.getIntensity());
            fire.incrementTicksBurning();

            int elapsed = fire.getTicksBurning();

            if (elapsed >= BURN_DURATION) {
                cell.getTerrain().burn(250);
                cell.setFire(null);
                cell.setState(BURNED_OUT);
                return;
            }

            int newIntensity = Math.max(1, (int) Math.round(10 - (elapsed * 10.0 / BURN_DURATION)));
            fire.setIntensityAbsolute(newIntensity);
        }
    }

    class BurnedOut implements CellState {
        @Override
        public boolean isBurning() {
            return false;
        }

        @Override
        public boolean isBurnedOut() {
            return true;
        }

        @Override
        public boolean canBurn(Cell cell) {
            return false;
        }

        @Override
        public void ignite(Cell cell, int intensity) {
        }

        @Override
        public void extinguish(Cell cell) {
        }

        @Override
        public void update(Cell cell) {
        }
    }
}
