package bizz.uccimpl;

import static util.Util.checkObject;

import java.util.ArrayList;

import bizz.domaine.JourneeEntreprise;
import bizz.dto.JourneeEntrepriseDto;
import bizz.ucc.JourneeEntrepriseUcc;
import dal.DalServices;
import dal.dao.JourneeEntrepriseDao;
import exception.BizzException;
import exception.FatalException;

public class JourneeEntrepriseUccImpl implements JourneeEntrepriseUcc {

  private JourneeEntrepriseDao jeDao;
  private DalServices dalServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param jeDao -> Data Access Object de la journée de l'entreprise
   * @param dalS -> Service Data Access Layer
   */
  public JourneeEntrepriseUccImpl(JourneeEntrepriseDao jeDao, DalServices dalS) {
    checkObject(jeDao);
    this.jeDao = jeDao;
    this.dalServices = dalS;
  }

  @Override
  public JourneeEntrepriseDto insererJourneeEntreprise(JourneeEntrepriseDto je) {
    try {
      checkObject(je);
      String erreurs = util.Util.checkInputJe(je);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      String[] infoDate = je.getDateJournee().split("-");
      je.setDateJournee(infoDate[2] + "-" + infoDate[1] + "-" + infoDate[0]);
      dalServices.startTransaction();
      JourneeEntreprise jeBizz = jeDao.verifierJeNonOuverte();
      // SI DIFFERENT DE NULL ALORS IL EXISTE UNE JE OUVERTE
      if (jeBizz != null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Veuillez d'abord cloturer la JE ouverte datant du "
            + jeBizz.getDateJournee() + "\"}");
      }
      jeBizz = jeDao.insererJourneeEntreprise(je);
      if (jeBizz == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Cette date de journée existe déjà\"}");
      }
      dalServices.commitTransaction();
      return jeBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Insertion journée impossible");
    }
  }

  @Override
  public ArrayList<JourneeEntrepriseDto> getJourneeEntreprise() {
    try {
      dalServices.startTransaction();
      @SuppressWarnings("unchecked")
      ArrayList<JourneeEntrepriseDto> jes =
          (ArrayList<JourneeEntrepriseDto>) (ArrayList<?>) jeDao.getJourneeEntreprise();
      dalServices.commitTransaction();
      return jes;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement journée impossible");
    }
  }

  @Override
  public JourneeEntrepriseDto getJourneeSelonId(JourneeEntrepriseDto je) {
    try {
      dalServices.startTransaction();
      JourneeEntrepriseDto jeDto = jeDao.getJourneeSelonId(je);
      dalServices.commitTransaction();
      return jeDto;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement journée selon id impossible");
    }
  }

  @Override
  public int getNumVersion(JourneeEntrepriseDto jeD) {
    try {
      return jeDao.getNumVersion(jeD);
    } catch (FatalException exception) {
      throw new FatalException();
    }
  }

  @Override
  public JourneeEntrepriseDto cloturerJourneeEntreprise(JourneeEntrepriseDto je) {
    try {
      checkObject(je);
      dalServices.startTransaction();
      JourneeEntreprise jeBizz = jeDao.cloturerJourneeEntreprise(je);
      if (jeBizz == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Aucune JE d'ouverte\"}");
      }
      dalServices.commitTransaction();
      return jeBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Cloture JE impossible" + exception.getMessage());
    }
  }

  @Override
  public boolean isJourneeActive() {
    try {
      dalServices.startTransaction();
      boolean reponse = this.jeDao.isJourneeActive();
      dalServices.commitTransaction();
      return reponse;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Recherche journee active impossible");
    }
  }

}
