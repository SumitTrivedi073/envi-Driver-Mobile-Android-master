package sgs.env.ecabsdriver.interfce;

public interface UImethodsI {   // used in all activity when api service is called
    void startProgessBar();  // make progressBar visible
    void endProgressBar();

    interface StartTrip {
        void moveToGoogleMapsNavigation();
    }

    interface EndTrip {
        void endTrip();
    }

    interface ApiFailed {
        void apiFailed();
    }

    interface RetryMstId {
        void retry();
    }
}
