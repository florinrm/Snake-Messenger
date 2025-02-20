package com.example.snakemessenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;
import androidx.viewpager2.widget.ViewPager2;

import com.example.snakemessenger.authentication.SignInActivity;
import com.example.snakemessenger.database.AppDatabase;
import com.example.snakemessenger.general.Constants;
import com.example.snakemessenger.logging.LogsActivity;
import com.example.snakemessenger.models.Message;
import com.example.snakemessenger.services.BackgroundCommunicationService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "[MainActivity]";

    private SharedPreferences loginPreferences;
    public static AppDatabase db;

    public static String myDeviceId;
    public static String currentChat = null;
    public static Map<Integer, List<Message>> notificationMessages = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = findViewById(R.id.main_page_toolbar);
        mToolbar.setTitle(Constants.APP_TITLE);
        setSupportActionBar(mToolbar);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .build();

        db.getContactDao().deleteOldContacts(System.currentTimeMillis());
        db.getMessageDao().deleteOldUndeliveredMessages(System.currentTimeMillis());

        Log.d(TAG, "onCreate: initialized Room DB");

        loginPreferences = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE);

        myDeviceId = loginPreferences.getString(Constants.SHARED_PREFERENCES_DEVICE_ID, "");

        if (!BackgroundCommunicationService.running) {
            Log.d(TAG, "Starting background communication service...");

            startService(new Intent(getApplicationContext(), BackgroundCommunicationService.class));
        }

        Log.d(TAG, "onCreate: user is signed in and will advertise using codeName " + myDeviceId);

        ViewPager2 mViewPager2 = findViewById(R.id.main_tabs_pager);
        mViewPager2.setAdapter(new TabsAccessorAdapter(this));

        TabLayout mTabLayout = findViewById(R.id.main_tabs);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(mTabLayout, mViewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(Constants.CHATS_TAB);
                    break;
                case 1:
                    tab.setText(Constants.CONTACTS_TAB);
                    break;
                default:
                    break;
            }
        });

        tabLayoutMediator.attach();

        if (!hasPermissions(this, Constants.REQUIRED_PERMISSIONS)) {
            Log.d(TAG, "onCreate: app does not have all the required permissions. Requesting permissions...");

            ActivityCompat.requestPermissions(
                    this,
                    Constants.REQUIRED_PERMISSIONS,
                    Constants.REQUEST_PERMISSIONS
            );
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != Constants.REQUEST_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, Constants.TOAST_MISSING_PERMISSIONS, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_settings_option) {
            Log.d(TAG, "onOptionsItemSelected: settings option selected");
            sendUserToEditProfileActivity();
        } else if (item.getItemId() == R.id.main_sign_out_option) {
            Log.d(TAG, "onOptionsItemSelected: sign out option selected");
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putBoolean(Constants.SHARED_PREFERENCES_SIGNED_IN, false);
            editor.putBoolean(Constants.SHARED_PREFERENCES_SERVICE_STARTED, false);
            editor.apply();

            stopService(new Intent(getApplicationContext(), BackgroundCommunicationService.class));

            sendUserToLoginActivity();
            Toast.makeText(MainActivity.this, Constants.TOAST_SIGNED_OUT, Toast.LENGTH_SHORT).show();
        } else if (item.getItemId() == R.id.main_check_logs_option) {
            Log.d(TAG, "onOptionsItemSelected: check logs option selected");
            sendUserToLogsActivity();
        }

        return true;
    }

    private void sendUserToEditProfileActivity() {
        Log.d(TAG, "sendUserToSettingActivity: starting settings activity...");
        Intent editProfileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        editProfileIntent.putExtra(Constants.EXTRA_CONTACT_DEVICE_ID, "");
        startActivity(editProfileIntent);
    }

    private void sendUserToLoginActivity() {
        Log.d(TAG, "sendUserToLoginActivity: starting login activity...");
        Intent loginIntent = new Intent(MainActivity.this, SignInActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void sendUserToLogsActivity() {
        Log.d(TAG, "sendUserToSettingActivity: starting logs activity...");
        Intent logsIntent = new Intent(MainActivity.this, LogsActivity.class);
        startActivity(logsIntent);
    }
}
