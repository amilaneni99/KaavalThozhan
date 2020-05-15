package com.abhinav.kaavalthozhan;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.abhinav.kaavalthozhan.R;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;


public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button login;
    FirebaseFirestore db;
    String usernameText,passwordText;
    LottieAnimationView lottieAnimationView;
    String TAG = "Login Activity";
    TextView label;
    Switch adminSwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        lottieAnimationView = findViewById(R.id.lottieAnimationView);
        adminSwitch = findViewById(R.id.adminSwitch);
        label = findViewById(R.id.label);

        db = FirebaseFirestore.getInstance();

        if (adminSwitch.isChecked()){
            label.setText("Admin Username");
            username.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            username.setHint("Username");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((username.getText().toString().trim().length() == 0) || (password.getText().toString().trim().length() == 0) || (username.getText() == null) || (password.getText() == null)){
                        Toast.makeText(LoginActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    }else{
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        usernameText = username.getText().toString();
                        passwordText = password.getText().toString();

                        db.collection("credentials")
                                .document("admin")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if (document.getData().get("username").equals(usernameText) && document.getData().get("password").equals(sha256(passwordText))){
                                                lottieAnimationView.setVisibility(View.GONE);
                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("admin",true);
                                                startActivity(intent);
                                                editSharedPrefs(true);
                                                SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor spEditor = sp.edit();
                                                spEditor.putBoolean("admin",true);
                                                spEditor.apply();
                                            }else{
                                                Toast.makeText(LoginActivity.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();
                                                lottieAnimationView.setVisibility(View.GONE);
                                            }
                                        }else{
                                            Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_SHORT).show();
                                            lottieAnimationView.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                }
            });
        }else{
            label.setText("Mobile Number");
            username.setInputType(InputType.TYPE_CLASS_PHONE);
            username.setHint("Mobile Number");

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ((username.getText().toString().trim().length() == 0) || (password.getText().toString().trim().length() == 0) || (username.getText() == null) || (password.getText() == null)){
                        Toast.makeText(LoginActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                    }else{
                        lottieAnimationView.setVisibility(View.VISIBLE);
                        usernameText = username.getText().toString();
                        passwordText = password.getText().toString();

                        db.collection("credentials")
                                .document(usernameText)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int flag = 0;
                                            DocumentSnapshot document = task.getResult();
                                            if (document.getData().get("username").equals(usernameText) && document.getData().get("password").equals(sha256(passwordText))){
                                                flag = 1;
                                            }
                                            if (flag == 0){
                                                Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                                editSharedPrefs(false);
                                                lottieAnimationView.setVisibility(View.GONE);
                                            }else{
                                                lottieAnimationView.setVisibility(View.GONE);
                                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra("admin",false);
                                                startActivity(intent);
                                                editSharedPrefs(true);
                                                SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor spEditor = sp.edit();
                                                spEditor.putBoolean("admin",false);
                                                spEditor.apply();
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            lottieAnimationView.setVisibility(View.GONE);
                                            editSharedPrefs(false);
                                        }
                                    }
                                });
                    }
                }
            });
        }

        adminSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    label.setText("Admin Username");
                    username.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                    username.setHint("Username");

                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((username.getText().toString().trim().length() == 0) || (password.getText().toString().trim().length() == 0) || (username.getText() == null) || (password.getText() == null)){
                                Toast.makeText(LoginActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                            }else{
                                lottieAnimationView.setVisibility(View.VISIBLE);
                                usernameText = username.getText().toString();
                                passwordText = password.getText().toString();

                                db.collection("credentials")
                                        .document("admin")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()){
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.getData().get("username").equals(usernameText) && document.getData().get("password").equals(sha256(passwordText))){
                                                        lottieAnimationView.setVisibility(View.GONE);
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("admin",true);
                                                        startActivity(intent);
                                                        editSharedPrefs(true);
                                                        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor spEditor = sp.edit();
                                                        spEditor.putBoolean("admin",true);
                                                        spEditor.apply();
                                                    }else{
                                                        Toast.makeText(LoginActivity.this,"Wrong Credentials",Toast.LENGTH_SHORT).show();
                                                        lottieAnimationView.setVisibility(View.GONE);
                                                    }
                                                }else{
                                                    Toast.makeText(LoginActivity.this,"Error Occurred",Toast.LENGTH_SHORT).show();
                                                    lottieAnimationView.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            }
                        }
                    });
                }else{
                    label.setText("Mobile Number");
                    username.setInputType(InputType.TYPE_CLASS_PHONE);
                    username.setHint("Mobile Number");

                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if ((username.getText().toString().trim().length() == 0) || (password.getText().toString().trim().length() == 0) || (username.getText() == null) || (password.getText() == null)){
                                Toast.makeText(LoginActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                            }else{
                                lottieAnimationView.setVisibility(View.VISIBLE);
                                usernameText = username.getText().toString();
                                passwordText = password.getText().toString();

                                db.collection("credentials")
                                        .document(usernameText)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int flag = 0;
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.getData().get("username").equals(usernameText) && document.getData().get("password").equals(sha256(passwordText))){
                                                        flag = 1;
                                                    }

                                                    if (flag == 0){
                                                        Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                                                        editSharedPrefs(false);
                                                        lottieAnimationView.setVisibility(View.GONE);
                                                    }else{
                                                        lottieAnimationView.setVisibility(View.GONE);
                                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra("admin",false);
                                                        startActivity(intent);
                                                        editSharedPrefs(true);
                                                        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                                                        SharedPreferences.Editor spEditor = sp.edit();
                                                        spEditor.putBoolean("admin",false);
                                                        spEditor.apply();
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                    lottieAnimationView.setVisibility(View.GONE);
                                                    editSharedPrefs(false);
                                                }
                                            }
                                        });
                            }
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

    public void editSharedPrefs(boolean status){
        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putBoolean("loggedIn",status);
        spEditor.apply();
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
