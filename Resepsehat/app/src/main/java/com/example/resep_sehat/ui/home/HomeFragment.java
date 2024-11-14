package com.example.resep_sehat.ui.home;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements RecipeAdapter.OnRecipeListener {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private DatabaseHelper databaseHelper;
    private TextInputEditText searchInput;
    private ChipGroup chipGroupCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_resep);
        searchInput = view.findViewById(R.id.search_input);
        // Initialize chipGroupCategory
        chipGroupCategory = view.findViewById(R.id.chipGroupCategory); // Ensure ID matches fragment_home.xml

        databaseHelper = new DatabaseHelper(getContext());
        loadRecipes();
        setupSearch();
        setupCategoryFilter();

        return view;
    }
    private void loadRecipes() {
        ArrayList<recipe> recipes = databaseHelper.getAllRecipes();

        recipeAdapter = new RecipeAdapter(getContext(), recipes, this);
        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipesByName(s.toString());  // Call the method here
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    // Inside HomeFragment.java

    private void setupCategoryFilter() {
        chipGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            String category = null;

            // Map chip ID to category
            if (checkedId == R.id.chip_breakfast) {
                category = "Makan Pagi";
            } else if (checkedId == R.id.chip_lunch) {
                category = "Makan Siang";
            } else if (checkedId == R.id.chip_dinner) {
                category = "Makan Malam";
            }

            // Filter recipes by category
            filterRecipesByCategory(category);
        });
    }

    private void filterRecipesByCategory(String category) {
        ArrayList<recipe> filteredRecipes;

        // Fetch all recipes if no category is selected
        if (category == null) {
            filteredRecipes = databaseHelper.getAllRecipes();
        } else {
            filteredRecipes = databaseHelper.getRecipesByCategory(category);
        }

        // Update RecyclerView adapter with filtered recipes
        recipeAdapter.updateRecipes(filteredRecipes);
    }

    private void filterRecipesByName(String query) {
        ArrayList<recipe> filteredRecipes = new ArrayList<>();

        // Query database if needed, or filter list in memory
        Cursor cursor = databaseHelper.searchRecipesByName(query);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_1);
                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COL_2);
                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COL_3);
                int imagePathIndex = cursor.getColumnIndex(DatabaseHelper.COL_7);
                int ratingIndex = cursor.getColumnIndex(DatabaseHelper.COL_6);
                int caloriesIndex = cursor.getColumnIndex(DatabaseHelper.COL_5);
                int durationIndex = cursor.getColumnIndex(DatabaseHelper.COL_11);

                // Ensure all required indices are valid
                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
                        imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {

                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String description = cursor.getString(descriptionIndex);
                    String imagePath = cursor.getString(imagePathIndex);
                    float rating = cursor.getFloat(ratingIndex);
                    int calories = cursor.getInt(caloriesIndex);
                    int duration = cursor.getInt(durationIndex);

                    // Add the recipe to the filtered list
                    filteredRecipes.add(new recipe(id, title, description, imagePath, rating, calories, duration));
                } else {
                    Log.e("DatabaseError", "One or more columns not found in the database.");
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Update adapter with filtered list
        recipeAdapter.updateRecipes(filteredRecipes);
    }

//    private void filterRecipesByCategory(String category) {
//        ArrayList<recipe> filteredRecipes = databaseHelper.getRecipesByCategory(category);
//        Cursor cursor = (Cursor) databaseHelper.getRecipesByCategory(category);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_1);
//                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COL_2);
//                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COL_3);
//                int imagePathIndex = cursor.getColumnIndex(DatabaseHelper.COL_7);
//                int ratingIndex = cursor.getColumnIndex(DatabaseHelper.COL_6);
//                int caloriesIndex = cursor.getColumnIndex(DatabaseHelper.COL_5);
//                int durationIndex = cursor.getColumnIndex(DatabaseHelper.COL_11);
//
//                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
//                        imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {
//                    int id = cursor.getInt(idIndex);
//                    String title = cursor.getString(titleIndex);
//                    String description = cursor.getString(descriptionIndex);
//                    String imagePath = cursor.getString(imagePathIndex);
//                    float rating = cursor.getFloat(ratingIndex);
//                    int calories = cursor.getInt(caloriesIndex);
//                    int duration = cursor.getInt(durationIndex);
//
//                    filteredRecipes.add(new recipe(id, title, description, imagePath, rating, calories, duration));
//                } else {
//                    Log.e("DatabaseError", "One or more columns not found in the database.");
//                }
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        recipeAdapter.updateRecipes(filteredRecipes);
//    }



//    private void filterRecipes(String query) {
//        ArrayList<recipe> filteredRecipes = new ArrayList<>();
//
//        Cursor cursor = databaseHelper.searchRecipesByName(query);  // Ensure `searchRecipesByName` is defined in DatabaseHelper
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_1);
//                int titleIndex = cursor.getColumnIndex(DatabaseHelper.COL_2);
//                int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COL_3);
//                int imagePathIndex = cursor.getColumnIndex(DatabaseHelper.COL_7);
//                int ratingIndex = cursor.getColumnIndex(DatabaseHelper.COL_6);
//                int caloriesIndex = cursor.getColumnIndex(DatabaseHelper.COL_5);
//
//                // Check if each column exists
//                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 &&
//                        imagePathIndex != -1 && ratingIndex != -1 && caloriesIndex != -1) {
//
//                    int id = cursor.getInt(idIndex);
//                    String title = cursor.getString(titleIndex);
//                    String description = cursor.getString(descriptionIndex);
//                    String imagePath = cursor.getString(imagePathIndex);
//                    float rating = cursor.getFloat(ratingIndex);
//                    int calories = cursor.getInt(caloriesIndex);
//
//                    filteredRecipes.add(new recipe(id, title, description, imagePath, rating, calories));
//                } else {
//                    // Log an error if any column is missing
//                    Log.e("DatabaseError", "One or more columns not found in the database.");
//                }
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        // Update adapter with filtered list
//        recipeAdapter.updateRecipes(filteredRecipes);
//    }

    @Override
    public void onRecipeClick(int position) {
        recipe clickedRecipe = recipeAdapter.getRecipeAt(position);
        Bundle bundle = new Bundle();
        bundle.putInt("RECIPE_ID", clickedRecipe.getId());

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_navigation_home_to_navigation_detail, bundle);
    }

}
