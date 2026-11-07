public class Grass extends Vegetation {
    private int density;

    public Grass(int age, int fuel, int burnRate, float moisture, int density) {
        super(age, fuel, burnRate, moisture);
        this.density = density;

        heatCalculator = new HeatDecorators.DensityBonus(heatCalculator, this);
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }
}
