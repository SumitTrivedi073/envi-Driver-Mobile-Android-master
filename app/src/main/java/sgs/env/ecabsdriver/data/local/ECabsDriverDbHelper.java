package sgs.env.ecabsdriver.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import sgs.env.ecabsdriver.model.CustomerLocation;
import sgs.env.ecabsdriver.model.CustomerNotfication;
import sgs.env.ecabsdriver.model.Destination;
import sgs.env.ecabsdriver.model.MstId;
import sgs.env.ecabsdriver.model.Soc;
import sgs.env.ecabsdriver.model.SocValues;


import static android.provider.BaseColumns._ID;

/**
 * Created by Lenovo on 4/19/2018.
 */

public class ECabsDriverDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DriverAppDBHelper";
    private static final int DATABASE_VERSION  = 1;
    private static final String DATABASE_NAME = "ECabsDriver.db";

    public ECabsDriverDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DRIVER_BREAK_TIME_TABLE =
                "CREATE TABLE " + ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME + " ( "
                        + ECabDriverContract.DriverTimeBreak._ID + " INTEGER PRIMARY KEY, "
                        + ECabDriverContract.DriverTimeBreak.BREAK_TYPE + " TEXT, "
                        + ECabDriverContract.DriverTimeBreak.BREAK_COUNT + " INTEGER, "
                        + ECabDriverContract.DriverTimeBreak.COLUMN_TOTAL_BREAK + " INTEGER "
                        + " )";

        final String SQL_CREATE_DRIVER_RIDE_STATUS =
                "CREATE TABLE " + ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS + " ( "
                        + ECabDriverContract.DRIVER_RIDE_STATUS._ID + " INT PRIMARY KEY, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_RIDE_STATUS + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_ID + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_NAME + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_PHONE + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LAT + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LONG + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LONG + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LAT + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_PASS_TRIP_MST_ID + " TEXT, "
                        + ECabDriverContract.DRIVER_RIDE_STATUS.COL_DRIVER_PAGE + " TEXT "
                        + " )";

        final String SQL_CREATE_TRIP_END =
                "CREATE TABLE " + ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED + " ( "
                        + ECabDriverContract.TRIP_ENDED._ID + " INTEGER PRIMARY KEY, "
                        + ECabDriverContract.TRIP_ENDED.COL_DEST_ADDRESS + " TEXT, "
                        + ECabDriverContract.TRIP_ENDED.COL_ORIGIN_ADDRESS + " TEXT, "
                        + ECabDriverContract.TRIP_ENDED.COL_DISTANCE + " TEXT, "
                        + ECabDriverContract.TRIP_ENDED.COL_DURATION + " TEXT, "
                        + ECabDriverContract.TRIP_ENDED.COL_AMOUNT + " TEXT "
                        + " )";

        final String SQL_CREATE_SOC =
                "CREATE TABLE " + ECabDriverContract.SOC_MST_ENTRY.TABLE + " ( "
                        + ECabDriverContract.SOC_MST_ENTRY.idPk + " INTEGER PRIMARY KEY, "
                        + ECabDriverContract.SOC_MST_ENTRY.COL_SOC1 + " INTEGER, "
                        + ECabDriverContract.SOC_MST_ENTRY.COL_SOC2 + " INTEGER, "
                        + ECabDriverContract.SOC_MST_ENTRY.COL_SOC3 + " INTEGER "
                        + " )";

        try {
            db.execSQL(SQL_CREATE_DRIVER_BREAK_TIME_TABLE);
            db.execSQL(SQL_CREATE_DRIVER_RIDE_STATUS);
            db.execSQL(SQL_CREATE_TRIP_END);
            Log.d(TAG, "onCreate: table " + SQL_CREATE_TRIP_END);
            try {
                db.execSQL(SQL_CREATE_SOC);
            }
            catch (Exception e) {
                Log.d(TAG, "onCreate: ex " + e.getMessage());
            }
//            db.execSQL(SQL_CREATE_MST_ID);
        }
        catch (Exception e) {
            Log.d(TAG, "onCreate: Table Create exception : " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion );

        if (oldVersion < newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME );
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS );
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED );
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  ECabDriverContract.MST_ID.TABLE );
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +  ECabDriverContract.SOC_MST_ENTRY.TABLE );
            onCreate(sqLiteDatabase);

        }
    }

    public boolean insertDriverBreakTime(String breakType, int count){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();

            contentValues.put(ECabDriverContract.DriverTimeBreak.BREAK_TYPE, breakType);
            contentValues.put(ECabDriverContract.DriverTimeBreak.BREAK_COUNT, count);

            long result = db.insert(ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME, null, contentValues);
            Log.d(TAG, "insertInspectorsData:db data inserted " + result);

        db.close();
        return true;
    }

    public void updateBreak(String breakType, int breakCount, int totalCount) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ECabDriverContract.DriverTimeBreak.BREAK_TYPE, breakType);
        contentValues.put(ECabDriverContract.DriverTimeBreak.BREAK_COUNT, breakCount);
        contentValues.put(ECabDriverContract.DriverTimeBreak.COLUMN_TOTAL_BREAK, totalCount);

        db.update(ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME, contentValues,
                ECabDriverContract.DriverTimeBreak.BREAK_TYPE + " = ? ",
                new String[]{String.valueOf(breakType)});

        db.close();
    }

    public boolean delBreak(){
        SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        try {
            result = db.delete(ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }
        return result > 0;
    }

    public boolean delDriverStatus(){
        SQLiteDatabase db = getWritableDatabase();
        int result = 0;
        try {
           result = db.delete(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }
        return result > 0;
    }

    @SuppressLint ("Range")
    public int getBreakTotalCnt() {

        SQLiteDatabase db = getReadableDatabase();
        int totalCount = 0;

        try {
            Cursor cursor = db.query(ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    totalCount = cursor.getInt(cursor.getColumnIndex(ECabDriverContract.DriverTimeBreak.COLUMN_TOTAL_BREAK));
                }
                cursor.close();
            }

        }
        catch (Exception e) {
            Log.d(TAG, "getPreviouBreakTime: exception " + e.getMessage());
        }

        db.close();
        return totalCount;
    }

    @SuppressLint ("Range")
    public int getBreakTypeCount(String breakType) {
        SQLiteDatabase db = getReadableDatabase();
        int breakCount = 0;

        try {
            Cursor cursor = db.query(ECabDriverContract.DriverTimeBreak.TABLE_BREAK_TIME,
                    null,
                    ECabDriverContract.DriverTimeBreak.BREAK_TYPE + " = ?",
                    new String[]{String.valueOf(breakType)},
                    null,
                    null,
                    null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    
                    breakCount = cursor.getInt(cursor.getColumnIndex(ECabDriverContract.DriverTimeBreak.BREAK_COUNT));
                }
                cursor.close();
            }

        }
        catch (Exception e) {
            Log.d(TAG, "getPreviouBreakTime: exception " + e.getMessage());
        }

        db.close();
        return breakCount;
    }


    public boolean insertDriverRideStatus(CustomerNotfication customerNotfication){
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }

        CustomerLocation fromLoc = customerNotfication.getFromLocation();
        CustomerLocation toLoc = customerNotfication.getToLocation();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_ID,customerNotfication.getUserId());
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_NAME,customerNotfication.getName());
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_PHONE,customerNotfication.getPhone());
        if(fromLoc!=null && fromLoc.getLatitude() != 0)
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LAT,fromLoc.getLatitude());
        if(fromLoc!=null && fromLoc.getLongitude() != 0)
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LONG,fromLoc.getLongitude());
        if(toLoc!=null && toLoc.getLatitude() != 0)
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LAT,toLoc.getLatitude());
        if(toLoc!=null && toLoc.getLongitude() != 0)
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LONG,toLoc.getLongitude());
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_DRIVER_PAGE,customerNotfication.getDriverPage());
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_RIDE_STATUS,customerNotfication.getRideStatus());
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_PASS_TRIP_MST_ID,customerNotfication.getPassengerTripMasterId());

        long result = db.insert(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS, null, contentValues);
        db.close();
       return result > 0;
    }

    public CustomerNotfication getRideStatus(/*String type*/){
        SQLiteDatabase db = getReadableDatabase();
        CustomerNotfication notfication = null;

        try {
/*           Cursor cursor = db.query(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS,null,
                   ECabDriverContract.DRIVER_RIDE_STATUS.COL_RIDE_STATUS + " = ? ", new String[]{String.valueOf(type)},
                   null,null,null);*/

            Cursor cursor = db.query(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS,null,null,null,
                    null,null,null);

           if(cursor != null) {
               if(cursor.moveToFirst()) {
                   notfication = new CustomerNotfication();

                   @SuppressLint ("Range")
                   String fromLat = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LAT));
                   @SuppressLint ("Range")
                   String fromLong = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_FROM_LONG));
                   @SuppressLint ("Range")
                   String toLat = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LAT));
                   @SuppressLint ("Range")
                   String toLong = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_TO_LONG));
                   @SuppressLint ("Range")
                   String name = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_NAME));
                   @SuppressLint ("Range")
                   String mobNum = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_PHONE));
                   @SuppressLint ("Range")
                   String rideStatus = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_RIDE_STATUS));
                   @SuppressLint ("Range")
                   String page = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_DRIVER_PAGE));
                   @SuppressLint ("Range")
                   String userId = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_ID));
                   @SuppressLint ("Range")
                   String passMstId = cursor.getString(cursor.getColumnIndex(ECabDriverContract.DRIVER_RIDE_STATUS.COL_PASS_TRIP_MST_ID));

                   CustomerLocation frmLoc = new CustomerLocation();
                   frmLoc.setLatitude(Double.parseDouble(fromLat));
                   frmLoc.setLongitude(Double.parseDouble(fromLong));
                   notfication.setFromLocation(frmLoc);

                   CustomerLocation toLoc = new CustomerLocation();
                   toLoc.setLatitude(Double.parseDouble(toLat));
                   toLoc.setLongitude(Double.parseDouble(toLong));
                   notfication.setToLocation(toLoc);

                   notfication.setName(name);
                   notfication.setPhone(mobNum);
                   notfication.setRideStatus(rideStatus);
                   notfication.setUserId(userId);
                   notfication.setDriverPage(page);
                   notfication.setPassengerTripMasterId(passMstId);
               }
               cursor.close();
           }
        }
        catch (Exception e) {
            Log.d(TAG, "getRideStatus: exc " + e.getMessage());
        }
        db.close();
        return notfication;
    }


    public void updateRideStatus(CustomerNotfication data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d(TAG, "updateRideStatus: "+data);
//        Log.d(TAG, "updateRideStatus: ");
        contentValues.put(ECabDriverContract.DRIVER_RIDE_STATUS.COL_RIDE_STATUS, data.getRideStatus());

        db.update(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS, contentValues,
                ECabDriverContract.DRIVER_RIDE_STATUS.COL_USER_ID + " = ? ",
                new String[]{String.valueOf(data.getUserId())});

        db.close();
    }

    public void insertDestination(Destination destination) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.delete(ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ECabDriverContract.TRIP_ENDED.COL_DEST_ADDRESS,destination.getDestination_addresses());
        contentValues.put(ECabDriverContract.TRIP_ENDED.COL_ORIGIN_ADDRESS,destination.getOrigin_addresses());
        contentValues.put(ECabDriverContract.TRIP_ENDED.COL_AMOUNT,destination.getAmount());
        contentValues.put(ECabDriverContract.TRIP_ENDED.COL_DISTANCE,destination.getDistance());
        contentValues.put(ECabDriverContract.TRIP_ENDED.COL_DURATION,destination.getDuration());

        long result = db.insert(ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED, null, contentValues);
        Log.d(TAG, "insertDestination: result " + result);
        db.close();
    }

    public Destination getDestination(){
        SQLiteDatabase db = getReadableDatabase();
        Destination destination = null;

        try {
            Cursor cursor = db.query(ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED, null, null, null,
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    destination = new Destination();
                    @SuppressLint ("Range")
                    String toAddress = cursor.getString(cursor.getColumnIndex(ECabDriverContract.TRIP_ENDED.COL_DEST_ADDRESS));
                    @SuppressLint ("Range")
                    String fromAddress = cursor.getString(cursor.getColumnIndex(ECabDriverContract.TRIP_ENDED.COL_ORIGIN_ADDRESS));
                    @SuppressLint ("Range")
                    String amount = cursor.getString(cursor.getColumnIndex(ECabDriverContract.TRIP_ENDED.COL_AMOUNT));
                    @SuppressLint ("Range")
                    String duration = cursor.getString(cursor.getColumnIndex(ECabDriverContract.TRIP_ENDED.COL_DURATION));
                    @SuppressLint ("Range")
                    String distance = cursor.getString(cursor.getColumnIndex(ECabDriverContract.TRIP_ENDED.COL_DISTANCE));

                    destination.setDestination_addresses(toAddress);
                    destination.setOrigin_addresses(fromAddress);
                    destination.setAmount(amount);
                    destination.setDuration(duration);
                    destination.setDistance(distance);
                }
                cursor.close();
            }
        }
        catch (Exception e) {
            Log.d(TAG, "getDestination: exception " + e.getMessage());
        }
        db.close();
        return destination;
    }

    public void deleteRideTable(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(ECabDriverContract.DRIVER_RIDE_STATUS.TABLE_RIDE_STATUS, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }
    }

    public void deleteDestTable() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(ECabDriverContract.TRIP_ENDED.TABLE_TRIP_ENDED, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }
    }

    // firbase notification purpose
    // inserting the values in to local db
    public boolean insertMstId(String mstId, String passId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ECabDriverContract.MST_ID.COL_DRVMST_ID,mstId);
        contentValues.put(ECabDriverContract.MST_ID.COL_PASSMST_ID,passId);

        long result = db.insert(ECabDriverContract.MST_ID.TABLE, null, contentValues);
        return result > 0;
    }

    // getting the mstId and checking wheter mst id exits or not
    // if exists show notification or info to user
    // else ignore the notification...
    public MstId getIds() {
        SQLiteDatabase db = getReadableDatabase();
        MstId mstId = new MstId();
        Cursor cursor = db.query(ECabDriverContract.MST_ID.TABLE, null,
                null, null,
                null, null, null);

        if(cursor != null) {
            if(cursor.moveToFirst()){
                do {
                    try {
                        @SuppressLint ("Range")
                        String pasMstId = cursor.getString(cursor.getColumnIndex(ECabDriverContract.MST_ID.COL_PASSMST_ID));
                        @SuppressLint ("Range")
                        String drvMstId = cursor.getString(cursor.getColumnIndex(ECabDriverContract.MST_ID.COL_DRVMST_ID));

                        mstId.setDrvMstId(drvMstId);
                        mstId.setPassMstId(pasMstId);
                    }
                    catch (Exception e) {
                        Log.d(TAG, "storeLocationList: exception " +e.getMessage());
                    }
                }
                while (cursor.moveToNext());
                cursor.close();
            }
        }
        db.close();
        return mstId;
    }

    // delete the record or table when driver logouts
    public void deleteMstId() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(ECabDriverContract.MST_ID.TABLE, null, null);
