package src.ca.uqac.registraire;
import java.io.*;
//import ca.uqac.8inf853.tp1.*;
/**
 * Title:        Classes for TP1
 * Description:
 * Copyright:    Copyright (c) 2015
 * Company:      UQAC
 * @author Hamid Mcheick et Hafedh Mili
 * @version 1.0
 */

/*public class ApplicationRegistraire {

  private BufferedReader commandesReader;
  private java.util.StringTokenizer tokenizer;

  public ApplicationRegistraire() {
  }
  public static void main(String[] args) {
    PrintWriter writer = null;
    try {
    writer = new PrintWriter(new FileWriter("commandes.txt"));
    } catch (Exception ec) {ec.printStackTrace();}
    Cours inf3123 = new Cours();
    writer.println("creation#ca.uqam.registraire.Cours#inf3123");
    inf3123.setTitre("Programmation Objet");
    writer.println("ecriture#inf3123#titre#Programmation Objet");
    String titreInf3123 = inf3123.getTitre();
    writer.println("lecture#inf3123#titre");
    Cours inf3300 = new Cours();
    writer.println("creation#ca.uqam.registraire.Cours#inf3300");
    inf3300.setTitre("Environnements de programmation");
    writer.println("ecriture#inf3123#titre#Environnements de programmation");
    Etudiant mathilde = new Etudiant();
    writer.println("creation#ca.uqam.registraire.Etudiant#mathilde");
    mathilde.setNom("Mathilde Boivin");
    writer.println("ecriture#mathilde#nom#Mathilde Boivin");
    Etudiant raymond = new Etudiant();
    writer.println("creation#ca.uqam.registraire.Etudiant#raymond");
    raymond.setNom("Raymond Sauve");
    writer.println("ecriture#raymond#nom#Raymond Sauve");
    // ne fonctionnera pas
    try {
      writer.println("fonction#inf3123#getNote#ca.uqam.registraire.Etudiant:mathilde");
      float note= inf3123.getNote(mathilde);
    } catch (Exception ex) {
      // writer.println("Fonction failed: " + ex);
    }
    mathilde.inscrisDansCours(inf3123);
    writer.println("fonction#mathilde#inscrisDansCours#ca.uqam.registraire.Cours:inf3123");
    inf3123.attributeNote(mathilde,3.7f);
    writer.println("fonction#inf3123#attributeNote#ca.uqam.registraire.Etudiant:mathilde,float:3.7");
    float noteInf3123= inf3123.getNote(mathilde);
    writer.println("fonction#inf3123#getNote#ca.uqam.registraire.Etudiant:mathilde");
    mathilde.inscrisDansCours(inf3300);
    writer.println("fonction#mathilde#inscrisDansCours#ca.uqam.registraire.Cours:inf3300");
    inf3300.attributeNote(mathilde,4.0f);
    writer.println("fonction#inf3300#attributeNote#ca.uqam.registraire.Etudiant:mathilde,float:4.0");
    float moyenneMathilde = mathilde.getMoyenne();
    writer.println("fonction#mathilde#getMoyenne#");

    raymond.inscrisDansCours(inf3123);
    writer.println("fonction#raymond#inscrisDansCours#ca.uqam.registraire.Cours:inf3123");
    raymond.inscrisDansCours(inf3300);
    writer.println("fonction#raymond#inscrisDansCours#ca.uqam.registraire.Cours:inf3300");
    inf3123.attributeNote(raymond,3.0f);
    writer.println("fonction#inf3123#attributeNote#ca.uqam.registraire.Etudiant:raymond,float:3.0");
    inf3300.attributeNote(raymond,2.7f);
    writer.println("fonction#inf3300#attributeNote#ca.uqam.registraire.Etudiant:raymond,float:2.7");
    float moyenneRaymond = raymond.getMoyenne();
    writer.println("fonction#mathilde#getMoyenne#");

    String listeInf3123 = inf3123.toString();
    writer.println("fonction#inf3123#toString#");
    writer.close();
    System.out.println(listeInf3123);
  }

  public Commande saisisCommande(BufferedReader fichier) {return (Commande)null;}

  public Object traiteCommande(Commande uneCommande) {return null;};

  private PrintWriter sortieWriter;

  public void scenario() {
    sortieWriter.println("Debut des traitements:");
    Commande prochaine = saisisCommande(commandesReader);
    while (prochaine != null) {
      sortieWriter.println("\tTraitement de la commande " + prochaine + " ...");
      Object resultat = traiteCommande(prochaine);
      sortieWriter.println("\t\tResultat: " + resultat);
      prochaine = saisisCommande(commandesReader);
    }
    sortieWriter.println("Fin des traitements");
  }
}*/