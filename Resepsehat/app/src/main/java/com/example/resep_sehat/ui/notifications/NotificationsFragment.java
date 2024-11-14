package com.example.resep_sehat.ui.notifications;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.resep_sehat.R;
import com.example.resep_sehat.helper.DatabaseHelper;

public class NotificationsFragment extends Fragment {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private Uri imageUri;
    private EditText editTitle, editDescription, editIngredients, editSteps, editDuration, editCategory, editCalories, editRating;
    private Button buttonImage, buttonSave;
    private ImageView imagePreview;
    private DatabaseHelper databaseHelper;

    // Registers a photo picker activity launcher in single-select mode.
    private ActivityResultLauncher<Intent> pickMedia = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData(); // Dapatkan URI gambar
                    if (uri != null) {
                        imageUri = uri;
                        imagePreview.setImageURI(imageUri); // Tampilkan gambar di ImageView
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Inisialisasi komponen UI
        imagePreview = view.findViewById(R.id.image_preview);
        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        editIngredients = view.findViewById(R.id.edit_ingredients);
        editSteps = view.findViewById(R.id.edit_steps);
        editDuration = view.findViewById(R.id.edit_duration);
        editCategory = view.findViewById(R.id.edit_category);
        editCalories = view.findViewById(R.id.edit_calories);
        editRating = view.findViewById(R.id.edit_rating);
        buttonImage = view.findViewById(R.id.button_image);
        buttonSave = view.findViewById(R.id.button_save);

        databaseHelper = new DatabaseHelper(getContext());

        // Memeriksa dan meminta izin
        checkAndRequestPermissions();

        // Listener untuk tombol pilih gambar
        buttonImage.setOnClickListener(v -> openFileChooser());

        // Listener untuk tombol simpan resep
        buttonSave.setOnClickListener(v -> saveRecipe());

        return view;
    }

    // Memeriksa dan meminta izin untuk akses penyimpanan
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Bisa menampilkan penjelasan kepada pengguna
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
            }
        } else {
            openFileChooser();
        }
    }

    // Menangani hasil permintaan izin
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan, buka galeri
                openFileChooser();
            } else {
                Toast.makeText(getContext(), "Izin diperlukan untuk mengakses penyimpanan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metode untuk membuka file chooser
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickMedia.launch(intent);
    }

    // Metode untuk menyimpan resep
    private void saveRecipe() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String ingredients = editIngredients.getText().toString().trim();
        String steps = editSteps.getText().toString().trim();
        String duration = editDuration.getText().toString().trim();
        String category = editCategory.getText().toString().trim();
        String caloriesString = editCalories.getText().toString().trim();
        String ratingString = editRating.getText().toString().trim();

        // Pastikan semua field diisi
        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty() ||
                steps.isEmpty() || duration.isEmpty() || category.isEmpty() || caloriesString.isEmpty() || ratingString.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "Silakan isi semua field dan pilih gambar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert kalori dan rating ke tipe data yang sesuai
        int calories = Integer.parseInt(caloriesString);
        float rating = Float.parseFloat(ratingString);

        // Simpan resep ke database
        boolean isInserted = databaseHelper.insertData(title, description,
                "Judul Bahan", ingredients, // Judul bahan jika ada
                "Judul Langkah", steps, // Judul langkah jika ada
                imageUri.toString(), // Simpan path gambar
                category, calories, rating); // Simpan kategori, kalori, dan rating
        if (isInserted) {
            Toast.makeText(getContext(), "Resep berhasil disimpan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Gagal menyimpan resep", Toast.LENGTH_SHORT).show();
        }
    }
}
