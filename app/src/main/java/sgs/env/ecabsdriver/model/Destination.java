package sgs.env.ecabsdriver.model;

public class Destination {

    private String destination_addresses;
    private String origin_addresses;
    private String distance;
    private String duration;
    private String amount;

    public String getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public String getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Destination{" +
                "destination_addresses='" + destination_addresses + '\'' +
                ", origin_addresses='" + origin_addresses + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
