package sgs.env.ecabsdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QRCodeModel {
    @SerializedName("resultInfo")
    @Expose
    private ResultInfo resultInfo;
    @SerializedName("qrCodeId")
    @Expose
    private String qrCodeId;
    @SerializedName("qrData")
    @Expose
    private String qrData;
    @SerializedName("image")
    @Expose
    private String image;

    public ResultInfo getResultInfo() {
        return resultInfo;
    }

    public void setResultInfo(ResultInfo resultInfo) {
        this.resultInfo = resultInfo;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static class ResultInfo {

        @SerializedName("resultStatus")
        @Expose
        private String resultStatus;
        @SerializedName("resultCode")
        @Expose
        private String resultCode;
        @SerializedName("resultMsg")
        @Expose
        private String resultMsg;

        public String getResultStatus() {
            return resultStatus;
        }

        public void setResultStatus(String resultStatus) {
            this.resultStatus = resultStatus;
        }

        public String getResultCode() {
            return resultCode;
        }

        public void setResultCode(String resultCode) {
            this.resultCode = resultCode;
        }

        public String getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }

        @Override
        public String toString() {
            return "ResultInfo{" +
                    "resultStatus='" + resultStatus + '\'' +
                    ", resultCode='" + resultCode + '\'' +
                    ", resultMsg='" + resultMsg + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QRCodeModel{" +
                "resultInfo=" + resultInfo +
                ", qrCodeId='" + qrCodeId + '\'' +
                ", qrData='" + qrData + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
