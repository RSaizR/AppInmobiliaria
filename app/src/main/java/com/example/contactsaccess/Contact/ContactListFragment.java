package com.example.contactsaccess.Contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.widget.ImageButton;
import android.widget.TextView;

import com.example.contactsaccess.R;




public class ContactListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContactListAdapter adapter;
    private List<Contact> contactList;
    private String searchText = "";
    private static final int REQUEST_READ_CONTACTS = 1;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Solicitar permiso READ_CONTACTS si no está concedido
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        } else {
            // Permiso READ_CONTACTS concedido
            loadContactList(getView());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso READ_CONTACTS concedido
                loadContactList(getView());
            }
        }
    }

    private void loadContactList(View view) {
        recyclerView = view.findViewById(R.id.contact_list);

        // Configurar el RecyclerView y el adaptador
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactList = getContactList();
        adapter = new ContactListAdapter(contactList);
        recyclerView.setAdapter(adapter);

        EditText searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = s.toString();
                filterContacts();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton floatingButton = view.findViewById(R.id.floating_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el controlador de navegación
                NavController navController = Navigation.findNavController(v);

                // Navegar hacia la ruta deseada
                navController.navigate(R.id.action_contactListFragment_to_createAssetFragment);
            }
        });
    }

    private List<Contact> getContactList() {
        List<Contact> contacts = new ArrayList<>();
        // Consultar los contactos del dispositivo
        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Contact contact = new Contact(name, phoneNumber);
                contacts.add(contact);
            }
            cursor.close();
        }
        return contacts;
    }

    private void filterContacts() {
        List<Contact> filteredContacts = new ArrayList<>();
        for (Contact contact : contactList) {
            if (contact.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredContacts.add(contact);
            }
        }
        adapter.updateContacts(filteredContacts);
    }

    private class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {

        private List<Contact> contacts;

        public ContactListAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Contact contact = contacts.get(position);
            holder.nameTextView.setText(contact.getName());
            holder.phoneTextView.setText(contact.getPhoneNumber());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener el controlador de navegación
                    NavController navController = Navigation.findNavController(v);

                    // Crear el argumento para pasar el contacto al fragmento de detalle
                    Bundle args = new Bundle();
                    args.putSerializable("arg_contact", contact);

                    // Navegar hacia el fragmento de detalle del contacto
                    navController.navigate(R.id.action_contactListFragment_to_contactDetailFragment, args);
                }
            });
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public void updateContacts(List<Contact> updatedContacts) {
            contacts = updatedContacts;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView nameTextView;
            private TextView phoneTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                nameTextView = itemView.findViewById(R.id.contact_name);
                phoneTextView = itemView.findViewById(R.id.contact_details);
            }
        }
    }
}
