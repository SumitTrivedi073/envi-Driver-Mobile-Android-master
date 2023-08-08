package sgs.env.ecabsdriver.model;

public class BookedDetails {
    private String fromLat;
    private String fromLong;
    private String toLat;
    private String toLong;
    private String startdate;
    private String ratingByUser;
    private String ratingByDriver;
    private String distance;
    private String amount;

    public String getFromLat() {
        return fromLat;
    }

    public void setFromLat(String fromLat) {
        this.fromLat = fromLat;
    }

    public String getFromLong() {
        return fromLong;
    }

    public void setFromLong(String fromLong) {
        this.fromLong = fromLong;
    }

    public String getToLat() {
        return toLat;
    }

    public void setToLat(String toLat) {
        this.toLat = toLat;
    }

    public String getToLong() {
        return toLong;
    }

    public void setToLong(String toLong) {
        this.toLong = toLong;
    }


    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getRatingByUser() {
        return ratingByUser;
    }

    public void setRatingByUser(String ratingByUser) {
        this.ratingByUser = ratingByUser;
    }

    public String getRatingByDriver() {
        return ratingByDriver;
    }

    public void setRatingByDriver(String ratingByDriver) {
        this.ratingByDriver = ratingByDriver;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "BookedDetails{" +
                "fromLat='" + fromLat + '\'' +
                ", fromLong='" + fromLong + '\'' +
                ", toLat='" + toLat + '\'' +
                ", toLong='" + toLong + '\'' +
                ", startdate='" + startdate + '\'' +
                ", ratingByUser='" + ratingByUser + '\'' +
                ", ratingByDriver='" + ratingByDriver + '\'' +
                ", distance='" + distance + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
