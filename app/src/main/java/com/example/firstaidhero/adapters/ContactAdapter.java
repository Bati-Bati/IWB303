package com.example.firstaidhero.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstaidhero.R;
import com.example.firstaidhero.models.EmergencyContact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    public interface OnContactActionListener {
        void onCall(EmergencyContact contact);
        void onDelete(EmergencyContact contact);
    }

    private List<EmergencyContact> contacts = new ArrayList<>();
    private final OnContactActionListener listener;

    public ContactAdapter(OnContactActionListener listener) {
        this.listener = listener;
    }

    public void setContacts(List<EmergencyContact> contacts) {
        this.contacts = contacts != null ? contacts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.bind(contacts.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView phoneText;
        private final Button callBtn;
        private final Button deleteBtn;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.contactNameText);
            phoneText = itemView.findViewById(R.id.contactPhoneText);
            callBtn = itemView.findViewById(R.id.callContactButton);
            deleteBtn = itemView.findViewById(R.id.deleteContactButton);
        }

        public void bind(EmergencyContact contact, OnContactActionListener listener) {
            nameText.setText(contact.getContactName());
            phoneText.setText(contact.getPhoneNumber());

            callBtn.setOnClickListener(v -> {
                if (listener != null) listener.onCall(contact);
            });

            deleteBtn.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(contact);
            });
        }
    }
}
