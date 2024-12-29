package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.androidlocaldatabase.database.DatabaseHelper;
import com.ahmet.androidlocaldatabase.database.DatabaseUtils;
import com.ahmet.androidlocaldatabase.database.Role;
import com.ahmet.androidlocaldatabase.database.User;
import com.ahmet.androidlocaldatabase.database.UserRepository;


public class AdminPanelActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Spinner roleSpinner;
    private Button addButton;
    private Button updateButton;
    private ListView userListView;
    private UserRepository userRepository;
    private List<User> users;
    private ArrayAdapter<User> adapter;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        usernameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        addButton = findViewById(R.id.registerButton);
        updateButton = findViewById(R.id.updateButton); // Güncelleme butonu
        userListView = findViewById(R.id.userListview);

        userRepository = new UserRepository(this);

        // Role Spinner'ı doldur
        ArrayAdapter<Role> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Role.values());
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(roleAdapter);

        loadUserList();

        // Kullanıcı Ekleme
        addButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            Role role = (Role) roleSpinner.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Yeni kullanıcı ekle
            userRepository.addUser(new User(0, username, password, role));
            loadUserList();
            clearInputs();
        });

        // Kullanıcı Güncelleme
        updateButton.setOnClickListener(v -> {
            if (selectedUser != null) {
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                Role role = (Role) roleSpinner.getSelectedItem();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kullanıcıyı güncelle
                selectedUser.setUsername(username);
                selectedUser.setPassword(password);
                selectedUser.setRole(role);

                userRepository.updateUser(selectedUser);
                loadUserList();
                clearInputs();
                selectedUser = null; // Seçilen kullanıcıyı null yap
                updateButton.setVisibility(View.INVISIBLE); // Güncelleme butonunu gizle
                addButton.setVisibility(View.VISIBLE); // Ekleme butonunu tekrar göster
            } else {
                Toast.makeText(this, "Güncellemek için bir kullanıcı seçin!", Toast.LENGTH_SHORT).show();
            }
        });

        // Kullanıcı Listesi tıklama işlemi
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = users.get(position);
            usernameEditText.setText(selectedUser.getUsername());
            passwordEditText.setText(selectedUser.getPassword());
            roleSpinner.setSelection(((ArrayAdapter<Role>) roleSpinner.getAdapter()).getPosition(selectedUser.getRole()));

            addButton.setVisibility(View.INVISIBLE); // Ekleme butonunu gizle
            updateButton.setVisibility(View.VISIBLE); // Güncelleme butonunu göster
        });
    }

    private void loadUserList() {
        users = userRepository.getUsersByRole(Role.SUPER_USER); // Örnek olarak SUPER_USER rolüyle filtreleme
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        userListView.setAdapter(adapter);
    }

    private void clearInputs() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        roleSpinner.setSelection(0); // İlk rolü seç
    }
}
