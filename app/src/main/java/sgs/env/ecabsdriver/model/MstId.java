package sgs.env.ecabsdriver.model;

public class MstId {

    private String passMstId;
    private String drvMstId;

    public String getPassMstId() {
        return passMstId;
    }

    public void setPassMstId(String passMstId) {
        this.passMstId = passMstId;
    }

    public String getDrvMstId() {
        return drvMstId;
    }

    public void setDrvMstId(String drvMstId) {
        this.drvMstId = drvMstId;
    }

    @Override
    public String toString() {
        return "MstId{" +
                "passMstId='" + passMstId + '\'' +
                ", drvMstId='" + drvMstId + '\'' +
                '}';
    }
}
