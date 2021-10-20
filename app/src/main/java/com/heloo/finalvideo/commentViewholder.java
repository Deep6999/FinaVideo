package com.heloo.finalvideo;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class commentViewholder extends RecyclerView.ViewHolder {
    ImageView commenterimage;
    TextView commenterName,dateinyear,timeintime,thecomment;
    public commentViewholder(@NonNull View itemView) {
        super(itemView);
        commenterimage = itemView.findViewById(R.id.commenterimage);
        commenterName = itemView.findViewById(R.id.commenterName);
        dateinyear = itemView.findViewById(R.id.dateinyear);
        timeintime = itemView.findViewById(R.id.timeintime);
        thecomment = itemView.findViewById(R.id.thecomment);
    }
}
