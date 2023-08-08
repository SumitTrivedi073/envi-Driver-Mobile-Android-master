package sgs.env.ecabsdriver.model;

import java.util.List;

public class TripHistoryResponse extends GeneralResponse {
    private HistoryContent content;

    public HistoryContent getContent() {
        return content;
    }

    public void setContent(HistoryContent content) {
        this.content = content;
    }
}


