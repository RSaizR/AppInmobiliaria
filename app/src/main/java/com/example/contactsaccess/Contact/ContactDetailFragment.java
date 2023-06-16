package com.example.contactsaccess.Contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.contactsaccess.Asset.Asset;
import com.example.contactsaccess.Asset.AssetsDatabase;
import com.example.contactsaccess.Contact.Contact;
import com.example.contactsaccess.R;
import android.telephony.TelephonyManager;
import android.content.Context;


import java.util.ArrayList;
import java.util.List;

public class ContactDetailFragment extends Fragment {
    private static final String ARG_CONTACT = "arg_contact";
    private Contact contact;
    private Spinner assetsSpinner;
    private List<Asset> assetsList;
    private int selectedAssetIndex = -1;

    public ContactDetailFragment() {
        // Required empty public constructor
    }

    public static ContactDetailFragment newInstance(Contact contact) {
        ContactDetailFragment fragment = new ContactDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONTACT, contact);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contact = (Contact) getArguments().getSerializable(ARG_CONTACT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nameTextView = view.findViewById(R.id.contact_name);
        TextView phoneTextView = view.findViewById(R.id.contact_phone_number);

        if (contact != null) {
            // Mostrar los datos del contacto en los campos de texto correspondientes
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhoneNumber());
        }

        // Obtener los datos guardados en la base de datos y mostrarlos
        if (contact != null) {
            ContactsDatabase appDatabase = ContactsDatabase.getInstance(requireContext());
            Contact savedContact = appDatabase.getContactByNameAndPhoneNumber(contact.getName(), contact.getPhoneNumber());
            if (savedContact != null) {
                TextView maxPriceTextView = view.findViewById(R.id.edit_max_price);
                TextView notesTextView = view.findViewById(R.id.edit_notes);

                maxPriceTextView.setText(savedContact.getMaxPrice());
                notesTextView.setText(savedContact.getNotes());
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        TextView nameTextView = view.findViewById(R.id.contact_name);
        TextView phoneTextView = view.findViewById(R.id.contact_phone_number);
        assetsSpinner = view.findViewById(R.id.spinner_assets);
        TextView maxPriceTextView = view.findViewById(R.id.edit_max_price);
        TextView notesTextView = view.findViewById(R.id.edit_notes);
        Button saveButton = view.findViewById(R.id.btn_save);
        Button callButton = view.findViewById(R.id.btn_call);

        if (contact != null) {
            // Mostrar los datos existentes en los campos de texto
            nameTextView.setText(contact.getName());
            phoneTextView.setText(contact.getPhoneNumber());
            maxPriceTextView.setText(contact.getMaxPrice());
            notesTextView.setText(contact.getNotes());
        }

        // Cargar los activos en el spinner
        loadAssets();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContactToDatabase();
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNumber)) {
                    // Verificar si el dispositivo es capaz de realizar llamadas telefónicas
                    if (isTelephonyEnabled()) {
                        // Realizar la llamada
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "El dispositivo no puede realizar llamadas telefónicas", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "No se ha proporcionado un número de teléfono", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }



    private void loadAssets() {
        AssetsDatabase assetsDatabase = new AssetsDatabase(requireContext());
        assetsList = assetsDatabase.getAllAssets();

        List<String> assetAddresses = new ArrayList<>();
        for (Asset asset : assetsList) {
            String addressWithSquareMeters = asset.getAddress() + " (" + asset.getPrice() + ")";
            assetAddresses.add(addressWithSquareMeters);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, assetAddresses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assetsSpinner.setAdapter(adapter);

        assetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Guardar el índice seleccionado
                selectedAssetIndex = position;

                // Obtener el activo seleccionado del spinner
                if (selectedAssetIndex >= 0 && selectedAssetIndex < assetsList.size()) {
                    Asset selectedAsset = assetsList.get(selectedAssetIndex);

                    // Realizar las operaciones necesarias con el activo seleccionado
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private void saveContactToDatabase() {
        // Verificar si se seleccionó un activo
        if (selectedAssetIndex != -1 && selectedAssetIndex < assetsList.size()) {
            // Obtener el activo seleccionado del spinner
            Asset selectedAsset = assetsList.get(selectedAssetIndex);

            // Obtener los valores de los campos de texto
            TextView nameTextView = requireView().findViewById(R.id.contact_name);
            TextView phoneTextView = requireView().findViewById(R.id.contact_phone_number);
            TextView maxPriceTextView = requireView().findViewById(R.id.edit_max_price);
            TextView notesTextView = requireView().findViewById(R.id.edit_notes);

            // Obtener los textos de los campos de texto
            String name = nameTextView.getText().toString().trim();
            String phoneNumber = phoneTextView.getText().toString().trim();
            String maxPriceString = maxPriceTextView.getText().toString().trim();
            String notes = notesTextView.getText().toString().trim();

            // Verificar si el contacto ya existe en la base de datos
            ContactsDatabase appDatabase = ContactsDatabase.getInstance(requireContext());
            Contact existingContact = appDatabase.getContactByNameAndPhoneNumber(name, phoneNumber);

            if (existingContact != null) {
                // El contacto ya existe, actualizarlo con los nuevos datos

                // Verificar si los campos están vacíos
                String maxPriceValue = TextUtils.isEmpty(maxPriceString) ? "" : maxPriceString;
                String notesValue = TextUtils.isEmpty(notes) ? "" : notes;
                existingContact.setFloorInterested(selectedAsset.getAddress()); // Usar la dirección del activo seleccionado
                existingContact.setMaxPrice(maxPriceValue);
                existingContact.setNotes(notesValue);

                appDatabase.updateContact(existingContact);
                Toast.makeText(requireContext(), "Contacto actualizado", Toast.LENGTH_SHORT).show();
            } else {
                // El contacto no existe, crear uno nuevo
                Contact newContact = new Contact(name, phoneNumber, selectedAsset.getAddress(), maxPriceString, notes); // Usar la dirección del activo seleccionado
                appDatabase.saveContact(newContact);
                Toast.makeText(requireContext(), "Contacto guardado", Toast.LENGTH_SHORT).show();
            }

            // Navegar de regreso al ContactListFragment
            NavHostFragment.findNavController(this).navigate(R.id.action_contactDetailFragment_to_contactListFragment);
        } else {
            Toast.makeText(requireContext(), "Selecciona un activo", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isTelephonyEnabled() {
        TelephonyManager telephonyManager = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }
}
