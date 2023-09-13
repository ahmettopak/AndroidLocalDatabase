package com.ahmet.androidlocaldatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    EditText nameText;
    EditText passwordText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameText = findViewById(R.id.nameEditText);
        passwordText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DatabaseUtils.isEmpty(nameText) && DatabaseUtils.isEmpty(passwordText)){
                    Toast.makeText(RegisterActivity.this, "İsim veya şifre boş!!!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dbHelper.addUser(String.valueOf(nameText.getText()), String.valueOf(passwordText.getText()));

                }
            }
        });
    }
}