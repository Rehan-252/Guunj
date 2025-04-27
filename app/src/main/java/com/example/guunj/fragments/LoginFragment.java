package com.example.guunj.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guunj.MainActivity;
import com.example.guunj.Models.Users;
import com.example.guunj.R;
import com.example.guunj.databinding.FragmentLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class LoginFragment extends Fragment {

    FragmentLoginBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait while we log you in...");

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.loginEmail.getText().toString().isEmpty() || binding.loginEmail.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill Email and Password!", Toast.LENGTH_SHORT).show();
                    return; // stop further
                }

                progressDialog.show();
                auth.createUserWithEmailAndPassword(binding.loginEmail.getText().toString(),binding.loginPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Users user = new Users(binding.userName.getText().toString(), binding.loginEmail.getText().toString(), binding.loginPassword.getText().toString());
                            String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                            database.getReference().child("Users").child(id).setValue(user);
                            Toast.makeText(getActivity(), "Account Create Successfully ", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();

                        }else {
                            Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}