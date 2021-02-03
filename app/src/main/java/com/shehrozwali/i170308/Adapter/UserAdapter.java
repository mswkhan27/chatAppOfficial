package com.shehrozwali.i170308.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shehrozwali.i170308.Contact;
import com.shehrozwali.i170308.MessageActivity;
import com.shehrozwali.i170308.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    int row_index=-1;
    List<Contact> ls; Context c;
    String emailIMP;
    boolean isOnline;
    public UserAdapter(List<Contact> ls, Context c,boolean isOnline) {
        this.c=c;
        this.ls=ls;
        this.isOnline=isOnline;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView= LayoutInflater.from(c).inflate(R.layout.row,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, final int position) {
        Contact contact=ls.get(position);


        if (contact.getImageURL().equals("")){
            holder.profile_image.setImageResource(R.drawable.dummydp);
        }
        else{
           Picasso.get().load(contact.getImageURL()).into(holder.profile_image);

        }
        holder.uName.setText(contact.getUsername());
        holder.showGender.setText(contact.getGender());











        if(isOnline){
            if(contact.getStatus().equals("online")){

                holder.online_icon.setVisibility(View.VISIBLE);

            }
            else{
                holder.online_icon.setVisibility(View.GONE);
            }

        }
        else{
            holder.online_icon.setVisibility(View.GONE);

        }




        holder.ll.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(v.getContext(), MessageActivity.class);
                i.putExtra("userId", contact.getId());
                v.getContext().startActivity(i);

            }
        });



    }

    @Override
    public int getItemCount() {
        return ls.size();
    }
    public void filterList(ArrayList<Contact> filteredList){
        ls=filteredList;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView uName;
        ImageView profile_image;
        CardView ll;
        RelativeLayout rr;
        CircleImageView online_icon;
        TextView showGender;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.displayProfPic);
            uName=itemView.findViewById(R.id.showUsername);
            showGender=itemView.findViewById(R.id.showGender);
            online_icon=itemView.findViewById(R.id.online_icon);
            ll=itemView.findViewById(R.id.ll);


        }
    }

}
