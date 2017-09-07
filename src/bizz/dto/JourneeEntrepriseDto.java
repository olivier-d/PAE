package bizz.dto;

import java.util.ArrayList;

public interface JourneeEntrepriseDto {

  public int getIdJournee();

  public void setIdJournee(int idJournee);

  public String getDateJournee();

  public void setDateJournee(String date);

  public boolean isOuverte();

  public void setOuverte(boolean ouverte);

  public int getVersion();

  public void setVersion(int version);

  public ArrayList<ParticipationDto> getParticipations();

  public void ajouterParticipation(ParticipationDto participationDto);

}
