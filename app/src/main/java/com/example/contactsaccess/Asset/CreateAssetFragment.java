package com.example.contactsaccess.Asset;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.contactsaccess.R;

public class CreateAssetFragment extends Fragment {

    private EditText editTextAddress, editTextPrice, editTextM2;
    private Button buttonSave;
    private Spinner spinnerType;
    private AssetsDatabase assetsDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_asset, container, false);

        assetsDatabase = new AssetsDatabase(getActivity());

        spinnerType = view.findViewById(R.id.spinner_asset_type);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextM2 = view.findViewById(R.id.editTextSquareMeters);
        buttonSave = view.findViewById(R.id.buttonSave);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.asset_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveAsset();
                listAllAssets();
            }
        });

        return view;
    }

    private void saveAsset() {
        String type = spinnerType.getSelectedItem().toString();
        String address = editTextAddress.getText().toString();
        String priceText = editTextPrice.getText().toString();
        String m2Text = editTextM2.getText().toString();

        if (isNumeric(priceText) && isNumeric(m2Text)) {
            double price = Double.parseDouble(priceText);
            double m2 = Double.parseDouble(m2Text);

            long newRowId = assetsDatabase.saveAsset(type, address, price, m2);

            if (newRowId != -1) {
                Toast.makeText(getActivity(), "Activo guardado correctamente", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(getActivity(), "Error al guardar el activo", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Solo se permiten números", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void listAllAssets() {
        assetsDatabase.listAllAssets();
    }

    private void clearFields() {
        spinnerType.setSelection(0);
        editTextAddress.setText("");
        editTextPrice.setText("");
        editTextM2.setText("");
    }
}
