package sgs.env.ecabsdriver.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;
import java.util.List;

public class TripDataModel implements Parcelable{

    @SerializedName("driverInfo")
    @Expose
    private DriverInfo driverInfo;
    @SerializedName("driverLocation")
    @Expose
    private DriverLocation driverLocation;
    @SerializedName("passengerInfo")
    @Expose
    private PassengerInfo passengerInfo;
    @SerializedName("tripInfo")
    @Expose
    private TripInfo tripInfo;
    @SerializedName("updatedAt")
    @Expose
    private com.google.firebase.Timestamp updatedAt;

    public TripDataModel() {
    }

    public TripDataModel(DriverInfo driverInfo, DriverLocation driverLocation, PassengerInfo passengerInfo, TripInfo tripInfo,
                         com.google.firebase.Timestamp updatedAt) {
        this.driverInfo = driverInfo;
        this.driverLocation = driverLocation;
        this.passengerInfo = passengerInfo;
        this.tripInfo = tripInfo;
        this.updatedAt = updatedAt;
    }

    protected TripDataModel(Parcel in) {
        driverInfo = in.readParcelable(DriverInfo.class.getClassLoader());
        driverLocation = in.readParcelable(DriverLocation.class.getClassLoader());
        passengerInfo = in.readParcelable(PassengerInfo.class.getClassLoader());
        tripInfo = in.readParcelable(TripInfo.class.getClassLoader());
        updatedAt = in.readParcelable(com.google.firebase.Timestamp.class.getClassLoader());
    }

