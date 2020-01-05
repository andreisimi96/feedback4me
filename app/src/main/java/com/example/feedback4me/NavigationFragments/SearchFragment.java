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
import android.widget.SearchView;

import com.example.feedback4me.R;
import com.example.feedback4me.Tools.FirebaseWrapper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener
{
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter recyclerAdapter;
    private SearchView userSearchview;

    public SearchFragment() {}

    public static SearchFragment newInstance()
    {
        SearchFragment fragment = new SearchFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        userSearchview = rootView.findViewById(R.id.user_searchview);
        userSearchview.setOnQueryTextListener(this);

        recyclerView = rootView.findViewById(R.id.user_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerAdapter = FirebaseWrapper.getSearchFirebaseRecyclerAdapter(recyclerView, "");
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();

        return rootView;
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        recyclerAdapter.stopListening();
        recyclerAdapter = FirebaseWrapper.getSearchFirebaseRecyclerAdapter(recyclerView, newText);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.startListening();
        return false;
    }
}
