package bizz.dto;

import java.time.LocalDate;

// Getteurs et Setteurs pour un utilisateur
public interface UtilisateurDto {

  public int getId();

  public void setId(int idUtilisateur);

  public String getPseudo();

  public void setPseudo(String pseudo);

  public String getNom();

  public void setNom(String nom);

  public String getPrenom();

  public void setPrenom(String prenom);

  public String getEmail();

  public void setEmail(String email);

  public String getMdp();

  public void setMdp(String mdp);

  public LocalDate getDateInscription();

  public void setDateInscription(LocalDate dateInscription);

  public boolean getResponsable();

  public void setResponsable(boolean responsable);

  public int getVersion();

  public void setVersion(int version);

}
