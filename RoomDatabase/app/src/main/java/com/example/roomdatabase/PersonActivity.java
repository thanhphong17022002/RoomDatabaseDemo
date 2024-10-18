package com.example.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.roomdatabase.db.AppDatabase;
import com.example.roomdatabase.model.Person;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executors;

public class PersonActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;
    private PersonAdapter adapter;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set edge-to-edge handling and layout
        setContentView(R.layout.activity_person);
        enableEdgeToEdge();

        // Initialize views
        fabAdd = findViewById(R.id.fabAdd);
        recyclerView = findViewById(R.id.rvPersons);

        // Set a LayoutManager for the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Add this line

        // Set up RecyclerView with adapter
        adapter = new PersonAdapter(this);
        recyclerView.setAdapter(adapter);

        // Initialize the Room database
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "app-database").build();

        // FloatingActionButton click to open Add/Edit activity
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(PersonActivity.this, EditPersonActivity.class));
        });

        // Swipe-to-delete functionality
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false; // No need for drag-and-drop
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<Person> persons = adapter.getTasks();
                Person personToDelete = persons.get(position);

                // Remove the person in the background thread
                Executors.newSingleThreadExecutor().execute(() -> {
                    db.personDao().delete(personToDelete);
                    runOnUiThread(() -> {
                        Toast.makeText(PersonActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        retrieveTasks(); // Refresh list after deletion
                    });
                });
            }
        }).attachToRecyclerView(recyclerView);

        // Load data into the RecyclerView
        retrieveTasks();
    }

    /**
     * Enable edge-to-edge window insets handling.
     */
    private void enableEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Fetches the list of persons from the database and updates the adapter.
     */
    private void retrieveTasks() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Person> persons = db.personDao().getAll();
            runOnUiThread(() -> adapter.setTasks(persons));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrieveTasks(); // Reload data when returning to the activity
    }
}
