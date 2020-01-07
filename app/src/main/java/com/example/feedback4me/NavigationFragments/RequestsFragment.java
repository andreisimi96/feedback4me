package com.example.feedback4me.NavigationFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseAdaptersWrapper;
import com.example.feedback4me.Tools.FirebaseRequestsWrapper;
import com.example.feedback4me.User.RequestsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

public class RequestsFragment extends Fragment
{
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter recyclerAdapter;

    public RequestsFragment() {}

    public static RequestsFragment newInstance()
    {
        RequestsFragment fragment = new RequestsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = rootView.findViewById(R.id.requests_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerAdapter = FirebaseAdaptersWrapper.getRequestsFirebaseRecyclerAdapter(FirebaseAuth.getInstance().getUid());
        recyclerView.setAdapter(recyclerAdapter);
        attachSwipeBehavior(recyclerView);

        recyclerAdapter.startListening();


        return rootView;
    }

    public void attachSwipeBehavior(RecyclerView recyclerView)
    {
        ItemTouchHelper helperRight = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
            {
                String crtUserUid = FirebaseAuth.getInstance().getUid();
                String reqUserUid = ((RequestsViewHolder)viewHolder).getUserUid();

                FirebaseRequestsWrapper.acceptFriendRequest(reqUserUid, crtUserUid);
            }
        });
        helperRight.attachToRecyclerView(recyclerView);

        ItemTouchHelper helperLeft = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT)
                {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
                    {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
                    {
                        String crtUserUid = FirebaseAuth.getInstance().getUid();
                        String reqUserUid = ((RequestsViewHolder)viewHolder).getUserUid();

                        FirebaseRequestsWrapper.deleteFriendRequest(reqUserUid, crtUserUid);
                    }
                });
        helperLeft.attachToRecyclerView(recyclerView);
    }
}
