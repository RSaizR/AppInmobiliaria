//package com.example.contactsaccess;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//
//public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {
//
//    private List<String> contacts;
//
//    public ContactListAdapter(List<String> contacts) {
//        this.contacts = contacts;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(MyViewHolder holder, int position) {
//        holder.contactName.setText(contacts.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return contacts.size();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        public TextView contactName;
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            contactName = itemView.findViewById(R.id.contact_name);
//        }
//    }
//}