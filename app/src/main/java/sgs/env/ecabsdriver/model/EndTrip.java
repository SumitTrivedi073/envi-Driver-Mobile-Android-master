package sgs.env.ecabsdriver.model;

public class EndTrip {

    private String tripid;
    private String bookid;
    private String tolat;
    private String tolong;
    private String  userid;
    private String  ratingByDriver;

    @Override
    public String toString() {
        return "EndTrip{" +
                "tripid='" + tripid + '\'' +
                ", bookid='" + bookid + '\'' +
                ", tolat='" + tolat + '\'' +
                ", tolong='" + tolong + '\'' +
                ", userid='" + userid + '\'' +
                ", ratingByDriver='" + ratingByDriver + '\'' +
                '}';
    }

    public String getTripid() {
        return tripid;
    }

    public void setTripid(String tripid) {
        this.tripid = tripid;
    }

    public String getBookid() {
        return bookid;
    }

    public void setBookid(String bookid) {
        this.bookid = bookid;
    }

    public String getTolat() {
        return tolat;
    }

    public void setTolat(String tolat) {
        this.tolat = tolat;
    }

    public String getTolong() {
        return tolong;
    }

    public void setTolong(String tolong) {
        this.tolong = tolong;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getRatingByDriver() {
        return ratingByDriver;
    }

    public void setRatingByDriver(String ratingByDriver) {
        this.ratingByDriver = ratingByDriver;
    }
}
