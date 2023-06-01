package com.climbsense.application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.climbsense.application.databinding.ActivityMainBinding;
import com.climbsense.application.function.InstanceFunction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private InstanceFunction instanceFunction;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private MapView mapView;

    private static final int PERMISSION_REQUEST_LOCATION_CODE = 1, PERMISSION_REQUEST_FOLDER_CODE = 7, PERMISSION_REQUEST_CAMERA = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();

        this.instanceFunction = instanceFunction.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        instanceFunction.setNavController(Navigation.findNavController(this, R.id.nav_host_fragment_activity_main));
        NavigationUI.setupWithNavController(binding.navView, instanceFunction.getNavController());

        BottomNavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    instanceFunction.getNavController().navigate(R.id.navigation_home);
                } else if (itemId == R.id.navigation_dashboard) {
                    instanceFunction.getNavController().navigate(R.id.navigation_dashboard);
                } else if (itemId == R.id.navigation_climb) {
                    instanceFunction.getNavController().navigate(R.id.navigation_climb);
                } else if (itemId == R.id.navigation_connection) {
                    instanceFunction.getNavController().navigate(R.id.navigation_connection);
                } else if (itemId == R.id.navigation_profile) {
                    instanceFunction.getNavController().navigate(R.id.navigation_profile);
                }

                return true;

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_FOLDER_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createFolder();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        //Copy the image selected into "ClimbSense" folder
        if (requestCode == 3 && resultCode == Activity.RESULT_OK && intent != null) {

            File source = new File(getRealPath(intent.getData()));
            File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/ClimbSense"), "profile.jpg");

            saveImage(source, destination);

            //Set display image
            String path = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_PICTURES + "/ClimbSense/profile.jpg";

            if (path != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                instanceFunction.getImageUriAward().setImageBitmap(bitmap);
            }

        }
    }

    //Check permissions of the application who has need
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            createFolder();
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_FOLDER_CODE);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_CODE);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        }
    }

    //Creation the folder image "ClimbSense"
    public void createFolder() {
        File folder_pictures    = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ClimbSense");
        File folder_documents   = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ClimbSense");

        if (!folder_pictures.exists()) {
            folder_pictures.mkdirs();
        }

        if (!folder_documents.exists()) {
            folder_documents.mkdirs();
        }
    }

    //Return the path of the selected image
    private String getRealPath(Uri selectImage) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String path = null;

        CursorLoader cursorLoader = new CursorLoader(getApplicationContext(), selectImage, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        }
        return path;
    }

    private void saveImage(File source, File destination) {
        try {
            FileChannel src = new FileInputStream(source).getChannel();
            FileChannel dst = new FileOutputStream(destination).getChannel();
            dst.transferFrom(src, 0, src.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}