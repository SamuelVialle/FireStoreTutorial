package com.samuelvialle.firestoretutorial;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.samuelvialle.firestoretutorial.model.NoteModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivityPart2 extends AppCompatActivity {

    private static final String KEY_DESCRIPTION = "description";
    private EditText et_title, et_description, et_priority; // (30)
    private String title, description;
    private int priority;
    private TextView tv_data;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection("Notebook");

    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        et_priority = findViewById(R.id.et_priority); // (31)
        tv_data = findViewById(R.id.tv_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        notebookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    NoteModel note = documentSnapshot.toObject(NoteModel.class);
                    note.setDocumentId(documentSnapshot.getId()); // (28)

                    String documentId = note.getDocumentId();
                    String title = note.getTitle();
                    String description = note.getDescription();
                    int priority = note.getPriority(); // (34)

                    data += "ID: " + documentId
                            + "\nTitle: " + title
                            + "\nDescription: " + description
                            + "\nPriority: " + priority // (34)
                            + "\n\n";
                }
                // Remplissage de la "list"" avec les datas
                tv_data.setText(data);
            }
        });

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
        notebookRef.add(note); //(26)
        // Si cela ne fonctionne pas ajouter les méthodes de vérifications pour savoir pourquoi
    }

//    public void loadNotes(View v) {
//        notebookRef
////                .whereEqualTo("priority", 2) // (35)
//
////                .whereGreaterThan("priority", 2) // (36)
//
////                .orderBy("priority", Query.Direction.DESCENDING) // (37)
//
////                .orderBy("priority", Query.Direction.ASCENDING) // (38)
//
////                .limit(3) //(39)
//
////                .whereGreaterThan("priority", 2) //(40)
////                .whereEqualTo("title", "Aa") //(40)
////                .orderBy("priority", Query.Direction.ASCENDING) //(40)
//
//                .orderBy("priority") //(41)
//                .orderBy("title") //(41)
//                .get()//(27)
//                // Si cela ne fonctionne pas ajouter les méthodes de vérifications pour savoir pourquoi
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        // On commence par créer un string vide pour la gestion de l'affichage des datas de la requête
//                        String data = "";
//                        // Dans une boucle for each nous allons lister tous les documents de la collection
//                        // ici documentSnapShot = 1 document // queryDocumentSnapshots = tous les documents
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            // Création de l'objet à partir de notre model
//                            NoteModel note = documentSnapshot.toObject(NoteModel.class);
//                            note.setDocumentId(documentSnapshot.getId()); // (28)
//
//                            String documentId = note.getDocumentId();
//                            String title = note.getTitle();
//                            String description = note.getDescription();
//                            int priority = note.getPriority(); // (33)
//
//                            // Création de la "liste"
//                            data += "ID: " + documentId
//                                    + "\nTitle: " + title
//                                    + "\nDescription: " + description
//                                    + "\nPriority: " + priority  // (33)
//                                    + "\n\n";
//                        }
//                        // Remplissage de la "list"" avec les datas
//                        tv_data.setText(data);
//                    }
//                })
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull @NotNull Exception e) {
//                Log.i("TAG", e.toString());
//            }
//        });
//    }

    public void loadNotes(View v) { //(42)
        Task task1 = notebookRef.whereLessThan("priority", 2)
                .orderBy("priority")
                .get();

        Task task2 = notebookRef.whereGreaterThan("priority", 2)
                .orderBy("priority")
                .get();
        // Bien ordonner vos requêtes car elles apparaîtront dans l'ordre que vous avez choisi !
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        // Notre task doit être une Liste de querySnapShot
        // Il existe plusieurs méthodes when qui peuvent être appelées en fonction de vos besoin
        // Pour info la plus utile est Tasks.whenAll(Collection<? extends Task ...
        allTasks.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {

                String data = "";
                // On crée une boucle qui va lire toutes les données des querySnapShot
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    // Dans laquelle on lit tous les documents grace à queryDocumentsSnapShot
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // On affiche les réusltats dans l'ordre des tasks !!
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
                }
                tv_data.setText(data);
            }
        });

    }
}