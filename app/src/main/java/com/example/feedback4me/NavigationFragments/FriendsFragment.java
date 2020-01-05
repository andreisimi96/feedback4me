package com.example.feedback4me.NavigationFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseWrapper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class FriendsFragment extends Fragment
{
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter recyclerAdapter;

    public FriendsFragment() {}

    public static FriendsFragment newInstance()
    {
        FriendsFragment fragment = new FriendsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        recyclerView = rootView.findViewById(R.id.friends_recyclerview);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        //recylerAdapter
        recyclerAdapter = FirebaseWrapper.getFriendsFirebaseRecyclerAdapter(FirebaseAuth.getInstance().getUid(), recyclerView);

        // Inflate the layout for this fragment
        return rootView;
    }

    public void onStart()
    {
        super.onStart();
        recyclerAdapter.startListening();
    }

    public void onStop()
    {
        super.onStop();
        recyclerAdapter.stopListening();
    }
}
