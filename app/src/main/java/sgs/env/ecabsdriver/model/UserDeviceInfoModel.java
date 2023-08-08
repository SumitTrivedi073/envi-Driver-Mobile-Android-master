package sgs.env.ecabsdriver.model;

public class UserDeviceInfoModel {
    
    private String model;
    
    private String manufacturer;
    
    private String brand;
    
    private String appVersion;
    
    private String androidVersion;
    
    private String product;
    
    private String userId;
    
    private String appVersionCode;
    
    private String batteryPercentage;
    
    @Override
    public String toString() {
        return "UserDeviceInfoModel{" + "model='" + model + '\'' + ", manufacturer='" + manufacturer + '\'' + ", brand='" + brand + '\'' + ", appVersion='" + appVersion + '\'' + ", androidVersion='" + androidVersion + '\'' + ", product='" + product + '\'' + ", userId='" + userId + '\'' + ", appVersionCode='" + appVersionCode + '\'' + ", batteryPercentage='" + batteryPercentage + '\'' + '}';
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getAppVersion() {
        return appVersion;
    }
    
    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
    
    public String getAndroidVersion() {
        return androidVersion;
    }
    
    public void setAndroidVersion(String androidVersion) {
        this.androidVersion = androidVersion;
    }
    
    public String getProduct() {
        return product;
    }
    
    public void setProduct(String product) {
        this.product = product;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getAppVersionCode() {
        return appVersionCode;
    }
    
    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }
    
    public String getBatteryPercentage() {
        return batteryPercentage;
    }
    
    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }
}
