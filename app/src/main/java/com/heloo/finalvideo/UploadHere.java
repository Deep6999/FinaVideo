package com.heloo.finalvideo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class UploadHere extends AppCompatActivity {
    private Button browse,upload;
    private VideoView videoView;
    Uri videoUri;
    private EditText vtitle,discrption;
    Intent intent;
    MediaController mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Uri uri1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_here);
        discrption = findViewById(R.id.discrption);
        vtitle = findViewById(R.id.vtitile);

        intent = getIntent();
        upload = findViewById(R.id.upload);
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("AllVideos");
        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent();
                        intent.setType("video/*")
                                .setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,100);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             check();
            }
        });
    }
    public String getextension(){
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videoUri));
    }

    private void ProcessingUpload() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();
        final StorageReference upload = storageReference.child("AllVideos/"+System.currentTimeMillis()+"."+getextension());
        upload.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                upload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String a = FirebaseAuth.getInstance().getUid();
                         uri1 = uri;
                        if (discrption.getText().toString().isEmpty()){
                            discrption.setText("Description Is Empty");
                        }
                        modelUpload modelUpload = new modelUpload(vtitle.getText().toString(),uri.toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail(),discrption.getText().toString());
                        databaseReference.child(databaseReference.push().getKey()).setValue(modelUpload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Intent intent = new Intent();
                                intent.putExtra("Nmaeofup",a);
                                finish();
                                Toast.makeText(UploadHere.this, "Uploaded Successful", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(UploadHere.this, "Failed "+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float per = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("Processing :" + (int)per+"%");
                progressDialog.setCancelable(false);

            }
        });
    }
    public void check(){
        if(vtitle.getText().toString().isEmpty() ) {
            Toast.makeText(UploadHere.this, "Enter The Title", Toast.LENGTH_SHORT).show();

        }
        else if (videoUri == null){
            Toast.makeText(UploadHere.this, "Select The Video", Toast.LENGTH_SHORT).show();

        }else {
            ProcessingUpload();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }
}