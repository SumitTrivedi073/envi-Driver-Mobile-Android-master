package sgs.env.ecabsdriver.model;

public class DriverWaitingForTrip {

    private String driverId;
    private boolean waitingForTrip;

    public DriverWaitingForTrip(String driverId, boolean waitingForTrip) {
        this.driverId = driverId;
        this.waitingForTrip = waitingForTrip;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public boolean getWaitingForTrip() {
        return waitingForTrip;
    }

    public void setWaitingForTrip(boolean waitingForTrip) {
        this.waitingForTrip = waitingForTrip;
    }

}
