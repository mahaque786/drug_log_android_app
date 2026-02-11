package com.druglogger.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrugEntryAdapter extends RecyclerView.Adapter<DrugEntryAdapter.ViewHolder> {
    private List<DrugEntry> entries;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(DrugEntry entry);
    }

    public DrugEntryAdapter(List<DrugEntry> entries, OnDeleteClickListener deleteListener) {
        this.entries = entries;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_drug_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DrugEntry entry = entries.get(position);
        holder.drugNameText.setText(entry.getDrugName());
        holder.dosageText.setText(entry.getDosage());
        holder.notesText.setText(entry.getNotes());
        holder.timestampText.setText(entry.getFormattedDate());

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(entry);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateEntries(List<DrugEntry> newEntries) {
        this.entries = newEntries;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView drugNameText;
        TextView dosageText;
        TextView notesText;
        TextView timestampText;
        ImageButton deleteButton;

        ViewHolder(View itemView) {
            super(itemView);
            drugNameText = itemView.findViewById(R.id.drug_name_text);
            dosageText = itemView.findViewById(R.id.dosage_text);
            notesText = itemView.findViewById(R.id.notes_text);
            timestampText = itemView.findViewById(R.id.timestamp_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
