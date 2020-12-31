package com.trungngo.asm2.ui.participants_list;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.trungngo.asm2.Constants;
import com.trungngo.asm2.R;
import com.trungngo.asm2.adapters.UserAdapter;
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.participants_list.participant_detail.ParticipantDetailViewModel;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsListFragment extends Fragment {

    private ParticipantsListViewModel mViewModel;
    private ParticipantDetailViewModel participantDetailViewModel;

    //Firebase, FireStore
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    //View variables
    private List<User> userList;
    private RecyclerView participantInfoContainer;

    private Site currentSite;

    public static ParticipantsListFragment newInstance() {
        return new ParticipantsListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        linkViewElements(view);
        return view;
    }

    /**
     * Link all view elements to global variables
     * @param rootView
     */
    private void linkViewElements(View rootView) {
        participantInfoContainer = rootView.findViewById(R.id.participant_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        participantInfoContainer.setLayoutManager(linearLayoutManager);
    }


    /**
     * Render all participants info cells, also listen to collections' real time update for re-rendering accordingly
     */
    private void loadParticipantsList() {
        userList = new ArrayList<>();

        if (currentSite == null) {
            return;
        }

        db.collection(Constants.FSUser.userCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        userList.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            if (currentSite.getParticipantsId().contains(doc.getId())){
                                User user = doc.toObject(User.class);
                                userList.add(user);
                            }
                        }

                        UserAdapter userAdapter = new UserAdapter(getActivity(), userList, participantDetailViewModel);
                        participantInfoContainer.setAdapter(userAdapter);
                    }
                });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        participantDetailViewModel = ViewModelProviders.of(getActivity()).get(ParticipantDetailViewModel.class);
        mViewModel = ViewModelProviders.of(getActivity()).get(ParticipantsListViewModel.class);
        //Use the ViewModel

        mViewModel.getCurrentSite().observe(getActivity(), new Observer<Site>() {
            @Override
            public void onChanged(Site site) {
                currentSite = site;
                loadParticipantsList();
            }
        });


    }

}