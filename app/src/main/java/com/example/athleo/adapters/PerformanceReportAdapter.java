package com.example.athleo.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.athleo.R;
import com.example.athleo.models.PerformanceReport;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PerformanceReportAdapter extends RecyclerView.Adapter<PerformanceReportAdapter.ReportViewHolder> {

    private List<PerformanceReport> reportList;

    public PerformanceReportAdapter(List<PerformanceReport> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performance_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        PerformanceReport report = reportList.get(position);
        holder.tvStudent.setText(report.getStudentName());
        holder.tvAuthor.setText("By: " + report.getAuthorName());
        holder.tvContent.setText(report.getReportContent());

        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(report.getTimestamp());
        String date = DateFormat.format("dd MMM yyyy, hh:mm a", cal).toString();
        holder.tvDate.setText(date);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudent, tvAuthor, tvDate, tvContent;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudent = itemView.findViewById(R.id.tvReportStudent);
            tvAuthor = itemView.findViewById(R.id.tvReportAuthor);
            tvDate = itemView.findViewById(R.id.tvReportDate);
            tvContent = itemView.findViewById(R.id.tvReportContent);
        }
    }
}