    public static final Creator<TripDataModel> CREATOR = new Creator<TripDataModel>() {
        @Override
        public TripDataModel createFromParcel(Parcel in) {
            return new TripDataModel(in);
        }

        @Override
        public TripDataModel[] newArray(int size) {
            return new TripDataModel[size];
        }
    };

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
    }

    public DriverLocation getDriverLocation() {
        return driverLocation;
    }

    public void setDriverLocation(DriverLocation driverLocation) {
        this.driverLocation = driverLocation;
    }

    public PassengerInfo getPassengerInfo() {
        return passengerInfo;
    }

    public void setPassengerInfo(PassengerInfo passengerInfo) {
        this.passengerInfo = passengerInfo;
    }

    public TripInfo getTripInfo() {
        return tripInfo;
    }

    public void setTripInfo(TripInfo tripInfo) {
        this.tripInfo = tripInfo;
    }

    public com.google.firebase.Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(com.google.firebase.Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeParcelable(driverInfo, flags);
        dest.writeParcelable(driverLocation, flags);
        dest.writeParcelable(passengerInfo, flags);
        dest.writeParcelable(tripInfo, flags);
        dest.writeParcelable((Parcelable) updatedAt, flags);
    }

    public static class ArrivalAtDestination implements Parcelable{

        @SerializedName("sgst")
        @Expose
        private Integer sgst;
        @SerializedName("tollAmount")
        @Expose
        private Integer tollAmount;
        @SerializedName("totalFare")
        @Expose
        private Integer totalFare;
        @SerializedName("distanceTravelled")
        @Expose
        private Integer distanceTravelled;
        @SerializedName("promotionsApplied")
        @Expose
        private List<Object> promotionsApplied = null;
        @SerializedName("kmFare")
        @Expose
        private Integer kmFare;
        @SerializedName("advancePaid")
        @Expose
        private Integer advancePaid;
        @SerializedName("discount")
        @Expose
        private Integer discount;
        @SerializedName("cgst")
        @Expose
        private Integer cgst;
        @SerializedName("minuteFare")
        @Expose
        private Integer minuteFare;
        @SerializedName("discountedPrice")
        @Expose
        private Integer discountedPrice;
        @SerializedName("amountTobeCollected")
        @Expose
        private Integer amountTobeCollected;
        @SerializedName("perKMPrice")
        @Expose
        private Integer perKMPrice;
        @SerializedName("location")
        @Expose
        private Location location;
        @SerializedName("priceInclusiveOfTax")
        @Expose
        private Integer priceInclusiveOfTax;
        @SerializedName("paymentStatus")
        @Expose
        private String paymentStatus;

        public ArrivalAtDestination() {
        }

        public ArrivalAtDestination(Integer sgst, Integer tollAmount, Integer totalFare, Integer distanceTravelled, List<Object> promotionsApplied, Integer kmFare, Integer advancePaid, Integer discount, Integer cgst, Integer minuteFare, Integer discountedPrice, Integer amountTobeCollected, Integer perKMPrice, Location location, Integer priceInclusiveOfTax, String paymentStatus) {
            this.sgst = sgst;
            this.tollAmount = tollAmount;
            this.totalFare = totalFare;
            this.distanceTravelled = distanceTravelled;
            this.promotionsApplied = promotionsApplied;
            this.kmFare = kmFare;
            this.advancePaid = advancePaid;
            this.discount = discount;
            this.cgst = cgst;
            this.minuteFare = minuteFare;
            this.discountedPrice = discountedPrice;
            this.amountTobeCollected = amountTobeCollected;
            this.perKMPrice = perKMPrice;
            this.location = location;
            this.priceInclusiveOfTax = priceInclusiveOfTax;
            this.paymentStatus = paymentStatus;
        }

        protected ArrivalAtDestination(Parcel in) {
            if (in.readByte() == 0) {
                sgst = null;
            } else {
                sgst = in.readInt();
            }
            if (in.readByte() == 0) {
                tollAmount = null;
            } else {
                tollAmount = in.readInt();
            }
            if (in.readByte() == 0) {
                totalFare = null;
            } else {
                totalFare = in.readInt();
            }
            if (in.readByte() == 0) {
                distanceTravelled = null;
            } else {
                distanceTravelled = in.readInt();
            }
            if (in.readByte() == 0) {
                kmFare = null;
            } else {
                kmFare = in.readInt();
            }
            if (in.readByte() == 0) {
                advancePaid = null;
            } else {
                advancePaid = in.readInt();
            }
            if (in.readByte() == 0) {
                discount = null;
            } else {
                discount = in.readInt();
            }
            if (in.readByte() == 0) {
                cgst = null;
            } else {
                cgst = in.readInt();
            }
            if (in.readByte() == 0) {
                minuteFare = null;
            } else {
                minuteFare = in.readInt();
            }
            if (in.readByte() == 0) {
                discountedPrice = null;
            } else {
                discountedPrice = in.readInt();
            }
            if (in.readByte() == 0) {
                amountTobeCollected = null;
            } else {
                amountTobeCollected = in.readInt();
            }
            if (in.readByte() == 0) {
                perKMPrice = null;
            } else {
                perKMPrice = in.readInt();
            }
            location = in.readParcelable(Location.class.getClassLoader());
            if (in.readByte() == 0) {
                priceInclusiveOfTax = null;
            } else {
                priceInclusiveOfTax = in.readInt();
            }
            paymentStatus = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (sgst == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(sgst);
            }
            if (tollAmount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(tollAmount);
            }
            if (totalFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(totalFare);
            }
            if (distanceTravelled == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(distanceTravelled);
            }
            if (kmFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(kmFare);
            }
            if (advancePaid == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(advancePaid);
            }
            if (discount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(discount);
            }
            if (cgst == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(cgst);
            }
            if (minuteFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(minuteFare);
            }
            if (discountedPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(discountedPrice);
            }
            if (amountTobeCollected == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(amountTobeCollected);
            }
            if (perKMPrice == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(perKMPrice);
            }
            dest.writeParcelable(location, flags);
            if (priceInclusiveOfTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(priceInclusiveOfTax);
            }
            dest.writeString(paymentStatus);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<ArrivalAtDestination> CREATOR = new Creator<ArrivalAtDestination>() {
            @Override
            public ArrivalAtDestination createFromParcel(Parcel in) {
                return new ArrivalAtDestination(in);
            }

            @Override
            public ArrivalAtDestination[] newArray(int size) {
                return new ArrivalAtDestination[size];
            }
        };

        public Integer getSgst() {
            return sgst;
        }

        public void setSgst(Integer sgst) {
            this.sgst = sgst;
        }

        public Integer getTollAmount() {
            return tollAmount;
        }

        public void setTollAmount(Integer tollAmount) {
            this.tollAmount = tollAmount;
        }

        public Integer getTotalFare() {
            return totalFare;
        }

        public void setTotalFare(Integer totalFare) {
            this.totalFare = totalFare;
        }

        public Integer getDistanceTravelled() {
            return distanceTravelled;
        }

        public void setDistanceTravelled(Integer distanceTravelled) {
            this.distanceTravelled = distanceTravelled;
        }

        public List<Object> getPromotionsApplied() {
            return promotionsApplied;
        }

        public void setPromotionsApplied(List<Object> promotionsApplied) {
            this.promotionsApplied = promotionsApplied;
        }

        public Integer getKmFare() {
            return kmFare;
        }

        public void setKmFare(Integer kmFare) {
            this.kmFare = kmFare;
        }

        public Integer getAdvancePaid() {
            return advancePaid;
        }

        public void setAdvancePaid(Integer advancePaid) {
            this.advancePaid = advancePaid;
        }

        public Integer getDiscount() {
            return discount;
        }

        public void setDiscount(Integer discount) {
            this.discount = discount;
        }

        public Integer getCgst() {
            return cgst;
        }

        public void setCgst(Integer cgst) {
            this.cgst = cgst;
        }

        public Integer getMinuteFare() {
            return minuteFare;
        }

        public void setMinuteFare(Integer minuteFare) {
            this.minuteFare = minuteFare;
        }

        public Integer getDiscountedPrice() {
            return discountedPrice;
        }

        public void setDiscountedPrice(Integer discountedPrice) {
            this.discountedPrice = discountedPrice;
        }

        public Integer getAmountTobeCollected() {
            return amountTobeCollected;
        }

        public void setAmountTobeCollected(Integer amountTobeCollected) {
            this.amountTobeCollected = amountTobeCollected;
        }

        public Integer getPerKMPrice() {
            return perKMPrice;
        }

        public void setPerKMPrice(Integer perKMPrice) {
            this.perKMPrice = perKMPrice;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public Integer getPriceInclusiveOfTax() {
            return priceInclusiveOfTax;
        }

        public void setPriceInclusiveOfTax(Integer priceInclusiveOfTax) {
            this.priceInclusiveOfTax = priceInclusiveOfTax;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

    }

    public static class DriverInfo implements Parcelable {

        @SerializedName("driverImgUrl")
        @Expose
        private String driverImgUrl;
        @SerializedName("driverId")
        @Expose
        private String driverId;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("countryCode")
        @Expose
        private String countryCode;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("vehicleNumber")
        @Expose
        private String vehicleNumber;

        public DriverInfo() {
        }

        public DriverInfo(String driverImgUrl, String driverId, String phone, String countryCode, String name, String vehicleNumber) {
            this.driverImgUrl = driverImgUrl;
            this.driverId = driverId;
            this.phone = phone;
            this.countryCode = countryCode;
            this.name = name;
            this.vehicleNumber = vehicleNumber;
        }

        protected DriverInfo(Parcel in) {
            driverImgUrl = in.readString();
            driverId = in.readString();
            phone = in.readString();
            countryCode = in.readString();
            name = in.readString();
            vehicleNumber = in.readString();
        }

        public static final Creator<DriverInfo> CREATOR = new Creator<DriverInfo>() {
            @Override
            public DriverInfo createFromParcel(Parcel in) {
                return new DriverInfo(in);
            }

            @Override
            public DriverInfo[] newArray(int size) {
                return new DriverInfo[size];
            }
        };

        public String getDriverImgUrl() {
            return driverImgUrl;
        }

        public void setDriverImgUrl(String driverImgUrl) {
            this.driverImgUrl = driverImgUrl;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVehicleNumber() {
            return vehicleNumber;
        }

        public void setVehicleNumber(String vehicleNumber) {
            this.vehicleNumber = vehicleNumber;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(driverImgUrl);
            dest.writeString(driverId);
            dest.writeString(phone);
            dest.writeString(countryCode);
            dest.writeString(name);
            dest.writeString(vehicleNumber);
        }
    }

    public static class DriverLocation implements Parcelable {

        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("longitude")
        @Expose
        private Double longitude;

        public DriverLocation() {
        }

        public DriverLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        protected DriverLocation(Parcel in) {
            if (in.readByte() == 0) {
                latitude = null;
            } else {
                latitude = in.readDouble();
            }
            if (in.readByte() == 0) {
                longitude = null;
            } else {
                longitude = in.readDouble();
            }
        }

        public static final Creator<DriverLocation> CREATOR = new Creator<DriverLocation>() {
            @Override
            public DriverLocation createFromParcel(Parcel in) {
                return new DriverLocation(in);
            }

            @Override
            public DriverLocation[] newArray(int size) {
                return new DriverLocation[size];
            }
        };

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (latitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(latitude);
            }
            if (longitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(longitude);
            }
        }
    }

    public static class DropLocation implements Parcelable {

        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("dropAddress")
        @Expose
        private String dropAddress;
        @SerializedName("longitude")
        @Expose
        private Double longitude;

        public DropLocation() {
        }

        public DropLocation(Double latitude, String dropAddress, Double longitude) {
            this.latitude = latitude;
            this.dropAddress = dropAddress;
            this.longitude = longitude;
        }

        protected DropLocation(Parcel in) {
            if (in.readByte() == 0) {
                latitude = null;
            } else {
                latitude = in.readDouble();
            }
            dropAddress = in.readString();
            if (in.readByte() == 0) {
                longitude = null;
            } else {
                longitude = in.readDouble();
            }
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            if (latitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(latitude);
            }
            dest.writeString(dropAddress);
            if (longitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(longitude);
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DropLocation> CREATOR = new Creator<DropLocation>() {
            @Override
            public DropLocation createFromParcel(Parcel in) {
                return new DropLocation(in);
            }

            @Override
            public DropLocation[] newArray(int size) {
                return new DropLocation[size];
            }
        };

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getDropAddress() {
            return dropAddress;
        }

        public void setDropAddress(String dropAddress) {
            this.dropAddress = dropAddress;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

    }

    public static class Location implements Parcelable {

        @SerializedName("coordinates")
        @Expose
        private List<Object> coordinates = null;
        @SerializedName("type")
        @Expose
        private String type;

        public Location() {
        }

        public Location(List<Object> coordinates) {
            this.coordinates = coordinates;
        }

        protected Location(Parcel in) {
            type = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(type);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Location> CREATOR = new Creator<Location>() {
            @Override
            public Location createFromParcel(Parcel in) {
                return new Location(in);
            }

            @Override
            public Location[] newArray(int size) {
                return new Location[size];
            }
        };

        public List<Object> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Object> coordinates) {
            this.coordinates = coordinates;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public static class PassengerInfo implements Parcelable {

        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("countryCode")
        @Expose
        private String countryCode;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("passengerId")
        @Expose
        private String passengerId;

        public PassengerInfo() {
        }

        public PassengerInfo(String phone, String countryCode, String name, String passengerId) {
            this.phone = phone;
            this.countryCode = countryCode;
            this.name = name;
            this.passengerId = passengerId;
        }

        protected PassengerInfo(Parcel in) {
            phone = in.readString();
            countryCode = in.readString();
            name = in.readString();
            passengerId = in.readString();
        }

        public static final Creator<PassengerInfo> CREATOR = new Creator<PassengerInfo>() {
            @Override
            public PassengerInfo createFromParcel(Parcel in) {
                return new PassengerInfo(in);
            }

            @Override
            public PassengerInfo[] newArray(int size) {
                return new PassengerInfo[size];
            }
        };

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(String passengerId) {
            this.passengerId = passengerId;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(phone);
            dest.writeString(countryCode);
            dest.writeString(name);
            dest.writeString(passengerId);
        }
    }

    public static class PickupLocation implements Parcelable {

        @SerializedName("pickupAddress")
        @Expose
        private String pickupAddress;
        @SerializedName("latitude")
        @Expose
        private Double latitude;
        @SerializedName("longitude")
        @Expose
        private Double longitude;

        public PickupLocation() {
        }

        public PickupLocation(String pickupAddress, Double latitude, Double longitude) {
            this.pickupAddress = pickupAddress;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        protected PickupLocation(Parcel in) {
            pickupAddress = in.readString();
            if (in.readByte() == 0) {
                latitude = null;
            } else {
                latitude = in.readDouble();
            }
            if (in.readByte() == 0) {
                longitude = null;
            } else {
                longitude = in.readDouble();
            }
        }

        public static final Creator<PickupLocation> CREATOR = new Creator<PickupLocation>() {
            @Override
            public PickupLocation createFromParcel(Parcel in) {
                return new PickupLocation(in);
            }

            @Override
            public PickupLocation[] newArray(int size) {
                return new PickupLocation[size];
            }
        };

        public String getPickupAddress() {
            return pickupAddress;
        }

        public void setPickupAddress(String pickupAddress) {
            this.pickupAddress = pickupAddress;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeString(pickupAddress);
            if (latitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(latitude);
            }
            if (longitude == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(longitude);
            }
        }
    }

    public static class PriceClass implements Parcelable {

        @SerializedName("passengerCapacity")
        @Expose
        private Integer passengerCapacity;
        @SerializedName("minFare")
        @Expose
        private Integer minFare;
        @SerializedName("discountPercent")
        @Expose
        private Integer discountPercent;
        @SerializedName("toll_charges")
        @Expose
        private Integer tollCharges;
        @SerializedName("distance")
        @Expose
        private Double distance;
        @SerializedName("seller_discount")
        @Expose
        private Double sellerDiscount;
        @SerializedName("total_fare")
        @Expose
        private Double totalFare;
        @SerializedName("advancePaid")
        @Expose
        private Integer advancePaid;
        @SerializedName("base_fare")
        @Expose
        private Double baseFare;
        @SerializedName("sku_id")
        @Expose
        private String skuId;
        @SerializedName("perKMFare")
        @Expose
        private Double perKMFare;
        @SerializedName("state_tax")
        @Expose
        private Double stateTax;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("maxKmRange")
        @Expose
        private Integer maxKmRange;
        @SerializedName("bootSpace")
        @Expose
        private String bootSpace;
        @SerializedName("gstp")
        @Expose
        private Integer gstp;
        @SerializedName("subcategory")
        @Expose
        private String subcategory;
        @SerializedName("amount_to_be_collected")
        @Expose
        private Double amountToBeCollected;

        public PriceClass() {
        }

        public PriceClass(Integer passengerCapacity, Integer minFare, Integer discountPercent, Integer tollCharges, Double distance, Double sellerDiscount, Double totalFare, Integer advancePaid, Double baseFare, String skuId, Double perKMFare, Double stateTax, String type, Integer maxKmRange, String bootSpace, Integer gstp, String subcategory, Double amountToBeCollected) {
            this.passengerCapacity = passengerCapacity;
            this.minFare = minFare;
            this.discountPercent = discountPercent;
            this.tollCharges = tollCharges;
            this.distance = distance;
            this.sellerDiscount = sellerDiscount;
            this.totalFare = totalFare;
            this.advancePaid = advancePaid;
            this.baseFare = baseFare;
            this.skuId = skuId;
            this.perKMFare = perKMFare;
            this.stateTax = stateTax;
            this.type = type;
            this.maxKmRange = maxKmRange;
            this.bootSpace = bootSpace;
            this.gstp = gstp;
            this.subcategory = subcategory;
            this.amountToBeCollected = amountToBeCollected;
        }

        protected PriceClass(Parcel in) {
            if (in.readByte() == 0) {
                passengerCapacity = null;
            } else {
                passengerCapacity = in.readInt();
            }
            if (in.readByte() == 0) {
                minFare = null;
            } else {
                minFare = in.readInt();
            }
            if (in.readByte() == 0) {
                discountPercent = null;
            } else {
                discountPercent = in.readInt();
            }
            if (in.readByte() == 0) {
                tollCharges = null;
            } else {
                tollCharges = in.readInt();
            }
            if (in.readByte() == 0) {
                distance = null;
            } else {
                distance = in.readDouble();
            }
            if (in.readByte() == 0) {
                sellerDiscount = null;
            } else {
                sellerDiscount = in.readDouble();
            }
            if (in.readByte() == 0) {
                totalFare = null;
            } else {
                totalFare = in.readDouble();
            }
            if (in.readByte() == 0) {
                advancePaid = null;
            } else {
                advancePaid = in.readInt();
            }
            if (in.readByte() == 0) {
                baseFare = null;
            } else {
                baseFare = in.readDouble();
            }
            skuId = in.readString();
            if (in.readByte() == 0) {
                perKMFare = null;
            } else {
                perKMFare = in.readDouble();
            }
            if (in.readByte() == 0) {
                stateTax = null;
            } else {
                stateTax = in.readDouble();
            }
            type = in.readString();
            if (in.readByte() == 0) {
                maxKmRange = null;
            } else {
                maxKmRange = in.readInt();
            }
            bootSpace = in.readString();
            if (in.readByte() == 0) {
                gstp = null;
            } else {
                gstp = in.readInt();
            }
            subcategory = in.readString();
            if (in.readByte() == 0) {
                amountToBeCollected = null;
            } else {
                amountToBeCollected = in.readDouble();
            }
        }

        public static final Creator<PriceClass> CREATOR = new Creator<PriceClass>() {
            @Override
            public PriceClass createFromParcel(Parcel in) {
                return new PriceClass(in);
            }

            @Override
            public PriceClass[] newArray(int size) {
                return new PriceClass[size];
            }
        };

        public Integer getPassengerCapacity() {
            return passengerCapacity;
        }

        public void setPassengerCapacity(Integer passengerCapacity) {
            this.passengerCapacity = passengerCapacity;
        }

        public Integer getMinFare() {
            return minFare;
        }

        public void setMinFare(Integer minFare) {
            this.minFare = minFare;
        }

        public Integer getDiscountPercent() {
            return discountPercent;
        }

        public void setDiscountPercent(Integer discountPercent) {
            this.discountPercent = discountPercent;
        }

        public Integer getTollCharges() {
            return tollCharges;
        }

        public void setTollCharges(Integer tollCharges) {
            this.tollCharges = tollCharges;
        }

        public Double getDistance() {
            return distance;
        }

        public void setDistance(Double distance) {
            this.distance = distance;
        }

        public Double getSellerDiscount() {
            return sellerDiscount;
        }

        public void setSellerDiscount(Double sellerDiscount) {
            this.sellerDiscount = sellerDiscount;
        }

        public Double getTotalFare() {
            return totalFare;
        }

        public void setTotalFare(Double totalFare) {
            this.totalFare = totalFare;
        }

        public Integer getAdvancePaid() {
            return advancePaid;
        }

        public void setAdvancePaid(Integer advancePaid) {
            this.advancePaid = advancePaid;
        }

        public Double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(Double baseFare) {
            this.baseFare = baseFare;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public Double getPerKMFare() {
            return perKMFare;
        }

        public void setPerKMFare(Double perKMFare) {
            this.perKMFare = perKMFare;
        }

        public Double getStateTax() {
            return stateTax;
        }

        public void setStateTax(Double stateTax) {
            this.stateTax = stateTax;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getMaxKmRange() {
            return maxKmRange;
        }

        public void setMaxKmRange(Integer maxKmRange) {
            this.maxKmRange = maxKmRange;
        }

        public String getBootSpace() {
            return bootSpace;
        }

        public void setBootSpace(String bootSpace) {
            this.bootSpace = bootSpace;
        }

        public Integer getGstp() {
            return gstp;
        }

        public void setGstp(Integer gstp) {
            this.gstp = gstp;
        }

        public String getSubcategory() {
            return subcategory;
        }

        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
        }

        public Double getAmountToBeCollected() {
            return amountToBeCollected;
        }

        public void setAmountToBeCollected(Double amountToBeCollected) {
            this.amountToBeCollected = amountToBeCollected;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            if (passengerCapacity == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(passengerCapacity);
            }
            if (minFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(minFare);
            }
            if (discountPercent == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(discountPercent);
            }
            if (tollCharges == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(tollCharges);
            }
            if (distance == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(distance);
            }
            if (sellerDiscount == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(sellerDiscount);
            }
            if (totalFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(totalFare);
            }
            if (advancePaid == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(advancePaid);
            }
            if (baseFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(baseFare);
            }
            dest.writeString(skuId);
            if (perKMFare == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(perKMFare);
            }
            if (stateTax == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(stateTax);
            }
            dest.writeString(type);
            if (maxKmRange == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(maxKmRange);
            }
            dest.writeString(bootSpace);
            if (gstp == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeInt(gstp);
            }
            dest.writeString(subcategory);
            if (amountToBeCollected == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(amountToBeCollected);
            }
        }
    }

    public static class TripInfo implements Parcelable {

        @SerializedName("priceClass")
        @Expose
        private PriceClass priceClass;
        @SerializedName("specialRemarks")
        @Expose
        private String specialRemarks;
        @SerializedName("paymentMode")
        @Expose
        private String paymentMode;
        @SerializedName("passengerTripMasterId")
        @Expose
        private String passengerTripMasterId;
        @SerializedName("otp")
        @Expose
        private String otp;
        @SerializedName("dropLocation")
        @Expose
        private DropLocation dropLocation;
        @SerializedName("arrivalAtDestination")
        @Expose
        private ArrivalAtDestination arrivalAtDestination;
        @SerializedName("scheduledTrip")
        @Expose
        private Boolean scheduledTrip;
        @SerializedName("pickupLocation")
        @Expose
        private PickupLocation pickupLocation;
        @SerializedName("mmtReferenceNumber")
        @Expose
        private String mmtReferenceNumber;
        @SerializedName("paymentStatus")
        @Expose
        private String paymentStatus;
        @SerializedName("tripStatus")
        @Expose
        private String tripStatus;


        public TripInfo() {
        }

        public TripInfo(PriceClass priceClass, String specialRemarks, String paymentMode, String passengerTripMasterId, String otp, DropLocation dropLocation, ArrivalAtDestination arrivalAtDestination, Boolean scheduledTrip, PickupLocation pickupLocation, String mmtReferenceNumber, String paymentStatus, String tripStatus,Timestamp updatedAt) {
            this.priceClass = priceClass;
            this.specialRemarks = specialRemarks;
            this.paymentMode = paymentMode;
            this.passengerTripMasterId = passengerTripMasterId;
            this.otp = otp;
            this.dropLocation = dropLocation;
            this.arrivalAtDestination = arrivalAtDestination;
            this.scheduledTrip = scheduledTrip;
            this.pickupLocation = pickupLocation;
            this.mmtReferenceNumber = mmtReferenceNumber;
            this.paymentStatus = paymentStatus;
            this.tripStatus = tripStatus;

        }

        protected TripInfo(Parcel in) {
            priceClass = in.readParcelable(PriceClass.class.getClassLoader());
            specialRemarks = in.readString();
            paymentMode = in.readString();
            passengerTripMasterId = in.readString();
            otp = in.readString();
            dropLocation = in.readParcelable(DropLocation.class.getClassLoader());
            arrivalAtDestination = in.readParcelable(ArrivalAtDestination.class.getClassLoader());
            byte tmpScheduledTrip = in.readByte();
            scheduledTrip = tmpScheduledTrip == 0 ? null : tmpScheduledTrip == 1;
            pickupLocation = in.readParcelable(PickupLocation.class.getClassLoader());
            mmtReferenceNumber = in.readString();
            paymentStatus = in.readString();
            tripStatus = in.readString();


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

        public PriceClass getPriceClass() {
            return priceClass;
        }

        public void setPriceClass(PriceClass priceClass) {
            this.priceClass = priceClass;
        }

        public String getSpecialRemarks() {
            return specialRemarks;
        }

        public void setSpecialRemarks(String specialRemarks) {
            this.specialRemarks = specialRemarks;
        }

        public String getPaymentMode() {
            return paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }

        public String getPassengerTripMasterId() {
            return passengerTripMasterId;
        }

        public void setPassengerTripMasterId(String passengerTripMasterId) {
            this.passengerTripMasterId = passengerTripMasterId;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public DropLocation getDropLocation() {
            return dropLocation;
        }

        public void setDropLocation(DropLocation dropLocation) {
            this.dropLocation = dropLocation;
        }

        public ArrivalAtDestination getArrivalAtDestination() {
            return arrivalAtDestination;
        }

        public void setArrivalAtDestination(ArrivalAtDestination arrivalAtDestination) {
            this.arrivalAtDestination = arrivalAtDestination;
        }

        public Boolean getScheduledTrip() {
            return scheduledTrip;
        }

        public void setScheduledTrip(Boolean scheduledTrip) {
            this.scheduledTrip = scheduledTrip;
        }

        public PickupLocation getPickupLocation() {
            return pickupLocation;
        }

        public void setPickupLocation(PickupLocation pickupLocation) {
            this.pickupLocation = pickupLocation;
        }

        public String getMmtReferenceNumber() {
            return mmtReferenceNumber;
        }

        public void setMmtReferenceNumber(String mmtReferenceNumber) {
            this.mmtReferenceNumber = mmtReferenceNumber;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public String getTripStatus() {
            return tripStatus;
        }

        public void setTripStatus(String tripStatus) {
            this.tripStatus = tripStatus;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel dest, int flags) {
            dest.writeParcelable(priceClass, flags);
            dest.writeString(specialRemarks);
            dest.writeString(paymentMode);
            dest.writeString(passengerTripMasterId);
            dest.writeString(otp);
            dest.writeParcelable(dropLocation, flags);
            dest.writeParcelable(arrivalAtDestination, flags);
            dest.writeByte((byte) (scheduledTrip == null ? 0 : scheduledTrip ? 1 : 2));
            dest.writeParcelable(pickupLocation, flags);
            dest.writeString(mmtReferenceNumber);
            dest.writeString(paymentStatus);
            dest.writeString(tripStatus);

        }
    }
}
