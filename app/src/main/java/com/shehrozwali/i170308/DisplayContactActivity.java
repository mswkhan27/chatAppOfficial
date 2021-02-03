package com.shehrozwali.i170308;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shehrozwali.i170308.Adapter.UserAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DisplayContactActivity extends AppCompatActivity {

    RecyclerView rv;
    List<Contact> ls;
    private  UserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contact);
        Intent i= getIntent();

        rv=findViewById(R.id.rv);
        ls = new ArrayList<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        RecyclerView.LayoutManager lm=new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(lm);

        readUsers();

        EditText search1=findViewById(R.id.searchuser);
        search1.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<Contact> filteredList=new ArrayList<>();

                for (Contact ca: ls){
                    if (ca.getUsername().toLowerCase().contains(s.toString().toLowerCase())){
                        filteredList.add(ca);

                    }

                }
                adapter.filterList(filteredList);

            }
        });


    }

    private void readUsers() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ls.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Contact c=snapshot.getValue(Contact.class);
                    assert c !=null;
                    assert firebaseUser != null;
                    if (!c.getId().equals(firebaseUser.getUid())) {
                        Log.d("PROFILE URL", c.getImageURL());
                        ls.add(c);
                    }


                }

                adapter=new UserAdapter(ls, getApplicationContext(),false);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        }



    public void backButton(View view) {
        finish();
    }
}