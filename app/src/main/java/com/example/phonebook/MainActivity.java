package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> contacts = new ArrayList<>();
    StringAdapter stringAdapter;
    ListView listContact;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listContact = findViewById(R.id.contactList);
        // создаем адаптер
        stringAdapter = new StringAdapter(this, contacts);
        // устанавливаем адаптер
        listContact.setAdapter(stringAdapter);



        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED) {
            loadContacts();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                READ_CONTACTS_GRANTED = true;
            }
        }
        if (READ_CONTACTS_GRANTED) {
            loadContacts();
        } else {
            Toast.makeText(this, "Требуется установить разрешения", Toast.LENGTH_LONG).show();
        }
    }

    private void loadContacts() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                // получаем каждый контакт
                @SuppressLint("Range") String contact = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                // добавляем контакт в список
                contacts.add(contact);
            }
            stringAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    public void showFiles(View view) {
        Intent intent = new Intent(this, FileManager.class);
        startActivity(intent);
    }
}