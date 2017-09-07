package dal.mock;

import java.util.ArrayList;

import bizz.domaine.JourneeEntreprise;
import bizz.dto.JourneeEntrepriseDto;
import bizz.persistance.JourneeEntrepriseFactory;
import dal.DalBackendServices;
import dal.dao.JourneeEntrepriseDao;
import exception.FatalException;


public class JourneeEntrepriseDaoMock implements JourneeEntrepriseDao {
  private JourneeEntrepriseFactory jeFactory;

  // Mocks objets
  private ArrayList<JourneeEntreprise> jeListe;

  /**
   * Constructeur initialisant la liste des journées d'entreprise et la rempli de 3 journées par
   * defaut.
   * 
   * @param jeFactory -> factory des journées d'entreprise
   * @param backDs -> DalServices coté BackEnd
   */
  public JourneeEntrepriseDaoMock(JourneeEntrepriseFactory jeFactory, DalBackendServices backDs) {
    this.jeFactory = jeFactory;

    jeListe = new ArrayList<>();
    JourneeEntreprise je1 = init("2000-10-10", false);
    jeListe.add(je1);
    JourneeEntreprise je2 = init("2018-12-24", false);
    jeListe.add(je2);
    JourneeEntreprise je3 = init("2018-15-10", true);
    jeListe.add(je3);
  }

  /**
   * Methode initialisant les journées des entreprises.
   * 
   * @param dateJournee -> date de la journée
   * @param ouverte -> est ouverte
   */
  // Methode init
  private JourneeEntreprise init(String dateJournee, boolean ouverte) {
    JourneeEntreprise je = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
    je.setIdJournee(jeListe.size() + 1);
    je.setOuverte(ouverte);
    je.setVersion(1);
    je.setDateJournee(dateJournee);
    return je;
  }

  /**
   * Méthode verifiant si une journée est ouverte.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public JourneeEntreprise verifierJeNonOuverte() throws FatalException {
    try {
      for (JourneeEntreprise journee : jeListe) {
        if (journee.isOuverte()) {
          return journee;
        }
      }
      return null;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode inserant une journée au sein de la liste.
   * 
   * @param jeDto -> Data Transfert Object d'une journée d'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public JourneeEntreprise insererJourneeEntreprise(JourneeEntrepriseDto jeDto)
      throws FatalException {
    try {
      JourneeEntreprise je = null;
      if (verifierJeNonOuverte() == null) {
        je = init(jeDto.getDateJournee(), true);
        jeListe.add(je);
        return je;
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les journées d'entreprise.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public ArrayList<JourneeEntreprise> getJourneeEntreprise() throws FatalException {
    try {
      return jeListe;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les journées d'entreprise selon l'id entrée en paramètre.
   * 
   * @param jeDto -> Data transfert Object contenant l'id de la journée
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public JourneeEntreprise getJourneeSelonId(JourneeEntrepriseDto jeDto) throws FatalException {
    try {
      if (jeDto.getIdJournee() <= jeListe.size()) {
        return jeListe.get(jeDto.getIdJournee());
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant le numero de version de la journée voulue.
   * 
   * @param jeDto -> Data transfert Object contenant l'id de la journée
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public int getNumVersion(JourneeEntrepriseDto jeDto) {
    try {
      if (jeDto.getIdJournee() <= jeListe.size()) {
        return jeListe.get(jeDto.getIdJournee()).getVersion();
      } else {
        return -1;
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode cloturant la journée entrée en paramètre.
   * 
   * @param jeDto -> Data transfert Object contenant le numero de version de la journée active
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public JourneeEntreprise cloturerJourneeEntreprise(JourneeEntrepriseDto jeDto)
      throws FatalException {
    try {
      JourneeEntreprise je = jeListe.get(jeDto.getIdJournee());
      if (je.isOuverte()) {
        je.setOuverte(false);
        jeListe.set(je.getIdJournee(), je);
        return je;
      }
      return null;
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode renvoyant si il y'a une journée active.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public boolean isJourneeActive() {
    for (JourneeEntreprise journeeEntreprise : jeListe) {
      if (journeeEntreprise.isOuverte()) {
        return true;
      }
    }
    return false;
  }
}
