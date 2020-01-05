package com.example.feedback4me.NavigationFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feedback4me.LoginActivity;
import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseWrapper;
import com.example.feedback4me.Tools.GlideWrapper;
import com.example.feedback4me.UserFragments.FeedbackDialogFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment
{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter recyclerAdapter;

    public HomeFragment() {}

    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        fillWithFirebaseData(rootView);
        attachClickHandlers(rootView);

        recyclerView = rootView.findViewById(R.id.feedback_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        //recylerAdapter
        recyclerAdapter = FirebaseWrapper.getFeedbackFirebaseRecyclerAdapter(FirebaseAuth.getInstance().getUid(), recyclerView);
        recyclerAdapter.startListening();

        return rootView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        recyclerAdapter.stopListening();

    }

    public void fillWithFirebaseData(View rootView)
    {
        ImageView userAvatar = rootView.findViewById(R.id.user_avatar_home);
        TextView userName = rootView.findViewById(R.id.user_name_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();

            userName.setText(name);
            FirebaseWrapper.asyncSetAvatar(user.getUid(), userAvatar);
        }
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    public void attachClickHandlers(View rootView)
    {
        Button writeFeedback = rootView.findViewById(R.id.write_feedback);
        writeFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create the fragment and show it as a dialog.
                DialogFragment newFragment = FeedbackDialogFragment.newInstance();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

}
