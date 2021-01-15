package com.example.uasamub_catatankeuangan_ti7a;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uasamub_catatankeuangan_ti7a.auth.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener fireAuthListener;
    Button btnemail, btnpassword;
    private FirebaseAuth firebaseAuth;
    EditText e_email, e_password, old_password;
    FirebaseUser user, users;
    TextView v_email, v_uid, v_nama, v_created, v_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user  = FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();

        btnemail    = findViewById(R.id.btn_email);
        btnpassword = findViewById(R.id.btn_password);


        v_nama      = findViewById(R.id.tv_name);
        v_email     = findViewById(R.id.tv_email);
        v_uid       = findViewById(R.id.tv_uid);


        firebaseAuth = FirebaseAuth.getInstance();
        fireAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                String nama = users.getEmail();
                String getnama = nama.replace("@gmail.com", "");
                v_nama.setText(getnama);
                v_email.setText(users.getEmail());
                v_uid.setText(users.getUid());

                if (users == null) {
                    com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this.startActivity(new Intent(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, LoginActivity.class));
                    com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this.finish();
                }
            }
        };


        //now change button visible for email changing
        btnemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialogEmail();
            }
        });

        //changing password
        btnpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDialogPassword();
            }
        });


//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                String oldEmailText = oldEmail.getText().toString().trim();
//
//                if (!oldEmailText.equals("")) {
//                    firebaseAuth.sendPasswordResetEmail(oldEmailText)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(ProfileActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(ProfileActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                } else {
//                    oldEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });


    }


    @SuppressLint("SimpleEmailFormat")
    private void showAddDialogEmail() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_change_email, null);
        dialogBuilder.setView(dialogView);

        e_email = dialogView.findViewById(R.id.et_email);
        old_password = dialogView.findViewById(R.id.et_oldpassword);

        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String newEmailText = e_email.getText().toString().trim();
                String password_old = old_password.getText().toString();
                if (!newEmailText.equals("") && !password_old.equals("")){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(users.getEmail(), password_old);
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.updateEmail(newEmailText)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Email is updated, sign in with new email!", Toast.LENGTH_SHORT).show();
                                                    firebaseAuth.signOut();
                                                    Intent intent = new Intent(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Failed to update email!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                }else {
                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Masukan email dan password anda!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    @SuppressLint("SimplePasswordFormat")
    private void showAddDialogPassword() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getLayoutInflater().getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        @SuppressLint("InflateParams")
        final View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        dialogBuilder.setView(dialogView);

        e_password = dialogView.findViewById(R.id.et_password);
        old_password = dialogView.findViewById(R.id.et_oldpassword);

        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String newPassText = e_password.getText().toString();
                String password_old = old_password.getText().toString();
                if (!newPassText.equals("") && !password_old.equals("")){
                    FirebaseUser userpass = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider
                    .getCredential(users.getEmail(), password_old);
                    userpass.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        userpass.updatePassword(newPassText).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                                    firebaseAuth.signOut();
                                                    Intent intent = new Intent(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Error auth failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(com.example.uasamub_catatankeuangan_ti7a.ProfileActivity.this, "Masukan password baru dan password lama anda!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }




    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(fireAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fireAuthListener != null){
            firebaseAuth.removeAuthStateListener(fireAuthListener);
        }
    }
}