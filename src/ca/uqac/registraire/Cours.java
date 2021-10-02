package ca.uqac.registraire;
import java.util.*;

/**
 * Title:        Classes for TP1
 * Description:
 * Copyright:    Copyright (c) 2015
 * Company:      UQAC
 * @author Hamid Mcheick et Hafedh Mili
 * @version 1.0
 */

public class Cours {

  private String titre;

  private Hashtable etudiants;

  public Cours() {
    etudiants = new Hashtable();
  }

  public Cours(String unTitre) {
    this();
    titre = unTitre;
  }

  public void setTitre(String unTitre) {
    titre = unTitre;
  }

  public String getTitre() {
    return titre;
  }

  public void ajouteEtudiant(Etudiant unEtudiant) {
    etudiants.put(unEtudiant,new Float(0));
  }

  public Enumeration getEtudiants() {
    return etudiants.keys();
  }

  public void attributeNote(Etudiant et,float note) {
    etudiants.put(et,new Float(note));
  }

  public float getNote(Etudiant et) {
    return ((Float)etudiants.get(et)).floatValue();
  }

  public String toString() {
    String chaine = "Cours(Titre: " + titre + " <";
    Enumeration etuds = getEtudiants();
    if (etuds.hasMoreElements()) {
      Etudiant et = (Etudiant) etuds.nextElement();
      chaine = chaine + et + " = " + getNote(et) +" ";
      while (etuds.hasMoreElements()) {
	et = (Etudiant) etuds.nextElement();
	chaine = chaine +", "+et + " = " + getNote(et) + " ";
      }
    }
    chaine = chaine +">)";
    return chaine;
  }
}