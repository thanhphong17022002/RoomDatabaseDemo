package com.example.roomdatabase;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomdatabase.constrants.Constants;
import com.example.roomdatabase.model.Person;

import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {
    private List<Person> persons = new ArrayList<>();
    private Context context;

    public PersonAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_person, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = persons.get(position);
        holder.textViewFirstName.setText(person.getFirstName());
        holder.textViewLastName.setText(person.getLastName());

        holder.imageViewEdit.setOnClickListener(v -> {
            // Handle click to edit
            Intent intent = new Intent(context, EditPersonActivity.class);
            intent.putExtra(Constants.UPDATE_Person_Id, person.getUid()); // Pass the person's ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public void setTasks(List<Person> persons) {
        this.persons = persons;
        notifyDataSetChanged();
    }

    public List<Person> getTasks() {
        return persons;
    }

    static class PersonViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFirstName;
        TextView textViewLastName;
        ImageView imageViewEdit;

        PersonViewHolder(View itemView) {
            super(itemView);
            textViewFirstName = itemView.findViewById(R.id.tvFirstName);
            textViewLastName = itemView.findViewById(R.id.tvLastName);
            imageViewEdit = itemView.findViewById(R.id.ivEdit);
        }
    }
}

