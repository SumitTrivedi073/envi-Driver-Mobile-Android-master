package sgs.env.ecabsdriver.model;

public class RateTrip {
    private float rate;
    private String passengerTripMasterId;
    private String message;

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RateTrip{" +
                "rate=" + rate +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
