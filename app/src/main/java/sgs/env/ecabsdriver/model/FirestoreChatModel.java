package sgs.env.ecabsdriver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FirestoreChatModel implements Parcelable {
    String docId,message, Name, driverName, driverId, adminId,messageType, time;



    public FirestoreChatModel(String docId, String message, String name, String driverName, String driverId, String adminId, String messageType, String time) {
        this.docId = docId;
        this.message = message;
        this.Name = name;
        this.driverName = driverName;
        this.driverId = driverId;
        this.adminId = adminId;
        this.messageType = messageType;
        this.time = time;
    }


    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static final Creator<FirestoreChatModel> CREATOR = new Creator<FirestoreChatModel>() {
        @Override
        public FirestoreChatModel createFromParcel(Parcel in) {
            return new FirestoreChatModel(in);
        }

        @Override
        public FirestoreChatModel[] newArray(int size) {
            return new FirestoreChatModel[size];
        }
    };

    protected FirestoreChatModel(Parcel in) {
        docId = in.readString();
        message = in.readString();
        Name = in.readString();
        driverName = in.readString();
        driverId = in.readString();
        adminId = in.readString();
        messageType = in.readString();
        time = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(docId);
        dest.writeString(message);
        dest.writeString(Name);
        dest.writeString(driverName);
        dest.writeString(driverId);
        dest.writeString(adminId);
        dest.writeString(messageType);
        dest.writeString(time);

    }
}
