package com.example.athleo.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.Announcement;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcementList;

    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.tvTitle.setText(announcement.getTitle());
        holder.tvAuthor.setText("By: " + announcement.getAuthorName());
        holder.tvMessage.setText(announcement.getMessage());

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(announcement.getTimestamp());
        String date = DateFormat.format("dd MMM yyyy, hh:mm a", cal).toString();
        holder.tvDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvDate, tvMessage;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAnnounceTitle);
            tvAuthor = itemView.findViewById(R.id.tvAnnounceAuthor);
            tvDate = itemView.findViewById(R.id.tvAnnounceDate);
            tvMessage = itemView.findViewById(R.id.tvAnnounceMessage);
        }
    }
}
