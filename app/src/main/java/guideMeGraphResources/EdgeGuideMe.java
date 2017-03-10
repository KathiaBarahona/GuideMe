package guideMeGraphResources;

/**
 * Created by Agile 2016 on 3/5/2017.
 */

public class EdgeGuideMe {
    private int distance = 0;
    private String unit = "metros";
    private String direction = "";
    public EdgeGuideMe(int distance, String direction) {
        this.distance = distance;
        this.direction = direction;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "EdgeGuideMe{" +
                "distance=" + distance +
                ", unit='" + unit + '\'' +
                '}';
    }
}
