package sgs.env.ecabsdriver.model;

import java.util.Date;

public class Log {
    private String tag;
    private String text;
    private Date date;
    private String type;
    private String stage;
    private String driverTripMasterId;
    private String passengerTripMasterId;

    public String getTag() {
        return tag;

    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getDriverTripMasterId() {
        return driverTripMasterId;
    }

    public void setDriverTripMasterId(String driverTripMasterId) {
        this.driverTripMasterId = driverTripMasterId;
    }

    public String getPassengerTripMasterId() {
        return passengerTripMasterId;
    }

    public void setPassengerTripMasterId(String passengerTripMasterId) {
        this.passengerTripMasterId = passengerTripMasterId;
    }

    @Override
    public String toString() {
        return "Log{" +
                "tag='" + tag + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", stage='" + stage + '\'' +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                '}';
    }
}
