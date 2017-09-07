package bizz.domaine;


public class PersonneContactImpl implements PersonneContact {
  private int idPersonneContact;
  private int idEntreprise;
  private String nom;
  private String prenom;
  private String telephone;
  private String email;
  private boolean estActif;
  private int version;

  /**
   * Constructeur initialisant certain paramètre de la classe par default (personne active).
   */
  public PersonneContactImpl() {
    estActif = true;
  }

  @Override
  public int getIdPersonneContact() {
    return idPersonneContact;
  }

  public void setIdPersonneContact(int idPersonneContact) {
    this.idPersonneContact = idPersonneContact;
  }

  @Override
  public String getNom() {
    return nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public String getPrenom() {
    return prenom;
  }

  @Override
  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  @Override
  public String getTelephone() {
    return telephone;
  }

  @Override
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public boolean getActif() {
    return estActif;
  }

  @Override
  public void setActif(boolean estActif) {
    this.estActif = estActif;
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
  public int getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }
}
