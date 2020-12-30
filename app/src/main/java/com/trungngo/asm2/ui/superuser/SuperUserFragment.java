package com.trungngo.asm2.ui.superuser;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.adapters.SiteAdapter;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.create_site.CreateSiteViewModel;
import com.trungngo.asm2.ui.edit_site.EditSiteViewModel;

import java.util.ArrayList;
import java.util.List;

public class SuperUserFragment extends Fragment {

    private SuperUserViewModel mViewModel;
    private EditSiteViewModel editSiteViewModel;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    User currentUserObject = null;
    String currentUserDocId = null;

    //View variables
    private List<Site> siteList;
    private RecyclerView siteInfoContainer;

    public static SuperUserFragment newInstance() {
        return new SuperUserFragment();
    }

    private void linkViewElements(View rootView) {
        siteInfoContainer = rootView.findViewById(R.id.site_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        siteInfoContainer.setLayoutManager(linearLayoutManager);
    }

    private void loadSiteCells() {
        siteList = new ArrayList<>();
        db.collection(Constants.FSSite.siteCollection)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        siteList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Site site = doc.toObject(Site.class);
                                siteList.add(site);
                            }
                        } else {
                        }

                        SiteAdapter siteAdapter = new SiteAdapter(getActivity(), siteList, true, editSiteViewModel);
                        siteInfoContainer.setAdapter(siteAdapter);
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_user, container, false);
        linkViewElements(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        loadSiteCells();


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(SuperUserViewModel.class);
        //Use the ViewModel
        mViewModel = ViewModelProviders.of(getActivity()).get(SuperUserViewModel.class);
        mViewModel.getCurrentUserObject().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                currentUserObject = user;
            }
        });

        mViewModel.getCurrentUserDocId().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUserDocId = s;
            }
        });

        //EditSiteViewModel
        editSiteViewModel = new ViewModelProvider(getActivity()).get(EditSiteViewModel.class);
    }

}