package com.ahmet.androidlocaldatabase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ahmet TOPAK
 * Since: 12/29/2024
 */

public class UserRepository {
    private final DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void addUser(User user) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_ROLE, user.getRole().name());
            db.insert(DatabaseHelper.TABLE_USERS, null, values);
        }
    }

    public List<User> getUsersByRole(Role role) {
        List<User> userList = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String selection = null;
            String[] selectionArgs = null;

            if (role == Role.SUPER_USER) {
                // Super User tüm kullanıcıları görebilir
                selection = null; // Filtreleme yok
                selectionArgs = null;
            } else if (role == Role.ADMIN) {
                // Admin yalnızca admin rolündeki kullanıcıları görebilir
                selection = DatabaseHelper.COLUMN_ROLE + "=?";
                selectionArgs = new String[]{Role.ADMIN.name()};
            } else if (role == Role.USER) {
                return userList;
            } else {
                // Hiçbir erişim yoksa, boş liste döndür
                return userList;
            }

            // Veritabanından kullanıcıları çek
            try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null)) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                    String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD));
                    Role userRole = Role.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));

                    userList.add(new User(id, username, password, userRole));
                }
            }
        }
        return userList;
    }

    public void updateUser(User user) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_ROLE, user.getRole().name());

            String whereClause = DatabaseHelper.COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(user.getId())};

            db.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
        }
    }
    public User getUser(String username, String password) {
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            String selection = DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            try (Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null)) {
                if (cursor.moveToFirst()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                    Role role = Role.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));
                    return new User(id, username, password, role);
                }
            }
        }
        return null;
    }
}