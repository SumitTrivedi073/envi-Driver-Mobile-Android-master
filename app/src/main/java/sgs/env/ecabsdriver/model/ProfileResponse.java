package sgs.env.ecabsdriver.model;

import java.util.List;

public class ProfileResponse  extends GeneralResponse{
private List<ProfileContent> content;

    @Override
    public String toString() {
        return "ProfileResponse{" +
                "content=" + content +
                '}';
    }

    public List<ProfileContent> getContent() {
        return content;
    }

    public void setContent(List<ProfileContent> content) {
        this.content = content;
    }
}
