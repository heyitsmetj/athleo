package com.example.athleo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.MatchSchedule;

import java.util.List;

public class MatchScheduleAdapter extends RecyclerView.Adapter<MatchScheduleAdapter.MatchViewHolder> {

    private List<MatchSchedule> scheduleList;

    public MatchScheduleAdapter(List<MatchSchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match_schedule, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchSchedule schedule = scheduleList.get(position);
        holder.tvTitle.setText(schedule.getTitle());
        holder.tvDate.setText(schedule.getDate());
        holder.tvTime.setText(schedule.getTime());
        holder.tvOpponent.setText("vs " + schedule.getOpponent());
        holder.tvLocation.setText(schedule.getLocation());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvTime, tvOpponent, tvLocation;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMatchTitle);
            tvDate = itemView.findViewById(R.id.tvMatchDate);
            tvTime = itemView.findViewById(R.id.tvMatchTime);
            tvOpponent = itemView.findViewById(R.id.tvMatchOpponent);
            tvLocation = itemView.findViewById(R.id.tvMatchLocation);
        }
    }
}
