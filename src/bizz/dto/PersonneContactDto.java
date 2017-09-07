package bizz.dto;

public interface PersonneContactDto {

  public int getIdPersonneContact();

  public void setIdPersonneContact(int idPersonneContact);

  public String getNom();

  public void setNom(String nom);

  public String getPrenom();

  public void setPrenom(String prenom);

  public String getTelephone();

  public void setTelephone(String telephone);

  public String getEmail();

  public void setEmail(String email);

  public boolean getActif();

  public void setActif(boolean estActif);

  public int getIdEntreprise();

  public void setIdEntreprise(int idEntreprise);

  public int getVersion();

  public void setVersion(int version);


}
