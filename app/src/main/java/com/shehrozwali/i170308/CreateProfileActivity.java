package com.shehrozwali.i170308;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateProfileActivity extends AppCompatActivity {
    private EditText firstName, lastName,dob,phone1,bio1;
    RadioButton male,female,diff;
    DatabaseReference reference;
    FirebaseUser currentUser;
    StorageReference mStorageRef;
    private CircleImageView uploadProfileImage;
    private CircleImageView dp1;
    private static final int PICK_IMAGE=1;
    Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);


        Intent i=getIntent();
        firstName=findViewById(R.id.fName);
        lastName=findViewById(R.id.lName);
        dob= findViewById(R.id.dateBirth);
        male=findViewById(R.id.addMale);
        female=findViewById(R.id.addFemale);
        diff=findViewById(R.id.addDiff);
        phone1=findViewById(R.id.addPhone);
        bio1=findViewById(R.id.addBio);

        uploadProfileImage=(CircleImageView)findViewById(R.id.dp);
        imgUri = Uri.parse("android.resource://com.shehrozwali.i170308/" + R.drawable.dummydp);
        seeProfileDetails();



        uploadProfileImage.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                Intent gallery =new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select Display"),PICK_IMAGE);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE && resultCode==RESULT_OK)
        {
            imgUri=data.getData();

            uploadProfileImage.setImageURI(imgUri);

        }
    }
    private void updateProfile(){
            String g;
            if(male.isChecked()){
                g=male.getText().toString();

            }
            else if(female.isChecked()){
                g=female.getText().toString();
            }
            else{
                g=diff.getText().toString();
            }

            String first=firstName.getText().toString();
            String last=lastName.getText().toString();
            String bio=bio1.getText().toString();
            String sdob=dob.getText().toString();
            String phNo=phone1.getText().toString();

        mStorageRef=FirebaseStorage.getInstance().getReference();
        mStorageRef= mStorageRef.child("usersDp/"+currentUser.getUid()+"/dp.jpg");
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
                                        String dpTaken=uri.toString();
                                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                        reference=FirebaseDatabase.getInstance().getReference("Users");

                                        if (user!=null){
                                            reference.child(user.getUid()).child("Bio").setValue(bio);
                                            reference.child(user.getUid()).child("FirstName").setValue(first);
                                            reference.child(user.getUid()).child("LastName").setValue(last);
                                            reference.child(user.getUid()).child("DateofBirth").setValue(sdob);
                                            reference.child(user.getUid()).child("Phone").setValue(phNo);
                                            reference.child(user.getUid()).child("imageURL").setValue(dpTaken);
                                            reference.child(user.getUid()).child("Gender").setValue(g);
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(CreateProfileActivity.this,
                                "Unable to Upload Image/Data",Toast.LENGTH_LONG).show();
                    }
                });


    }

    public void saveUpdateButton(View view) {
        updateProfile();
        Intent i = new Intent(CreateProfileActivity.this,MainActivity.class);
        startActivity(i);
    }


    private void seeProfileDetails(){
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact=dataSnapshot.getValue(Contact.class);
                assert contact != null;

                if (contact.getImageURL().equals("") ){

                }

                else{

                    Picasso.get().load(contact.getImageURL()).into(uploadProfileImage);

                }

                firstName.setText(contact.getFirstName());
                lastName.setText(contact.getLastName());
                dob.setText(contact.getDateofBirth());
                if(contact.getGender().equals("Male")){
                    male.setSelected(true);

                }
                else if(contact.getGender().equals("Female")){
                    female.setSelected(true);

                }
                else{
                    diff.setSelected(true);

                }

                phone1.setText(contact.getPhone());
                bio1.setText(contact.getBio());



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}