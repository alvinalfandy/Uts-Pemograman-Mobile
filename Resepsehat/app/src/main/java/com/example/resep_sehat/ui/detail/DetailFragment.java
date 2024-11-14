package com.example.resep_sehat.ui.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.activity.OnBackPressedCallback;

import com.bumptech.glide.Glide;
import com.example.resep_sehat.R;
import com.example.resep_sehat.helper.DatabaseHelper;

public class DetailFragment extends Fragment {

    private TextView titleTextView, descriptionTextView, stepsTextView;
    private ImageView detailImageView;
    private Button favoriteButton;
    private DatabaseHelper databaseHelper;
    private int recipeId; // Store recipeId for use in multiple methods

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // Initialize UI components
        detailImageView = view.findViewById(R.id.detail_image);
        titleTextView = view.findViewById(R.id.title_bahan);
        descriptionTextView = view.findViewById(R.id.description_bahan);
        stepsTextView = view.findViewById(R.id.description_cara_pembuatan);
        favoriteButton = view.findViewById(R.id.button_favorite);

        databaseHelper = new DatabaseHelper(requireContext());

        // Retrieve recipe ID from arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            recipeId = bundle.getInt("RECIPE_ID", -1);
            if (recipeId != -1) {
                loadRecipeDetails(recipeId);
            } else {
                Toast.makeText(requireContext(), "Invalid recipe ID", Toast.LENGTH_SHORT).show();
            }
        }

        // Add a click listener to the favorite button
        favoriteButton.setOnClickListener(v -> {
            boolean isSaved = databaseHelper.setFavorite(recipeId, 1); // Mark as favorite

            if (isSaved) {
                Toast.makeText(requireContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
                // Navigate to DashboardFragment
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.navigation_dashboard);
            } else {
                Toast.makeText(requireContext(), "Failed to add to favorites", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle back press to go back to MainActivity
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Trigger back press to MainActivity
                requireActivity().onBackPressed();
            }
        });

        return view;
    }

    private void loadRecipeDetails(int recipeId) {
        Cursor cursor = databaseHelper.getRecipeById(recipeId);

        if (cursor != null && cursor.moveToFirst()) {
            // Use constants from DatabaseHelper for column names
            int titleIndex = cursor.getColumnIndex(DatabaseHelper.COL_2);
            int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COL_3);
            int stepsIndex = cursor.getColumnIndex(DatabaseHelper.COL_11);
            int imagePathIndex = cursor.getColumnIndex(DatabaseHelper.COL_7);

            // Safely retrieve values
            String title = titleIndex != -1 ? cursor.getString(titleIndex) : "Title Not Available";
            String description = descriptionIndex != -1 ? cursor.getString(descriptionIndex) : "Description Not Available";
            String steps = stepsIndex != -1 ? cursor.getString(stepsIndex) : "Steps Not Available";
            String imagePath = imagePathIndex != -1 ? cursor.getString(imagePathIndex) : null;

            // Set values to views
            titleTextView.setText(title);
            descriptionTextView.setText(description);
            stepsTextView.setText(steps);

            // Load image using Glide
            if (imagePath != null) {
                Glide.with(this)
                        .load(imagePath)
                        .placeholder(R.drawable.ic_add) // Use an appropriate placeholder image
                        .into(detailImageView);
            } else {
                detailImageView.setImageResource(R.drawable.ic_fav); // Set a placeholder if imagePath is null
            }

            cursor.close();
        } else {
            Toast.makeText(requireContext(), "Failed to load recipe details", Toast.LENGTH_SHORT).show();
        }
    }
}
