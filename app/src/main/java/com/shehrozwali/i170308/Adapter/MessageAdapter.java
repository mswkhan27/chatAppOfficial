package com.shehrozwali.i170308.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shehrozwali.i170308.Contact;
import com.shehrozwali.i170308.Message;
import com.shehrozwali.i170308.MessageActivity;
import com.shehrozwali.i170308.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    FirebaseUser fireUser;
    int row_index=-1;
    private static final int TYPE_MESSAGE_LEFT=0;
    private static final int TYPE_MESSAGE_RIGHT=1;
    List<Message> chats;
    String imageURL;
    Context c;

    String emailIMP;


    public MessageAdapter(List<Message> msg, Context c,String imageURL) {
        this.c=c;
        this.chats=msg;
        this.imageURL=imageURL;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==TYPE_MESSAGE_LEFT){
            View view= LayoutInflater.from(c).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.MyViewHolder(view);

        }
        else{
            View view= LayoutInflater.from(c).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.MyViewHolder(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, final int position) {

       Message  msg=chats.get(position);
       holder.messagesShow.setText(msg.getMessage());
       if(imageURL.equals("")){
           holder.profile_image.setImageResource(R.drawable.dummydp);

       }
       else{
           Picasso.get().load(imageURL).into(holder.profile_image);
       }

       if(position==getItemCount()-1){
           if(msg.isSeen()){
               holder.tickIcon.setImageResource(R.drawable.seenicon);
           }
           else{
               holder.tickIcon.setImageResource(R.drawable.deliveredicon);
           }
       }
       else{
           holder.tickIcon.setVisibility(View.GONE);
       }


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView messagesShow;
        CircleImageView profile_image;
        ImageView tickIcon;
        RelativeLayout rr;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_image);
            messagesShow=itemView.findViewById(R.id.message_show);
            tickIcon=itemView.findViewById(R.id.tickIcon);


        }
    }

    @Override
    public int getItemViewType(int position) {
        fireUser= FirebaseAuth.getInstance().getCurrentUser();

        if(!chats.get(position).getSender().equals(fireUser.getUid())){
            return TYPE_MESSAGE_LEFT;
        }
        else{
            return TYPE_MESSAGE_RIGHT;
        }

    }
}
