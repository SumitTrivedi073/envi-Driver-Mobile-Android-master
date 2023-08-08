package sgs.env.ecabsdriver.model;

public class Coupons {
    public int promotionId;
    public String phoneNumber;
    public PromotionRegister promotionRegister;
    public UserPromotion userPromotion;
    public String userId;

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PromotionRegister getPromotionRegister() {
        return promotionRegister;
    }

    public void setPromotionRegister(PromotionRegister promotionRegister) {
        this.promotionRegister = promotionRegister;
    }

    public UserPromotion getUserPromotion() {
        return userPromotion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserPromotion(UserPromotion userPromotion) {
        this.userPromotion = userPromotion;
    }

}
