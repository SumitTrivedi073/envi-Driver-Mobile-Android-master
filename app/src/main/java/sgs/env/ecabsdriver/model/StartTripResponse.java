package sgs.env.ecabsdriver.model;

public class StartTripResponse extends GeneralResponse{

    private StTripContent content;

    public StTripContent getContent() {
        return content;
    }

    public void setContent(StTripContent content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "StartTripResponse{" +
                "content=" + content +
                '}';
    }
}


