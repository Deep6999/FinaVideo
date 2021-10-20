package com.heloo.finalvideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FloatingActionButton fab;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        i = getIntent();
        recyclerView = findViewById(R.id.recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UploadHere.class));
            }
        });


        final FirebaseRecyclerOptions<modelUpload> Option = new FirebaseRecyclerOptions.Builder<modelUpload>().setQuery(FirebaseDatabase.getInstance().getReference().child("AllVideos"),modelUpload.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerAdapter<modelUpload,mViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<modelUpload, mViewHolder>(Option) {

            @Override
            protected void onBindViewHolder(@NonNull final mViewHolder holder, final int position, @NonNull final modelUpload model) {
                holder.prepareexoplayer(getApplication(),model.getTitle(),model.getVurl());
                final String postkey = getRef(position).getKey();
                holder.linear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),test.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("postkey",postkey);
                        intent.putExtra("description",model.getDiscription());
                        intent.putExtra("gmailof",model.getGmailofuploader());
                        intent.putExtra("videoUri",model.getVurl());
                        v.getContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow,parent,false);
                return new mViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                finish();
                break;
            case R.id.profile:
                startActivity(new Intent(getApplicationContext(),Profile.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}