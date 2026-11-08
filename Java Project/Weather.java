import java.util.function.Consumer;

public class Weather {
    private final String name;
    private int duration;
    private final Consumer<ForestFireSimulation> effect;

    public Weather(String name, int duration, Consumer<ForestFireSimulation> effect) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }

        this.name = name;
        this.duration = duration;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public void decrementDuration() {
        duration--;
    }

    public void affectSimulation(ForestFireSimulation simulation) {
        effect.accept(simulation);
    }
}
