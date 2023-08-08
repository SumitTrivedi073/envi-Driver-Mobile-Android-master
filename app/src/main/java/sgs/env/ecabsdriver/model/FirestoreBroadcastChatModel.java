package sgs.env.ecabsdriver.model;

import android.os.Parcel;
import android.os.Parcelable;

public class FirestoreBroadcastChatModel implements Parcelable {

    String doc_id,message,Name,adminId,time,serviceType,messageType;


    public FirestoreBroadcastChatModel(String doc_id, String message, String name, String adminId, String time, String serviceType, String messageType) {
        this.doc_id = doc_id;
        this.message = message;
        this.Name = name;
        this.adminId = adminId;
        this.time = time;
        this.serviceType = serviceType;
        this.messageType = messageType;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
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

   public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }


    public static final Creator<FirestoreBroadcastChatModel> CREATOR = new Creator<FirestoreBroadcastChatModel>() {
        @Override
        public FirestoreBroadcastChatModel createFromParcel(Parcel in) {
            return new FirestoreBroadcastChatModel(in);
        }

        @Override
        public FirestoreBroadcastChatModel[] newArray(int size) {
            return new FirestoreBroadcastChatModel[size];
        }
    };

    protected FirestoreBroadcastChatModel(Parcel in) {
        doc_id = in.readString();
        message = in.readString();
        Name = in.readString();
        adminId = in.readString();
        time = in.readString();
        serviceType = in.readString();
        messageType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doc_id);
        dest.writeString(message);
        dest.writeString(Name);
        dest.writeString(adminId);
        dest.writeString(time);
        dest.writeString(serviceType);
        dest.writeString(messageType);
    }
}
