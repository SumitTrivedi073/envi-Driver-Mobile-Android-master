package sgs.env.ecabsdriver.model;

public class BreakResoponse  extends GeneralResponse{

   private String  remainingTeaBreaks;
   private String  remainingLunchBreaks;

    public String getRemainingTeaBreaks() {
        return remainingTeaBreaks;
    }

    public void setRemainingTeaBreaks(String remainingTeaBreaks) {
        this.remainingTeaBreaks = remainingTeaBreaks;
    }

    public String getRemainingLunchBreaks() {
        return remainingLunchBreaks;
    }

    public void setRemainingLunchBreaks(String remainingLunchBreaks) {
        this.remainingLunchBreaks = remainingLunchBreaks;
    }

    @Override
    public String toString() {
        return "BreakResoponse{" +
                "remainingTeaBreaks='" + remainingTeaBreaks + '\'' +
                ", remainingLunchBreaks='" + remainingLunchBreaks + '\'' +
                '}';
    }
}
