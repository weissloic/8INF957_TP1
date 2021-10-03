# 8INF957_TP1

##### Structure générale du programme:

Il s’agit de développer un programme client-serveur illustré dans la figure 1. La partie client ira lire dans un fichier de données un ensemble de commandes que le client devra faire exécuter par le serveur. Le client et le serveur communiquent à l’aide de sockets, RMI, etc. La séquence de commandes commence par :
* Des demandes de compilation d’un fichier source (« UneClass.java ») présentes sur le serveur
* Le chargement des classes ainsi compilées


Par la suite, le programme client pourra demander au serveur de créer des objets de ces classes là, pour ensuite exécuter des opérations dessus. Le programme client imprime les résultats de l’exécution des commandes envoyées au serveur dans un fichier de sortie local (client).

##### Déroulement de l'implémentation:

1) Création du serveur et de sa boucle infinie
2) Création du client et connexion au serveur avec les sockets
3) Envoie d'un objet depuis le client vers le serveur
4) Ouverture du fichier de cmd du client
5) Création de la classe Parse et de ses fonctions
6) Envoie de l'objet Parse vers le serveur
7) Renvoie d'un résultat du serveur vers le client et écriture dans un fichier
8) Gestion des commandes côté serveur
9) Création d'un docker-compose pour éviter les erreurs lors du lancement sur un environnement différent


