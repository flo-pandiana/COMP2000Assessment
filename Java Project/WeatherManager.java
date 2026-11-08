import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

public class WeatherManager {
    private ArrayList<Weather> weatherList = new ArrayList<>();

    public boolean isActive(Weather w) {
        if (w.getDuration() <= 0) {
            return false;
        }

        return true;
    }

    public boolean hasActiveWeather(Class<? extends Weather> type) {
        return weatherList.stream().anyMatch(w -> type.isInstance(w) && isActive(w));
    }

    public String describeActiveWeather() {
        String description = weatherList.stream()
                .filter(this::isActive)
                .map(w -> w.getClass().getSimpleName() + " (" + w.getDuration() + ")")
                .collect(Collectors.joining(", "));

        return description.isEmpty() ? "None" : description;
    }

    public void createWeather(String weather) {
        Weather w = null;

        switch (weather) {
            case "Rain":
                w = new Rain(8, 20, null);
                break;
            case "Heatwave":
                w = new Heatwave(2, 9, null);
                break;
            case "Lightning":
                w = new Lightning(10, 3, null, 3);
                break;
            default:
                break;
        }

        if (w != null) {
            weatherList.add(w);
        }
    }

    public void createWeather(String weather, String selectedDirection) {
        Weather w = null;
        int direction = 0;

        switch (selectedDirection) {
            case "East":
                direction = 1;
                break;
            case "South":
                direction = 2;
                break;
            case "West":
                direction = 3;
                break;
            default:
                break;
        }

        w = new Wind(20, 25, null, direction);

        if (w != null) {
            weatherList.add(w);
        }
    }

    public void advanceDecrement(Weather w) {
        w.decrementDuration();
    }

    public void update(ForestFireSimulation simulation) {
        Iterator<Weather> it = weatherList.iterator();

        while (it.hasNext()) {
            Weather w = it.next();
            advanceDecrement(w);

            if (!isActive(w)) {
                it.remove();
            } else {
                w.affectSimulation(simulation);
            }
        }
    }
}
