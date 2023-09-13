package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText nameText;
    EditText passwordText;
    Button loginButton;
    private String adminName = "elk";
    private  String adminPassword = "2005";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameText = findViewById(R.id.nameEditText);
        passwordText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DatabaseUtils.isEmpty(nameText) && DatabaseUtils.isEmpty(passwordText)){
                    Toast.makeText(LoginActivity.this, "İsim veya şifre boş!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Is admin
                    if (adminName.equals(String.valueOf(nameText.getText()) )&& adminPassword.equals(String.valueOf(passwordText.getText()))){
                        Intent intent = new Intent(LoginActivity.this, AdminPanelActivity.class);

                        startActivity(intent);
                    }
                    //Normal user
                    else{
                        if (dbHelper.checkUser(String.valueOf(nameText.getText()), String.valueOf(passwordText.getText()))){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(LoginActivity.this, "Kullanıcı Bilgileri Doğru ", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Kullanıcı bilgileri yanlış", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }
        });

    }
}