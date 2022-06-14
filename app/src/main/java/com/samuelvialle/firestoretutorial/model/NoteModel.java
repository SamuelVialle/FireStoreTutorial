package com.samuelvialle.firestoretutorial.model;

import com.google.firebase.firestore.Exclude;

public class NoteModel {
    private String documentId;
    private String title;
    private String description;
    private int priority;


    public NoteModel() {
        // Ne pas oublier le constructeur vide pour Firestore
    }

    // Constructeur pour la part1
    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //Constructeur pour la part2
    public NoteModel(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Exclude // Pour ne pas changer l'id automatique de Firebase
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
