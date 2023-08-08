package sgs.env.ecabsdriver.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionUtils {

    private final Context context;
    private final Activity currentActivity;
    private final PermissionResultCallback permissionResultCallback;
    private ArrayList<String> permissionList = new ArrayList<>();
    private ArrayList<String> listPermissionsNeeded;
    private String dialog_content = "";
    private int reqCode;

    public PermissionUtils(Context context) {
        this.context = context;
        this.currentActivity = (Activity) context;
        permissionResultCallback = (PermissionResultCallback) context;
    }

    public PermissionUtils(Context context, PermissionResultCallback callback) {
        this.context = context;
        this.currentActivity = (Activity) context;
        permissionResultCallback = callback;
    }

    // interface for 4 different permmissions
    public interface PermissionResultCallback {
        void permissionGranted(int request_code);   // permissionGranted

        void partialPermissionGranted(int request_code, ArrayList<String> granted_permissions);  // partialPermmissions

        void permissionDenied(int request_code);  // if user clicks on the deny

        void neverAskAgain(int request_code);     // if user cliks on the neverAsk checkBox.
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(currentActivity)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    // for checking the permissions
    public void checkPermission(ArrayList<String> permissions, String dialogContent, int requestCode)  // by using the requestCode we can find out
    // which permssion is it ?
    {
        this.permissionList = permissions;
        this.dialog_content = dialogContent;
        this.reqCode = requestCode;

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions(permissions, requestCode)) {
                permissionResultCallback.permissionGranted(requestCode);
                Log.i("all permissions", "granted");
                Log.i("proceed", "to callback");
            }
        } else {
            permissionResultCallback.permissionGranted(requestCode);
            Log.i("all permissions", "granted");
            Log.i("proceed", "to callback");
        }
    }

    private boolean checkAndRequestPermissions(ArrayList<String> permissions, int requestCode) {
        int permissionCount = permissions.size();

        if (permissionCount > 0) {
            listPermissionsNeeded = new ArrayList<>();

            for (int i = 0; i < permissionCount; i++) {
                int hasPermission = ContextCompat.checkSelfPermission(currentActivity, permissions.get(i));

                if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permissions.get(i));
                }
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(currentActivity,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), requestCode);
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0) {
                    Map<String, Integer> perms = new HashMap<>();

                    for (int i = 0; i < permissions.length; i++) {
                        perms.put(permissions[i], grantResults[i]);
                    }

                    final ArrayList<String> pending_permissions = new ArrayList<>();
                    for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                        if (perms.get(listPermissionsNeeded.get(i)) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, listPermissionsNeeded.get(i)))
                                pending_permissions.add(listPermissionsNeeded.get(i));
                            else {
                                Log.i("Go to settings", "and enable permissions");
                                permissionResultCallback.neverAskAgain(reqCode);
                                Toast.makeText(currentActivity, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }

                    }
                    if (pending_permissions.size() > 0) {
                        showMessageOKCancel(dialog_content,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkPermission(permissionList, dialog_content, requestCode);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                Log.i("permisson", "not fully given");
                                                if (permissionList.size() == pending_permissions.size())
                                                    permissionResultCallback.permissionDenied(requestCode);
                                                else
                                                    permissionResultCallback.partialPermissionGranted(requestCode, pending_permissions);
                                                break;
                                        }


                                    }
                                });
                    } else {
                        Log.i("all", "permissions granted");
                        Log.i("proceed", "to next step");
                        permissionResultCallback.permissionGranted(reqCode);
                    }
                }
                break;
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final String TAG = "PermissionUtil";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static boolean checkStoragePermission(Context context) {
        return ((Build.VERSION.SDK_INT < 23) || (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
    }

    public static boolean checkLocationPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        } else {
            return true;
        }
    }

    public static boolean checkImagePermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

        } else {
            return true;
        }
    }


    public static boolean checkingCameraPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);

        } else {
            return true;
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            Log.d(TAG, "verifyPermissions: ");
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static String[] checkGoogleNearbyPermissions(Context context) {

        String[] mPermissions = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<String> mPermissionList = new ArrayList<>();

            for (String permission : mPermissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permission);
                }
            }

            String[] result = mPermissionList.toArray(new String[mPermissionList.size()]);

            if (result.length == 0)
                return null;
            else
                return result;

        } else {
            return null;
        }
    }
}
