package sgs.env.ecabsdriver.model;

public class PassengerData {
    private Double totalFare;
    private Boolean promotionApplied;
    private Boolean tripEnd;
    private String paymentMode;
    private String paymentStatus;


    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public Boolean getPromotionApplied() {
        return promotionApplied;
    }

    public void setPromotionApplied(Boolean promotionApplied) {
        this.promotionApplied = promotionApplied;
    }

    public Boolean getTripEnd() {
        return tripEnd;
    }

    public void setTripEnd(Boolean tripEnd) {
        this.tripEnd = tripEnd;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PassengerData{" +
                "totalFare=" + totalFare +
                ", promotionApplied=" + promotionApplied +
                ", tripEnd=" + tripEnd +
                ", paymentMode='" + paymentMode + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}