package com.abhinav.kaavalthozhan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText personName,usernameSignup,passwordSignup;
    String name,username,password;
    Button signup;
    FirebaseFirestore db;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        personName = findViewById(R.id.personName);
        usernameSignup = findViewById(R.id.usernameSignUp);
        passwordSignup = findViewById(R.id.passwordSignUp);
        signup = findViewById(R.id.signUpButton);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.alreadySignedUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                editSharedPrefs(true);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int l1 = personName.getText().toString().trim().length();
                int l2 = usernameSignup.getText().toString().trim().length();
                int l3 = passwordSignup.getText().toString().trim().length();
                if ((l1==0) || (l2==0) || (l3==0)){
                    Toast.makeText(SignupActivity.this,"Fill in all the fields",Toast.LENGTH_SHORT).show();
                }else{
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    name = personName.getText().toString();
                    username = usernameSignup.getText().toString();
                    password = passwordSignup.getText().toString();
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",name);
                    map.put("username",username);
                    map.put("password",sha256(password));
                    db.collection("pending")
                            .document(username)
                            .set(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SignupActivity.this,"Signed Up. Please wait for confirmation",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra("username",username);
                                    startActivity(intent);
                                    editSharedPrefs(true);
                                    lottieAnimationView.setVisibility(View.GONE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this,"Failed to Sign Up!",Toast.LENGTH_SHORT).show();
                                    editSharedPrefs(false);
                                    lottieAnimationView.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });
        findViewById(R.id.devName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.linkedin.com/in/amilaneni-abhinav"));
                startActivity(intent);
            }
        });
    }

    public String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void editSharedPrefs(boolean status){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("signedUp",status);
        spEditor.apply();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
