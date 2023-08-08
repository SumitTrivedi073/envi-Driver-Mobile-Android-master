package sgs.env.ecabsdriver.model;

import java.util.Arrays;
import java.util.Objects;

public class MongoLocation {
    private String[] coordinates;
    private String type;

    public String[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String[] coordinates) {
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
        return "MongoLocation{" +
                "coordinates=" + Arrays.toString(coordinates) +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MongoLocation)) return false;
        MongoLocation that = (MongoLocation) o;
        return Arrays.equals(coordinates, that.coordinates) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(type);
        result = 31 * result + Arrays.hashCode(coordinates);
        return result;
    }
}
