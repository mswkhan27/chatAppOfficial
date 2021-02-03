package com.shehrozwali.i170308;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shehrozwali.i170308.Adapter.MessageUserAdapter;
import com.shehrozwali.i170308.Adapter.UserAdapter;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    /*
      bottom Sheet contents
     */

    TextView bname;
    TextView bphone;
    TextView bbio;
    TextView bgender;
    CircleImageView bphoto;


    /*VIEWING MESSAGES */
    private RecyclerView rv;
    private List<Contact> ls2;
    MessageUserAdapter uAdapt;
    private List<String>messageContactList;
    private BottomSheetDialog bsd;
    String name;

    FirebaseUser currentUser;
    DatabaseReference reference;
    DrawerLayout dl;
    ActionBarDrawerToggle toggle;
    NavigationView navi;
    CircleImageView mainPhoto;
    TextView navName;
    TextView navEmail;
    CircleImageView navPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent= getIntent();
        TextView search1=findViewById(R.id.search1);
        rv=findViewById(R.id.rv);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        rv.setHasFixedSize(true);
        rv.setLayoutManager(linearLayoutManager);


        messageContactList=new ArrayList<>();
        dl=findViewById(R.id.drawer);
        mainPhoto=findViewById(R.id.mainPhoto);
        navi=findViewById(R.id.navMenu);
        Toolbar toolbar=findViewById(R.id.maincpl1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                assert contact != null;
                if(contact.getImageURL().equals("")){
                    mainPhoto.setImageResource(R.drawable.dummydp);

                }
                else{
                    Picasso.get().load(contact.getImageURL()).into(mainPhoto);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


/*Getting userslist having the chats*/
        currentUser=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageContactList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message mess = snapshot.getValue(Message.class);
                    assert mess != null;
                    assert currentUser != null;
                    if (mess.getSender().equals(currentUser.getUid())) {
                        messageContactList.add(mess.getReceiver());
                    }
                    if (mess.getReceiver().equals(currentUser.getUid())) {
                        messageContactList.add(mess.getSender());

                    }

                }

                /*Now Displaying chats by Sending it to the adapter*/
                ls2=new ArrayList<>();
                reference= FirebaseDatabase.getInstance().getReference("Users");
                reference.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ls2.clear();
                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                            Contact c=snapshot.getValue(Contact.class);
                            assert c !=null;
                            assert currentUser != null;
                            for(String s:messageContactList){

                                if(c.getId().equals(s)){
                                    if(ls2.size()!=0){
                                        for (Contact c1:ls2){
                                            if(!c.getId().equals(c1.getId())){
                                                ls2.add(c);
                                            }
                                        }

                                    }
                                    else{
                                        ls2.add(c);
                                    }
                                }
                            }


                        }

                        uAdapt=new MessageUserAdapter(ls2,getApplicationContext(),true);
                        rv.setAdapter(uAdapt);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    public void searchButton(View view) {
        Log.d("Check Here PLEASE", "SHEHROZ11");
        Intent intent=new Intent (MainActivity.this,DisplayContactActivity.class);
        startActivity(intent);
    }

    public void menuButton(View view) {
        dl.openDrawer(GravityCompat.START);

        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                assert contact != null;
                navPhoto=findViewById(R.id.navPhoto);
                navEmail=findViewById(R.id.navEmail);
                navName=findViewById(R.id.navUserName);


                if(contact.getImageURL().equals("")){
                    navPhoto.setImageResource(R.drawable.dummydp);
                    navName.setText("Your Name");
                }
                else{
                    Picasso.get().load(contact.getImageURL()).into(navPhoto);
                    navName.setText(contact.getFirstName()+" "+contact.getLastName());
                }

                navEmail.setText(currentUser.getEmail());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        navi.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
               if(menuItem.getItemId()==R.id.logouticon){
                   Intent i=new Intent(MainActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(i);
                   finish();

               }
               return true;
            }
        });
    }

    public void viewProfileButton(View v){

        Intent i=new Intent(MainActivity.this,CreateProfileActivity.class);
        startActivity(i);
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
    }

    public void checkProfileButton(View view) {


        bsd = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetTheme);
        View sView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomheetlayout,
                findViewById(R.id.bSheet));


        bname = sView.findViewById(R.id.bottomName);
        bphone = sView.findViewById(R.id.bottomPhone);
        bgender = sView.findViewById(R.id.bottomGender);
        bbio = sView.findViewById(R.id.bottomBio);
        bphoto = sView.findViewById(R.id.bottomPhoto);


        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
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

        sView.findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CreateProfileActivity.class));
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

}