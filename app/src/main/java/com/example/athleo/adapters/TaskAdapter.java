package com.example.athleo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.TrainingTask;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<TrainingTask> taskList;
    private OnTaskSubmitListener listener;

    public interface OnTaskSubmitListener {
        void onSubmitClick(TrainingTask task);
    }

    public TaskAdapter(List<TrainingTask> taskList, OnTaskSubmitListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TrainingTask task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());
        holder.tvStatus.setText(task.getStatus());
        
        // Hide button if already submitted
        if ("Submitted".equals(task.getStatus())) {
            holder.btnSubmit.setVisibility(View.GONE);
        } else {
            holder.btnSubmit.setVisibility(View.VISIBLE);
            holder.btnSubmit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubmitClick(task);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvStatus;
        Button btnSubmit;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvItemTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvItemTaskDesc);
            tvStatus = itemView.findViewById(R.id.tvItemTaskStatus);
            btnSubmit = itemView.findViewById(R.id.btnItemSubmitAction);
        }
    }
}
