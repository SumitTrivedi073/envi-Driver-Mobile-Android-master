package sgs.env.ecabsdriver.data.local;

import android.provider.BaseColumns;

/**
 * Created by Lenovo on 4/19/2018.
 */

public class ECabDriverContract {

    public static final class DriverTimeBreak implements BaseColumns {
        public static final String TABLE_BREAK_TIME = "breakTime";
        public static final String _ID = "_id";
        public static final String BREAK_TYPE = "breakType";
        public static final String BREAK_COUNT = "breakCount";
        public static final String COLUMN_TOTAL_BREAK = "totalBreak";
    }

    public static final class DRIVER_RIDE_STATUS implements  BaseColumns {
        public static final String TABLE_RIDE_STATUS = "RIDE_STATUS";
        public static final String _ID = "_id";
        public static final String COL_RIDE_STATUS = "RIDE_STATUS";  //START_TRIP, IN TRIP, TRIP_COMPLETED
        public static final String COL_USER_ID = "USER_ID";
        public static final String COL_USER_NAME = "USER_NAME";
        public static final String COL_USER_PHONE = "USER_PHONE_NUM";
        public static final String COL_FROM_LAT = "USER_FRM_LAT";
        public static final String COL_TO_LAT = "USR_TO_LAT";
        public static final String COL_FROM_LONG = "USR_FROM_LONG";
        public static final String COL_TO_LONG = "USR_TO_LONG";
        public static final String COL_PASS_TRIP_MST_ID = "PASS_MST_ID";
        public static final String COL_DRIVER_PAGE = "DRIVER_PAGE";
    }

    public static final class TRIP_ENDED implements  BaseColumns {
        public static final String _ID = "_id";
        public static final String TABLE_TRIP_ENDED = "TRIP_ENDED";
        public static final String COL_DEST_ADDRESS = "DEST_ADDRESS";
        public static final String COL_ORIGIN_ADDRESS = "ORIGIN_ADDRESS";
        public static final String COL_DISTANCE = "DISTANCE";
        public static final String COL_DURATION = "DURATION";
        public static final String COL_AMOUNT = "AMOUNT";
    }

    public static final class MST_ID implements BaseColumns {
        public static final String TABLE = "MstId";
//        public static final String _ID = "_id";
        public static final String COL_PASSMST_ID = "pssMstId";
        public static final String COL_DRVMST_ID = "drvMstId";
    }

    public static final class SOC_MST_ENTRY implements BaseColumns {
        public static final String idPk = "idPk";
        public static final String TABLE = "SocTable";
        public static final String COL_SOC1 = "soc1";
        public static final String COL_SOC2 = "soc2";
        public static final String COL_SOC3 = "soc3";
    }

}
