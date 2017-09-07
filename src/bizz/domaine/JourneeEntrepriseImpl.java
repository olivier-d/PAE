package bizz.domaine;

import static util.Util.checkObject;

import java.util.ArrayList;

import bizz.dto.ParticipationDto;

public class JourneeEntrepriseImpl implements JourneeEntreprise {
  private int idJournee;
  private String dateJournee;
  private boolean ouverte;
  private int version;
  private ArrayList<ParticipationDto> listeParticipations;

  /**
   * Constructeur initialisant certain paramètre de la classe par default (journee ouverte, version
   * à 0 et la liste des participations).
   */
  public JourneeEntrepriseImpl() {
    this.ouverte = true;
    this.version = 0;
    listeParticipations = new ArrayList<ParticipationDto>();
  }

  public int getIdJournee() {
    return idJournee;
  }

  public void setIdJournee(int id) {
    this.idJournee = id;
  }

  public String getDateJournee() {
    return dateJournee;
  }

  public void setDateJournee(String date) {
    this.dateJournee = date;
  }

  public boolean isOuverte() {
    return ouverte;
  }

  public void setOuverte(boolean ouverte) {
    this.ouverte = ouverte;
  }

  @Override
  public int getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public ArrayList<ParticipationDto> getParticipations() {
    return this.listeParticipations;
  }

  @Override
  public void ajouterParticipation(ParticipationDto participationDto) {
    checkObject(participationDto);
    this.listeParticipations.add(participationDto);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dateJournee == null) ? 0 : dateJournee.hashCode());
    result = prime * result + idJournee;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    JourneeEntrepriseImpl other = (JourneeEntrepriseImpl) obj;
    if (dateJournee == null) {
      if (other.dateJournee != null) {
        return false;
      }
    } else if (!dateJournee.equals(other.dateJournee)) {
      return false;
    }
    if (idJournee != other.idJournee) {
      return false;
    }
    return true;
  }


}
