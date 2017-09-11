package bizz.domaine;

import static util.Util.checkObject;

import java.util.ArrayList;

import bizz.dto.PersonneContactDto;

public class ParticipationImpl implements Participation {

  private int idParticipation;
  private Etat etat;
  private boolean annulee;
  private int version;
  private int idJournee;
  private int idEntreprise;
  private ArrayList<PersonneContactDto> listePersonnes;
  private String commentaire;

  public enum Etat {
    INVITEE("Invitee"), CONFIRMEE("Confirmee"), REFUSEE("Refusee"), FACTUREE("Facturee"), PAYEE(
        "Payee");

    private String abreviation;

    private Etat(String abreviation) {

      this.abreviation = abreviation;
    }

    public String getEnumEtat() {
      return this.abreviation;
    }

    /**
     * Fonction transformant un String représentant l'état de la participation en l'objet Etat
     * lui-même.
     * 
     * @param abreviation | abreviation de l'état de la participation.
     * @return Etat | objet Etat
     * @exception IllegalArgumentException | exception jetée si l'état de la participation est
     *            incorrect et donc n'existe pas
     */
    public static Etat getEtatByAbreviation(String abreviation) {
      for (Etat e : Etat.values()) {
        if (e.abreviation.equalsIgnoreCase(abreviation)) {
          return e;
        }
      }
      throw new IllegalArgumentException("Etat incorrect Participation !");
    }
  }

  /**
   * Constructeur initialisant certain paramètre de la classe par default (participation active,
   * version à 0, etat 'invitée' et la liste des personne).
   */
  public ParticipationImpl() {
    this.annulee = false;
    this.version = 0;
    this.etat = Etat.INVITEE;
    this.listePersonnes = new ArrayList<PersonneContactDto>();
    this.commentaire = "";
  }

  @Override
  public int getIdParticipation() {
    return idParticipation;
  }

  @Override
  public void setIdParticipation(int idParticipation) {
    this.idParticipation = idParticipation;
  }

  @Override
  public Etat getEtat() {
    return etat;
  }

  @Override
  public void setEtat(Etat etat) {
    this.etat = etat;
  }

  @Override
  public boolean getAnnulee() {
    return annulee;
  }

  @Override
  public void setAnnulee(boolean annulee) {
    this.annulee = annulee;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public int getIdJournee() {
    return idJournee;
  }

  @Override
  public void setIdJournee(int idJournee) {
    this.idJournee = idJournee;
  }

  @Override
  public int getIdEntreprise() {
    return idEntreprise;
  }

  @Override
  public void setIdEntreprise(int idEntreprise) {
    this.idEntreprise = idEntreprise;
  }

  @Override
  public ArrayList<PersonneContactDto> getPersonnesContact() {
    return this.listePersonnes;
  }

  @Override
  public void ajouterPersonneContact(PersonneContactDto pdcDto) {
    checkObject(pdcDto);
    this.listePersonnes.add(pdcDto);
  }

@Override
public void setCommentaire(String commentaire) {
	this.commentaire = commentaire;
}

@Override
public String getCommentaire() {
	return this.commentaire;
}

}
