public class Cell {
    Terrain terrain;
    Fire fire;
    private CellState state;

    public Cell() {
        terrain = null;
        fire = null;
        state = CellState.UNBURNT;
    }

    public Cell(Terrain t) {
        terrain = t;
        fire = null;
        state = CellState.UNBURNT;
    }

    public Cell(Terrain t, Fire f) {
        terrain = t;
        fire = f;
        state = f != null ? CellState.BURNING : CellState.UNBURNT;
    }

    public Cell(Fire f) {
        terrain = null;
        fire = f;
        state = f != null ? CellState.BURNING : CellState.UNBURNT;
    }

    public Boolean hasTerrain() {
        return terrain != null;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public boolean isBurning() {
        return state.isBurning();
    }

    public void extinguish() {
        state.extinguish(this);
    }

    public boolean canBurn() {
        return state.canBurn(this);
    }

    public void updateBurningState() {
        state.update(this);
    }

    public void ignite(int intensity) {
        state.ignite(this, intensity);
    }

    public Fire getFire() {
        return fire;
    }

    public boolean isPermanentlyBurnedOut() {
        return state.isBurnedOut();
    }

    void setFire(Fire fire) {
        this.fire = fire;
    }

    void setState(CellState state) {
        this.state = state;
    }
}
