package sgs.env.ecabsdriver.model;

public class SearchBody {

    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "SearchBody{" +
                "location=" + location +
                '}';
    }
}
