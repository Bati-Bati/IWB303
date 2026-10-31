package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.CaseLog;

import java.util.ArrayList;
import java.util.List;

public class CaseLogAdapter extends RecyclerView.Adapter<CaseLogAdapter.CaseLogViewHolder> {

    public interface OnLogDeleteListener {
        void onDelete(CaseLog log);
    }

    private List<CaseLog> logs = new ArrayList<>();
    private final OnLogDeleteListener deleteListener;

    public CaseLogAdapter(OnLogDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setLogs(List<CaseLog> logs) {
        this.logs = logs != null ? logs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CaseLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_case_log, parent, false);
        return new CaseLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CaseLogViewHolder holder, int position) {
        holder.bind(logs.get(position), deleteListener);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class CaseLogViewHolder extends RecyclerView.ViewHolder {
        private final TextView descText;
        private final TextView dateText;
        private final TextView proceduresText;
        private final Button deleteBtn;

        public CaseLogViewHolder(@NonNull View itemView) {
            super(itemView);
            descText = itemView.findViewById(R.id.logDescriptionText);
            dateText = itemView.findViewById(R.id.logDateText);
            proceduresText = itemView.findViewById(R.id.logProceduresText);
            deleteBtn = itemView.findViewById(R.id.deleteLogButton);
        }

        public void bind(CaseLog log, OnLogDeleteListener listener) {
            descText.setText(log.getDescription());
            dateText.setText(log.getDate());
            proceduresText.setText("الإجراءات المتخذة:\n" + log.getProcedures());

            deleteBtn.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(log);
            });
        }
    }
}
