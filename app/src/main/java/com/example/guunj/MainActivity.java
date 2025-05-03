package com.example.guunj;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.guunj.databinding.ActivityMainBinding;
import com.example.guunj.fragments.AccountFragment;
import com.example.guunj.fragments.HomeFragment;
import com.example.guunj.fragments.MusicFragment;
import com.example.guunj.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View mainLayout = findViewById(R.id.main);
        BottomNavigationView bottomNav = findViewById(R.id.bottomnavigation);

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            bottomNav.setPadding(0, 0, 0, systemBars.bottom);  // Push BottomNav down
            return insets;
        });


        replaceFragment(new HomeFragment());
        binding.bottomnavigation.setBackground(null);

        binding.bottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    replaceFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.music) {
                    replaceFragment(new MusicFragment());
                }else if (item.getItemId() == R.id.search) {
                    replaceFragment(new SearchFragment());
                }else if (item.getItemId() == R.id.account) {
                    replaceFragment(new AccountFragment());
                }
                return true;
            }
        });

    } // Last Bracket

    private  void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}