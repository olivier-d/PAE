package bizz.domaine;

import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;

import com.owlike.genson.annotation.JsonIgnore;

// Implemente l'interface qui contient les methodes business
public class UtilisateurImpl implements Utilisateur {
  private int idUtilisateur;
  private String pseudo;
  private String nom;
  private String prenom;
  private String email;
  private String mdp;
  private LocalDate dateInscription;
  private boolean responsable;
  private int version;

  /**
   * Constructeur initialisant certain paramètre de la classe par default (non responsable, date
   * d'inscription a null et la version à 1).
   */
  public UtilisateurImpl() {
    this.responsable = false;
    this.dateInscription = null;
    this.version = 1;
  }

  @Override
  public int getId() {
    return this.idUtilisateur;
  }

  @Override
  public String getPseudo() {
    return this.pseudo;
  }

  @Override
  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public String getPrenom() {
    return this.prenom;
  }

  @Override
  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  @Override
  public String getEmail() {
    return this.email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  @JsonIgnore(deserialize = true)
  public String getMdp() {
    return this.mdp;
  }

  @Override
  public void setMdp(String mdp) {
    this.mdp = mdp;
  }

  @Override
  public LocalDate getDateInscription() {
    return this.dateInscription;
  }

  @Override
  public void setDateInscription(LocalDate dateInscription) {
    this.dateInscription = dateInscription;
  }

  @Override
  public boolean getResponsable() {
    return this.responsable;
  }

  @Override
  public void setId(int idUtilisateur) {
    this.idUtilisateur = idUtilisateur;
  }

  @Override
  public void setResponsable(boolean responsable) {
    this.responsable = responsable;
  }

  @Override
  public boolean verifierPassword(String password) {
    return BCrypt.checkpw(password, this.getMdp());
  }

  @Override
  public int getVersion() {
    return this.version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

}
