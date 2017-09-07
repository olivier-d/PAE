package bizz.dto;

import java.time.LocalDate;
import java.util.ArrayList;

public interface EntrepriseDto {

  public int getIdEntreprise();

  public void setIdEntreprise(int idEntreprise);

  public String getNomEntreprise();

  public void setNomEntreprise(String nomEntreprise);

  public String getRue();

  public void setRue(String rue);

  public String getNumero();

  public void setNumero(String numero);

  public String getBoite();

  public void setBoite(String boite);

  public String getCodePostal();

  public void setCodePostal(String codePostal);

  public String getCommune();

  public void setCommune(String commune);

  public LocalDate getDatePremierContact();

  public void setDatePremierContact(LocalDate date);

  public LocalDate getDateDerniereParticipation();

  public void setDateDerniereParticipation(LocalDate dateDerniereParticipation);

  public int getCreateur();

  public void setCreateur(int createur);

  public int getVersion();

  public void setVersion(int version);

  public ArrayList<PersonneContactDto> getPersonnesContact();

  public void ajouterPersonneContact(PersonneContactDto pdcDto);

}
