package sgs.env.ecabsdriver.model;

public class UserPromotion {
    String userId;
    String promotionId;
//    String _id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String  getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionIds) {
        this.promotionId = promotionIds;
    }



    @Override
    public String toString() {
        return "UserPromotion{" +
                "userId='" + userId + '\'' +
                ", promotionIds=" + promotionId +
                '}';
    }
}
