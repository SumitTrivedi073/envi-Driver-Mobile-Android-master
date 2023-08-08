package sgs.env.ecabsdriver.model;

public class ChargeStation {

    private String name;
    private String address;
    private LocationPoints location;
    private String status;
    private Boolean malborkOwned;
    private String chargerId;
    private String connectorId;
    private String connectorStatus;
    private String chargerStatus;
    private String connectorPowerLevel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocationPoints getLocation() {
        return location;
    }

    public void setLocation(LocationPoints location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getMalborkOwned() {
        return malborkOwned;
    }

    public void setMalborkOwned(Boolean malborkOwned) {
        this.malborkOwned = malborkOwned;
    }

    public String getChargerId() {
        return chargerId;
    }

    public void setChargerId(String chargerId) {
        this.chargerId = chargerId;
    }

    public String getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(String connectorId) {
        this.connectorId = connectorId;
    }

    public String getConnectorStatus() {
        return connectorStatus;
    }

    public void setConnectorStatus(String connectorStatus) {
        this.connectorStatus = connectorStatus;
    }

    public String getChargerStatus() {
        return chargerStatus;
    }

    public void setChargerStatus(String chargerStatus) {
        this.chargerStatus = chargerStatus;
    }

    public String getConnectorPowerLevel() {
        return connectorPowerLevel;
    }

    public void setConnectorPowerLevel(String connectorPowerLevel) {
        this.connectorPowerLevel = connectorPowerLevel;
    }

    @Override
    public String toString() {
        return "ChargeStation{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location=" + location +
                ", status='" + status + '\'' +
                ", malborkOwned=" + malborkOwned +
                ", chargerId='" + chargerId + '\'' +
                ", connectorId='" + connectorId + '\'' +
                ", connectorStatus='" + connectorStatus + '\'' +
                ", chargerStatus='" + chargerStatus + '\'' +
                ", connectorPowerLevel='" + connectorPowerLevel + '\'' +
                '}';
    }
}
