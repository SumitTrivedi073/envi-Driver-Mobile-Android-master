package sgs.env.ecabsdriver.model;

import java.util.List;

public class LocationPoints {
    private String type;
    private List<Double> coordinates;

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LocationPoints{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}
