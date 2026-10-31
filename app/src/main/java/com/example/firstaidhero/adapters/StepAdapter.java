package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.EmergencyStep;

import java.util.ArrayList;
import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    private List<EmergencyStep> steps = new ArrayList<>();

    public void setSteps(List<EmergencyStep> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        EmergencyStep step = steps.get(position);
        holder.bind(step, position + 1);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final TextView stepNumberText;
        private final TextView stepInstructionText;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumberText = itemView.findViewById(R.id.stepNumberText);
            stepInstructionText = itemView.findViewById(R.id.stepInstructionText);
        }

        public void bind(EmergencyStep step, int fallbackIndex) {
            int num = step.getStepNumber() > 0 ? step.getStepNumber() : fallbackIndex;
            stepNumberText.setText(String.valueOf(num));
            stepInstructionText.setText(step.getInstructionText());
        }
    }
}
