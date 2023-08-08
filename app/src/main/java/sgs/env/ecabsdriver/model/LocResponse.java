package sgs.env.ecabsdriver.model;

public class LocResponse {

    private String message;
    private Veh vehicle;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Veh getVehicle() {
        return vehicle;
    }

    public void setVehicle(Veh vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return "LocResponse{" +
                "message='" + message + '\'' +
                ", vehicle=" + vehicle +
                '}';
    }
}
