package sgs.env.ecabsdriver.model;

public class TripDetails {
    private String totalFare;
    private String distanceTravelled;
    private String kmFare;
    private String minuteFare;
    private String cgst;
    private String sgst;
    private String discountedPrice;
    private String perKMPrice;
    private String discount;
    private String amountTobeCollected;
    private String tollAmount;
    private String advancePaid;

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
    }

    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(String distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public String getKmFare() {
        return kmFare;
    }

    public void setKmFare(String kmFare) {
        this.kmFare = kmFare;
    }

    public String getMinuteFare() {
        return minuteFare;
    }

    public void setMinuteFare(String minuteFare) {
        this.minuteFare = minuteFare;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getPerKMPrice() {
        return perKMPrice;
    }

    public void setPerKMPrice(String perKMPrice) {
        this.perKMPrice = perKMPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmountTobeCollected() {
        return amountTobeCollected;
    }

    public void setAmountTobeCollected(String amountTobeCollected) {
        this.amountTobeCollected = amountTobeCollected;
    }

    public String getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(String tollAmount) {
        this.tollAmount = tollAmount;
    }

    public String getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(String advancePaid) {
        this.advancePaid = advancePaid;
    }

    @Override
    public String toString() {
        return "TripDetails{" +
                "totalFare='" + totalFare + '\'' +
                ", distanceTravelled='" + distanceTravelled + '\'' +
                ", kmFare='" + kmFare + '\'' +
                ", minuteFare='" + minuteFare + '\'' +
                ", cgst='" + cgst + '\'' +
                ", sgst='" + sgst + '\'' +
                ", discountedPrice='" + discountedPrice + '\'' +
                ", perKMPrice='" + perKMPrice + '\'' +
                ", discount='" + discount + '\'' +
                ", amountTobeCollected='" + amountTobeCollected + '\'' +
                ", tollAmount='" + tollAmount + '\'' +
                ", advancePaid='" + advancePaid + '\'' +
                '}';
    }

}
