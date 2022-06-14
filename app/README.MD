# MANAGE DATAS WITH FIRESTORE

## Le projet
* Créer un nouveau projet avec une empty activity
* Connecter le projet Android Studio aux outils Firebase en utilisant le menu Tools>Firebase
* Cliquer sur Cloud FireStore et suivre les instructions 1 et 2 
* Activer Firestore Database; modifier les régles pour rendre la base accessible sans authentification

## Le design de activity_main.xml
* Changer le container principal par un LinearLayout Vertical, et lui ajouter un padding de 16dp pour qu'il ne colle pas aux bords
* Ajouter 2 EditText 
    <EditText
            android:id="@+id/xxxxxx"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="xxxxxx"
            android:inputType="text"/>
* Ajouter un bouton pour valider les actions

## Le code de MainActivity 
* Ajouter les variables globales
    * Les constantes pour les clés (1)
    * Les variables pour la liaison des widgets avec le code (2)
    * La  variable de connexion à Firestore et l'instancier avec la référence à la base de donnée (3)
* Initialisation des widgets dans la méthode initUI() (4) sans oublier son appel dans le onCreate() (4.1) 

### Save data to Firebase (5)
* Création de la méthode sauvegarde des données dans Firestore qui sera associée au 
bouton via la méthode onClick du XML
* Dans ce premier exemple nous allons utiliser un HashMap pour transférer les données (6)

### Retrieve date from Firebase (7)
* Ajout d'un second bouton Load dans activity_main.xml
* Ajout d'un textView pour afficher les données sauvegardées faire le lien dans ma méthode initUI() (9)
* Mis en place d'une référence directement vers le document dans la base(8)
* Modification de la méthode d'envoi (8.1)
* Création de la méthode loadNote pour afficher les datas de la base avec l'utilisation de get() (10)
 
### Ajout du snapshotListener
Le snapshotListenr permet la mise à jour en temps réel, et sans cliquer sur le bouton load du contenu de la base
Ce listner doit être déclaré dans la méthode onStart de notre application
* Ajout de la méthode onStart() et du listener (11)
* Copie du code qui est effectué dans la méthode du bouton LOAD(12)
* Il faut détacher le listener dans la méthode onStop() pour ne pas quelle tourne en boucle
* Commencer par ajouter un objet de type ListenerRegistration de façon global (13)
* l'affecter à notre listener dans la méthode onStart() (14)
* De façon à pouvoir le retirer dans la méthode onStop() (15)
* Il existe une seconde manière qui est de définir l'emplacement dans lequel cette méthode doit s'exécuter :
    * Ajouter this lors de la création du addSnapShotListener (16) et supprimer son appel dans la méthode onStop()
    
### Update du champ description seulement
* Ajouter un Bouton dans main_activity layout pour faire l'update de la description
* Ajout la méthode updateDescription() dans MainActivity (18)
* 1ère méthode avec merge
* 2de méthode avec update
A noter que les deux méthodes n'ont pas le même impacte sur la visualisation, prenons le cas ou l'on supprime les données 
de la base  de données, et que l'on clique à nouveau sur le bouton update description : un merge supprimera les données affichées 
sur le terminal alors que l'update laissera les données enregistrées sur le termnial. 

### Delete data 
* Commençons par ajouter un LinearLayout qui contiendra le bouton save et update description
* Et un second LinearLayout qui contiendra les boutons delete all et delete description (boutons que nous allons créer)
* Modifier MainActivity pour ajouter les méthodes 
        * Les méthodes delete description (19) et l'ajout du champ vide dans le textView pour éviter l'afficahge de null(22)
        * La méthode delete all (20) 
        * Il faut ensuite modifier la condition if value.exists de la méthode onStart() pour gérer l'affichage vide (21)

### Ajout du model pour gérer les datas
* Création d'un package model 
* Création d'une classe NoteModel, attention à bien utiliser les mêmes noms de champs entre la base de donnée et le modèle
* Modification du MainActivity pour remplacer la Map par l'appel de notre modèle de données
    * Dans la méthode saveNote (23) 
    * Dans la méthode loadNote (24)
    * On copie alors les quatres dernières lignes de code pour les ajouter dans le aadSnapShotListener (24)

#### Avant de continuer avec la gestion de plusieurs valeurs
* Je met à disposition une version simplifiée sans les vérifications et sans les commentaires MainActivitySimple
* Créer une nouvelle activité MainActivityPart2 dans laquelle on ajoute le contenu de la première version sans les commentairess 
ainsi que le contenu du layout activity_main_part1 pour faire les modifications suivantes : 

### Gestion de différents documents
* Modification du layout activity_main_part2
    * Supression de tous les boutons sauf de Save et load 
    * Changer save par add (la méthode aussi addNote)
    * Changer la méthode loadNote par loadNotes (avec un s)
    * Ajouter un NestedScrollView qui va encadrer le textView d'affichage
        Il est évident qu'un RecyclerView serait plus approprié pour cette méthode mais dans le cas présent
        restons focus sur la bas de donnée
 
