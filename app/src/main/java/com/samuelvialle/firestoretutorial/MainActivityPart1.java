package com.samuelvialle.firestoretutorial;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.samuelvialle.firestoretutorial.model.NoteModel;

import org.jetbrains.annotations.NotNull;

public class MainActivityPart1 extends AppCompatActivity {

    private static final String TAG = "MainActivityPart2";

    /**
     * Variables globales
     **/
    // (1) Les clés
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";

    // (2) Les variables de liaison avec le code
    private EditText et_title, et_description;
    // (9)
    private TextView tv_data;

    /**
     * (3) Variable pour la connexion à Firebase
     **/
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // (8) Ajout de la référence à la collection
    private DocumentReference noteRef = db.collection("Notebook").document("My first note");
//    private DocumentReference noteRef = db.document("Notebook/my first note"); // En raccourci

    // (13) Ajout du listenerRegistration
    private ListenerRegistration noteListeneer;

    /**
     * (4) Initialisation des widgets avec le code
     */
    private void initUI() {
        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        tv_data = findViewById(R.id.tv_data); // (9.1)
    }

    /**
     * (11) Ajout de la méthode onStart et du listener
     **/
    @Override
    protected void onStart() {
        super.onStart();
        //(14)(16)
        // noteListeneer = noteRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
        noteRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                // On commence par vérifier qu'il n'y ai pas d'erreur
                if (error != null) {
                    Toast.makeText(MainActivityPart1.this, "Error while loading: " + error, Toast.LENGTH_SHORT).show();
                    return; // Pour quitter la méthode s'il y a des erreurs
                }
                if (value.exists()) {
                    // (24) Collage des données
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
        setContentView(R.layout.activity_main_part1);
        // Appel des différentes méthodes
        initUI(); // (4.1)
    }

    @Override
    protected void onStop() {
        super.onStop();
        //noteListeneer.remove(); // (15)(17)
    }

    /**
     * (5) Méthode pour la sauvegarde des données dans la db
     **/
    public void saveNote(View v) {
        String title = et_title.getText().toString();
        String description = et_description.getText().toString();

        // (23) Avec l'appel à notre model
        NoteModel note = new NoteModel(title, description);


        // (6) Création du tableau pour envoyer les données en fonction des clés
//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_TITLE, title);
//        note.put(KEY_DESCRIPTION, description);

        // Envoi vers firestore dans la collection NoteBook avec pour id My first note
        // ci-dessous la version sans référence au document
        //db.collection("NoteBook").document("My first note")
        // Et en utilisant noteRef, on obtient :
        noteRef // (8.1)
                // il est possible de raccourcir la ligne ci-dessus
                // .document("Notebook/My first note")
                .set(note) // Il est possible de s'arrêter ici mais avec un vérification c'est plus propre
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        Toast.makeText(MainActivityPart1.this, "Note saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivityPart1.this, "Error!" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** (18) Méthode pour faire juste l'update d'un champ **/
    public void updateDescription(View v){
        String description = et_description.getText().toString();
//        // Méthode 1 avec le HashMap et un set plus merge
//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_DESCRIPTION, description);
//
//        noteRef.set(note, SetOptions.merge());

        // Méthode 2 en utilisant update et la clé de référence
        noteRef.update(KEY_DESCRIPTION, description);
        // Pour les vérifications ajouter addOnCompleteListener et addOnFailureListener
    }

    /** (19) Méthode deleteDescription **/
    public void deleteDescription(View v){
//        // Méthode 1 avec le HashMap et un delete de la valeur du champ
//        Map<String, Object> note = new HashMap<>();
//        note.put(KEY_DESCRIPTION, FieldValue.delete());
//
//        noteRef.update(note);
        // Méthode 2 en utilisant update et la clé de référence
        //noteRef.update(KEY_DESCRIPTION, FieldValue.delete());
        // (22) Envoi de vide pour ne pas afficher null
        noteRef.update(KEY_DESCRIPTION, "");
        // Pour les vérifications ajouter addOnCompleteListener et addOnFailureListener
    }

    public void deleteNote(View v){
        noteRef.delete();
        // Pour les vérifications ajouter addOnCompleteListener et addOnFailureListener
    }

    /**
     * (10) Méthode pour afficher les données de la base
     **/
    public void loadNote(View v) { //(7)
        noteRef.get() // (10)
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Les données de la base sont dans le documentSnapshot il suffit donc de vérifier que ce la ne soit pas vide
                        if (documentSnapshot.exists()) {
                            // (23) On commente les définitions des string et ajoute le model
                            NoteModel note = documentSnapshot.toObject(NoteModel.class);
                            // et on ajoute les string à afficher dans le TextView
                            String title = note.getTitle();
                            String description = note.getDescription();
//                            String title = documentSnapshot.getString(KEY_TITLE);
//                            String description = documentSnapshot.getString(KEY_DESCRIPTION);
//                            // On peut aussi utiliser une map pour récupérer les objets
//                            //Map<String, Object> note = documentSnapshot.getData();

                            // Affichage du résultat dans le TextView
                            tv_data.setText("Title: " + title + "\n" + "Description: " + description);
                        } else {
                            // On affiche un toast pour dire qu'il n'y a pas de données
                            Toast.makeText(MainActivityPart1.this, "Document does not exist", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(MainActivityPart1.this, "Error retrieving data!" + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}