package com.abhinav.kaavalthozhan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.abhinav.kaavalthozhan.adapters.TabAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean admin = false;
        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
        if (sp.contains("admin")){
            admin = sp.getBoolean("admin",false);
        }


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Kaaval Nanban");

        if (admin){
            toolbar.inflateMenu(R.menu.top_app_bar_admin);
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.list).getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,ApproveActivity.class);
                    startActivity(intent);
                }
            });
            menu.findItem(R.id.logout).getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putBoolean("loggedIn",false);
                    spEditor.apply();
                    Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                }
            });
        }else {
            toolbar.inflateMenu(R.menu.top_app_bar);
            Menu menu = toolbar.getMenu();
            menu.findItem(R.id.logout).getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
                    SharedPreferences.Editor spEditor = sp.edit();
                    spEditor.putBoolean("loggedIn",false);
                    spEditor.apply();
                    Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logoutIntent);
                }
            });
        }

//        if (!admin){
//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    if (item.getItemId() == R.id.logout){
//                        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor spEditor = sp.edit();
//                        spEditor.putBoolean("loggedIn",false);
//                        spEditor.apply();
//                        Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
//                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(logoutIntent);
//                    }
//                    return false;
//                }
//            });
//        }else{
//            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    if (item.getItemId() == R.id.logout){
//                        SharedPreferences sp = getApplicationContext().getSharedPreferences("com.abhinav.KaavalThozhan", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor spEditor = sp.edit();
//                        spEditor.putBoolean("loggedIn",false);
//                        spEditor.apply();
//                        Intent logoutIntent = new Intent(MainActivity.this,LoginActivity.class);
//                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(logoutIntent);
//                    }else if (item.getItemId() == R.id.list){
//                        Intent intent = new Intent(MainActivity.this,ApproveActivity.class);
//                        startActivity(intent);
//                    }
//                    return false;
//                }
//            });
//        }


        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SearchFragment(), "Search");
        adapter.addFragment(new AddVehicle(), "Add");
        adapter.addFragment(new FrequentFragment(), "Frequent");
        viewPager.setAdapter(adapter);
    }
}
