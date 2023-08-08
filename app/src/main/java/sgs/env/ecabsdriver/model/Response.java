package sgs.env.ecabsdriver.model;


public class Response {

    private String token;
    private String name;
    private String id;
    private Vehicle vehicle;
    private String pic;
    private Address address;
    private SocValues socvalues;

    public SocValues getSocvalues() {
        return socvalues;
    }

    public void setSocvalues(SocValues socvalues) {
        this.socvalues = socvalues;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Response{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", vehicle=" + vehicle +
                ", address=" + address +
                '}';
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
