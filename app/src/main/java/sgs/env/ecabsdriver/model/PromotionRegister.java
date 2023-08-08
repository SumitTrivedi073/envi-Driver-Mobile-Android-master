package sgs.env.ecabsdriver.model;

import java.util.Date;

public class PromotionRegister {
    public String _id;
    public String userId;
    public String promotionId;
    public String promotionEventReferenceId; // in malbork, it is the trip id that created the promotion.
    public Boolean expired;
    public Date createdDate;
    public Date validTill;
    public double grossValue;
    public double netValue;
    public double promotionValue;
    public Boolean applied;
    public Date validFrom;
//    private String comments;
//    private String appliedEventReferenceId; //in malbork this is the trip Id that applied the promotion.


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionEventReferenceId() {
        return promotionEventReferenceId;
    }

    public void setPromotionEventReferenceId(String promotionEventReferenceId) {
        this.promotionEventReferenceId = promotionEventReferenceId;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }

    public double getGrossValue() {
        return grossValue;
    }

    public void setGrossValue(double grossValue) {
        this.grossValue = grossValue;
    }

    public double getNetValue() {
        return netValue;
    }

    public void setNetValue(double netValue) {
        this.netValue = netValue;
    }

    public double getPromotionValue() {
        return promotionValue;
    }

    public void setPromotionValue(double promotionValue) {
        this.promotionValue = promotionValue;
    }

    public Boolean getApplied() {
        return applied;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public String toString() {
        return "PromotionRegister{" +
                "_id='" + _id + '\'' +
                ", userId='" + userId + '\'' +
                ", promotionId='" + promotionId + '\'' +
                ", promotionEventReferenceId='" + promotionEventReferenceId + '\'' +
                ", expired=" + expired +
                ", createdDate=" + createdDate +
                ", validTill=" + validTill +
                ", grossValue=" + grossValue +
                ", netValue=" + netValue +
                ", promotionValue=" + promotionValue +
                ", applied=" + applied +
                ", validFrom=" + validFrom +
                '}';
    }
}
