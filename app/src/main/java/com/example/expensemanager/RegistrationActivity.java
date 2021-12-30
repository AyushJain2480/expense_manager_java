package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.example.expensemanager.Model.Users;
import com.example.expensemanager.databinding.ActivityHomeBinding;
import com.example.expensemanager.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    private ProgressDialog mDialog;
    private FirebaseAuth mAuth;// auth is used for sign up with email and password
    private FirebaseDatabase database;//database is used to store the values taken by the user in edittext in firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_background));
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDialog = new ProgressDialog(this);
        registration();
    }
    private void registration(){

        binding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailReg.getText().toString().trim();
                String pass = binding.passwordReg.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    binding.emailReg.setError("Email Required..");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    binding.passwordReg.setError("Password Required..");
                    return;
                }
                mDialog.setMessage("Processing..");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            Users user = new Users(binding.emailReg.getText().toString(),binding.passwordReg.getText().toString());
                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);

                            Toast.makeText(RegistrationActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        binding.signInHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }
}