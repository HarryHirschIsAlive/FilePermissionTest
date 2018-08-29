package com.example.android.filepermissiontest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 666;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // activate timber logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        // get permissions
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Timber.d("version code > M, must handle permissions on runtime");

            // permissions not granted -> ask user
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Timber.d("permission denied to read storage, will ask user");
                String[] permissions = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                };
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            } else {
                Timber.d("permission to read storage OK");
                initOnAllPermissionsGranted();
            }
        }
        // TODO Test legacy device (without runtime permission handling)
        else {
            Timber.d("permission to read storage OK");
            initOnAllPermissionsGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Timber.d("onRequestPermissionsResult(): permissions indeed granted");
                    initOnAllPermissionsGranted();
                } else {
                    // permission denied, boo!
                    Timber.d("onRequestPermissionsResult(): permissions DENIED");
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    private void initOnAllPermissionsGranted() {

        // TODO: on first start after granting permissions: listing files in folder ALWAYS returns NULL!
        // though system says permissions were granted
        // closing the app and restarting: listing files works immediately

        // ------------------- phone storage ----------------------

        Timber.d("CHECKING phone storage");
        File folderPhoneStorage = Environment.getExternalStorageDirectory();
        File[] subfoldersPhoneStorage = folderPhoneStorage.listFiles();
        Timber.d(String.format("%s contains %s files/subfolders",
                folderPhoneStorage.getAbsolutePath(),
                subfoldersPhoneStorage != null ? String.valueOf(subfoldersPhoneStorage.length) : "NULL"
                ));


        // ------------------------  checking for SD cards under /storage/... --------------------

        Timber.d("CHECKING for sd cards under /storage/...");
        File folderStorage = new File("/storage/");
        File[] subfoldersInStorage = folderStorage.listFiles();
        if (subfoldersInStorage != null && subfoldersInStorage.length > 0) {
            int subfolderCount = subfoldersInStorage.length;
            for (File subfolder : subfoldersInStorage) {

                Timber.d("checking storage subfolder: " + subfolder.getAbsolutePath());

                // try to read contents -> if not possible: skip current folder
                File[] contents = subfolder.listFiles();
                if (contents == null) {
                    Timber.d("-> contents: NULL");
                    continue;
                }
                Timber.d("-> contents: " + Arrays.asList(contents));

                String currentPath = subfolder.getAbsolutePath();
                String currentFolderName = subfolder.getName();
                // TODO: length is >1 in most cases, just not every subfolder is interesting...
                // count folders that CAN be handled
                // or store in DB and offer possibility to edit names
                if (subfolderCount > 1) {
                    String nameSdCard = String.format("SD-Card (%s)", currentFolderName);
                }

            }
        }

    }
}
