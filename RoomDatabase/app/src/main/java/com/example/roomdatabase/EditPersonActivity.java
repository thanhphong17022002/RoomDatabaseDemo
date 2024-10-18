package com.example.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.roomdatabase.constrants.Constants;
import com.example.roomdatabase.db.AppDatabase;
import com.example.roomdatabase.model.Person;

public class EditPersonActivity extends AppCompatActivity {

    private EditText eFirstName;
    private EditText eLastName;
    private Button btnSave;
    private int mPersonId;

    private Intent intent;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person); // Set content view first

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        mDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database").build();

        intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.UPDATE_Person_Id)) {
            btnSave.setText("Update");
            mPersonId = intent.getIntExtra(Constants.UPDATE_Person_Id, -1);

            com.example.roomdatabase.executors.AppExecutors.getInstance().diskIO().execute(() -> {
                Person person = mDb.personDao().loadPersonById(mPersonId);
                populateUI(person);
            });
        }
    }

    private void populateUI(Person person) {
        if (person == null) {
            return;
        }

        eFirstName.setText(person.getFirstName());
        eLastName.setText(person.getLastName());
    }

    private void initViews() {
        eFirstName = findViewById(R.id.editTextFirstName);
        eLastName = findViewById(R.id.editTextLastName);
        btnSave = findViewById(R.id.buttonSave);

        btnSave.setOnClickListener(view -> onSaveBtnClicked());
    }

    public void onSaveBtnClicked() {
        final Person person = new Person(
                eFirstName.getText().toString(),
                eLastName.getText().toString());

        // Generate a unique ID
        com.example.roomdatabase.executors.AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (!intent.hasExtra(Constants.UPDATE_Person_Id)) {
                    // Generate unique uid
                    int newId = generateUniqueId();
                    person.setUid(newId);
                    mDb.personDao().insert(person);
                } else {
                    person.setUid(mPersonId);
                    mDb.personDao().update(person);
                }
                finish();
            }
        });
    }



    private int generateUniqueId() {
        // Fetch the max UID
        int maxId = mDb.personDao().getMaxUid();
        // If maxId is null (no entries in database), start from 1
        return (maxId == 0) ? 1 : maxId + 1;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
