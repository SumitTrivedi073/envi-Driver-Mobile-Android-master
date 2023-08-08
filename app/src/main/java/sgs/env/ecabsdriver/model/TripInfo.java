package sgs.env.ecabsdriver.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class TripInfo implements Parcelable {

    String fromAddress, passengerName, toAddress, status,
            driverName, driverPhone, passengerPhone, driverPropic;
    Double initialDistance;
    Integer initialPrice;
    Timestamp scheduledAt;

    public TripInfo(String fromAddress, String passengerName, String toAddress,
                    String status, String driverName, String driverPhone,
                    Double initialDistance, String passengerPhone,
                    Timestamp scheduledAt, String driverPropic, Integer initialPrice) {
        this.fromAddress = fromAddress;
        this.passengerName = passengerName;
        this.toAddress = toAddress;
        this.status = status;
        this.driverName = driverName;
        this.driverPhone = driverPhone;
        this.initialDistance = initialDistance;
        this.passengerPhone = passengerPhone;
        this.scheduledAt = scheduledAt;
        this.driverPropic = driverPropic;
        this.initialPrice = initialPrice;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public Double getInitialDistance() {
        return initialDistance;
    }

    public void setInitialDistance(Double initialDistance) {
        this.initialDistance = initialDistance;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public Integer getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(Integer initialPrice) {
        this.initialPrice = initialPrice;
    }

    public Timestamp getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Timestamp scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getDriverPropic() {
        return driverPropic;
    }

    public void setDriverPropic(String driverPropic) {
        this.driverPropic = driverPropic;
    }


    public static final Creator<TripInfo> CREATOR = new Creator<TripInfo>() {
        @Override
        public TripInfo createFromParcel(Parcel in) {
            return new TripInfo(in);
        }

        @Override
        public TripInfo[] newArray(int size) {
            return new TripInfo[size];
        }
    };

    protected TripInfo(Parcel in) {
        fromAddress = in.readString();
        passengerName = in.readString();
        toAddress = in.readString();
        status = in.readString();
        driverName = in.readString();
        driverPhone = in.readString();
        passengerPhone = in.readString();
        driverPropic = in.readString();
        if (in.readByte() == 0) {
            initialDistance = null;
        } else {
            initialDistance = in.readDouble();
        }
        if (in.readByte() == 0) {
            initialPrice = null;
        } else {
            initialPrice = in.readInt();
        }
        scheduledAt = in.readParcelable(Timestamp.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fromAddress);
        dest.writeString(passengerName);
        dest.writeString(toAddress);
        dest.writeString(status);
        dest.writeString(driverName);
        dest.writeString(driverPhone);
        dest.writeString(passengerPhone);
        dest.writeString(driverPropic);
        if (initialDistance == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(initialDistance);
        }
        if (initialPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(initialPrice);
        }
        dest.writeParcelable(scheduledAt, flags);
    }

}
