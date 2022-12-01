package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword, edtEmail;
    private Button btnSubmit;
    private TextView txtLoginInfo;
    private boolean isSigningUp = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) { //-> Hide Action bar
            getSupportActionBar().hide();
        }

        TextView welcometxt = (TextView) findViewById(R.id.welcomeText);
        welcometxt.setText("Welcome to Oct");


        edtEmail = findViewById(R.id.edtEmail); //-> connects the variable from xml by id
        edtPassword = findViewById(R.id.edtPassword); //-> R is for textView or any other thing
        edtUsername = findViewById(R.id.edtUsername); //By default this method takes int
        btnSubmit = findViewById(R.id.btnSubmit);
        txtLoginInfo = findViewById(R.id.txtLoginInfo);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            //if user is already logged in -> redirect to friends activity
            startActivity(new Intent(MainActivity.this, FriendsActivity.class));
            finish(); //finished main activity
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() { //-> button action
            @Override
            public void onClick(View v) {

                if(edtEmail.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() && !isSigningUp) {
                    Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
                if(isSigningUp && edtUsername.getText().toString().isEmpty() || edtPassword.getText().toString().isEmpty() || edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }

                if(isSigningUp) { //-> if user is signing up
                    handleSignIn();
                } else { //-> if user is log in
                    handleLogin();
                }
            }
        });

        txtLoginInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSigningUp) {

                    isSigningUp = false;
                    edtUsername.setVisibility(View.GONE);
                    btnSubmit.setText("Log in");
                    txtLoginInfo.setText("Don't have an account? Sign up");
                } else {

                    isSigningUp = true;
                    btnSubmit.setText("Sign up");
                    edtUsername.setVisibility(View.VISIBLE);
                    txtLoginInfo.setText("Already have an account? Log in");
                }
            }
        });

    }

    private void handleSignIn() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference("user/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(edtUsername.getText().toString(), edtEmail.getText().toString(), ""));
                    startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    Toast.makeText (MainActivity.this,  "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),  Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void handleLogin() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    startActivity(new Intent(MainActivity.this, FriendsActivity.class));
                    Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                    Toast.makeText (MainActivity.this,  "Welcome " + FirebaseAuth.getInstance().getCurrentUser().getEmail().toString(),  Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}