package com.example.contactsaccess.Contact;

import static java.lang.Integer.parseInt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.contactsaccess.Asset.Asset;
import com.example.contactsaccess.Asset.AssetsDatabase;
import com.example.contactsaccess.Contact.Contact;
import com.example.contactsaccess.Contact.ContactsDatabase;
import com.example.contactsaccess.R;

import java.util.ArrayList;
import java.util.List;

public class ContactDetailFragment extends Fragment {
    private static final String ARG_CONTACT = "arg_contact";
    private Contact contact;
    private Spinner assetsSpinner;
    private List<Asset> assetsList;
    private int selectedAssetIndex = -1;
    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;
    private static final String KEY_SELECTED_ASSET_INDEX = "selected_asset_index";

    public ContactDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            contact = (Contact) getArguments().getSerializable(ARG_CONTACT);
        }
        if (savedInstanceState != null) {
            selectedAssetIndex = savedInstanceState.getInt(KEY_SELECTED_ASSET_INDEX, -1);
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
            System.out.println("ON CREATE " + contact.toString());

        // Obtener los datos guardados en la base de datos y mostrarlos
            ContactsDatabase appDatabase = ContactsDatabase.getInstance(requireContext());
            Contact savedContact = appDatabase.getContactByNameAndPhoneNumber(contact.getName(), contact.getPhoneNumber());
            if (savedContact != null) {
                TextView maxPriceTextView = view.findViewById(R.id.edit_max_price);
                TextView notesTextView = view.findViewById(R.id.edit_notes);

                maxPriceTextView.setText(savedContact.getMaxPrice());
                notesTextView.setText(savedContact.getNotes());

                int floorAssetIndex = Integer.parseInt(savedContact.getFloorInterested())-1;
                loadAssets(floorAssetIndex);

            }else{
                loadAssets(0);
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
                        // Verificar si se tienen los permisos necesarios
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            // Los permisos ya han sido concedidos, realizar la llamada
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(intent);
                        } else {
                            // Solicitar los permisos al usuario
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
                        }
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

    private void loadAssets(int showAsset) {
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

        // Aquí establecemos la segunda opción como selección predeterminada
        assetsSpinner.setSelection(showAsset);

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

    private int getFloorAssetIndex() {
        if (contact != null && assetsList != null) {
            String floorAsset = contact.getFloorInterested();
            if (floorAsset != null && !floorAsset.isEmpty()) {
                try {
                    return Integer.parseInt(floorAsset);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }


    private void saveContactToDatabase() {
        // Verificar si se seleccionó un activo
        if (selectedAssetIndex != -1 && selectedAssetIndex < assetsList.size()) {
            // Obtener el activo seleccionado del spinner
            String selectedAsset = String.valueOf(assetsList.get(selectedAssetIndex).getId());
            System.out.println("IHRWJFEOAK" + assetsList.get(selectedAssetIndex).getId());
            // Obtener los valores de los campos de texto
            TextView nameTextView = requireView().findViewById(R.id.contact_name);
            TextView phoneTextView = requireView().findViewById(R.id.contact_phone_number);
            TextView maxPriceTextView = requireView().findViewById(R.id.edit_max_price);
            TextView notesTextView = requireView().findViewById(R.id.edit_notes);

            String name = nameTextView.getText().toString().trim();
            String phoneNumber = phoneTextView.getText().toString().trim();
            String maxPrice = maxPriceTextView.getText().toString().trim();
            String notes = notesTextView.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(requireContext(), "El nombre y el número de teléfono son campos requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            ContactsDatabase appDatabase = ContactsDatabase.getInstance(requireContext());

            // Verificar si ya existe un contacto con el mismo nombre y número de teléfono
            Contact existingContact = appDatabase.getContactByNameAndPhoneNumber(name, phoneNumber);
            if (existingContact != null) {
                // El contacto ya existe, actualiza sus datos en lugar de crear uno nuevo
                existingContact.setMaxPrice(maxPrice);
                existingContact.setNotes(notes);
                existingContact.setFloorInterested(selectedAsset);
                System.out.println("ACTUALIZAR " + existingContact);// Almacenar el número del spinner
                appDatabase.updateContact(existingContact);

                Toast.makeText(requireContext(), "Contacto actualizado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                // El contacto no existe, crea uno nuevo y guárdalo en la base de datos
                Contact newContact = new Contact(name, phoneNumber, maxPrice, notes, selectedAsset);
                newContact.setFloorInterested(String.valueOf(selectedAssetIndex));
                appDatabase.saveContact(newContact);

                Toast.makeText(requireContext(), "Contacto guardado exitosamente", Toast.LENGTH_SHORT).show();
            }

            // Navegar de regreso a la lista de contactos
            NavHostFragment.findNavController(this).popBackStack();
        } else {
            Toast.makeText(requireContext(), "Por favor, seleccione un activo", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isTelephonyEnabled() {
        TelephonyManager telephonyManager = (TelephonyManager) requireContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL_PHONE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realizar la llamada
                TextView phoneTextView = requireView().findViewById(R.id.contact_phone_number);
                String phoneNumber = phoneTextView.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            } else {
                // Permiso denegado
                Toast.makeText(requireContext(), "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
