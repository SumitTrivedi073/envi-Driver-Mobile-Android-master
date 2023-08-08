package sgs.env.ecabsdriver.model;

public class Soc {
    private String soc1;
    private String soc2;
    private String soc3;

    public String getSoc1() {
        return soc1;
    }

    public void setSoc1(String soc1) {
        this.soc1 = soc1;
    }

    public String getSoc2() {
        return soc2;
    }

    public void setSoc2(String soc2) {
        this.soc2 = soc2;
    }

    public String getSoc3() {
        return soc3;
    }

    public void setSoc3(String soc3) {
        this.soc3 = soc3;
    }

    @Override
    public String toString() {
        return "Soc{" +
                "soc1='" + soc1 + '\'' +
                ", soc2='" + soc2 + '\'' +
                ", soc3='" + soc3 + '\'' +
                '}';
    }
}
