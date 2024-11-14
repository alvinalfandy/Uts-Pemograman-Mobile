package com.example.resep_sehat.ui.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resep_sehat.R;
import com.example.resep_sehat.adapter.RecipeAdapter;
import com.example.resep_sehat.helper.DatabaseHelper;
import com.example.resep_sehat.model.recipe;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = view.findViewById(R.id.rv_fav);
        databaseHelper = new DatabaseHelper(getContext());

        loadFavoriteRecipes();

        return view;
    }

    private void loadFavoriteRecipes() {
        ArrayList<recipe> favoriteRecipes = new ArrayList<>();

        Cursor cursor = databaseHelper.getFavoriteRecipes(); // Query only favorite recipes
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        // Safely retrieve column indices
                        int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_1);
                        int titleIndex = cursor.getColumnIndex(DatabaseHelper.COL_2);
                        int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COL_3);
                        int imagePathIndex = cursor.getColumnIndex(DatabaseHelper.COL_7);
                        int ratingIndex = cursor.getColumnIndex(DatabaseHelper.COL_6);
                        int caloriesIndex = cursor.getColumnIndex(DatabaseHelper.COL_5);
                        int durationIndex = cursor.getColumnIndex("duration"); // Ensure column exists

                        // Ensure all indices are valid
                        if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
                                imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {

                            // Retrieve column values safely
                            int id = cursor.getInt(idIndex);
                            String title = cursor.getString(titleIndex);
                            String description = cursor.getString(descriptionIndex);
                            String imagePath = cursor.getString(imagePathIndex);
                            float rating = cursor.getFloat(ratingIndex);
                            int calories = cursor.getInt(caloriesIndex);
                            int duration = durationIndex != -1 ? cursor.getInt(durationIndex) : 0; // Default to 0 if missing

                            favoriteRecipes.add(new recipe(id, title, description, imagePath, rating, calories, duration));
                        } else {
                            Log.e("DatabaseError", "One or more columns not found in the database schema.");
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("DatabaseError", "Cursor is null. Query might have failed or no rows match the criteria.");
        }

        // Set up RecyclerView adapter
        recipeAdapter = new RecipeAdapter(getContext(), favoriteRecipes, position -> {
            recipe clickedRecipe = favoriteRecipes.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt("RECIPE_ID", clickedRecipe.getId());

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_detail, bundle);
        });

        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}
