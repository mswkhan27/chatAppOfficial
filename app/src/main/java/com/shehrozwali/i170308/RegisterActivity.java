package com.shehrozwali.i170308;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {
EditText email,password,passwordConfirm;
FirebaseAuth auth;
DatabaseReference reference;
Button btn_Register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent i= getIntent();
        email=findViewById(R.id.email);
        password=findViewById(R.id.createPassword);
        passwordConfirm=findViewById(R.id.confirmPassword);



        btn_Register=findViewById(R.id.btn_Register);
        auth = FirebaseAuth.getInstance();
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email=email.getText().toString();
                Random rand = new Random();
                int randNo = rand.nextInt(10);
                String randomNum=Integer.toString(randNo);
                String txt_username="";

                /*Converting Email to Char Array for users*/
                char[]  charEmail= txt_email.toCharArray();
                for (int i=0;charEmail[i]!='@';i++){

                    txt_username+=charEmail[i];
                }


                String txt_password=password.getText().toString();
                String txt_passwordConfirm=passwordConfirm.getText().toString();
                if (TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)||TextUtils.isEmpty(txt_passwordConfirm) ){

                    Toast.makeText(RegisterActivity.this,"All Fields are required",Toast.LENGTH_SHORT).show();
                }

                else if (txt_password.length()<5){
                    Toast.makeText(RegisterActivity.this,"Password must be atleast 5 character",Toast.LENGTH_SHORT).show();
                }
                else {

                    register(txt_username,txt_email,txt_password);

                }
            }
        });

    }
    private void register(final String username, String email, String password){

        Log.d("Check HERE","SHEHROZKKK");
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String userid=firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            HashMap<String,String> hashMap= new HashMap<>();
                            hashMap.put("id",userid);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","");
                            hashMap.put("FirstName","");
                            hashMap.put("LastName","");
                            hashMap.put("Phone","");
                            hashMap.put("Gender","");
                            hashMap.put("DateofBirth","");
                            hashMap.put("Bio","");


                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent=new Intent (RegisterActivity.this,CreateProfileActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK );
                                        startActivity(intent);
                                        finish();

                                    }

                                }
                            });

                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"You can't register with this email or password",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


    public void goSignInButton(View view) {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
}