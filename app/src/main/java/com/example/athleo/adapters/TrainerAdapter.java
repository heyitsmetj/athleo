package com.example.athleo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.User;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder> {

    private List<User> trainerList;

    public TrainerAdapter(List<User> trainerList) {
        this.trainerList = trainerList;
    }

    @NonNull
    @Override
    public TrainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trainer, parent, false);
        return new TrainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainerViewHolder holder, int position) {
        User trainer = trainerList.get(position);
        holder.tvName.setText(trainer.getName());
        holder.tvEmail.setText(trainer.getEmail());
    }

    @Override
    public int getItemCount() {
        return trainerList.size();
    }

    static class TrainerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;

        public TrainerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTrainerName);
            tvEmail = itemView.findViewById(R.id.tvTrainerEmail);
        }
    }
}
