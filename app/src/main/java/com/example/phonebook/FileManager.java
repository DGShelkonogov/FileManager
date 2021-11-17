package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

public class FileManager extends AppCompatActivity {

    StringAdapter stringAdapter;
    ArrayList<String> files = new ArrayList<>();
    ListView listFiles;

    TextView txtGlobalPath;

    Stack<String> globalPath = new Stack<>();
    private static final int REQUEST_CODE_READ_FILES = 1;
    private static boolean READ_FILES_GRANTED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);


        listFiles = findViewById(R.id.filesList);
        // создаем адаптер
        stringAdapter = new StringAdapter(this, files);
        // устанавливаем адаптер
        listFiles.setAdapter(stringAdapter);

        txtGlobalPath = findViewById(R.id.txtGlobalPath);


        listFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                globalPath.push(files.get(position));
                if (READ_FILES_GRANTED) {
                    loadFiles();
                }
            }
        });

        // получаем разрешения
        int hasReadFilesPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // если устройство до API 23, устанавливаем разрешение
        if (hasReadFilesPermission == PackageManager.PERMISSION_GRANTED) {
            READ_FILES_GRANTED = true;
        } else {
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_FILES);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_FILES_GRANTED) {
            loadFiles();
        }
    }


    private void loadFiles() {


        try{
            String globalPath = createGlobalPath();
            File dir = new File(Environment.getExternalStoragePublicDirectory(globalPath).getPath());
            File f = new File(dir.toString());
            File[] fileList = f.listFiles();
            files.clear();
            for (File file : fileList) {
                files.add(file.getName());
            }
            txtGlobalPath.setText(globalPath);

        }catch (Exception e){
            Toast.makeText(FileManager.this, "Это не директория", Toast.LENGTH_SHORT).show();
        }




        stringAdapter.notifyDataSetChanged();
    }

    public void showContact(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public String createGlobalPath(){
        String path = "/";
        for (String str : globalPath) {
            path += (str + "/");
        }
        return path;
    }

    public void GoBack(View view) {
       try{
           globalPath.pop();
           if (READ_FILES_GRANTED) {
               loadFiles();
           }
       }catch (Exception e){}


    }
}