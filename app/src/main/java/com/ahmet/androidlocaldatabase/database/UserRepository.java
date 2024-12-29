package com.ahmet.androidlocaldatabase.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.ahmet.androidlocaldatabase.database.exceptions.DatabaseConnectionException;
import com.ahmet.androidlocaldatabase.database.exceptions.UserAlreadyExistsException;
import com.ahmet.androidlocaldatabase.database.exceptions.WeakPasswordException;

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

    public void addUser(User user) throws UserAlreadyExistsException, WeakPasswordException, DatabaseConnectionException {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            // Kullanıcı adı zaten mevcut mu kontrol et
            if (getUser(user.getUsername(), user.getPassword()) != null) {
                throw new UserAlreadyExistsException("Kullanıcı adı zaten mevcut.");
            }

            // Şifre uzunluğunu kontrol et
            if (user.getPassword().length() < 6) {
                throw new WeakPasswordException("Şifre çok basit, lütfen daha güçlü bir şifre girin.");
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_ROLE, user.getRole().name());
            db.insert(DatabaseHelper.TABLE_USERS, null, values);
        } catch (SQLException e) {
            Log.e("UserRepository", "Error adding user: " + e.getMessage());
            throw new DatabaseConnectionException("Veritabanına kullanıcı eklerken hata oluştu: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public void deleteUser(int userId) throws DatabaseConnectionException {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = DatabaseHelper.COLUMN_ID + " = ?";
            String[] whereArgs = {String.valueOf(userId)};
            int rowsDeleted = db.delete(DatabaseHelper.TABLE_USERS, whereClause, whereArgs);
            if (rowsDeleted == 0) {
                throw new DatabaseConnectionException("Silinecek kullanıcı bulunamadı.");
            }
        } catch (SQLException e) {
            Log.e("UserRepository", "Error deleting user: " + e.getMessage());
            throw new DatabaseConnectionException("Kullanıcı silinirken hata oluştu: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public List<User> getUsersByRole(Role role) throws DatabaseConnectionException {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = null;
            String[] selectionArgs = null;

            if (role == Role.SUPER_USER) {
                // Super User tüm kullanıcıları görebilir
                selection = null;
                selectionArgs = null;
            } else if (role == Role.ADMIN) {
                // Admin sadece Admin kullanıcılarını görebilir
                selection = DatabaseHelper.COLUMN_ROLE + "=?";
                selectionArgs = new String[]{Role.ADMIN.name()};
            } else {
                // Diğerleri boş liste döner
                return userList;
            }

            cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);

            while (cursor != null && cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PASSWORD));
                Role userRole = Role.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));

                userList.add(new User(id, username, password, userRole));
            }
        } catch (SQLException e) {
            Log.e("UserRepository", "Error fetching users: " + e.getMessage());
            throw new DatabaseConnectionException("Kullanıcılar alınırken hata oluştu: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return userList;
    }

    public void updateUser(User user) throws DatabaseConnectionException {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_ROLE, user.getRole().name());

            String whereClause = DatabaseHelper.COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(user.getId())};

            int rowsUpdated = db.update(DatabaseHelper.TABLE_USERS, values, whereClause, whereArgs);
            if (rowsUpdated == 0) {
                throw new DatabaseConnectionException("Güncellenecek kullanıcı bulunamadı.");
            }
        } catch (SQLException e) {
            Log.e("UserRepository", "Error updating user: " + e.getMessage());
            throw new DatabaseConnectionException("Kullanıcı güncellenirken hata oluştu: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public User getUser(String username, String password) throws DatabaseConnectionException {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String selection = DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            cursor = db.query(DatabaseHelper.TABLE_USERS, null, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
                Role role = Role.fromString(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ROLE)));
                return new User(id, username, password, role);
            }
        } catch (SQLException e) {
            Log.e("UserRepository", "Error retrieving user: " + e.getMessage());
            throw new DatabaseConnectionException("Kullanıcı alınırken hata oluştu: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return null;
    }
}
