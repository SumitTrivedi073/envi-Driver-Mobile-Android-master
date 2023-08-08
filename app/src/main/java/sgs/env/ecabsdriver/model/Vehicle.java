package sgs.env.ecabsdriver.model;

public class Vehicle {

    private String model;
    private String number;
    private String color;
    private int id;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "model='" + model + '\'' +
                ", number='" + number + '\'' +
                ", color='" + color + '\'' +
                ", id=" + id +
                '}';
    }
}
