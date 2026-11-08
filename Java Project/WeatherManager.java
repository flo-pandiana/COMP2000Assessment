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

    public boolean hasActiveWeather(String name) {
        return weatherList.stream().anyMatch(w -> w.getName().equals(name) && isActive(w));
    }

    public String describeActiveWeather() {
        String description = weatherList.stream()
                .filter(this::isActive)
                .map(w -> w.getName() + " (" + w.getDuration() + ")")
                .collect(Collectors.joining(", "));

        return description.isEmpty() ? "None" : description;
    }

    public void createWeather(String weather) {
        Weather w = null;

        switch (weather) {
            case "Rain":
                w = new Weather("Rain", 20, simulation -> simulation.addRain(8));
                break;
            case "Heatwave":
                w = new Weather("Heatwave", 9, simulation -> simulation.addHeatwave(2));
                break;
            case "Lightning":
                w = new Weather("Lightning", 3, simulation -> simulation.addLightning(10, 3));
                break;
            default:
                break;
        }

        if (w != null) {
            weatherList.add(w);
        }
    }

    public void createWeather(String weather, String selectedDirection) {
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

        int windDirection = direction;

        weatherList.add(new Weather("Wind", 25, simulation -> simulation.addWind(windDirection, 20)));
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
