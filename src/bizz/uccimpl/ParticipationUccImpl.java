package bizz.uccimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.Participation;
import bizz.domaine.ParticipationImpl.Etat;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;
import bizz.ucc.ParticipationUcc;
import dal.DalServices;
import dal.dao.ParticipationDao;
import exception.BizzException;
import exception.FatalException;

public class ParticipationUccImpl implements ParticipationUcc {

  private ParticipationDao participationDao;
  private DalServices dalServices;


  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param partDao -> Data Access Object de la participation
   * @param dalS -> Service Data Access Layer
   */
  public ParticipationUccImpl(ParticipationDao partDao, DalServices dalS) {
    this.participationDao = partDao;
    this.dalServices = dalS;
  }

  @Override
  public List<Integer> insererParticipation(List<EntrepriseDto> listeIdEntreprise) {
    try {
      dalServices.startTransaction();
      List<Integer> listeEntreprises =
          this.participationDao.insererParticipation(listeIdEntreprise);
      if (listeEntreprises == null) {
        dalServices.rollbackTransaction();
        throw new BizzException(
            "{\"fail\":\"Participation(s) déjà créée(s), veuillez rafraichir votre page\"}");
      }
      if (listeEntreprises.contains(-1)) {
        dalServices.rollbackTransaction();
        throw new BizzException(
            "{\"fail\":\"Insertion participation(s) impossible, aucune JE d'ouverte\"}");
      }
      this.dalServices.commitTransaction();
      return listeEntreprises;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Insertion participation(s) impossible");
    }
  }

  @Override
  public List<Integer> insererPersonneContactParticipation(List<PersonneContactDto> listePdc,
      int idParticipation) {
    try {
      dalServices.startTransaction();
      List<Integer> liste =
          this.participationDao.insererPersonneContactParticipation(listePdc, idParticipation);
      if (liste == null) {
        dalServices.rollbackTransaction();
        throw new BizzException("{\"fail\":\"Personne(s) déjà ajoutée(s) à la participation n°"
            + idParticipation + ", veuillez rafraichir votre page\"}");
      }
      if (liste.contains(-1)) {
        dalServices.rollbackTransaction();
        throw new BizzException(
            "{\"fail\":\"Personne de contact inactive détéctée, insertion échouée\"}");
      }
      dalServices.commitTransaction();
      return liste;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException(
          "Insertion des pers. pour la participation n°" + idParticipation + " impossible");
    }
  }

  @Override
  public ArrayList<ParticipationDto> getParticipations(int journee) {
    try {
      dalServices.startTransaction();
      @SuppressWarnings("unchecked")
      ArrayList<ParticipationDto> pcs =
          (ArrayList<ParticipationDto>) (ArrayList<?>) participationDao.getParticipations(journee);
      dalServices.commitTransaction();
      return pcs;
    } catch (FatalException exception) {
      throw new FatalException("Chargement participation(s) impossible");
    }
  }


  @Override
  public Map<ParticipationDto, String> getParticipationsEntreprises(int idJournee) {
    try {
      dalServices.startTransaction();
      Map<ParticipationDto, String> map = participationDao.getParticipationsEntreprises(idJournee);
      dalServices.commitTransaction();
      return map;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement participations impossible");
    }
  }

  @Override
  public Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(int idJournee) {
    try {
      dalServices.startTransaction();
      Map<Integer, List<PersonneContactDto>> map =
          participationDao.getParticipationsPersonnesPresentes(idJournee);
      dalServices.commitTransaction();
      return map;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement participants impossible");
    }
  }

  @Override
  public Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJePers(
      int idEntreprise) {
    try {
      dalServices.startTransaction();
      Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> map =
          this.participationDao.getParticipationsJePers(idEntreprise);
      dalServices.commitTransaction();
      return map;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement liste participants impossible");
    }
  }

  @Override
  public ParticipationDto annulerParticipation(ParticipationDto part) {
    try {
      dalServices.startTransaction();
      ParticipationDto participation = this.participationDao.annulerParticipation(part);
      if (participation == null) {
        dalServices.rollbackTransaction();
        throw new BizzException(
            "{\"fail\":\"Cette participation est déjà annulée, veuillez rafraichir votre page\"}");
      }
      dalServices.commitTransaction();
      return participation;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Annulation de la participation impossible");
    }
  }

  @Override
  public ParticipationDto updateEtatParticipation(ParticipationDto part) {
    try {
      dalServices.startTransaction();
      Participation newParticipation = participationDao.updateEtatParticipation(part);
      if (newParticipation == null) {
        dalServices.rollbackTransaction();
        throw new BizzException(
            "{\"fail\":\"Modification état impossible, veuillez rafraichir votre page\"}");
      }
      if (newParticipation.getEtat() == Etat.PAYEE) {
        newParticipation = participationDao.updateDateDerniereParticipation(newParticipation);
        if (newParticipation == null) {
          dalServices.rollbackTransaction();
          throw new BizzException(
              "{\"fail\":\"Modification date dernière participation impossible\"}");
        }
      }
      dalServices.commitTransaction();
      return newParticipation;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Modification etat participation impossible");
    }
  }

  @Override
  public List<PersonneContactDto> getIdPersonnesInviteesPourUneParticipation(int idParticipation) {
    try {
      dalServices.startTransaction();
      List<PersonneContactDto> listePdc =
          this.participationDao.getIdPersonnesInviteesPourUneParticipation(idParticipation);
      dalServices.commitTransaction();
      return listePdc;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement personnes invitées impossible");
    }
  }

  @Override
  public Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees() {
    try {
      dalServices.startTransaction();
      Map<JourneeEntrepriseDto, Integer> map =
          this.participationDao.nbrParticipationConfirmeeParJournees();
      dalServices.commitTransaction();
      return map;
    } catch (FatalException exception) {
      dalServices.rollbackTransaction();
      throw new FatalException("Chargement graphique impossible");
    }
  }
}
