package sgs.env.ecabsdriver.model;

import java.util.List;

public class HistoryContent {

    private List<BookedDetails> sucessRideDetails;
    private List<CanceledDetails> canceledBookingDetails;
    private List<TripDta> result;
    private int total_items;
    private int total_pages;

    public List<TripDta> getResult() {
        return result;
    }

    public void setResult(List<TripDta> result) {
        this.result = result;
    }

    public int getTotal_items() {
        return total_items;
    }

    public void setTotal_items(int total_items) {
        this.total_items = total_items;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public List<BookedDetails> getSucessRideDetails() {
        return sucessRideDetails;
    }

    public void setSucessRideDetails(List<BookedDetails> sucessRideDetails) {
        this.sucessRideDetails = sucessRideDetails;
    }

    public List<CanceledDetails> getCanceledBookingDetails() {
        return canceledBookingDetails;
    }

    public void setCanceledBookingDetails(List<CanceledDetails> canceledBookingDetails) {
        this.canceledBookingDetails = canceledBookingDetails;
    }
}

