package com.example.guunj.fragments;

import static android.content.ContentValues.TAG;
import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.guunj.MainActivity;
import android.Manifest;
import com.example.guunj.Models.Users;
import com.example.guunj.Musicadapter;
import com.example.guunj.R;
import com.example.guunj.databinding.FragmentLoginBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginFragment extends Fragment {

    private static final int STORAGE_PERMISSION_CODE = 101;
    FragmentLoginBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    CredentialManager credentialManager;
    CallbackManager callbackManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        checkStoragePermission();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        credentialManager = CredentialManager.create(requireContext());
        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait while we loging you in...");

        // Email/Password Login
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.loginEmail.getText().toString();
            String password = binding.loginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please fill Email and Password!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Users user = new Users(email, password);
                            String id = Objects.requireNonNull(task.getResult().getUser()).getUid();
                            database.getReference().child("Users").child(id).setValue(user);

                            Toast.makeText(getActivity(), "Account Login Successfully", Toast.LENGTH_LONG).show();
                            startMainActivity();
                        } else {
                            Toast.makeText(getActivity(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Google Login Button
        binding.googleLogin.setOnClickListener(v -> {
            GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .build();

            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                credentialManager.getCredentialAsync(
                        requireActivity(),
                        request,
                        null,
                        requireActivity().getMainExecutor(),
                        new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                            @Override
                            public void onResult(GetCredentialResponse result) {
                                Credential credential = result.getCredential();
                                handleSignIn(credential);
                            }

                            @Override
                            public void onError(@NonNull GetCredentialException e) {
                                Log.e(TAG, "Google sign-in failed", e);
                                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";

                                // Check for common "no account" message
                                if (errorMessage.toLowerCase().contains("no eligible credentials") ||
                                        errorMessage.toLowerCase().contains("no credentials available")) {
                                    Toast.makeText(getActivity(), "Login failed: No Google account found on this device", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getActivity(), "Google sign-in failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
            }
        });

        // Facebook Login Button with OnClickListener
        binding.facebookLogin.setOnClickListener(v -> {
            // Trigger Facebook Login
            LoginManager.getInstance().logInWithReadPermissions(requireActivity(), Arrays.asList("email", "public_profile"));

            // Register the callback for the Facebook login process
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    Toast.makeText(getActivity(), "Facebook login cancelled", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(@NonNull FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    Toast.makeText(getActivity(), "Facebook login failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Auto-login if already signed in
        if (auth.getCurrentUser() != null) {
            startMainActivity();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;
            if (TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(customCredential.getType())) {
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());
                    String idToken = googleIdTokenCredential.getIdToken();
                    firebaseAuthWithGoogle(idToken);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing GoogleIdTokenCredential", e);
                    Toast.makeText(getActivity(), "Error parsing GoogleIdTokenCredential", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Unknown custom credential type", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Credential is not of type CustomCredential", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ requires separate media permissions
            if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED)) {

                // Requesting separate media permissions for Android 13+
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{
                                Manifest.permission.READ_MEDIA_AUDIO,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO
                        },
                        STORAGE_PERMISSION_CODE);
            } else {
                Toast.makeText(getContext(), "Media Permissions Already Granted", Toast.LENGTH_SHORT).show();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 (API 29) to Android 12 (API 32)
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Requesting READ_EXTERNAL_STORAGE permission for Android 10-12
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                Toast.makeText(getContext(), "Storage Permission Already Granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            // For Android 9 (Pie) and below
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Requesting READ_EXTERNAL_STORAGE permission for Android 9 and below
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            } else {
                Toast.makeText(getContext(), "Storage Permission Already Granted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                // Load or access media here
            } else {
                // Handle permission denial
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                // Optionally show rationale if necessary
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Explain why the permission is needed (for example, in a dialog)
                    Toast.makeText(getContext(), "Storage permission is required to access media files.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
