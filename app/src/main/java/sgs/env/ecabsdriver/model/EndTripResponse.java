package sgs.env.ecabsdriver.model;

public class EndTripResponse {

    private String message;
    private Destination content;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Destination getContent() {
        return content;
    }

    public void setContent(Destination content) {
        this.content = content;
    }

    @Override
    public String   toString() {
        return "EndTripResponse{" +
                "message='" + message + '\'' +
                ", content=" + content +
                '}';
    }
}
