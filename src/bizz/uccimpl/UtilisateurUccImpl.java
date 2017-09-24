package bizz.uccimpl;

import static util.Util.checkObject;

import bizz.domaine.Utilisateur;
import bizz.dto.UtilisateurDto;
import bizz.ucc.UtilisateurUcc;
import dal.DalServices;
import dal.dao.UtilisateurDao;
import exception.BizzException;
import exception.FatalException;

public class UtilisateurUccImpl implements UtilisateurUcc {
  private UtilisateurDao utilisateurDao;
  private DalServices dalServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param utilisateurDao -> Data Access Object de l'utilisateur
   * @param dalS -> Service Data Access Layer
   */
  public UtilisateurUccImpl(UtilisateurDao utilisateurDao, DalServices dalS) {
    checkObject(utilisateurDao);
    this.utilisateurDao = utilisateurDao;
    this.dalServices = dalS;
  }

  @Override
  public UtilisateurDto connecterUtilisateur(UtilisateurDto utilisateurDto) {
    try {
      checkObject(utilisateurDto);
      String erreurs = util.Util.checkMdp(utilisateurDto);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      Utilisateur utilisateurBizz = utilisateurDao.utilisateurExiste(utilisateurDto);
      if (utilisateurBizz == null || !utilisateurBizz.verifierPassword(utilisateurDto.getMdp())) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Login et/ou mdp incorrecte\"}");
      }
      dalServices.commitTransaction();
      return utilisateurBizz;

    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Connexion impossible");
    }
  }

  @Override
  public UtilisateurDto inscrireUtilisateur(UtilisateurDto utilisateurDto) {
    try {
      checkObject(utilisateurDto);
      String erreurs = util.Util.checkInputInscription(utilisateurDto);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      Utilisateur utilisateurBizz = utilisateurDao.verifierDoublonPseudo(utilisateurDto);
      if (utilisateurBizz == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Le login " + utilisateurDto.getPseudo().toUpperCase()
            + " n'est pas disponible\"}");
      }
      utilisateurBizz = utilisateurDao.insererUtilisateur(utilisateurDto);
      dalServices.commitTransaction();
      return utilisateurBizz;

    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Inscription utilisateur impossible");
    }

  }

  @Override
  public UtilisateurDto changerMdp(UtilisateurDto utilisateurDto) {
    try {
      checkObject(utilisateurDto);
      String erreurs = util.Util.checkChangementMdp(utilisateurDto);
      if (!"".equals(erreurs)) {
        throw new BizzException(erreurs);
      }
      dalServices.startTransaction();
      Utilisateur newUtilisateur = utilisateurDao.changerMotDePasse(utilisateurDto);
      dalServices.commitTransaction();
      return newUtilisateur;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Changement mot de passe impossible");
    }
  }

  @Override
  public int getNumVersion(UtilisateurDto utilisateurDto) {
    try {
      checkObject(utilisateurDto);
      dalServices.startTransaction();
      int reponse = utilisateurDao.getNumVersion(utilisateurDto);
      if (reponse < 0) {
        dalServices.rollbackTransaction();
        return reponse;
      }
      dalServices.commitTransaction();
      return reponse;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Erreur serveur lors de l'inscription");
    }
  }

  @Override
  public UtilisateurDto verifierUtilisateur(UtilisateurDto utilisateurDto) {
    try {
      checkObject(utilisateurDto);
      dalServices.startTransaction();
      Utilisateur utilisateurBizz = utilisateurDao.verifierUtilisateur(utilisateurDto);
      if (utilisateurBizz == null) {
        dalServices.rollbackTransaction();
        return null;
      }
      dalServices.commitTransaction();
      return utilisateurBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Verification session utilisateur impossible");
    }
  }

@Override
public Utilisateur creerOuChargerUser(UtilisateurDto utilisateurDto) {
	try {
      dalServices.startTransaction();
      Utilisateur utilisateurBizz = utilisateurDao.isEmailExiste(utilisateurDto);
      if (utilisateurBizz == null) {
    	  // Alors on crée l'user
    	  utilisateurBizz = utilisateurDao.insererUserEmail(utilisateurDto);
      }
      dalServices.commitTransaction();
      return utilisateurBizz;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement ou création utilisateur impossible");
    }
}

}
