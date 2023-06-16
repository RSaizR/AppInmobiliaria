package com.example.contactsaccess.Contact;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactsDatabase {
    private static ContactsDatabase instance;
    private SQLiteDatabase database;

    private ContactsDatabase(Context context) {
        database = context.openOrCreateDatabase("ContactsDb", Context.MODE_PRIVATE, null);
        // Crear la tabla de contactos si no existe
        createContactsTable();
    }

    public static synchronized ContactsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ContactsDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private void createContactsTable() {
        database.execSQL("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phoneNumber TEXT, floor TEXT, maxPrice TEXT, notes TEXT, unique(name, phoneNumber))");
    }


    public void saveContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phoneNumber", contact.getPhoneNumber());
        values.put("floor", contact.getFloorInterested());
        values.put("maxPrice", contact.getMaxPrice());
        values.put("notes", contact.getNotes());
        database.insert("contacts", null, values);
    }

    public Contact getContactByNameAndPhoneNumber(String name, String phoneNumber) {
        Contact contact = null;
        Cursor cursor = database.query(
                "contacts",
                null,
                "name = ? AND phoneNumber = ?",
                new String[]{name, phoneNumber},
                null,
                null,
                null
        );
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String contactName = cursor.getString(cursor.getColumnIndex("name"));
            @SuppressLint("Range") String contactPhoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            @SuppressLint("Range") String contactFloor = cursor.getString(cursor.getColumnIndex("floor"));
            @SuppressLint("Range") String contactMaxPrice = cursor.getString(cursor.getColumnIndex("maxPrice"));
            @SuppressLint("Range") String contactNotes = cursor.getString(cursor.getColumnIndex("notes"));

            contact = new Contact(contactName, contactPhoneNumber, contactFloor, contactMaxPrice, contactNotes);
        }
        cursor.close();
        return contact;
    }

    public void updateContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put("floor", contact.getFloorInterested());
        values.put("maxPrice", contact.getMaxPrice());
        values.put("notes", contact.getNotes());

        System.out.println("FUNCION UPDATE CONTACT" + values.toString());

        database.update(
                "contacts",
                values,
                "name = ? AND phoneNumber = ?",
                new String[]{contact.getName(), contact.getPhoneNumber()}
        );
    }


    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();

        Cursor cursor = database.query(
                "contacts",
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
                @SuppressLint("Range") String floor = cursor.getString(cursor.getColumnIndex("floor"));
                @SuppressLint("Range") String maxPrice = cursor.getString(cursor.getColumnIndex("maxPrice"));
                @SuppressLint("Range") String notes = cursor.getString(cursor.getColumnIndex("notes"));

                Contact contact = new Contact(name, phoneNumber, floor, maxPrice, notes);
                contactList.add(contact);
            }
            cursor.close();
        }

        return contactList;
    }


}
