package sgs.env.ecabsdriver.model;

public class ProfileContent {
private String id;
private String name;
private String mailid;
private String gender;
private String propic;
private String address;
private float rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMailid() {
        return mailid;
    }

    public void setMailid(String mailid) {
        this.mailid = mailid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "ProfileContent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", mailid='" + mailid + '\'' +
                ", gender='" + gender + '\'' +
                ", propic='" + propic + '\'' +
                ", address='" + address + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }
}
