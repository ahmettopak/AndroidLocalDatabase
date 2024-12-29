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
    private User currentUser; // Giriş yapan kullanıcı

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
        currentUser = getCurrentUser();  // Burada, giriş yapan kullanıcıyı alıyoruz (Örn: SharedPreferences ile)

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
                String password = passwordEditText.getText().toString().trim();
                Role role = (Role) roleSpinner.getSelectedItem();

                if (password.isEmpty()) {
                    Toast.makeText(this, "Şifreyi boş bırakmayın!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Eğer güncellenen kullanıcı, giriş yapan kullanıcı ise sadece şifreyi değiştirebilir
                if (selectedUser.getId() == currentUser.getId()) {
                    selectedUser.setPassword(password);
                } else {
                    selectedUser.setPassword(password);
                    selectedUser.setRole(role);  // Adminse, rolü de değiştirebilir
                }

                userRepository.updateUser(selectedUser);
                loadUserList();
                clearInputs();
                selectedUser = null; // Seçilen kullanıcıyı null yap
                updateButton.setVisibility(View.INVISIBLE); // Güncelleme butonunu gizle
                addButton.setVisibility(View.VISIBLE); // Ekleme butonunu tekrar göster

                // Re-enable the fields after update
                usernameEditText.setEnabled(true); // Enable username field
                roleSpinner.setEnabled(true); // Enable role spinner
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
            usernameEditText.setEnabled(false); // Disable username field
            roleSpinner.setEnabled(false); // Disable role spinner

            addButton.setVisibility(View.INVISIBLE); // Ekleme butonunu gizle
            updateButton.setVisibility(View.VISIBLE); // Güncelleme butonunu göster
        });

        // Kullanıcı Listesi uzun tıklama işlemi (Silme onayı)
        userListView.setOnItemLongClickListener((parent, view, position, id) -> handleUserDeletion(position));
    }

    private boolean handleUserDeletion(int position) {
        User userToDelete = users.get(position);

        if (userToDelete.getId() == currentUser.getId()) {
            Toast.makeText(this, "Kendi kullanıcıyı silemezsin!", Toast.LENGTH_SHORT).show();
            return true; // Return true to indicate the event is consumed
        }

        new AlertDialog.Builder(this)
                .setTitle("Kullanıcıyı Sil")
                .setMessage("Bu kullanıcıyı silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    userRepository.deleteUser(userToDelete.getId());
                    loadUserList();
                    Toast.makeText(this, "Kullanıcı silindi", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hayır", null)
                .show();

        return true; // Return true to indicate the event is consumed
    }

    private void loadUserList() {
        users = userRepository.getUsersByRole(currentUser.getRole()); // Admin sadece User'ları görür

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
        userListView.setAdapter(adapter);
    }

    private void clearInputs() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        roleSpinner.setSelection(0); // İlk rolü seç
        usernameEditText.setEnabled(true); // Enable username field
        roleSpinner.setEnabled(true); // Enable role spinner
    }

    // Giriş yapan kullanıcıyı almak
    private User getCurrentUser() {
        // Burada, oturum açan kullanıcının bilgilerini almak için bir yöntem kullanılmalı
        // Örneğin, SharedPreferences veya SessionManager
        // Şu an için varsayılan bir kullanıcı döndürüyoruz:
        return new User(1, "admin", "admin_password", Role.SUPER_USER);
    }
}
