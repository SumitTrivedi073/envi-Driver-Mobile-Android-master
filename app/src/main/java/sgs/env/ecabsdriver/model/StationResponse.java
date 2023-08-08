package sgs.env.ecabsdriver.model;

import java.util.List;

public class StationResponse {

    private String message;

    private List<ChargeStation> station;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChargeStation> getStation() {
        return station;
    }

    public void setStation(List<ChargeStation> station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "StationResponse{" +
                "message='" + message + '\'' +
                ", station=" + station +
                '}';
    }
}