//            db.delete(ECabDriverContract.MST_ID.TABLE,
//                    ECabDriverContract.MST_ID.TABLE + " = ? ", new String[]{String.valueOf(id)});
        }
        catch (Exception e) {
            Log.d(TAG, "insertMstId: exception " + e.getMessage());
        }
        db.close();
    }

    public boolean insertSoc(SocValues socValues) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ECabDriverContract.SOC_MST_ENTRY.COL_SOC1, socValues.getSoc1());
        contentValues.put(ECabDriverContract.SOC_MST_ENTRY.COL_SOC2, socValues.getSoc2());
        contentValues.put(ECabDriverContract.SOC_MST_ENTRY.COL_SOC3, socValues.getSoc3());

        long result = db.insert(ECabDriverContract.SOC_MST_ENTRY.TABLE, null, contentValues);
        return result > 0;
    }

    public SocValues getSoc(){
        SQLiteDatabase db = getReadableDatabase();
        SocValues soc = null;

        try {
            Cursor cursor = db.query(ECabDriverContract.SOC_MST_ENTRY.TABLE, null, null, null,
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    soc = new SocValues();
                    @SuppressLint ("Range")
                    int soc1 = cursor.getInt(cursor.getColumnIndex(ECabDriverContract.SOC_MST_ENTRY.COL_SOC1));
                    @SuppressLint ("Range")
                    int soc2 = cursor.getInt(cursor.getColumnIndex(ECabDriverContract.SOC_MST_ENTRY.COL_SOC2));
                    @SuppressLint ("Range")
                    int soc3 = cursor.getInt(cursor.getColumnIndex(ECabDriverContract.SOC_MST_ENTRY.COL_SOC3));

                    soc.setSoc1(soc1);
                    soc.setSoc2(soc2);
                    soc.setSoc3(soc3);
                }
                cursor.close();
            }
        }
        catch (Exception e) {
            Log.d(TAG, "getDestination: exception " + e.getMessage());
        }
        db.close();
        return soc;
    }

    public void delSoc() {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.delete(ECabDriverContract.SOC_MST_ENTRY.TABLE, null, null);
        }
        catch (Exception e) {
            Log.d(TAG, "insertDriverBreakTime: exception " + e.getMessage());
        }
    }
}
