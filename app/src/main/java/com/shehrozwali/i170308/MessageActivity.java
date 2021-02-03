package com.shehrozwali.i170308;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shehrozwali.i170308.Adapter.MessageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    Uri imgUri;
    FirebaseUser currentUser;
    EditText text_Message;
    ImageButton send_Button;
    RecyclerView rv;
    MessageAdapter adapter;
    List<Message> chats;
    DatabaseReference reference;
    TextView user1;
    ValueEventListener listenerSeen;
    CircleImageView displayProfile;
    private static final int PICK_IMAGE=1;
    BottomSheetDialog bsd;
    TextView bname;
    TextView bphone;
    TextView bbio;
    TextView bgender;
    CircleImageView bphoto;
    String uIdforBottomSheet;
    Random rand;

    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        text_Message=findViewById(R.id.sendText);
        send_Button=findViewById(R.id.sendBtn);

        Intent i=getIntent();

        final String userId= i.getStringExtra("userId");
        uIdforBottomSheet=userId;
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        user1=findViewById(R.id.topBarUsername);
        displayProfile=findViewById(R.id.topBarUserDp);

        rv=findViewById(R.id.rvchat);
        rv.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);


        send_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMessage=text_Message.getText().toString();
                if (!textMessage.equals("")){
                    sendMessage( userId, currentUser.getUid(), textMessage);
                }
                else{
                    Toast.makeText(MessageActivity.this,"Empty Message",Toast.LENGTH_LONG).show();
                }
                text_Message.setText("");
            }
        });

        reference=FirebaseDatabase.getInstance().getReference("Users").child(userId);
        listenerSeen=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                assert contact != null;

                user1.setText(contact.getUsername());
                if (contact.getImageURL().equals("")) {
                    displayProfile.setImageResource(R.drawable.dummydp);
                }
                else{
                    Picasso.get().load(contact.getImageURL()).into(displayProfile);
                }



                chatRead(currentUser.getUid(),userId,contact.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        messageSeen(userId);

    }
    private void messageSeen(String uId) {
      reference = FirebaseDatabase.getInstance().getReference("Chats");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message mess = snapshot.getValue(Message.class);

                    if (mess.getSender().equals(uId) && mess.getReceiver().equals(currentUser.getUid())) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);

                    }


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });



    }

    private void sendMessage( String receiver,String sender, String message){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
       hashMap.put("isSeen",false);

        reference.child("Chats").push().setValue(hashMap);

    }
    private void chatRead( final String currentUserId,final String userId, final String imageURL) {
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chats.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message mess = snapshot.getValue(Message.class);

                    if ( mess.getSender().equals(userId) && mess.getReceiver().equals(currentUserId)  ||
                            mess.getSender().equals(currentUserId) && mess.getReceiver().equals(userId)  ) {

                        chats.add(mess);

                    }

                    adapter=new MessageAdapter(chats, MessageActivity.this,imageURL);
                    rv.setAdapter(adapter);
                }

            }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }



        });
    }

    public void backButton(View view) {


        startActivity(new Intent(MessageActivity.this,MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    private void userStatus(String userStatus){
        reference=FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",userStatus);
        reference.updateChildren(hashMap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        userStatus("online");
    }


    @Override
    protected void onPause() {
        super.onPause();
        userStatus("offline");
        reference.removeEventListener(listenerSeen);
    }

    public void topBarUserDpButton(View view) {


        bsd = new BottomSheetDialog(MessageActivity.this, R.style.BottomSheetTheme);
        View sView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.userbottomlayout,
                findViewById(R.id.bSheet));


        bname = sView.findViewById(R.id.bottomName);
        bphone = sView.findViewById(R.id.bottomPhone);
        bgender = sView.findViewById(R.id.bottomGender);
        bbio = sView.findViewById(R.id.bottomBio);
        bphoto = sView.findViewById(R.id.bottomPhoto);


        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(uIdforBottomSheet);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                assert contact != null;
                if(contact.getImageURL().equals("")){
                    bphoto .setImageResource(R.drawable.dummydp);}

                else{
                    Picasso.get().load(contact.getImageURL()).into(  bphoto );
                }


                bname.setText(contact.getFirstName()+" "+contact.getLastName());
                bbio.setText(contact.getBio());
                bgender.setText(contact.getGender());
                bphone.setText(contact.getPhone());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        sView.findViewById(R.id.line1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsd.dismiss();
            }
        });
        bsd.setContentView(sView);
        bsd.show();

    }

    public void sendImageButton(View view) {


        Intent gallery =new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Display"),PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imgUri=data.getData();

            imgUri.toString();

            reference=FirebaseDatabase.getInstance().getReference("Chats");
            mStorageRef= FirebaseStorage.getInstance().getReference();
            rand = new Random();
            int randNo = rand.nextInt(1000);
            String randomNum=Integer.toString(randNo);
            mStorageRef= mStorageRef.child("imageFiles/"+randomNum+".jpg");
            mStorageRef.getFile(imgUri);
            mStorageRef.putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                            task
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String msg=uri.toString();

                                                sendMessage(uIdforBottomSheet,currentUser.getUid(), msg);
                                                reference=FirebaseDatabase.getInstance().getReference("Users");


                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(MessageActivity.this,
                                    "Unable to Upload Image/Data",Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }

}