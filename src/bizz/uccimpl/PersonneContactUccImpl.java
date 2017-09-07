package bizz.uccimpl;

import static util.Util.checkObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.PersonneContact;
import bizz.dto.PersonneContactDto;
import bizz.ucc.PersonneContactUcc;
import dal.DalServices;
import dal.dao.PersonneContactDao;
import exception.BizzException;
import exception.FatalException;

public class PersonneContactUccImpl implements PersonneContactUcc {

  private PersonneContactDao pdcDao;
  private DalServices dalServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param pdcDao -> Data Access Object de la personne de contact
   * @param dalS -> Service Data Access Layer
   */
  public PersonneContactUccImpl(PersonneContactDao pdcDao, DalServices dalS) {
    checkObject(pdcDao);
    this.pdcDao = pdcDao;
    this.dalServices = dalS;
  }

  @Override
  public PersonneContactDto insererPersonneContact(PersonneContactDto pdcDto) {
    try {
      String erreurs = util.Util.checkInputPersonneContact(pdcDto);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      PersonneContact pdcBizz = pdcDao.insererPersonneContact(pdcDto);
      dalServices.commitTransaction();
      return pdcBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Insertion pers. de contact impossible");
    }
  }

  @Override
  public PersonneContactDto modifierPersonneContact(PersonneContactDto pdcDto) {
    try {
      String erreurs = util.Util.checkInputModifPersonneContact(pdcDto);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      PersonneContact pdc = pdcDao.modifierPersonneContact(pdcDto);
      if (pdc == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"La pers. de contact a été récemment modifiée, "
            + "modification annulée, veuillez rafraîchir la page\"}");
      }
      dalServices.commitTransaction();
      return pdc;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Modification pers. de contact impossible");
    }
  }

  @Override
  public PersonneContactDto desactiverPersContact(int idPersContact) {
    try {
      dalServices.startTransaction();
      PersonneContactDto pdc = this.pdcDao.desactiverPersContact(idPersContact);
      if (pdc == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"La pers. de contact a déjà été désactivée\"}");
      }
      dalServices.commitTransaction();
      return pdc;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("La pers. de contact n'a pas pu être désactivée");
    }
  }

  @Override
  public ArrayList<PersonneContactDto> getPersonnesContact() {
    try {
      dalServices.startTransaction();
      @SuppressWarnings("unchecked")
      ArrayList<PersonneContactDto> pdcs =
          (ArrayList<PersonneContactDto>) (ArrayList<?>) pdcDao.getPersonnesContact();
      dalServices.commitTransaction();
      return pdcs;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement pers. de contact impossible");
    }
  }

  @Override
  public int getNumVersion(PersonneContactDto pdc) {
    try {
      return pdcDao.getNumVersion(pdc);
    } catch (FatalException exception) {
      throw new FatalException();
    }
  }

  @Override
  public Map<PersonneContactDto, String> getPersonnesContactEtEntreprise() {
    try {
      dalServices.startTransaction();
      Map<PersonneContactDto, String> mapPers = pdcDao.getPersonnesContactEtEntreprise();
      dalServices.commitTransaction();
      return mapPers;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement pers. de contact et nom entreprise impossible");
    }
  }

  @Override
  public List<PersonneContactDto> getPersonnesContactDeLEntreprise(int idEntreprise) {
    try {
      dalServices.startTransaction();
      List<PersonneContactDto> listePdcs =
          this.pdcDao.getPersonnesContactDeLEntreprise(idEntreprise);
      dalServices.commitTransaction();
      return listePdcs;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement pers. de contact de l'entreprise impossible");
    }
  }

  @Override
  public List<PersonneContactDto> getPersonnesContactActifEtInactifDeLEntreprise(int idEntreprise) {
    try {
      dalServices.startTransaction();
      List<PersonneContactDto> listePdcs =
          this.pdcDao.getPersonnesContactActifEtInactifDeLEntreprise(idEntreprise);
      dalServices.commitTransaction();
      return listePdcs;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement pers. de contact de l'entreprise impossible");
    }
  }

  @Override
  public Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(
      PersonneContactDto p) {
    try {
      dalServices.startTransaction();
      Map<PersonneContactDto, String> map = this.pdcDao.rechercherPersonneDeContactSelonNomEtPrenom(p);
      dalServices.commitTransaction();
      return map;
    } catch(FatalException e) {
      dalServices.rollbackTransaction();
      throw new FatalException("Recherche personne de contact impossible");
    }
  }
}
