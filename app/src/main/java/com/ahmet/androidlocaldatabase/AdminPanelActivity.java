package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class AdminPanelActivity extends AppCompatActivity {

    EditText nameText;
    EditText passwordText;
    Button registerButton;
    ArrayAdapter<User> adapter;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);


        nameText = findViewById(R.id.nameEditText);
        passwordText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        ListView listView = findViewById(R.id.userListview);

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        userList = databaseHelper.getAllUsers();


        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, userList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                User user = getItem(position);

                // TextView'i bulun ve kullanıcı adı ile şifreyi ayarlayın
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText("Name: "+user.getUsername() + " \n Password: "+ user.getPassword());

                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedUserId = userList.get(position).getId();
            databaseHelper.deleteUser(selectedUserId);
            adapter.remove(userList.get(position));
            adapter.notifyDataSetChanged();
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DatabaseUtils.isEmpty(nameText) && DatabaseUtils.isEmpty(passwordText)){
                    Toast.makeText(AdminPanelActivity.this, "İsim veya şifre boş!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dbHelper.addUser(String.valueOf(nameText.getText()), String.valueOf(passwordText.getText()));

                    userList = databaseHelper.getAllUsers();
                    adapter.notify();

                }
            }
        });
    }

}