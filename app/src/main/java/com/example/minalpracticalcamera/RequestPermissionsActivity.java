package com.example.minalpracticalcamera;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Trace;

import androidx.annotation.NonNull;


/**
 * Repeatedly ask the user for runtime permissions, until they grant all the permissions.
 * For now this is designed for activities used in Contacts. However, the ImportVCardActivity is
 * also used in the Dialer. When Dialer begins to support runtime permissions in their app, they
 * may wish to use a more targeted list of permissions or allow the user to reject using
 * some permissions.
 * <p>
 * At the time of writing this Activity, most permissions cause crashes when not granted.
 * So it is risky to not possess them.
 */
public class RequestPermissionsActivity extends Activity {
    public static final String PREVIOUS_ACTIVITY_INTENT = "previous_intent";
    private static final int PERMISSIONS_REQUEST_ALL_PERMISSIONS = 999;
    private static String[] permissions = new String[]{
            permission.READ_EXTERNAL_STORAGE,
            permission.WRITE_EXTERNAL_STORAGE,
            permission.CAMERA

    };
    private Intent mPreviousActivityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreviousActivityIntent = (Intent) getIntent().getExtras().get(PREVIOUS_ACTIVITY_INTENT);
        requestPermissions();


    }

    /**
     * If any permissions the Contacts app needs are missing, open an Activity
     * to prompt the user for these permissions. Moreover, finish the current activity.
     * <p>
     * This is designed to be called inside {@link Activity#onCreate}
     */
    public static boolean startPermissionActivity(Activity activity) {
        if (!RequestPermissionsActivity.hasPermissions(activity)) {
            final Intent intent = new Intent(activity, RequestPermissionsActivity.class);
            intent.putExtra(PREVIOUS_ACTIVITY_INTENT, activity.getIntent());
            activity.startActivity(intent);
            activity.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (isAllGranted(grantResults)) {
            mPreviousActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mPreviousActivityIntent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            requestPermissions();
        }
    }

    private boolean isAllGranted(int[] grantResult) {
        for (int result : grantResult) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        Trace.beginSection("requestPermissions");
        requestPermissions(permissions, PERMISSIONS_REQUEST_ALL_PERMISSIONS);
        Trace.endSection();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean hasPermissions(Context context) {
        Trace.beginSection("hasPermission");
        try {
            for (String permission : permissions) {
                if (context.checkSelfPermission(permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        } finally {
            Trace.endSection();
        }
    }
}