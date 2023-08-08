package sgs.env.ecabsdriver.activity;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.record_view.OnBasketAnimationEnd;
import com.record_view.OnRecordClickListener;
import com.record_view.OnRecordListener;
import com.record_view.RecordButton;
import com.record_view.RecordPermissionHandler;
import com.record_view.RecordView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import sgs.env.ecabsdriver.R;
import sgs.env.ecabsdriver.adapter.MessageListAdapter;
import sgs.env.ecabsdriver.model.FirestoreChatModel;
import sgs.env.ecabsdriver.util.AppConstants;
import sgs.env.ecabsdriver.util.AudioRecorder;
import sgs.env.ecabsdriver.util.PermissionUtils;
import sgs.env.ecabsdriver.util.SharedPrefsHelper;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    TextView button_chat_send;
    EditText input_message;
    RecyclerView chatMessageList;
    MessageListAdapter messageListAdapter;
    CollectionReference collectionReference;
    private ListenerRegistration removeCollectionReference;
    List<FirestoreChatModel> messageList = new ArrayList<>();
    List<FirestoreChatModel> allMessageList = new ArrayList<>();
    ArrayList<String> ReadableMessageDriverList = new ArrayList<String>();
    FirebaseStorage storage;
    StorageReference storageReference;
    private static String fileName = null;
    ProgressDialog progressDialog;
    RelativeLayout pickFileRelative;
    Uri uri, filePath;
    private final int PICK_IMAGE_REQUEST = 22, PICK_AUDIO_VIDEO = 5, RequestPermissionCode = 3;
    AlertDialog alertDialog;
    MediaPlayer mediaPlayer;
    private AudioRecorder audioRecorder;
    private File recordFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inIt();
        retriveDataFromFirestore();
    }

    private void inIt() {

        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Envi Chat");

        button_chat_send = findViewById(R.id.button_chat_send);
        input_message = findViewById(R.id.input_message);
        chatMessageList = findViewById(R.id.chatMessageList);
        pickFileRelative = findViewById(R.id.pickFileRelative);
        RecordView recordView = findViewById(R.id.record_view);
        final RecordButton recordButton = findViewById(R.id.record_button);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        button_chat_send.setOnClickListener(v -> {
            if (!input_message.getText().toString().trim().isEmpty()) {
                sentMessage(AppConstants.MessageTypeText, input_message.getText().toString().trim());

            }
        });

        pickFileRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    pickFile();
                } else {
                    CheakPermissions();
                }
            }
        });

        //Audio Recording Code
        audioRecorder = new AudioRecorder();
        recordButton.setRecordView(recordView);
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                Log.d("RecordButton", "RECORD BUTTON CLICKED");
            }
        });

        //Cancel Bounds is when the Slide To Cancel text gets before the timer . default is 8
        recordView.setCancelBounds(8);

        recordView.setSmallMicColor(Color.parseColor("#c2185b"));

        //prevent recording under one Second
        recordView.setLessThanSecondAllowed(false);

        recordView.setSlideToCancelText("Slide To Cancel");

        recordView.setCustomSounds(R.raw.record_start, R.raw.record_finished, 0);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {

                try {
                    recordFile = null;
                    recordFile = new File(getExternalCacheDir(), UUID.randomUUID().toString() + ".mp3");
                    fileName = recordFile.getAbsolutePath();
                    audioRecorder.start(fileName);
                    input_message.setVisibility(View.GONE);
                    button_chat_send.setVisibility(View.GONE);
                    pickFileRelative.setVisibility(View.GONE);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("RecordView", "onStart");
                //   Toast.makeText(ChatActivity.this, "OnStartRecord", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                stopRecording();
                // Toast.makeText(ChatActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime, boolean limitReached) {
                stopRecording();
                String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish" + " Limit Reached? " + limitReached);
                Log.d("RecordTime", time);
                uploadToFirestoreAudio();
            }

            @Override
            public void onLessThanSecond() {
                stopRecording();
                Toast.makeText(ChatActivity.this, "OnLessThanSecond", Toast.LENGTH_SHORT).show();
                Log.d("RecordView", "onLessThanSecond");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

        recordView.setRecordPermissionHandler(new RecordPermissionHandler() {
            @Override
            public boolean isPermissionGranted() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                boolean recordPermissionAvailable = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.RECORD_AUDIO) == PERMISSION_GRANTED;
                if (recordPermissionAvailable) {
                    return true;
                }

                ActivityCompat.
                        requestPermissions(ChatActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                0);

                return false;

            }
        });
    }

    private void retriveDataFromFirestore() {
        collectionReference = FirebaseFirestore.getInstance().collection("chat_service").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID))
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

                assert messageList != null;
                if (messageList.size() > 0) {
                    allMessageList = new ArrayList<>();
                    allMessageList.addAll(messageList);

                    Collections.sort(allMessageList, new Comparator<FirestoreChatModel>() {
                        @Override
                        public int compare(FirestoreChatModel r1, FirestoreChatModel r2) {
                            try {
                                return Objects.requireNonNull(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(r1.getTime())).compareTo(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(r2.getTime()));
                            } catch (ParseException e) {
                                return 0;
                            }
                        }
                    });
                    UpdateasRead();

                    messageListAdapter = new MessageListAdapter(allMessageList);
                    chatMessageList.setHasFixedSize(true);
                    chatMessageList.setAdapter(messageListAdapter);
                    chatMessageList.scrollToPosition(allMessageList.size() - 1);
                    adapterItemClickListner();
                }

            }
        });
    }

    private void UpdateasRead() {
        for (int i = 0; i < allMessageList.size(); i++) {
            if (!allMessageList.get(i).getName().toString().trim()
                    .equals(SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, "").trim())) {
                messageReaded(allMessageList.get(i).getDocId());
            }

        }
    }

    private void messageReaded(String docId) {
        Map<String, Object> data = new HashMap<>();

        data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
        data.put("time", new Date());
        Map<String, Object> dummyMap = new HashMap<>();

        DocumentReference df = FirebaseFirestore.getInstance().collection("chat_service").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""))
                .collection("chat_list").document("admin").collection("msg")
                .document(docId.trim());
        df.collection("messageReaded").add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
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

    private void stopRecording() {
        audioRecorder.stop();
        input_message.setVisibility(View.VISIBLE);
        button_chat_send.setVisibility(View.VISIBLE);
        pickFileRelative.setVisibility(View.VISIBLE);

    }

    private void CheakPermissions() {
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) ||
                !(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, RequestPermissionCode);

        }
    }

    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public void pickFile() {
        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.selectfile_popup, null);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.MyDialogTheme);

        builder.setView(layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        if (!isFinishing()) {
            alertDialog.show();
        }

        CardView selectImage = layout.findViewById(R.id.selectImage);
        CardView selectVideo = layout.findViewById(R.id.selectVideo);
        CardView cancelBtn = layout.findViewById(R.id.cancelBtn);

        selectImage.setOnClickListener(v -> {
            alertDialog.dismiss();
            selectImage();
        });

        selectVideo.setOnClickListener(v -> {
            alertDialog.dismiss();
            selectVideo();
        });

        cancelBtn.setOnClickListener(v -> alertDialog.dismiss());
    }

    // Select Image method
    private void selectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void selectVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_AUDIO_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            Log.e("filePath", String.valueOf(filePath));
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                uploadImage(bitmap);

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        } else if (requestCode == PICK_AUDIO_VIDEO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            upload_VideoFirestore();
        }
    }

    // UploadImage method
    private void uploadImage(Bitmap bitmap) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        StorageReference mountainsRef = storageReference.child("Files/chatImage/").child("images" + calendar.getTimeInMillis() + ".png");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(ChatActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.d("hivu", taskSnapshot.getUploadSessionUri() + "");
                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        // Got the download URL for 'users/me/profile.png'
                        sentMessage(AppConstants.MessageTypeImage, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    // UploadVideo method
    public void upload_VideoFirestore() {

        if (uri != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            // save the selected video in Firebase storage
            final StorageReference reference = FirebaseStorage.getInstance().getReference("Files/chatVideo/" + System.currentTimeMillis() + "." + getfiletype(uri));
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // get the link of video
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            // Got the download URL for 'users/me/profile.png'
                            sentMessage(AppConstants.MessageTypeVideo, uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Error, Image not uploaded
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ChatActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                // Progress Listener for loading
                // percentage on the dialog box
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    // show the progress bar
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                }
            });
        }
    }

    // UploadAudio method
    private void uploadToFirestoreAudio() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();
        Uri file = Uri.fromFile(new File(fileName));
        StorageReference mountainsRef = storageReference.child("Files/chatAudio/" + file.getLastPathSegment());
        ;
        UploadTask uploadTask = mountainsRef.putFile(file, metadata);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                //progressBar.setProgress(progressInt);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                System.out.println("Upload Failed! Please try again.");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        // Got the download URL for 'users/me/profile.png'
                        Log.e("DownloadUrl===>", uri.toString());
                        sentMessage(AppConstants.MessageTypeAudio, uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    private String getfiletype(Uri audiouri) {
        ContentResolver r = getContentResolver();
        // get the file type ,in this case its mp4
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(r.getType(audiouri));
    }

    private void sentMessage(String messageType, String message) {
        if (PermissionUtils.isNetworkConnected(this)) {
            Map<String, Object> data = new HashMap<>();
            data.put("message", message);
            data.put("Name", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
            data.put("driverName", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_NAME, ""));
            data.put("driverId", SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID, ""));
            data.put("adminId", "");
            data.put("messageType", messageType);
            data.put("time", new Date());

            Map<String, Object> dummyMap = new HashMap<>();
            DocumentReference df = FirebaseFirestore.getInstance().collection("chat_service").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID))
                    .collection("chat_list").document("admin");
            df.set(dummyMap);

            df.collection("msg").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

            Map<String, Object> dummyMap1 = new HashMap<>();
            DocumentReference df1 = FirebaseFirestore.getInstance().collection("chat_service").document("admin")
                    .collection("chat_list").document("drv_" + SharedPrefsHelper.getInstance().get(AppConstants.DRIVER_ID));
            df1.set(dummyMap1);
            df1.collection("msg").add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

            input_message.getText().clear();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }

    private void adapterItemClickListner() {
        messageListAdapter.setItemClickListener(new MessageListAdapter.OnItemClickListener() {
            @Override
            public void onImageMessageItemClick(FirestoreChatModel message) {
                onFileMessageClicked(message);
            }

            @Override
            public void onAudioMessageItemClick(FirestoreChatModel message) {
                showMediaPlayerDialog(message);
            }

            @Override
            public void onVideoMessageItemClick(FirestoreChatModel message) {
                onFileMessageClicked(message);
            }
        });
    }

    private void onFileMessageClicked(FirestoreChatModel message) {

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

    private void showMediaPlayerDialog(FirestoreChatModel message) {

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
                            if (mediaPlayer != null) {
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
    protected void onPause() {
        super.onPause();
        if (audioRecorder != null) {
            stopRecording();
        }
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

        if (audioRecorder != null) {
            stopRecording();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}