* Modifications de MainActivity (pour des oucis de lecture je ne fais pas les vérifications)
    * Ajouter une CollectionReference pour faire le lien vers la collection de documents (25)
    * Changer la méthode saveNote par addNote (26)
    * Supprimer les méthodes suivantes updateDescription, deleteDescription et deleteNote
    * Changer la méthode loadNote en loadNotes (27)
        Comme nous utilisons la référence à une collection à la place de documentSnapShot nous utiliserons
        QuerySnapShot
    * Modifications de la métode onStart, en supprimant le snapShotListener (nous avons un bouton load)
    * Ajout de l'affichage automatique avec le snapShotListener

### La modification de données dans une collection
* Modifier le model en ajoutant le string documentId qui est l'id du document dans la collection
    * Ajouter @Exclude avnat son getter pour ne pas modifier l'id automatique donné par Firebase
* Ajouter ensuite dans la méthode loadNotes la récupération de cette id en vue d'une modification (28)         
* Faire de même dans la méthode onStart() (29)

### Simple query
* Gérer l'ordre des données (pour le moement les données sont ordonnées par chiffre, Majuscule et minuscule)
* Ajouter un editTexte qui déterminera la priorité de la note
* Modifier le MainActivity pour 
    * Ajouter (30) et lier (31) ce nouveau textView 
    * Ajouter ce champs pour le transmettre à la base dans addNote plus une vérification pour envoyer 0 si le champs est vide (32)
    * Modifier le model pour ajouter un nouveau constructeur ainsi que les getters et setters
    * Ajouter ce champ dans la méthode load pour l'afficher avec le bouton load de la méthode loadNotes et dans le onStart (33) (34)

* Afficher seulement les notes de priorité 2 (35)
* Afficher seulement les notes supérieur à la priorité 2 (36)
* Ordonner les éléments avec la priorité dans la méthode loadNotes
    * Décroissant (37)
    * Croissant (38)
A noter qu'il est possible de mixer wherEqualto... avec un ordre dé/croissant 
* Afficher seulement les 3 premiers résultats (39)
NB Sous cette forme il n'est possible de faire des requêtes qu'avec un seul type d'argument !!

### Combiner les requêtes avec différents arguments
Pour effectuer des requêtes sur différents champs de la base il faut créer un index dans la console Firebase, voila la marche à suivre :
* Supprimer toutes les documents de la collection (pour l'exemple)
* Ajouter les valeurs ci-dessous pour l'exemple
    * Title:Aa
        Description:
        Priority: 3
    * Title:Bb
        Description:
        Priority: 1
    * Title:Aa
        Description:
        Priority: 5
    * Title:Aa
        Description:
        Priority: 1
* Modifier le code pour faire une recherche des titres Aa dont la priorité est supérieur à 2 (40)
* Ajouter le addOnFailureListener pour afficher l'erreur que nous allons générer en cliquant sur le bouton load
* Lors de l'appui sur load rien ne se passe il faut regarder dans les log pour afficher l'erreur : il manque un index 
et Firestore nous propose de le cliquer en utilisant le lien dans l'erreur.
Ces index sont obligatoire lorsque l'on combine des requêtes 'plus grand que' et égale, des requêtes avec plusieurs orderBy
Mais ne sont pas obligatoire pour des requêtes où l'on mixe égale et égale
Il est possible de les créer directement dans la console dans la section indexes de Cloud Firestore
* De même un exemple avec orderBy priority et orderBy title (41)

Vous l'aurez noté dans les exemples précédents nous avons juste utilisé l'opérande ET pour récupérer les valeurs de la base

### Merge de différentes query pour créer l'illusion d'utiliser OR
* Il faut créer plusieurs query pour les merger localement afin d'afficher les résultats. Ces Query retourne des Task, nous pouvons alors 
les encapsuler dans plusieurs pour les regrouper et les affichar par la suite (42) (Pour une meilleure lisibilté, ce code sera dans une seconde méthodes loadNotes
la première sear alors commentée)
* Ajoutons l'exemple dans lequel nous n'afficherons pas les notes de priorité 2

### La pagination ou comment ne charger qu'une partie du contenu pour accélerer l'UX
* Pour une meilleure lisibilité cette partie sera coder dans une nouvelle Activité dans laquelle il faudra recopier une partie de la partie précédente
* Changer aussi l'Intent du Launcher dans le manifest pour arriver sur MainActivityPart3
* Modifications apportées : 
    * Suppression des variables non utilisées
    * Optimiser les imports (avec [ALT] + [ENTER])
    * Suppression de la méthode onStart()
    * Suppression du contenu de loadNotes

* Récupération des notes dont la priorité est supérieur ou égale à 3 avec startAt (43)
    A noter qu'avec startAfter on récupère les notes strictement supérieur à 3 
    
* L'utilité de startAt est  que l'on peut combiner les champs (avec bien sur un ajout de l'index dans Firebase) (44) 
* Sa seconde utilité est que l'on peut utiliser un documentSnapShot comme paramètre (45)
 et donc dans le cadre de l'exemple commencé à partir de l'id d'un document mais cela peut être pratique avec des dates
 (Pour cette exemple voir la méthode non commentée dans laquelle on récupère un des documentId au hasard) (45)

* Utilisation dans le cadre de la pagination
    * Ajouter une variable globale de type DocumentSnapShot appelée lastResult (46) 
     cette variable correspond à la dernière note affichée 
    * De plus nous allons ajouter le fait de n'afficher que 3 résultats puis en cliquant sur load 3 autres s'afficheront sans 
    supprimer les premiers affiché grace à la méthode append
    
   

