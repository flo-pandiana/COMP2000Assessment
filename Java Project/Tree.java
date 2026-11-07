public class Tree extends Vegetation {
    private int height;

    public Tree(int age, int fuel, int burnRate, float moisture, int height) {
        super(age, fuel, burnRate, moisture);
        this.height = height;

        heatCalculator = new HeatDecorators.HeightBonus(heatCalculator, this);
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void update() {
        super.update(); // Increases age via parent update logic
        if (getAge() % 5 == 0) {
            this.height++; // Increase height every 5 age units
        }
    }
}
