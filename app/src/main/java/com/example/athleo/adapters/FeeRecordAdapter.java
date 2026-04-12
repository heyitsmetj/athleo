package com.example.athleo.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.FeeRecord;

import java.util.List;

public class FeeRecordAdapter extends RecyclerView.Adapter<FeeRecordAdapter.FeeViewHolder> {

    private List<FeeRecord> feeList;
    private OnFeePaidListener listener;

    public interface OnFeePaidListener {
        void onPaidClick(FeeRecord record);
    }

    public FeeRecordAdapter(List<FeeRecord> feeList, OnFeePaidListener listener) {
        this.feeList = feeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fee_record, parent, false);
        return new FeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeeViewHolder holder, int position) {
        FeeRecord record = feeList.get(position);
        holder.tvStudentName.setText(record.getStudentName());
        holder.tvFeeAmount.setText("$" + String.format("%.2f", record.getAmount()));
        holder.tvDueDate.setText(record.getDueDate());
        holder.tvFeeStatus.setText(record.getStatus());

        if ("Paid".equals(record.getStatus())) {
            holder.tvFeeStatus.setTextColor(Color.parseColor("#388E3C")); // Green
            holder.btnMarkPaid.setVisibility(View.GONE);
        } else {
            holder.tvFeeStatus.setTextColor(Color.parseColor("#D32F2F")); // Red
            holder.btnMarkPaid.setVisibility(View.VISIBLE);
        }

        holder.btnMarkPaid.setOnClickListener(v -> listener.onPaidClick(record));
    }

    @Override
    public int getItemCount() {
        return feeList.size();
    }

    static class FeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvFeeAmount, tvDueDate, tvFeeStatus;
        Button btnMarkPaid;

        public FeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tvStudentName);
            tvFeeAmount = itemView.findViewById(R.id.tvFeeAmount);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            tvFeeStatus = itemView.findViewById(R.id.tvFeeStatus);
            btnMarkPaid = itemView.findViewById(R.id.btnMarkPaid);
        }
    }
}
