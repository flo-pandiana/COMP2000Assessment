import Flammability.Burnable;

public abstract class Vegetation extends Terrain {
    private int age;
    private float moisture;
    protected HeatCalculator heatCalculator;

    public Vegetation(int age, int fuel, int burnRate, float moisture) {
        this.age = age;
        flammable = new Burnable(fuel, burnRate);
        this.moisture = moisture;

        heatCalculator = new HeatDecorators.Intensity( new HeatDecorators.Age( new HeatDecorators.Moisture( new HeatDecorators.Base(), this), this));
    }

    public int getAge() {
        return age;
    }

    public int getFuel() {
        return flammable.getFuel();
    }

    public int getBurnRate() {
        return flammable.getBurnRate();
    }

    public float getMoisture() {
        return moisture;
    }

    public void setMoisture(float moisture) {
        this.moisture = moisture;
    }

    public float calculateSpreadHeat(int fireIntensity) {
        if (fireIntensity <= 0) {
            throw new IllegalArgumentException(
                    "Fire intensity must be greater than zero, got: " + fireIntensity);
        }

        return heatCalculator.calculate(fireIntensity);
    }

    public void update() {
        age++;
    }

    @Override
    public void burn(int intensity) {
        flammable.burn(intensity, moisture);
    }
}