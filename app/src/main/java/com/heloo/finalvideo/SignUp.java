package com.heloo.finalvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    EditText NameSignUp,gmailSignup,passwordSignUp;
    CardView cardSignUp;
    TextView LogIn_btn;
    FirebaseAuth firebaseAuth;
    DatabaseReference dbreference;
    FirebaseFirestore firebaseFirestore;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing Up");
        NameSignUp = findViewById(R.id.NameSignUp);
        gmailSignup = findViewById(R.id.gmailSignup);
        firebaseAuth = FirebaseAuth.getInstance();
        passwordSignUp = findViewById(R.id.passwordSignUp);
        cardSignUp = findViewById(R.id.cardSignUp);
        firebaseFirestore = FirebaseFirestore.getInstance();
        LogIn_btn = findViewById(R.id.LogIn_btn);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        cardSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final String email = gmailSignup.getText().toString().trim();
                final String name = NameSignUp.getText().toString().trim();
                String password = passwordSignUp.getText().toString().trim();
                if (TextUtils.isEmpty(email))
                {
                    gmailSignup.setError("Email Required");
                    return;
                }
                if (TextUtils.isEmpty(name))
                {
                    NameSignUp.setError("Name Required");
                    return;
                }
                if (TextUtils.isEmpty(password))
                {
                    passwordSignUp.setError("Password Required");
                    return;
                }
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && !(passwordSignUp.length()< 6)){
                }
                firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"User Created",Toast.LENGTH_LONG).show();
                            userId = firebaseAuth.getCurrentUser().getUid();
                            DocumentReference documentReference =firebaseFirestore.collection("users").document(userId);
                            dbreference= FirebaseDatabase.getInstance().getReference().child("userprofile");
                            final Map<String,Object> user = new HashMap<>();
                            user.put("uname",name);
                            user.put("uimage","https://i0.wp.com/www.repol.copl.ulaval.ca/wp-content/uploads/2019/01/default-user-icon.jpg?fit=300%2C300");
                            dbreference.child(userId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    dbreference.child(userId).setValue(user);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Error"+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        LogIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                finish();
            }
        });

    }
}