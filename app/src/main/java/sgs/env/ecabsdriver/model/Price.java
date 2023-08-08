package sgs.env.ecabsdriver.model;

public class Price {

    private String totalFare;
    private String kmFare;
    private String minutePrice;
    private String tax;
    private String cgst;
    private String sgst;
    private String discountedPrice;
    private String amountTobeCollected;
    private String discount;
    private String perKMPrice;
    private String taxableAmount;
    private String tollAmount;
    private String advancePaid;
    private String distanceTravelled;
    private String payTMMoney;
    private String totalPrice;
    private String amountDriverOwes;


    public String getAmountDriverOwes() {
        return amountDriverOwes;
    }

    public void setAmountDriverOwes(String amountDriverOwes) {
        this.amountDriverOwes = amountDriverOwes;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPayTMMoney() {
        return payTMMoney;
    }

    public void setPayTMMoney(String payTMMoney) {
        this.payTMMoney = payTMMoney;
    }

    public String getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(String totalFare) {
        this.totalFare = totalFare;
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

    public String getKmFare() {
        return kmFare;
    }

    public void setKmFare(String kmFare) {
        this.kmFare = kmFare;
    }

    public String getMinutePrice() {
        return minutePrice;
    }

    public void setMinutePrice(String minutePrice) {
        this.minutePrice = minutePrice;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getAmountTobeCollected() {
        return amountTobeCollected;
    }

    public void setAmountTobeCollected(String amountTobeCollected) {
        this.amountTobeCollected = amountTobeCollected;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getPerKMPrice() {
        return perKMPrice;
    }

    public void setPerKMPrice(String perKMPrice) {
        this.perKMPrice = perKMPrice;
    }

    public String getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(String taxableAmount) {
        this.taxableAmount = taxableAmount;
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

    public String getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(String distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    @Override
    public String toString() {
        return "Price{" +
                "totalFare='" + totalFare + '\'' +
                ", kmFare='" + kmFare + '\'' +
                ", minutePrice='" + minutePrice + '\'' +
                ", tax='" + tax + '\'' +
                ", cgst='" + cgst + '\'' +
                ", sgst='" + sgst + '\'' +
                ", discountedPrice='" + discountedPrice + '\'' +
                ", amountTobeCollected='" + amountTobeCollected + '\'' +
                ", discount='" + discount + '\'' +
                ", perKMPrice='" + perKMPrice + '\'' +
                ", taxableAmount='" + taxableAmount + '\'' +
                ", tollAmount='" + tollAmount + '\'' +
                ", advancePaid='" + advancePaid + '\'' +
                ", distanceTravelled='" + distanceTravelled + '\'' +
                ", payTMMoney='" + payTMMoney + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", amountDriverOwes='" + amountDriverOwes + '\'' +
                '}';
    }
}

