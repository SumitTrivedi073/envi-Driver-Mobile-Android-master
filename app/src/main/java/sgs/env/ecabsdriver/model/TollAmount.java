package sgs.env.ecabsdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TollAmount {
    @SerializedName("tollAmount")
    @Expose
    private Double  tollAmount;
    @SerializedName("driverTripMasterId")
    @Expose
    private String driverTripMasterId;
    @SerializedName("passengerTripMasterId")
    @Expose
    private String passengerTripMasterId;
    @SerializedName("advancePaid")
    @Expose
    private Double  advancePaid;
    @SerializedName("amountTobeCollected")
    @Expose
    private Double  amountTobeCollected;
    @SerializedName("cgst")
    @Expose
    private Double  cgst;
    @SerializedName("sgst")
    @Expose
    private Double  sgst;
    @SerializedName("distanceTravelled")
    @Expose
    private Double  distanceTravelled;
    @SerializedName("totalFare")
    @Expose
    private Double  totalFare;

    public Double  getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(Double  tollAmount) {
        this.tollAmount = tollAmount;
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

    public Double  getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(Double  advancePaid) {
        this.advancePaid = advancePaid;
    }

    public Double  getAmountTobeCollected() {
        return amountTobeCollected;
    }

    public void setAmountTobeCollected(Double  amountTobeCollected) {
        this.amountTobeCollected = amountTobeCollected;
    }

    public Double  getCgst() {
        return cgst;
    }

    public void setCgst(Double  cgst) {
        this.cgst = cgst;
    }

    public Double  getSgst() {
        return sgst;
    }

    public void setSgst(Double  sgst) {
        this.sgst = sgst;
    }

    public Double  getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(Double  distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public Double  getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double  totalFare) {
        this.totalFare = totalFare;
    }
    @Override
    public String toString() {
        return "TollAmount{" +
                "tollAmount=" + tollAmount +
                ", totalFare=" + totalFare +
                ", driverTripMasterId='" + driverTripMasterId + '\'' +
                ", passengerTripMasterId='" + passengerTripMasterId + '\'' +
                '}';
    }
}
