package com.trungngo.asm2.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.trungngo.asm2.R;
import com.trungngo.asm2.model.User;
import com.trungngo.asm2.ui.participants_list.participant_detail.ParticipantDetailViewModel;


import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Activity currentContext; //Context of the class that called
    private List<User> userList;
    private ParticipantDetailViewModel participantDetailViewModel;

    public UserAdapter(Activity currentContext, List<User> userList, ParticipantDetailViewModel participantDetailViewModel) {
        this.currentContext = currentContext;
        this.userList = userList;
        this.participantDetailViewModel = participantDetailViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(currentContext).inflate(R.layout.participant_info_cell, parent, false);
        return new ViewHolder(view);
    }

    private void setDetailBtnListener(Button detailBtn, User user){
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(currentContext, R.id.nav_host_fragment).navigate(R.id.participant_detail);
                participantDetailViewModel.setData(user);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.username.setText("Username: " + user.getUsername());
        holder.email.setText("Email: " + user.getEmail());
        setDetailBtnListener(holder.detailBtn, user);
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView email;
        private Button detailBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.participant_info_cell_username);
            this.email = itemView.findViewById(R.id.participant_info_cell_email);
            this.detailBtn = itemView.findViewById(R.id.participant_info_cell_detailBtn);
        }
    }
}
