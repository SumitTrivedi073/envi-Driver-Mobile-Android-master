package sgs.env.ecabsdriver.model;

public class StTripContent {
    private String tripId;
    private String bookId;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    @Override
    public String toString() {
        return "StTripContent{" +
                "tripId='" + tripId + '\'' +
                ", bookId='" + bookId + '\'' +
                '}';
    }
}
