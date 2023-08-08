package sgs.env.ecabsdriver.activity;

import static sgs.env.ecabsdriver.util.ECabsApp.BroadcastChatScreenPaused;
import static sgs.env.ecabsdriver.util.ECabsApp.BroadcastChatScreenResumed;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.adapter.BroadcastMessageListAdapter;
import sgs.env.ecabsdriver.model.FirestoreBroadcastChatModel;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class BroadcastChatActivity extends AppCompatActivity {
    private static final String TAG = "BroadcastChatActivity";
    RecyclerView chatMessageList;
    BroadcastMessageListAdapter BroadcastMessageListAdapter;
    CollectionReference collectionReference;
    private ListenerRegistration removeCollectionReference;
    List<FirestoreBroadcastChatModel> BroadcastMessageList = new ArrayList<>();
    List<FirestoreBroadcastChatModel> BroadcastAllMessageList = new ArrayList<>();
    FirebaseStorage storage;
    StorageReference storageReference;
    MediaPlayer mediaPlayer;
    ArrayList<String> InformationDriverList = new ArrayList<String>();
    ArrayList<String> acknowledgeDriverList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_chat);

        inIt();
        retriveDataFromFirestore();
    }

    private void inIt() {
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Envi Broadcast");

        chatMessageList = findViewById(R.id.chatMessageList);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }

    private void retriveDataFromFirestore() {
        collectionReference = FirebaseFirestore.getInstance().collection("broadcast_service")
                .document("admin").collection("all_driver");

        removeCollectionReference = collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                BroadcastMessageList = new ArrayList<>();
                if (value != null) {
                    for (QueryDocumentSnapshot doc : value) {

                        if (BroadcastMessageList != null) {

                            addBroadcastMessageInfoData(doc);
                        }

                    }
                }

                assert BroadcastMessageList != null;
                if (BroadcastMessageList.size() > 0) {
                    BroadcastAllMessageList = new ArrayList<>();
                    BroadcastAllMessageList.addAll(BroadcastMessageList);

                    Collections.sort(BroadcastAllMessageList, new Comparator<FirestoreBroadcastChatModel>() {
                        @Override
                        public int compare(FirestoreBroadcastChatModel r1, FirestoreBroadcastChatModel r2) {
                            try {
                                return Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(r1.getTime())).compareTo(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(r2.getTime()));
                            } catch (ParseException e) {
                                return 0;
                            }
                        }
                    });

                    UpdateasRead();

                    BroadcastMessageListAdapter = new BroadcastMessageListAdapter(BroadcastAllMessageList);
                    chatMessageList.setHasFixedSize(true);
                    chatMessageList.setAdapter(BroadcastMessageListAdapter);
                    chatMessageList.scrollToPosition(BroadcastAllMessageList.size() - 1);
                    adapterItemClickListner();
                }

            }
        });
    }

    private void UpdateasRead() {
        for (int i = 0; i < BroadcastAllMessageList.size(); i++) {

            if (BroadcastAllMessageList.get(i).getServiceType().equals("Information")) {
                int finalI = i;
                collectionReference.document(BroadcastAllMessageList.get(i).getDoc_id())
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
                                              messageReaded("Information",BroadcastAllMessageList.get(finalI).getDoc_id());
                                        }
                                    } else {
                                        messageReaded("Information",BroadcastAllMessageList.get(finalI).getDoc_id());
                                    }
                                }

                            }
                        });
            }

            if (BroadcastAllMessageList.get(i).getServiceType().equals("Acknowledgment")) {
                int finalI1 = i;
                collectionReference.document(BroadcastAllMessageList.get(i).getDoc_id())
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
                                            messageReaded("Acknowledgment", BroadcastAllMessageList.get(finalI1).getDoc_id());
                                        }
                                    } else {
                                        messageReaded("Acknowledgment", BroadcastAllMessageList.get(finalI1).getDoc_id());
                                    }
                                }

                            }
                        });
            }

        }
    }

    private void messageReaded(String serviceType, String doc_id) {
        Map<String, Object> data = new HashMap<>();
        data.put("Name", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
        data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
        data.put("serviceType", serviceType);
        data.put("time", new Date());
        Map<String, Object> dummyMap = new HashMap<>();

        DocumentReference df = FirebaseFirestore.getInstance().collection("broadcast_service")
                .document("admin").collection("all_driver")
                .document(doc_id);
        df.collection(serviceType).add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    private void addBroadcastMessageInfoData(QueryDocumentSnapshot doc) {
        Timestamp timestamp = (Timestamp) doc.getTimestamp("time");
        long milliseconds = 0;
        if (timestamp != null) {
            milliseconds = timestamp.getSeconds() * 1000 + timestamp.getNanoseconds() / 1000000;
        }
        Date netDate = new Date(milliseconds);
        Date currentDate = new Date();
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        BroadcastMessageList.add(new FirestoreBroadcastChatModel(
                doc.getId(),
                doc.getString("message"),
                doc.getString("Name"),
                doc.getString("adminId"),
                output.format(netDate),
                doc.getString("serviceType"),
                doc.getString("messageType")));
    }

    private void adapterItemClickListner() {
        BroadcastMessageListAdapter.setItemClickListener(new BroadcastMessageListAdapter.OnItemClickListener() {
            @Override
            public void onImageMessageItemClick(FirestoreBroadcastChatModel message) {
                onFileMessageClicked(message);
            }

            @Override
            public void onAudioMessageItemClick(FirestoreBroadcastChatModel message) {
                showMediaPlayerDialog(message);
            }

            @Override
            public void onVideoMessageItemClick(FirestoreBroadcastChatModel message) {
                onFileMessageClicked(message);
            }
        });
    }

    private void onFileMessageClicked(FirestoreBroadcastChatModel message) {

        if (message.getMessageType().equals(AppConstants.MessageTypeImage)) {
            Intent i = new Intent(this, PhotoViewerActivity.class);
            i.putExtra("url", message.getMessage());
            startActivity(i);
        } else if (message.getMessageType().equals(AppConstants.MessageTypeVideo)) {
            Intent intent = new Intent(this, MediaPlayerActivity.class);
            intent.putExtra("url", message.getMessage());
            startActivity(intent);
        }
    }

    private void showMediaPlayerDialog(FirestoreBroadcastChatModel message) {

        Drawable playButton = ContextCompat.getDrawable(this, R.drawable.ic_baseline_play_arrow_24);
        Drawable pauseButton = ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_24);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_audio_play);

        ImageView buttonPlayPause = dialog.findViewById(R.id.playButton);
        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);
        ProgressBar progress_loader = dialog.findViewById(R.id.progress_loader);

        progressBar.setProgress(0);
        buttonPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setImageDrawable(playButton);
            } else {
                mediaPlayer.start();
                buttonPlayPause.setImageDrawable(pauseButton);
            }
        });

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(message.getMessage());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progress_loader.setVisibility(View.GONE);
                    buttonPlayPause.setImageDrawable(pauseButton);
                    mediaPlayer.start();
                    progressBar.setMax(mediaPlayer.getDuration() / 1000);
                    final Handler mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer!=null) {
                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                progressBar.setProgress(mCurrentPosition);
                                Log.e("progress", "" + mCurrentPosition);
                            }
                            if (progressBar.getProgress() == progressBar.getMax()) {
                                mHandler.removeCallbacks(this);
                            }
                            mHandler.postDelayed(this, 1000);
                        }
                    });
                }
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                buttonPlayPause.setImageDrawable(playButton);
                mediaPlayer.stop();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setOnDismissListener(dialog1 -> {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        });

        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BroadcastChatScreenResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BroadcastChatScreenPaused();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (removeCollectionReference != null) {
            removeCollectionReference.remove();
            removeCollectionReference = null;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}