package sgs.env.ecabsdriver.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppConfig {

    @SerializedName("swVersionConfig")
    @Expose
    private SwVersionConfig swVersionConfig;

    public SwVersionConfig getSwVersionConfig() {
        return swVersionConfig;
    }

    public void setSwVersionConfig(SwVersionConfig swVersionConfig) {
        this.swVersionConfig = swVersionConfig;
    }

    @SerializedName("searchConfig")
    @Expose
    private SearchConfig searchConfig;

    public SearchConfig getSearchConfig() {
        return searchConfig;
    }

    public void setSearchConfig(SearchConfig searchConfig) {
        this.searchConfig = searchConfig;
    }


    public static class SwVersionConfig {

        @SerializedName("minDriverAppVersion")
        @Expose
        private Integer minDriverAppVersion;
        @SerializedName("driverAppUrl")
        @Expose
        private String driverAppUrl;

        public Integer getMinDriverAppVersion() {
            return minDriverAppVersion;
        }

        public void setMinDriverAppVersion(Integer minDriverAppVersion) {
            this.minDriverAppVersion = minDriverAppVersion;
        }

        public String getDriverAppUrl() {
            return driverAppUrl;
        }

        public void setDriverAppUrl(String driverAppUrl) {
            this.driverAppUrl = driverAppUrl;
        }

    }

    public static class SearchConfig {

        @SerializedName("googleDirectionWFDriverIntervalInMin")
        @Expose
        private Integer googleDirectionWFDriverIntervalInMin;
        @SerializedName("distanceAPISearchInetrval")
        @Expose
        private Integer distanceAPISearchInetrval;
        @SerializedName("searchRadiusExtnMultiplier")
        @Expose
        private Double searchRadiusExtnMultiplier;
        @SerializedName("seacrhMinDistance")
        @Expose
        private Integer seacrhMinDistance;
        @SerializedName("googlePlaceSearchTriggger")
        @Expose
        private Integer googlePlaceSearchTriggger;
        @SerializedName("searchFrequency")
        @Expose
        private Integer searchFrequency;
        @SerializedName("googleDirectionWFDriverIntervalMaxTrialCount")
        @Expose
        private Integer googleDirectionWFDriverIntervalMaxTrialCount;
        @SerializedName("minSoc")
        @Expose
        private Integer minSoc;
        @SerializedName("searchRadius")
        @Expose
        private Integer searchRadius;

        public Integer getGoogleDirectionWFDriverIntervalInMin() {
            return googleDirectionWFDriverIntervalInMin;
        }

        public void setGoogleDirectionWFDriverIntervalInMin(Integer googleDirectionWFDriverIntervalInMin) {
            this.googleDirectionWFDriverIntervalInMin = googleDirectionWFDriverIntervalInMin;
        }

        public Integer getDistanceAPISearchInetrval() {
            return distanceAPISearchInetrval;
        }

        public void setDistanceAPISearchInetrval(Integer distanceAPISearchInetrval) {
            this.distanceAPISearchInetrval = distanceAPISearchInetrval;
        }

        public Double getSearchRadiusExtnMultiplier() {
            return searchRadiusExtnMultiplier;
        }

        public void setSearchRadiusExtnMultiplier(Double searchRadiusExtnMultiplier) {
            this.searchRadiusExtnMultiplier = searchRadiusExtnMultiplier;
        }

        public Integer getSeacrhMinDistance() {
            return seacrhMinDistance;
        }

        public void setSeacrhMinDistance(Integer seacrhMinDistance) {
            this.seacrhMinDistance = seacrhMinDistance;
        }

        public Integer getGooglePlaceSearchTriggger() {
            return googlePlaceSearchTriggger;
        }

        public void setGooglePlaceSearchTriggger(Integer googlePlaceSearchTriggger) {
            this.googlePlaceSearchTriggger = googlePlaceSearchTriggger;
        }

        public Integer getSearchFrequency() {
            return searchFrequency;
        }

        public void setSearchFrequency(Integer searchFrequency) {
            this.searchFrequency = searchFrequency;
        }

        public Integer getGoogleDirectionWFDriverIntervalMaxTrialCount() {
            return googleDirectionWFDriverIntervalMaxTrialCount;
        }

        public void setGoogleDirectionWFDriverIntervalMaxTrialCount(Integer googleDirectionWFDriverIntervalMaxTrialCount) {
            this.googleDirectionWFDriverIntervalMaxTrialCount = googleDirectionWFDriverIntervalMaxTrialCount;
        }

        public Integer getMinSoc() {
            return minSoc;
        }

        public void setMinSoc(Integer minSoc) {
            this.minSoc = minSoc;
        }

        public Integer getSearchRadius() {
            return searchRadius;
        }

        public void setSearchRadius(Integer searchRadius) {
            this.searchRadius = searchRadius;
        }

        @Override
        public String toString() {
            return "SearchConfig{" +
                    "googleDirectionWFDriverIntervalInMin=" + googleDirectionWFDriverIntervalInMin +
                    ", distanceAPISearchInetrval=" + distanceAPISearchInetrval +
                    ", searchRadiusExtnMultiplier=" + searchRadiusExtnMultiplier +
                    ", seacrhMinDistance=" + seacrhMinDistance +
                    ", googlePlaceSearchTriggger=" + googlePlaceSearchTriggger +
                    ", searchFrequency=" + searchFrequency +
                    ", googleDirectionWFDriverIntervalMaxTrialCount=" + googleDirectionWFDriverIntervalMaxTrialCount +
                    ", minSoc=" + minSoc +
                    ", searchRadius=" + searchRadius +
                    '}';
        }
    }
}
