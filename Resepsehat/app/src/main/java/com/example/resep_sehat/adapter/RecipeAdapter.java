package com.example.resep_sehat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resep_sehat.R;
import com.example.resep_sehat.model.recipe;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private ArrayList<recipe> recipeList;
    public OnRecipeListener onRecipeListener;

    public RecipeAdapter(Context context, ArrayList<recipe> recipeList, OnRecipeListener onRecipeListener) {
        this.context = context;
        this.recipeList = recipeList;
        this.onRecipeListener = onRecipeListener;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view, onRecipeListener);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        recipe recipe = recipeList.get(position);

        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipeDescription.setText(recipe.getDescription());
        holder.recipeRating.setText(String.valueOf(recipe.getRating()));

        // Display duration and calories
        holder.recipeDuration.setText("Duration: " + recipe.getDuration() + " mins");
        holder.recipeCalories.setText("Calories: " + recipe.getCalories());

        // Load image using Glide
        Glide.with(context)
                .load(recipe.getImagePath())
                .placeholder(R.drawable.ic_add) // Placeholder image if null
                .into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Method to update recipes dynamically
    public void updateRecipes(ArrayList<recipe> newRecipes) {
        recipeList.clear();        // Clear the old list
        recipeList.addAll(newRecipes); // Add the new list
        notifyDataSetChanged();    // Notify the adapter to refresh the RecyclerView
    }

    // New method to get recipe at a specific position
    public recipe getRecipeAt(int position) {
        return recipeList.get(position);
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView recipeImage;
        TextView recipeTitle, recipeDescription, recipeRating, recipeDuration, recipeCalories; // Add these
        Button viewRecipeButton;
        OnRecipeListener onRecipeListener;

        public RecipeViewHolder(View itemView, OnRecipeListener onRecipeListener) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeDescription = itemView.findViewById(R.id.recipe_description);
            recipeRating = itemView.findViewById(R.id.recipe_rating_duration);
            recipeDuration = itemView.findViewById(R.id.recipe_duration); // Initialize duration
            recipeCalories = itemView.findViewById(R.id.recipe_calories); // Initialize calories
            viewRecipeButton = itemView.findViewById(R.id.btn_detail_recipe);

            this.onRecipeListener = onRecipeListener;
            itemView.setOnClickListener(this);

            // Set click listener for the "View Recipe" button
            viewRecipeButton.setOnClickListener(v -> onRecipeListener.onRecipeClick(getAdapterPosition()));
        }

        @Override
        public void onClick(View v) {
            onRecipeListener.onRecipeClick(getAdapterPosition());
        }
    }


    public interface OnRecipeListener {
        void onRecipeClick(int position);
    }
}
