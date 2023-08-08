package sgs.env.ecabsdriver.service;

import static sgs.env.ecabsdriver.util.ECabsApp.isBroadcastChatScreenVisible;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import sgs.env.ecabsdriver.model.AppConfig;
import sgs.env.ecabsdriver.model.FirestoreBroadcastChatModel;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.model.TripDataModel;
import sgs.env.ecabsdriver.model.TripInfo;
import sgs.env.ecabsdriver.reciver.FirebaseBroadcastReceiver;
import sgs.env.ecabsdriver.reciver.FirestoreBroadcastReceiver;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class RetrieveFirestoreData extends Service {
    private final String TAG = "RetrieveFirestoreData";
    public static boolean isServiceRunning;
    ArrayList<FirestoreChatModel> messageList = new ArrayList<>();
    ArrayList<FirestoreChatModel> allMessageList = new ArrayList<>();
    CollectionReference broadCastCollectionRefrencce, collectionReference;
    ArrayList<FirestoreBroadcastChatModel> broadcastMessageList = new ArrayList<>();
    ArrayList<FirestoreBroadcastChatModel> broadcastAllMessageList = new ArrayList<>();
    ArrayList<String> acknowledgeDriverList = new ArrayList<String>();
    ArrayList<String> ResponseDriverList = new ArrayList<String>();
    ArrayList<String> InformationDriverList = new ArrayList<String>();
    ArrayList<String> ReadableMessageDriverList = new ArrayList<String>();
    ArrayList<TripInfo> tripInfos = new ArrayList<>();
    private ListenerRegistration removeCollectionReference,
            removebroadCastCollectionRefrence, removeInformationCollectionReference;

    CollectionReference scheduleTripCollectionRef;

   public static DocumentReference appConfigRef, logoutDocumentRef,tripDataRef;

    private ListenerRegistration removeScheduleTripCollectionRef, appConfigEventListner,tripDataEventListner, logoutAppEventListner;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate called");
        getFirestoreDatabaseRefrence();
        isServiceRunning = true;
    }

    private void getFirestoreDatabaseRefrence() {

        if (SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "") != null && !SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").toString().isEmpty()) {

            scheduleTripCollectionRef = FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance()
                    .get(AppConstants.DRIVER_ID, "")).collection("scheduled-trips");

            removeScheduleTripCollectionRef = scheduleTripCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    tripInfos = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {

                            Timestamp timestamp = (Timestamp) doc.getTimestamp("scheduledAt");
                            long milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
                            Date netDate = new Date(milliseconds);
                            Date currentDate = new Date();

                            if (netDate.equals(currentDate) || netDate.after(currentDate)) {

                                tripInfos.add(new TripInfo(doc.getString("fromAddress"),
                                        doc.getString("passengerName"),
                                        doc.getString("toAddress"),
                                        doc.getString("status"),
                                        doc.getString("driverName"),
                                        doc.getString("driverPhone"),
                                        doc.getDouble("initialDistance"),
                                        doc.getString("passengerPhone"),
                                        doc.getTimestamp("scheduledAt"),
                                        doc.getString("driverPropic"),
                                        Objects.requireNonNull(doc.getLong("initialPrice")).intValue()));
                            } else {
                                if (!doc.getId().isEmpty()) {
                                    scheduleTripCollectionRef.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error deleting document", e);
                                                }
                                            });
                                }
                            }
                        }
                    }

                    if (tripInfos.size() > 0) {
                        if (tripInfos.get(0).getStatus().equals("driverassigned")) {
                            Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                            broadcastIntent.putParcelableArrayListExtra(AppConstants.scheduleTripInfo, tripInfos);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }
            });

            collectionReference = FirebaseFirestore.getInstance().collection("chat_service").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                    .collection("chat_list").document("admin").collection("msg");

            removeCollectionReference = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    messageList = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {

                            if (messageList != null) {
                                addMessageInfoData(doc);

                            }

                        }
                    }

                    Log.e("messageList", String.valueOf(messageList.size()));
                    assert messageList != null;
                    if (messageList.size() > 0) {
                        allMessageList = new ArrayList<>();
                        allMessageList.addAll(messageList);

                        Log.e("allMessageList", String.valueOf(allMessageList.size()));
                        Collections.sort(allMessageList, new Comparator<FirestoreChatModel>() {
                            @Override
                            public int compare(FirestoreChatModel r1, FirestoreChatModel r2) {
                                try {
                                    return Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy").parse(r1.getTime())).compareTo(new SimpleDateFormat("dd-MM-yyyy").parse(r2.getTime()));
                                } catch (ParseException e) {
                                    return 0;
                                }
                            }
                        });

                        for (int i = 0; i < allMessageList.size(); i++) {

                            int finalI = i;
                            collectionReference.document(allMessageList.get(i).getDocId())
                                    .collection("messageReaded").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }

                                            if (value != null) {
                                                ReadableMessageDriverList = new ArrayList<>();

                                                for (QueryDocumentSnapshot doc : value) {
                                                    ReadableMessageDriverList.add(doc.getString("driverId"));
                                                }

                                                if (ReadableMessageDriverList.size() > 0) {
                                                    if (!ReadableMessageDriverList.contains(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").trim())) {

                                                        if (!allMessageList.get(finalI).getName().toString().trim()
                                                                .equals(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, "").trim())) {
                                                            Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                            broadcastIntent.putExtra(AppConstants.allMessageList, allMessageList.get(finalI));
                                                            sendBroadcast(broadcastIntent);

                                                        }
                                                    }
                                                } else {
                                                    if (!allMessageList.get(finalI).getName().toString().trim()
                                                            .equals(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, "").trim())) {
                                                        Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                        broadcastIntent.putExtra(AppConstants.allMessageList, allMessageList.get(finalI));
                                                        sendBroadcast(broadcastIntent);
                                                    }
                                                }
                                            }

                                        }
                                    });

                        }

                        //     }

                    }

                }
            });

            broadCastCollectionRefrencce = FirebaseFirestore.getInstance().collection("broadcast_service")
                    .document("admin").collection("all_driver");

            FirebaseFirestore.getInstance().collection("broadcast_service")
                    .document("admin").collection("all_driver").limit(5)
                    .orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }
                            broadcastMessageList = new ArrayList<>();
                            if (value != null) {
                                for (QueryDocumentSnapshot doc : value) {

                                    if (broadcastMessageList != null) {
                                        addBroadcastMessageInfoData(doc);

                                    }

                                }
                            }

                            assert broadcastMessageList != null;
                            if (broadcastMessageList.size() > 0) {
                                broadcastAllMessageList = new ArrayList<>();
                                broadcastAllMessageList.addAll(broadcastMessageList);

                                Collections.sort(broadcastAllMessageList, new Comparator<FirestoreBroadcastChatModel>() {
                                    @Override
                                    public int compare(FirestoreBroadcastChatModel r1, FirestoreBroadcastChatModel r2) {
                                        try {
                                            return Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy").parse(r1.getTime())).compareTo(new SimpleDateFormat("dd-MM-yyyy").parse(r2.getTime()));
                                        } catch (ParseException e) {
                                            return 0;
                                        }
                                    }
                                });

                                if (!isBroadcastChatScreenVisible()) {
                                    for (int i = 0; i < broadcastAllMessageList.size(); i++) {

                                        if (broadcastAllMessageList.get(i).getServiceType().equals("Information")) {
                                            int finalI = i;
                                            broadCastCollectionRefrencce.document(broadcastAllMessageList.get(i).getDoc_id())
                                                    .collection("Information").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot value,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Log.w(TAG, "Listen failed.", e);
                                                                return;
                                                            }
                                                            if (value != null) {
                                                                InformationDriverList = new ArrayList<>();

                                                                for (QueryDocumentSnapshot doc : value) {
                                                                    InformationDriverList.add(doc.getString("driverId"));
                                                                }

                                                                if (InformationDriverList.size() > 0) {
                                                                    if (!InformationDriverList.contains(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").trim())) {

                                                                        Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                        broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI).getServiceType());
                                                                        broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI));
                                                                        sendBroadcast(broadcastIntent);
                                                                    }
                                                                } else {

                                                                    Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                    broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI).getServiceType());
                                                                    broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI));
                                                                    sendBroadcast(broadcastIntent);
                                                                }
                                                            }

                                                        }
                                                    });
                                        }

                                        if (broadcastAllMessageList.get(i).getServiceType().equals("Acknowledgment")) {
                                            int finalI1 = i;
                                            broadCastCollectionRefrencce.document(broadcastAllMessageList.get(i).getDoc_id())
                                                    .collection("Acknowledgment").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot value,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Log.w(TAG, "Listen failed.", e);
                                                                return;
                                                            }
                                                            if (value != null) {
                                                                acknowledgeDriverList = new ArrayList<>();

                                                                for (QueryDocumentSnapshot doc : value) {
                                                                    acknowledgeDriverList.add(doc.getString("driverId"));
                                                                }
                                                                if (acknowledgeDriverList.size() > 0) {
                                                                    if (!acknowledgeDriverList.contains(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").trim())) {
                                                                        Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                        broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI1).getServiceType());
                                                                        broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI1));
                                                                        sendBroadcast(broadcastIntent);
                                                                    }
                                                                } else {
                                                                    Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                    broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI1).getServiceType());
                                                                    broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI1));
                                                                    sendBroadcast(broadcastIntent);
                                                                }
                                                            }

                                                        }
                                                    });
                                        }

                                        if (broadcastAllMessageList.get(i).getServiceType().equals("Response")) {
                                            int finalI2 = i;
                                            broadCastCollectionRefrencce.document(broadcastAllMessageList.get(i).getDoc_id())
                                                    .collection("Response").addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable QuerySnapshot value,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Log.w(TAG, "Listen failed.", e);
                                                                return;
                                                            }
                                                            if (value != null) {
                                                                ResponseDriverList = new ArrayList<>();

                                                                for (QueryDocumentSnapshot doc : value) {
                                                                    ResponseDriverList.add(doc.getString("driverId"));
                                                                }
                                                                if (ResponseDriverList.size() > 0) {
                                                                    if (!ResponseDriverList.contains(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, "").trim())) {
                                                                        Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                        broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI2).getServiceType());
                                                                        broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI2));
                                                                        sendBroadcast(broadcastIntent);
                                                                    }
                                                                } else {
                                                                    Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                                                    broadcastIntent.putExtra(AppConstants.messageType, broadcastAllMessageList.get(finalI2).getServiceType());
                                                                    broadcastIntent.putExtra(AppConstants.broadcastAllMessageList, broadcastAllMessageList.get(finalI2));
                                                                    sendBroadcast(broadcastIntent);
                                                                }
                                                            }

                                                        }
                                                    });
                                        }
                                    }
                                }
                            }

                        }
                    });

            //New Trip Detail
            tripDataRef =  FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                    .collection("running-trip").document("tripInfo");

            tripDataEventListner = tripDataRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    String PassangerTripMasterID;
                    if(value!=null){
                        PassangerTripMasterID = value.getString("passengerTripMasterId");
                       if(PassangerTripMasterID!=null && !PassangerTripMasterID.isEmpty()){
                           Log.e("PassangerTripMasterID",PassangerTripMasterID);
                        }else {
                           Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                           broadcastIntent.putExtra(AppConstants.TripCancel, AppConstants.TripCancel);
                           sendBroadcast(broadcastIntent);

                       }
                    }
                }
            });

            //Logout From Firestore
            logoutDocumentRef = FirebaseFirestore.getInstance().collection("driver").document(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));

            logoutAppEventListner = logoutDocumentRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        Boolean loggedIn = value.getBoolean("loggedIn");
                        if (loggedIn != null && !loggedIn) {
                            logoutFromFirebase();
                        } else if (loggedIn == null) {
                            logoutFromFirebase();
                        }
                    }
                }
            });
        }

        //Retrive App Config data
        appConfigRef = FirebaseFirestore.getInstance().collection("settings").document("AppConfig");
        appConfigEventListner = appConfigRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());

                    AppConfig appConfig = snapshot.toObject(AppConfig.class);
                    if (appConfig != null) {
                        try {
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            if (pInfo != null && appConfig.getSwVersionConfig().getMinDriverAppVersion() != null
                                    && !appConfig.getSwVersionConfig().getMinDriverAppVersion().toString().isEmpty()) {
                                Log.e("versionCode", String.valueOf(pInfo.versionCode));
                                Log.e("MinDriverAppVersion", String.valueOf(appConfig.getSwVersionConfig().getMinDriverAppVersion()));

                                if (pInfo.versionCode < appConfig.getSwVersionConfig().getMinDriverAppVersion()) {
                                    SharedPrefsHelper.getInstance().save(AppConstants.AppURL, appConfig.getSwVersionConfig().getDriverAppUrl());
                                    Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirestoreBroadcastReceiver.class);
                                    broadcastIntent.putExtra(AppConstants.SwVersionConfig, AppConstants.SwVersionConfig);
                                    sendBroadcast(broadcastIntent);
                                }
                            }


                            if (appConfig.getSearchConfig() != null) {
                                SharedPrefsHelper.getInstance().save(AppConstants.searchFrequency,
                                        appConfig.getSearchConfig().getSearchFrequency());
                                SharedPrefsHelper.getInstance().save(AppConstants.seacrhMinDistance,
                                        appConfig.getSearchConfig().getSeacrhMinDistance());
                                SharedPrefsHelper.getInstance().save(AppConstants.MinSoc,
                                        String.valueOf(appConfig.getSearchConfig().getMinSoc()));

                            }
                        } catch (PackageManager.NameNotFoundException exception) {
                            exception.printStackTrace();
                        }

                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });



 }

    private void addMessageInfoData(QueryDocumentSnapshot doc) {
        Timestamp timestamp = (Timestamp) doc.getTimestamp("time");
        long milliseconds = 0;
        if (timestamp != null) {
            milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
        }
        Date netDate = new Date(milliseconds);
        Date currentDate = new Date();
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat output2 = new SimpleDateFormat("dd-MM-yyyy");

        if (output2.format(netDate).equals(output2.format(currentDate)) && !String.valueOf(doc.getString("Name")).equals(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME))) {
            messageList.add(new FirestoreChatModel(
                    doc.getId(),
                    doc.getString("message"),
                    doc.getString("Name"),
                    doc.getString("driverName"),
                    doc.getString("driverId"),
                    doc.getString("adminId"),
                    doc.getString("messageType"),
                    output.format(netDate)));

        }

    }

    private void addBroadcastMessageInfoData(QueryDocumentSnapshot doc) {
        Timestamp timestamp = (Timestamp) doc.getTimestamp("time");
        long milliseconds = 0;
        if (timestamp != null) {
            milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
        }
        Date netDate = new Date(milliseconds);
        Date currentDate = new Date();
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");

        if (output.format(netDate).equals(output.format(currentDate))) {
            broadcastMessageList.add(new FirestoreBroadcastChatModel(
                    doc.getId(),
                    doc.getString("message"),
                    doc.getString("Name"),
                    doc.getString("adminId"),
                    output.format(netDate),
                    doc.getString("serviceType"),
                    doc.getString("messageType")));

        }
    }

    private void logoutFromFirebase() {
        Intent broadcastIntent = new Intent(RetrieveFirestoreData.this, FirebaseBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand called");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy called");
        isServiceRunning = false;
        if (removeCollectionReference != null) {
            removeCollectionReference.remove();
            removeCollectionReference = null;
        }
        if (removebroadCastCollectionRefrence != null) {
            removebroadCastCollectionRefrence.remove();
            removebroadCastCollectionRefrence = null;
        }
        if (removeInformationCollectionReference != null) {
            removeInformationCollectionReference.remove();
            removeInformationCollectionReference = null;
        }

        if (removeScheduleTripCollectionRef != null) {
            removeScheduleTripCollectionRef.remove();
            removeScheduleTripCollectionRef = null;
        }

        if (appConfigEventListner != null) {
            appConfigEventListner.remove();
            appConfigEventListner = null;
        }

        if (logoutAppEventListner != null) {
            logoutAppEventListner.remove();
            logoutAppEventListner = null;
        }

        if(tripDataEventListner!=null){
            tripDataEventListner.remove();
            tripDataEventListner = null;
        }
        //appConfigRef, logoutDocumentRef,tripDataRef
        stopForeground(true);

        super.onDestroy();
    }

}
