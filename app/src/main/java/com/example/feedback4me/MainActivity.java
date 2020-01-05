package com.example.feedback4me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.feedback4me.NavigationFragments.FriendsFragment;
import com.example.feedback4me.NavigationFragments.HomeFragment;
import com.example.feedback4me.NavigationFragments.RequestsFragment;
import com.example.feedback4me.NavigationFragments.SearchFragment;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.UserFragments.UserSettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_IMAGE_CAPTURE = 1400;

    BottomNavigationView bottomNavigation;
    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener;

    ImageView userAvatar;

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

        userAvatar = findViewById(R.id.user_avatar_toolbar);

        //setup navigation bar and listener
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigationListener = createBottomNavigationListener();
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);

        //initialize navigation fragments
        homeFragment = HomeFragment.newInstance();
        friendsFragment = FriendsFragment.newInstance();
        requestsFragment = RequestsFragment.newInstance();
        searchFragment = SearchFragment.newInstance();

        //setup toolbar & profile picture
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setProfilePictureInToolbar();

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

    public void openUserSettingsFragment(View view)
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


    private void setProfilePictureInToolbar()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            FirebaseRequestsWrapper.asyncSetAvatar(user.getUid(), userAvatar);
        }
    }
}
