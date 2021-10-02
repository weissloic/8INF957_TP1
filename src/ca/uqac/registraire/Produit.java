package ca.uqac.registraire;
class Produit {
	private String cle;
	private String nom;
	private String description;
	public float prix;
	public float inventaire;

	public String getDescription() {
		return description;
	}

	public void setDescription(String descr){
		description = descr;
	}

	public float valeurInventaire() {
		return inventaire*prix;
	}

	public void decrementeInventaire(float qte) {
		inventaire-=qte;
	}
}
