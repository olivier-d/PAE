package bizz.dto;

import java.util.ArrayList;

import bizz.domaine.ParticipationImpl.Etat;

public interface ParticipationDto {

  public int getIdParticipation();

  public void setIdParticipation(int idParticipation);

  public Etat getEtat();

  public void setEtat(Etat etat);

  public boolean getAnnulee();

  public void setAnnulee(boolean annulee);

  public int getVersion();

  public void setVersion(int version);

  public int getIdJournee();

  public void setIdJournee(int idJournee);

  public int getIdEntreprise();

  public void setIdEntreprise(int idEntreprise);

  public ArrayList<PersonneContactDto> getPersonnesContact();

  public void ajouterPersonneContact(PersonneContactDto pdcDto);

}
