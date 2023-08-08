package sgs.env.ecabsdriver.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lenovo on 4/17/2018.
 */

public class Home implements Parcelable{
    private String name;
    private int image;

    protected Home(Parcel in) {
        name = in.readString();
        image = in.readInt();
    }

    public static final Creator<Home> CREATOR = new Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel in) {
            return new Home(in);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Home(String name, int image) {
        this.name = name;
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(image);
    }
}
