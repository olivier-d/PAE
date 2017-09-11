package bizz.uccimpl;

import static util.Util.checkObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.Entreprise;
import bizz.dto.EntrepriseDto;
import bizz.ucc.EntrepriseUcc;
import dal.DalServices;
import dal.dao.EntrepriseDao;
import exception.BizzException;
import exception.FatalException;

public class EntrepriseUccImpl implements EntrepriseUcc {

  private EntrepriseDao entrepriseDao;
  private DalServices dalServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param entrepriseDao -> Data Access Object de l'entreprise
   * @param dalServices -> Service Data Access Layer
   */
  public EntrepriseUccImpl(EntrepriseDao entrepriseDao, DalServices dalServices) {
    checkObject(entrepriseDao);
    this.entrepriseDao = entrepriseDao;
    this.dalServices = dalServices;
  }

  @Override
  public Entreprise insererEntreprise(EntrepriseDto entreprise) {
    try {
      checkObject(entreprise);
      String erreurs = util.Util.checkInputEntreprise(entreprise);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      Entreprise entrepriseBizz = entrepriseDao.insererEntreprise(entreprise);
      dalServices.commitTransaction();
      return entrepriseBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Insertion entreprise échouée");
    }
  }

  @Override
  public ArrayList<EntrepriseDto> getEntreprises() {
    try {
      dalServices.startTransaction();
      @SuppressWarnings("unchecked")
      ArrayList<EntrepriseDto> entreprises =
          (ArrayList<EntrepriseDto>) (ArrayList<?>) entrepriseDao.getEntreprises();
      dalServices.commitTransaction();
      return entreprises;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement entreprises impossible");
    }
  }

  @Override
  public Map<EntrepriseDto, String> getEntreprisesEtCreateurs() {
    try {
      dalServices.startTransaction();
      Map<EntrepriseDto, String> map = this.entrepriseDao.getEntreprisesEtCreateurs();
      dalServices.commitTransaction();
      return map;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement entreprises et créateur impossible");
    }
  }

  @Override
  public EntrepriseDto getEntrepriseSelonId(int id) {
    try {
      dalServices.startTransaction();
      EntrepriseDto entreprise = (EntrepriseDto) entrepriseDao.getEntreprisesSelonId(id);
      dalServices.commitTransaction();
      return entreprise;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Recherche entreprise selon l'id impossible");
    }
  }

  @Override
  public Map<EntrepriseDto, String> rechercherEntreprise(EntrepriseDto entrepriseDto) {
    try {
      dalServices.startTransaction();
      Map<EntrepriseDto, String> entreprises =  entrepriseDao.rechercherEntreprise(entrepriseDto);
      dalServices.commitTransaction();
      return entreprises;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      System.out.println(exception.getMessage());
      throw new FatalException("Recherche entreprise impossible");
    }
  }

  @Override
  public int getNumVersion(EntrepriseDto entreprise) {
    try {
      return entrepriseDao.getNumVersion(entreprise);
    } catch (FatalException exception) {
      throw new FatalException();
    }
  }

  @Override
  public List<EntrepriseDto> getEntreprisesInvitables() {
    try {
      dalServices.startTransaction();
      List<EntrepriseDto> listeEntreprises = this.entrepriseDao.getEntreprisesInvitables();
      dalServices.commitTransaction();
      return listeEntreprises;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement des entreprises invitables impossible");
    }
  }

  @Override
  public List<EntrepriseDto> getEntreprisesInvitees() {
    try {
      dalServices.startTransaction();
      List<EntrepriseDto> listeEntreprises = this.entrepriseDao.getEntreprisesInvitees();
      dalServices.commitTransaction();
      return listeEntreprises;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement des entreprises invitées impossible");
    }
  }

@Override
public List<EntrepriseDto> getEntreprisesAvecCommentaires() {
	try {
	      dalServices.startTransaction();
	      List<EntrepriseDto> listeEntreprises = this.entrepriseDao.getEntreprisesAvecCommentaires();
	      dalServices.commitTransaction();
	      return listeEntreprises;
	    } catch (FatalException exception) {
	      dalServices.rollbackTransaction();
	      throw new FatalException("Chargement des entreprises avec commentaires impossible");
	    }
	}

}
