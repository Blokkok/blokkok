package com.blokkok.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar _actionBar;
    private DrawerLayout _drawer;
    private NavigationView _drawer_navView;
    private ActionBarDrawerToggle _toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _actionBar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(_actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        _drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        _drawer_navView = (NavigationView) findViewById(R.id.nav_view);

        _toggle = new ActionBarDrawerToggle(MainActivity.this, _drawer, _actionBar, R.string.app_name, R.string.app_name);
        _drawer.addDrawerListener(_toggle);
        _toggle.syncState();

        _drawer_navView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

        /*
        if(sharedPreferences.getBoolean("dark_mode", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        */

        if(savedInstanceState == null) {
            getSupportActionBar().setSubtitle("Projects");

            HomeFragment homeFragment = HomeFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent i1 = new Intent();

        FragmentManager drawerFragmentManager = getSupportFragmentManager();
        FragmentTransaction drawerFragmentTransaction = drawerFragmentManager.beginTransaction();

        switch (item.getItemId()) {
            case R.id.projects:
                _drawer.closeDrawer(GravityCompat.START);
                _drawer_navView.setCheckedItem(R.id.projects);
                getSupportActionBar().setSubtitle("Projects");

                HomeFragment homeFragment = HomeFragment.newInstance();

                drawerFragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                drawerFragmentTransaction.commit();

                break;
            case R.id.modules:
                _drawer.closeDrawer(GravityCompat.START);
                _drawer_navView.setCheckedItem(R.id.modules);
                getSupportActionBar().setSubtitle("Modules");

                ModulesFragment modulesFragment = ModulesFragment.newInstance();

                drawerFragmentTransaction.replace(R.id.fragmentContainer, modulesFragment);
                drawerFragmentTransaction.commit();

                break;
            case R.id.about:
                _drawer.closeDrawer(GravityCompat.START);
                _drawer_navView.setCheckedItem(R.id.about);
                getSupportActionBar().setSubtitle("About");

                AboutFragment aboutFragment = AboutFragment.newInstance();

                drawerFragmentTransaction.replace(R.id.fragmentContainer, aboutFragment);
                drawerFragmentTransaction.commit();
                break;
            case R.id.settings:
                _drawer.closeDrawer(GravityCompat.START);
                _drawer_navView.setCheckedItem(R.id.settings);
                getSupportActionBar().setSubtitle("Settings");

                SettingsFragment settingsFragment = new SettingsFragment();

                drawerFragmentTransaction.replace(R.id.fragmentContainer, settingsFragment);
                drawerFragmentTransaction.commit();
                break;
            case R.id.dc:
                i1.setAction(Intent.ACTION_VIEW);
                i1.setData(Uri.parse("https://discord.gg/"));
                startActivity(i1);
                break;
            case R.id.gh:
                i1.setAction(Intent.ACTION_VIEW);
                i1.setData(Uri.parse("https://github.com/Blokkok"));
                startActivity(i1);
                break;
            case R.id.web:
                i1.setAction(Intent.ACTION_VIEW);
                i1.setData(Uri.parse("https://blokkok.tk/"));
                startActivity(i1);
                break;
        }

        return false;
    }
}