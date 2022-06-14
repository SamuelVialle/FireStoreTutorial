package com.samuelvialle.firestoretutorial;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samuelvialle.firestoretutorial.model.NoteModel;

public class MainActivityPart3 extends AppCompatActivity {

    private EditText et_title, et_description, et_priority; // (30)
    private String title, description;
    private int priority;
    private TextView tv_data;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");

    private DocumentSnapshot lastResult; //(46)

    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        et_priority = findViewById(R.id.et_priority);
        tv_data = findViewById(R.id.tv_data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_part2);
        initUI();
    }

    public void addNote(View v) {
        title = et_title.getText().toString();
        description = et_description.getText().toString();

        if (et_priority.length() == 0) {
            et_priority.setText("0");
        } else {
            priority = Integer.parseInt(et_priority.getText().toString());
        }
        NoteModel note = new NoteModel(title, description, priority);
        notebookRef.add(note);
    }


    /** Méthode de base **/
//    public void loadNotes(View v) {
//        notebookRef.orderBy("priority") // (43)
//                .startAt(3, "title") // (43)

//        notebookRef.orderBy("priority")  // (44)
//                .orderBy("title") // (44)
//                .startAt(3, "title2") // (44) // title2 est le string utilisé comme titre
//
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        String data = "";
//
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            NoteModel note = documentSnapshot.toObject(NoteModel.class);
//
//                            note.setDocumentId((documentSnapshot.getId()));
//
//                            String documentId = note.getDocumentId();
//                            String title = note.getTitle();
//                            String description = note.getDescription();
//                            int priority = note.getPriority();
//                            data += "ID: " + documentId
//                                    + "\nTitle: " + title + "\nDescription: " + description
//                                    + "\nPriority: " + priority + "\n\n";
//                        }
//                        tv_data.setText(data);
//                    }
//                });
//    }

    /** Méthode avec le query d'un documentSnapShot **/

//    public void loadNotes(View v) {
//        notebookRef.document("gIyp7MVgkMGT5tPHzHaU") // Doc pris au hasard dans la base
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        notebookRef.orderBy("priority")
//                                .startAt(documentSnapshot)
//                                .get()
//                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                        String data = "";
//
//                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                            NoteModel note = documentSnapshot.toObject(NoteModel.class);
//
//                                            note.setDocumentId((documentSnapshot.getId()));
//
//                                            String documentId = note.getDocumentId();
//                                            String title = note.getTitle();
//                                            String description = note.getDescription();
//                                            int priority = note.getPriority();
//                                            data += "ID: " + documentId
//                                                    + "\nTitle: " + title + "\nDescription: " + description
//                                                    + "\nPriority: " + priority + "\n\n";
//                                        }
//                                        tv_data.setText(data);
//                                    }
//                                });
//                    }
//                });
//    }

    /**
     * Méthode avec la gestion de la pagination
     **/
    public void loadNotes(View v) {
        Query query; // Création de la query
        if (lastResult == null) { // Si lastResult est null
            query = notebookRef.orderBy("priority") // alors on affiche
                    .limit(3); // 3 résultats
        } else { // sinon
            query = notebookRef.orderBy("priority")
                    .startAfter(lastResult) // on affiche les résultats suivant
                    .limit(3);
        }
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            NoteModel note = documentSnapshot.toObject(NoteModel.class);
                            note.setDocumentId(documentSnapshot.getId());
                            String documentId = note.getDocumentId();
                            String title = note.getTitle();
                            String description = note.getDescription();
                            int priority = note.getPriority();
                            data += "ID: " + documentId
                                    + "\nTitle: " + title + "\nDescription: " + description
                                    + "\nPriority: " + priority + "\n\n";
                        }
                        if (queryDocumentSnapshots.size() > 0) { // Pour éviter des erreurs avec le -1 ci-dessous
                            data += "___________\n\n";
                            tv_data.append(data);  // ajout avec append
                            lastResult = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                });
    }
}