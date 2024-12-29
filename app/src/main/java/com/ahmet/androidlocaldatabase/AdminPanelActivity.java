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

import java.util.ArrayList;
import java.util.List;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.androidlocaldatabase.database.DatabaseHelper;
import com.ahmet.androidlocaldatabase.database.DatabaseUtils;
import com.ahmet.androidlocaldatabase.database.Role;
import com.ahmet.androidlocaldatabase.database.User;
import com.ahmet.androidlocaldatabase.database.UserRepository;
import com.ahmet.androidlocaldatabase.database.exceptions.DatabaseConnectionException;
import com.ahmet.androidlocaldatabase.database.exceptions.UserAlreadyExistsException;
import com.ahmet.androidlocaldatabase.database.exceptions.WeakPasswordException;


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
    private User currentUser;

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

        // Giriş yapan kullanıcıyı tanımla
        currentUser = getCurrentUser();

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

            try {
                userRepository.addUser(new User(0, username, password, role));
                loadUserList();
                clearInputs();
            } catch (UserAlreadyExistsException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (WeakPasswordException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (DatabaseConnectionException e) {
                Toast.makeText(this, "Veritabanı hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Kullanıcı Güncelleme
        updateButton.setOnClickListener(v -> {
            if (selectedUser != null) {
                String password = passwordEditText.getText().toString().trim();
                Role role = (Role) roleSpinner.getSelectedItem();

                if (password.isEmpty()) {
                    Toast.makeText(this, "Şifreyi boş bırakmayın!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Güncelleme işlemi
                selectedUser.setPassword(password);
                selectedUser.setRole(role);

                try {
                    userRepository.updateUser(selectedUser);
                } catch (DatabaseConnectionException e) {
                    Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();

                }
                loadUserList();
                clearInputs();
                selectedUser = null;
                updateButton.setVisibility(View.INVISIBLE);
                addButton.setVisibility(View.VISIBLE);
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

            // Disable username and role for update
            usernameEditText.setEnabled(false);
            roleSpinner.setEnabled(false);

            addButton.setVisibility(View.INVISIBLE);
            updateButton.setVisibility(View.VISIBLE);
        });
    }

    private void loadUserList() {
        try {
            users = userRepository.getUsersByRole(currentUser.getRole());
        } catch (DatabaseConnectionException e) {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        userListView.setAdapter(adapter);
    }

    private void clearInputs() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        roleSpinner.setSelection(0);
        usernameEditText.setEnabled(true);
        roleSpinner.setEnabled(true);
    }

    private User getCurrentUser() {
        return new User(1, "admin", "admin_password", Role.SUPER_USER);
    }
}
