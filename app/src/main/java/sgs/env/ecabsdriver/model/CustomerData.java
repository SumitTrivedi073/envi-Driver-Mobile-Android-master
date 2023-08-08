package sgs.env.ecabsdriver.model;

public class CustomerData {
    private String userid;
    private String name;
    private String phone;
    private String fromlati;
    private String fromlongi;
    private String tolati;
    private String tolongi;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFromlati() {
        return fromlati;
    }

    public void setFromlati(String fromlati) {
        this.fromlati = fromlati;
    }

    public String getFromlongi() {
        return fromlongi;
    }

    public void setFromlongi(String fromlongi) {
        this.fromlongi = fromlongi;
    }

    @Override
    public String toString() {
        return "CustomerData{" +
                "userid='" + userid + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", fromlati='" + fromlati + '\'' +
                ", fromlongi='" + fromlongi + '\'' +
                '}';
    }

    public String getTolati() {
        return tolati;
    }

    public void setTolati(String tolati) {
        this.tolati = tolati;
    }

    public String getTolongi() {
        return tolongi;
    }

    public void setTolongi(String tolongi) {
        this.tolongi = tolongi;
    }
}
