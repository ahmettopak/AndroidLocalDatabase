package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ahmet.androidlocaldatabase.database.DatabaseHelper;
import com.ahmet.androidlocaldatabase.database.DatabaseUtils;
import com.ahmet.androidlocaldatabase.database.Role;
import com.ahmet.androidlocaldatabase.database.User;
import com.ahmet.androidlocaldatabase.database.UserRepository;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        userRepository = new UserRepository(this);

        userRepository.addUser(new User(0, "a", "a", Role.SUPER_USER));

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userRepository.getUser(username, password);
            if (user == null) {
                Toast.makeText(this, "Hatalı kullanıcı adı veya şifre!", Toast.LENGTH_SHORT).show();
                return;
            }

            navigateToRoleSpecificActivity(user.getRole());
        });
    }

    private void navigateToRoleSpecificActivity(Role role) {
        Intent intent;
        if (role == Role.ADMIN || role == Role.SUPER_USER) {
            intent = new Intent(this, AdminPanelActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}