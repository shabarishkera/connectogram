package com.example.connectogram;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.example.connectogram.adapters.Adapter_discover;
import com.example.connectogram.models.Model_discover;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    FirebaseAuth fauth;
    private String mParam2;

    RecyclerView recyclerView;
    Adapter_discover adapterDiscover;
    List<Model_discover> userlist;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fauth=FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_discover, container, false);


         recyclerView=view.findViewById(R.id.users_recycleView);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         userlist=new ArrayList<>();
         getAllusers();

        return  view;
    }

    private void getAllusers() {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear(); // Clear the list before adding new data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                  Model_discover modelDiscover=dataSnapshot.getValue(Model_discover.class);


                  if(modelDiscover.getUid()!=null&&!modelDiscover.getUid().equals(fuser.getUid()))
                      userlist.add(modelDiscover);


                }
                adapterDiscover=new Adapter_discover(getActivity(),userlist);
                // Notify the adapter that the data set has changed
               recyclerView.setAdapter(adapterDiscover);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    public void checkUserStatus()
    {
        FirebaseUser user= fauth.getCurrentUser();
        if(user!=null)
        {
            //email.setText(user.getEmail());
        }
        else {
            startActivity(new Intent(getActivity(),MainActivity.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
     inflater.inflate(R.menu.menu_main,menu);


     menu.findItem(R.id.action_add_post).setVisible(false);
     MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when user submits query

                if(!TextUtils.isEmpty(query.trim()))
                {
                    searchusers(query);
                }
                else{
                    getAllusers();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as user types
                // Here you can filter the userlist based on the newText
                // For example:
                if(!TextUtils.isEmpty(newText.trim()))
                {
                    searchusers(newText);
                }
                else{
                    getAllusers();
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    private void searchusers(String query) {


        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userlist.clear(); // Clear the list before adding new data

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Model_discover modelDiscover=dataSnapshot.getValue(Model_discover.class);

                    if(!modelDiscover.getUid().equals(fuser.getUid())){
                        if(modelDiscover.getName().toLowerCase().contains(query.toLowerCase())|| modelDiscover.getEmail().toLowerCase().contains(query.toLowerCase()))
                        userlist.add(modelDiscover);
                    }

                }
                adapterDiscover=new Adapter_discover(getActivity(),userlist);
                // Notify the adapter that the dataa set has changed
                adapterDiscover.notifyDataSetChanged();
                recyclerView.setAdapter(adapterDiscover);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_logout)
        {
            fauth.signOut();
            checkUserStatus();
            return  true;
        }
        if(id==R.id.action_settings)
        {
            startActivity(new Intent(getActivity(),SettingsMainActivity.class));


        }
        if(id==R.id.action_add_post)
        {
            startActivity(new Intent(getActivity(),AddPostActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}