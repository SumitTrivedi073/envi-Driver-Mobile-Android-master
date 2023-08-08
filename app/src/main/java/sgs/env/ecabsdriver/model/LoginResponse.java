package sgs.env.ecabsdriver.model;

public class LoginResponse extends GeneralResponse{

    private Response content;
    
    private ResponseConfig config;
    
    public ResponseConfig getConfig() {
        return config;
    }
    
    public void setConfig(ResponseConfig config) {
        this.config = config;
    }
    
    public Response getContent() {
        return content;
    }

    public void setContent(Response content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                ", content=" + content +
                ", config=" + config +
                '}';
    }
}
