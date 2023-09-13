package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.ListView;


public class AdminPanelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        // ListView'i tanımlayın
        ListView listView = findViewById(R.id.userListview);

        // Kullanıcıları veritabanından alın
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<User> userList = databaseHelper.getAllUsers(); // Bu metodunuzun olması gerekiyor.

        // Liste görünümünü doldurun
        ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        // Kullanıcıyı seçtiğinizde silmek için bir dinleyici ekleyin
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedUserId = userList.get(position).getId();
            // Seçilen kullanıcıyı veritabanından sil ve liste görünümünden kaldır
            databaseHelper.deleteUser(selectedUserId);
            adapter.remove(userList.get(position));
            adapter.notifyDataSetChanged();
        });
    }

}