package com.example.guunj.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guunj.LoginSignupActivity;
import com.example.guunj.MainActivity;
import com.example.guunj.R;
import com.example.guunj.databinding.FragmentAccountBinding;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    FirebaseAuth auth;
    FragmentAccountBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        auth = FirebaseAuth.getInstance();

        binding.signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Toast.makeText(getActivity(), "Account Logout Successfully ", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), LoginSignupActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }
}