package com.example.resep_sehat.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.resep_sehat.model.recipe;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "recipes.db";
    public static final String TABLE_NAME = "recipes";
    public static final int DATABASE_VERSION = 3;

    // Kolom tabel
    public static final String COL_1 = "ID";
    public static final String COL_2 = "TITLE";
    public static final String COL_3 = "DESCRIPTION";
    public static final String COL_4 = "CATEGORY";
    public static final String COL_5 = "CALORIES";
    public static final String COL_6 = "RATING";  // Ensure this matches the schema
    public static final String COL_7 = "IMAGE_PATH";
    public static final String COL_8 = "INGREDIENTS_TITLE";
    public static final String COL_9 = "INGREDIENTS_DESCRIPTION";
    public static final String COL_10 = "STEPS_TITLE";
    public static final String COL_11 = "STEPS_DESCRIPTION";
    public static final String COL_12 = "IS_FAVORITE";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Versi database diperbarui ke 2
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " INTEGER, " +
                COL_6 + " FLOAT, " +  // RATING
                COL_7 + " TEXT, " +
                COL_8 + " TEXT, " +
                COL_9 + " TEXT, " +
                COL_10 + " TEXT, " +
                COL_11 + " TEXT, " +
                COL_12 + " INTEGER DEFAULT 0" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) { // Assuming version 4 is the latest
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN duration INTEGER DEFAULT 0");
        }
    }

    public Cursor getFavoriteRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE IS_FAVORITE = 1", null);
    }


    // Metode untuk menyimpan data resep
    public boolean insertData(String title, String description, String ingredientsTitle, String ingredientsDescription,
                              String stepsTitle, String stepsDescription, String imagePath, String category,
                              int calories, float rating) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_2, title);
        contentValues.put(COL_3, description);
        contentValues.put(COL_4, category);  // Save the category
        contentValues.put(COL_5, calories);  // Save the calories
        contentValues.put(COL_6, rating);    // Save the rating
        contentValues.put(COL_7, imagePath);
        contentValues.put(COL_8, ingredientsTitle);
        contentValues.put(COL_9, ingredientsDescription);
        contentValues.put(COL_10, stepsTitle);
        contentValues.put(COL_11, stepsDescription);
        contentValues.put(COL_12, 0); // Default value for IS_FAVORITE (not favorite)

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // Return true if insert is successful
    }


    // Mengambil semua data resep
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Mengambil resep berdasarkan ID
    public Cursor getRecipeById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM recipes WHERE ID = ?", new String[]{String.valueOf(id)});
    }

    // Menandai resep sebagai favorit (update IS_FAVORITE)
    public boolean setFavorite(int id, int isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_12, isFavorite); // Set the value for IS_FAVORITE

        int result = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{String.valueOf(id)});
        return result > 0; // Return true if update is successful
    }

    public Cursor searchRecipesByName(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_2 + " LIKE ?", new String[]{"%" + query + "%"});
    }

    // Mendapatkan status favorit resep
    public int getFavoriteStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_12 + " FROM " + TABLE_NAME + " WHERE ID = ?", new String[]{String.valueOf(id)});

        if (cursor != null && cursor.moveToFirst()) {
            // Pastikan kolom ada sebelum mengaksesnya
            int columnIndex = cursor.getColumnIndex(COL_12);
            if (columnIndex != -1) {
                return cursor.getInt(columnIndex); // Kembalikan nilai IS_FAVORITE
            } else {
                return 0; // Kolom tidak ada, kembalikan nilai default
            }
        }
        return 0; // Defaultkan ke 0 jika tidak ada data ditemukan
    }


    public ArrayList<recipe> getRecipesByCategory(String category) {
        ArrayList<recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Ensure column names match your database schema and use case-insensitive filtering if needed
        String selection = "category = ?";
        String[] selectionArgs = new String[]{category};

        // Assuming there's a column "category" in your recipes table
        Cursor cursor = db.query(TABLE_NAME, null, "category = ?", new String[]{category}, null, null, null);


        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        // Safely retrieve indices to avoid exceptions
                        int idIndex = cursor.getColumnIndex("COL_1");
                        int titleIndex = cursor.getColumnIndex("COL_2");
                        int descriptionIndex = cursor.getColumnIndex("COL_3");
                        int imagePathIndex = cursor.getColumnIndex("COL_7");
                        int ratingIndex = cursor.getColumnIndex("COL_6");
                        int caloriesIndex = cursor.getColumnIndex("COL_5");
                        int durationIndex = cursor.getColumnIndex("COL_11");

                        // Ensure indices are valid
                        if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
                                imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {

                            // Retrieve values
                            int id = cursor.getInt(idIndex);
                            String title = cursor.getString(titleIndex);
                            String description = cursor.getString(descriptionIndex);
                            String imagePath = cursor.getString(imagePathIndex);
                            float rating = cursor.getFloat(ratingIndex);
                            int calories = cursor.getInt(caloriesIndex);
                            int duration = cursor.getInt(durationIndex);

                            // Add the recipe to the list
                            recipeList.add(new recipe(id, title, description, imagePath, rating, calories, duration));
                        } else {
                            Log.e("DatabaseError", "One or more columns not found in the database schema.");
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                // Ensure cursor is closed to avoid memory leaks
                cursor.close();
            }
        } else {
            Log.e("DatabaseError", "Cursor is null. Query might have failed or no matching rows found.");
        }

        return recipeList;
    }

    public ArrayList<recipe> getAllRecipes() {
        ArrayList<recipe> recipeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get all recipes
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COL_1);
                int titleIndex = cursor.getColumnIndex(COL_2);
                int descriptionIndex = cursor.getColumnIndex(COL_3);
                int imagePathIndex = cursor.getColumnIndex(COL_7);
                int ratingIndex = cursor.getColumnIndex(COL_6);
                int caloriesIndex = cursor.getColumnIndex(COL_5);
                int durationIndex = cursor.getColumnIndex(COL_11);

                // Safely retrieve values, handling potential -1 index errors
                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
                        imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {

                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descriptionIndex);
                    String imagePath = cursor.getString(imagePathIndex);
                    float rating = cursor.getFloat(ratingIndex);
                    int calories = cursor.getInt(caloriesIndex);
                    int duration = cursor.getInt(durationIndex);

                    // Add the recipe to the list
                    recipeList.add(new recipe(id, title, description, imagePath, rating, calories, duration));
                } else {
                    Log.e("DatabaseError", "One or more columns not found in the database.");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        return recipeList;
    }

}
