package com.example.guunj;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.guunj.databinding.ActivityLoginSignupBinding;
import com.example.guunj.fragments.LoginFragment;
import com.example.guunj.fragments.SignupFragment;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;

public class LoginSignupActivity extends AppCompatActivity {

    ActivityLoginSignupBinding binding;
    private static final int STORAGE_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkAndRequestStoragePermissions();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginSignup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());

        replaceFragment(new LoginFragment());
        binding.loginButton.setTextColor(getResources().getColor(android.R.color.black));
        binding.signupButton.setTextColor(getResources().getColor(android.R.color.darker_gray));

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LoginFragment());
                binding.loginButton.setTextColor(getResources().getColor(android.R.color.black));
                binding.signupButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        });

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SignupFragment());
                binding.signupButton.setTextColor(getResources().getColor(android.R.color.black));
                binding.loginButton.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
        });

    }
    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    private void checkAndRequestStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            };
            requestMissingPermissions(permissions);

        } else {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            requestMissingPermissions(permissions);
        }
    }

    private void requestMissingPermissions(String[] permissions) {
        ArrayList<String> missing = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                missing.add(permission);
            }
        }

        if (!missing.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    missing.toArray(new String[0]),
                    STORAGE_PERMISSION_CODE);
        } else {
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            boolean allGranted = true;
            boolean permanentlyDenied = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                        permanentlyDenied = true;
                    }
                }
            }

            if (allGranted) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                if (permanentlyDenied) {
                    Toast.makeText(this, "Permission permanently denied. Please enable in settings.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}