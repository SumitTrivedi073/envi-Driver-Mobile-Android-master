package sgs.env.ecabsdriver.model;

public class SocValues {

    private int id;
    private int soc1;
    private int soc2;
    private int soc3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSoc1() {
        return soc1;
    }

    public void setSoc1(int soc1) {
        this.soc1 = soc1;
    }

    public int getSoc2() {
        return soc2;
    }

    public void setSoc2(int soc2) {
        this.soc2 = soc2;
    }

    public int getSoc3() {
        return soc3;
    }

    public void setSoc3(int soc3) {
        this.soc3 = soc3;
    }

    @Override
    public String toString() {
        return "SocValues{" +
                "id=" + id +
                ", soc1=" + soc1 +
                ", soc2=" + soc2 +
                ", soc3=" + soc3 +
                '}';
    }
}
