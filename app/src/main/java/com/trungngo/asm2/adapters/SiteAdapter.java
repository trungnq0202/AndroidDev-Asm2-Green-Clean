package com.trungngo.asm2.adapters;

import android.annotation.SuppressLint;
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
import com.trungngo.asm2.model.Site;
import com.trungngo.asm2.ui.edit_site.EditSiteViewModel;
import com.trungngo.asm2.ui.participants_list.ParticipantsListViewModel;
import com.trungngo.asm2.ui.site_detail.SiteDetailViewModel;

import java.util.List;

public class SiteAdapter extends RecyclerView.Adapter<SiteAdapter.ViewHolder> {

    private Activity currentContext; //Context of the class that called
    private List<Site> siteList;
    private boolean editable;
    private EditSiteViewModel editSiteViewModel;
    private ParticipantsListViewModel participantsListViewModel;
    private SiteDetailViewModel siteDetailViewModel;

    public SiteAdapter(Activity currentContext, List<Site> siteList, boolean editable, EditSiteViewModel editSiteViewModel, ParticipantsListViewModel participantsListViewModel, SiteDetailViewModel siteDetailViewModel) {
        this.currentContext = currentContext;
        this.siteList = siteList;
        this.editable = editable;
        this.editSiteViewModel = editSiteViewModel;
        this.participantsListViewModel = participantsListViewModel;
        this.siteDetailViewModel = siteDetailViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(currentContext).inflate(R.layout.site_info_cell, parent, false);
        return new ViewHolder(view);
    }

    private void setEditBtnListener(Button editBtn, Site site){
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(currentContext, R.id.nav_host_fragment).navigate(R.id.edit_site);
                editSiteViewModel.setCurrentSiteData(site);
            }
        });
    }

    private void setShowParticipantsBtnListener(Button showParticipantsBtn, Site site){
        showParticipantsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(currentContext, R.id.nav_host_fragment).navigate(R.id.site_participants);
                participantsListViewModel.setCurrentSiteData(site);
            }
        });
    }

    private void setSiteDetailBtnListener(Button detailBtn, Site site){
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Navigation.findNavController(currentContext, R.id.nav_host_fragment).navigate(R.id.site_detail);
                siteDetailViewModel.setData(site);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Site site = siteList.get(position);
        holder.siteNameTextView.setText("Event: " + site.getSiteName());
        holder.placeNameTextView.setText(site.getPlaceName());
        //Admin/Superuser
        if (editable) {
            holder.detailBtn.setVisibility(View.GONE);
            setEditBtnListener(holder.editBtn, site);
            setShowParticipantsBtnListener(holder.participantsListBtn, site);
            //Participant
        } else {
            holder.editBtn.setVisibility(View.GONE);
            holder.participantsListBtn.setVisibility(View.GONE);
            setSiteDetailBtnListener(holder.detailBtn, site);
        }
    }

    @Override
    public int getItemCount() {
        return this.siteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView siteNameTextView;
        public TextView placeNameTextView;
        public Button editBtn;
        public Button detailBtn;
        public Button participantsListBtn;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.siteNameTextView = itemView.findViewById(R.id.site_info_cell_siteName);
            this.placeNameTextView = itemView.findViewById(R.id.site_info_cell_placeName);
            this.editBtn = itemView.findViewById(R.id.site_info_cell_editBtn);
            this.detailBtn = itemView.findViewById(R.id.site_info_cell_detailBtn);
            this.participantsListBtn = itemView.findViewById(R.id.site_info_cell_showUserListBtn);
        }

    }


}
