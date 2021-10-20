package com.heloo.finalvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {
    EditText gmail,password;
    CardView cardSignIn;
    TextView createNow_btn,forgot;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing In");
        gmail = findViewById(R.id.gmail);
        password = findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        cardSignIn = findViewById(R.id.btnupdate);
        firebaseFirestore = FirebaseFirestore.getInstance();
        createNow_btn = findViewById(R.id.createNow_btn);
        forgot = findViewById(R.id.forgot);

        createNow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                finish();
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resend =new EditText(v.getContext());
                //   final String ma =resend.getText().toString().trim();

                final AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
                alert.setTitle("Reset Your Password");
                alert.setMessage("Enter Your Email");
                alert.setView(resend);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(resend.getText().toString())){
                            Toast.makeText(SignIn.this,"Link Send UnSuccessful ", Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            String mail = resend.getText().toString();
                            firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignIn.this,"Link Send Successful ", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignIn.this,"Link Send UnSuccessful "+e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.create().show();

            }
        });

        cardSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String mail = gmail.getText().toString().trim();
                final String pass = password.getText().toString().trim();
                if(TextUtils.isEmpty(mail)){
                    gmail.setError("ENTER THE EMAIL");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    password.setError("ENTER THE PASSWORD");
                    return;
                }
                if (!TextUtils.isEmpty(pass) && !TextUtils.isEmpty(mail) && !(password.length()< 6)){
                }

                firebaseAuth.signInWithEmailAndPassword(gmail.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            //     Users user = new Users(binding.Name.getText().toString(),binding.EmailAddress.getText().toString(),binding.Password.getText().toString());
                            Toast.makeText(SignIn.this, "User Log In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                            //      String id = task.getResult().getUser().getUid();
                            //      Database.getReference().child("user").child(id).setValue(user);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(SignIn.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}