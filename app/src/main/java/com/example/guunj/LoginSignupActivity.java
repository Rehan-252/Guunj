package com.example.guunj;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

public class LoginSignupActivity extends AppCompatActivity {

    ActivityLoginSignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginSignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
}