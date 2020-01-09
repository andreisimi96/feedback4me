package com.example.feedback4me.NavigationFragments;

import android.content.Intent;
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
import com.example.feedback4me.Tools.FirebaseAdaptersWrapper;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.User.Notifications;
import com.example.feedback4me.UserFragments.FeedbackDialogFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeFragment extends Fragment
{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter recyclerAdapter;

    private Notifications notifications;

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
        setupRecyclerAdapter(rootView);

        return rootView;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        recyclerAdapter.stopListening();

    }

    private void fillWithFirebaseData(View rootView)
    {
        ImageView userAvatar = rootView.findViewById(R.id.user_avatar_home);
        TextView username = rootView.findViewById(R.id.user_name_home);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();

            username.setText(name);
            FirebaseRequestsWrapper.asyncSetAvatar(user.getUid(), userAvatar);
        }
        else
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

    }

    private void attachClickHandlers(View rootView)
    {
        Button writeFeedback = rootView.findViewById(R.id.write_feedback);
        writeFeedback.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create the fragment and show it as a dialog.
                FeedbackDialogFragment newFragment = FeedbackDialogFragment.newInstance();
                newFragment.setUserUid(FirebaseAuth.getInstance().getUid());
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void setupRecyclerAdapter(View rootView)
    {
        recyclerView = rootView.findViewById(R.id.feedback_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerAdapter = FirebaseAdaptersWrapper.getFeedbackFirebaseRecyclerAdapter(getActivity(), FirebaseAuth.getInstance().getUid());
        recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
        {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount)
            {
                recyclerView.smoothScrollToPosition(positionStart);
            }
        });

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
    }

}
