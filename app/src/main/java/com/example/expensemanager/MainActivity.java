package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.databinding.ActivityMainBinding;
import com.example.expensemanager.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

     TextView forgotTextLink;
    private ProgressDialog mDialog;
    // Firebase...
    FirebaseAuth mAuth;

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        forgotTextLink = findViewById(R.id.forget_password);
        getWindow().setStatusBarColor(getResources().getColor(R.color.color_background));
        mAuth = FirebaseAuth.getInstance();


       if(mAuth.getCurrentUser()!= null){
           startActivity(new Intent(getApplicationContext(),HomeActivity.class));
       }

       mDialog = new ProgressDialog(this);
        loginDetails();
    }
    private void loginDetails(){

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.emailLogin.getText().toString().trim();
                String pass = binding.passwordLogin.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                   binding.emailLogin.setError("Email Required..");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    binding.passwordLogin.setError("Password Required..");
                    return;
                }
                mDialog.setMessage("Processing..");
                mDialog.show();
               mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           mDialog.dismiss();
                           startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                           Toast.makeText(getApplicationContext(), "Login Successful..", Toast.LENGTH_SHORT).show();
                       }else{
                           mDialog.dismiss();
                           Toast.makeText(getApplicationContext(),task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });
       // Registration Activity
    binding.signupReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegistrationActivity.class);
                startActivity(intent);
            }
        });
        // Reset Activity
    forgotTextLink.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
              EditText resetMail = new EditText(view.getContext());
               AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
               passwordResetDialog.setTitle("Reset Password ?");
               passwordResetDialog.setMessage("Enter your Email to Received Reset Link.");
               passwordResetDialog.setView(resetMail);

               passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       //Extract the email and reset link
                    String mail = resetMail.getText().toString();
                    mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error ! Reset Link is Not Sent"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                   }
               });
               passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       //Close the dialog
                   }
               });
               passwordResetDialog.create().show();
           }
       });
    }
}