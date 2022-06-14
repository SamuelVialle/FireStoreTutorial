package com.samuelvialle.firestoretutorial;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.samuelvialle.firestoretutorial.model.NoteModel;

public class MainActivitySimple extends AppCompatActivity {

    private static final String KEY_DESCRIPTION = "description";
    private EditText et_title, et_description;
    private TextView tv_data;
    private String title, description;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("Notebook/my first note");

    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        tv_data = findViewById(R.id.tv_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(MainActivitySimple.this, "Error while loading: " + error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (value.exists()) {
                    NoteModel note = value.toObject(NoteModel.class);
                    String title = note.getTitle();
                    String description = note.getDescription();
                    tv_data.setText("Title: " + title + "\n" + "Description: " + description);
                } else {
                    tv_data.setText("Title: " + "" + "\n" + "Description: " + "");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_part2);
        initUI();
        title = et_title.getText().toString();
        description = et_description.getText().toString();
    }

    public void saveNote(View v) {
        NoteModel note = new NoteModel(title, description);
        noteRef.set(note);
    }

    public void updateDescription(View v){
        noteRef.update(KEY_DESCRIPTION, description);
    }

    public void deleteDescription(View v){
        noteRef.update(KEY_DESCRIPTION, "");
    }

    public void deleteNote(View v){
        noteRef.delete();
    }

    public void loadNote(View v) {
        noteRef.get();
    }
}