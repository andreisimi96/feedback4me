package com.example.feedback4me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;

import com.example.feedback4me.NavigationFragments.FriendsFragment;
import com.example.feedback4me.NavigationFragments.HomeFragment;
import com.example.feedback4me.NavigationFragments.RequestsFragment;
import com.example.feedback4me.NavigationFragments.SearchFragment;
import com.example.feedback4me.SettingsFragments.UserSettingsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_IMAGE_CAPTURE = 1400;

    BottomNavigationView bottomNavigation;
    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener;

    //navigation fragments;
    HomeFragment homeFragment;
    FriendsFragment friendsFragment;
    SearchFragment searchFragment;
    RequestsFragment requestsFragment;

    boolean firstActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        firstActivity = true;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup navigation bar and listener
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigationListener = createBottomNavigationListener();
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);

        //initialize navigation fragments
        homeFragment = HomeFragment.newInstance();
        friendsFragment = FriendsFragment.newInstance();
        requestsFragment = RequestsFragment.newInstance();
        searchFragment = SearchFragment.newInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //start home fragment
        openNavigationFragment(homeFragment);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener createBottomNavigationListener ()
    {
         return new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.navigation_home:
                                openNavigationFragment(homeFragment);
                                return true;
                            case R.id.navigation_friends:
                                openNavigationFragment(friendsFragment);
                                return true;
                            case R.id.navigation_requests:
                                openNavigationFragment(requestsFragment);
                                return true;
                            case R.id.navigation_search:
                                openNavigationFragment(searchFragment);
                                return true;
                        }
                        return false;
                    }
                };
    }

    public void openNavigationFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        if (firstActivity)
        {
            firstActivity = false;
        }
        else
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public void openUserFragment(View view)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.main_layout, UserSettingsFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        handleSelectedNavigationOnBack();
    }

    /*
        Without this, on back hit, navigation buttons will remain in the old state.
     */
    public void handleSelectedNavigationOnBack()
    {
        if (homeFragment.isVisible())
        {
            bottomNavigation.getMenu().findItem(R.id.navigation_home).setChecked(true);
        }
        else if (friendsFragment.isVisible())
        {
            bottomNavigation.getMenu().findItem(R.id.navigation_friends).setChecked(true);
        }
        else if (requestsFragment.isVisible())
        {
            bottomNavigation.getMenu().findItem(R.id.navigation_requests).setChecked(true);
        }
        else if (searchFragment.isVisible())
        {
            bottomNavigation.getMenu().findItem(R.id.navigation_search).setChecked(true);
        }
    }

    public void logOut(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));

        GoogleSignIn.getClient(
                MainActivity.this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        ).signOut();

    }

    public void changeProfilePicture(View view)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }
}
