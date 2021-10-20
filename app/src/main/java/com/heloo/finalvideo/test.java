package com.heloo.finalvideo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class test extends AppCompatActivity {

    Intent i;
    SimpleExoPlayerView simpleExoPlayerView;
    TextView uploadedby,commenttext;
    RecyclerView recofcomment;
    ImageButton likeBtn,sendcommentbtn,full;
    String post;
    TextView like;
    String uid;
    Boolean okLike = false;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    SimpleExoPlayer simpleExoPlayer;
    DatabaseReference likedatabase,userref,commentref;
    TextView vtitleview,Discrption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        i = getIntent();
        recofcomment = findViewById(R.id.recofcomment);
        uploadedby = findViewById(R.id.uploadedby);
        full = findViewById(R.id.full);
        firebaseAuth = FirebaseAuth.getInstance();
        Discrption = findViewById(R.id.discrption);
        sendcommentbtn = findViewById(R.id.sendcommentbtn);
        commenttext = findViewById(R.id.commenttext);
        post = i.getStringExtra("postkey");
        commentref = FirebaseDatabase.getInstance().getReference().child("AllVideos").child(post).child("Comments");
        userref = FirebaseDatabase.getInstance().getReference("userprofile");
        like = findViewById(R.id.like);
        likeBtn = findViewById(R.id.likeBtn);
        likedatabase = FirebaseDatabase.getInstance().getReference("likes");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = firebaseUser.getUid();
        vtitleview=findViewById(R.id.title);
        simpleExoPlayerView=findViewById(R.id.exoplayer);
        String description = i.getStringExtra("description");
        recofcomment.setLayoutManager(new LinearLayoutManager(this));
        if (i.getStringExtra("description") == null){
            Discrption.setText("Description Is Empty");
        }
        else {
            Discrption.setText("Description :- "+description);
        }
        if (i.getStringExtra("gmailof") == null){
            uploadedby.setText("UPLOADED BY: Anonymous");
        }else {
            uploadedby.setText("UPLOADED BY: "+i.getStringExtra("gmailof"));
        }
        try
        {
            vtitleview.setText(i.getStringExtra("title"));
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            simpleExoPlayer =(SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(getApplication(),trackSelector);

            Uri videoURI = Uri.parse(i.getStringExtra("videoUri"));

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(true);
            getlike(post,uid);


        }catch (Exception ex)
        {
            Log.d("Explayer Creshed", ex.getMessage());
        }
        full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),Fullscreen.class);
                intent.putExtra("VideoUrI",i.getStringExtra("videoUri"));
                intent.putExtra("Title",i.getStringExtra("title"));
                v.getContext().startActivity(intent);
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okLike = true;
                likedatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (okLike){
                            if (snapshot.child(post).hasChild(uid)){
                                likedatabase.child(post).child(uid).removeValue();
                                okLike=false;
                            }else {
                                likedatabase.child(post).child(uid).setValue(true);
                                okLike =false;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        sendcommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commenttext.getText().toString().isEmpty()) {
                    Toast.makeText(test.this, "Comment Is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    userref.child(uid).addValueEventListener(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String unameofcommenter = snapshot.child("uname").getValue().toString();
                                String uimageofcommenter = snapshot.child("uimage").getValue().toString();
                                processingcomment(unameofcommenter, uimageofcommenter);
                                commenttext.setText("");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void processingcomment(String unameofcommenter , String uimageofcommenter) {
        String commentpost=commenttext.getText().toString();
        String randompostkey=firebaseAuth.getUid()+new Random().nextInt(1000);

        Calendar datevalue = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yy");
        String cdate=dateFormat.format(datevalue.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        String ctime=timeFormat.format(datevalue.getTime());

        HashMap cmnt=new HashMap();
        cmnt.put("uid",firebaseAuth.getCurrentUser().getUid());
        cmnt.put("username",unameofcommenter);
        cmnt.put("userimage",uimageofcommenter);
        cmnt.put("usermsg",commentpost);
        cmnt.put("date",cdate);
        cmnt.put("time",ctime);

        commentref.child(randompostkey).updateChildren(cmnt).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(test.this, "Error : "+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void  getlike(final String post ,final String uid){

        likedatabase = FirebaseDatabase.getInstance().getReference("likes");
        likedatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(post).hasChild(uid)){
                    int likecount = (int) snapshot.child(post).getChildrenCount();
                    like.setText(likecount +" Likes ");
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_24_red);
                }else {
                    int likecount = (int) snapshot.child(post).getChildrenCount();
                    like.setText(likecount +" Likes ");
                    likeBtn.setImageResource(R.drawable.ic_baseline_favorite_24);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        simpleExoPlayer.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<modelComment> comments
                = new FirebaseRecyclerOptions.Builder<modelComment>().setQuery(commentref.orderByChild("time"), modelComment.class).build();
        FirebaseRecyclerAdapter<modelComment, commentViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<modelComment, commentViewholder>(comments) {
            @Override
            protected void onBindViewHolder(@NonNull commentViewholder holder, int position, @NonNull modelComment model) {
                holder.commenterName.setText(model.getUsername());
                holder.dateinyear.setText(model.getDate());
                holder.timeintime.setText(model.getTime());
                holder.thecomment.setText(model.getUsermsg());
                Glide.with(getApplicationContext()).load(model.getUserimage()).into(holder.commenterimage);
            }

            @NonNull
            @Override
            public commentViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow_comment, parent, false);
                return new commentViewholder(view);
            }
        };
        firebaseRecyclerAdapter.startListening();
        recofcomment.setAdapter(firebaseRecyclerAdapter);
    }
}