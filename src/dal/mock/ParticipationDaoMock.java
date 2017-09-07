package dal.mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bizz.domaine.Entreprise;
import bizz.domaine.Participation;
import bizz.domaine.ParticipationImpl.Etat;
import bizz.domaine.PersonneContact;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;
import bizz.persistance.EntrepriseFactory;
import bizz.persistance.ParticipationFactory;
import bizz.persistance.PersonneContactFactory;
import bizz.uccimpl.EntrepriseUccImpl;
import bizz.uccimpl.JourneeEntrepriseUccImpl;
import bizz.uccimpl.PersonneContactUccImpl;
import dal.DalBackendServices;
import dal.dao.ParticipationDao;
import exception.FatalException;

public class ParticipationDaoMock implements ParticipationDao {
  private ParticipationFactory participationFactory;
  private List<Participation> participations;
  private List<ParticipationPersonne> participationsPersonne;
  private EntrepriseUccImpl entreprise;
  private JourneeEntrepriseUccImpl journee;
  private PersonneContactUccImpl personneContact;


  /**
   * Constructeur initialisant la liste des participations et la rempli de 3 participations par
   * defaut.
   * 
   * @param participationFact -> factory des participations
   * @param backDs -> DalServices coté BackEnd
   * @param entrepriseFact -> factory des entreprises
   * @param pdcFact -> factory des personnes de contact
   */
  public ParticipationDaoMock(ParticipationFactory participationFact, DalBackendServices backDs,
      EntrepriseFactory entrepriseFact, PersonneContactFactory pdcFact) {
    this.participationFactory = participationFact;

    // donnees pour tests unitaires
    entreprise = (EntrepriseUccImpl) DalServicesImplMock.getOType("entreprise");
    journee = (JourneeEntrepriseUccImpl) DalServicesImplMock.getOType("je");
    personneContact = (PersonneContactUccImpl) DalServicesImplMock.getOType("pc");
    participations = new ArrayList<>();
    participationsPersonne = new ArrayList<>();

    Participation participation = null;
    participation = initParticipation(1, 1);
    participations.add(participation);
    Participation participation2 = null;
    participation2 = initParticipation(2, 1);
    participations.add(participation2);
    Participation participation3 = null;
    participation3 = initParticipation(2, 2);
    participations.add(participation3);

    ParticipationPersonne pa = null;
    pa = new ParticipationPersonne(participationsPersonne.size() + 1, 1, 0);
    participationsPersonne.add(pa);
    ParticipationPersonne pa2 = null;
    pa2 = new ParticipationPersonne(participationsPersonne.size() + 1, 1, 1);
    participationsPersonne.add(pa2);
    ParticipationPersonne pa3 = null;
    pa3 = new ParticipationPersonne(participationsPersonne.size() + 1, 0, 2);
    participationsPersonne.add(pa3);
  }

  /**
   * Methode initialisant les participations.
   * 
   * @param idJournee -> id de la journée
   * @param idParticipation -> id de l'entreprise
   */
  private Participation initParticipation(int idJournee, int idEntreprise) {
    Participation participation = (Participation) participationFactory.creerParticipation();
    participation.setIdParticipation(participations.size() + 1);
    participation.setIdJournee(idJournee);
    participation.setIdEntreprise(idEntreprise);
    return participation;
  }

  /**
   * Méthode checkant si la participation existe.
   * 
   * @param participationDto -> Data Transfert Object contenant l'id de l'entreprise et l'id de la
   *        journée
   */
  private ParticipationDto participationExistante(ParticipationDto participationDto) {
    for (Participation participation : participations) {
      if (participation.getIdEntreprise() == participationDto.getIdEntreprise()
          && participation.getIdJournee() == participationDto.getIdJournee()) {
        return participation;
      }
    }
    return null;
  }

  /**
   * Méthode checkant si la l'entreprise existe.
   * 
   * @param entrepriseDto -> Data Transfert Object de l'entreprise
   */
  private boolean entrepriseExiste(EntrepriseDto entrepriseDto) {
    if (entreprise.getEntreprises().contains(entrepriseDto)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Méthode checkant si la journée existe.
   * 
   * @param journeeDto -> Data Transfert Object d'une journee d'entreprise
   */
  private boolean journeeExiste(JourneeEntrepriseDto jeDto) {
    if (journee.getJourneeEntreprise().contains(jeDto)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Méthode inserant une participation au sein de la liste.
   * 
   * @param listeIdEntreprises -> liste d'id des entreprises
   */
  @Override
  public List<Integer> insererParticipation(List<EntrepriseDto> listeIdEntreprises) {
    List<Integer> liste = new ArrayList<>();
    for (EntrepriseDto entreprise : listeIdEntreprises) {
      ArrayList<JourneeEntrepriseDto> journees = journee.getJourneeEntreprise();
      JourneeEntrepriseDto je = journees.get(journees.size() - 1);
      Participation part = initParticipation(je.getIdJournee(), entreprise.getIdEntreprise());
      if (participationExistante(part) == null) {
        participations.add(part);
        liste.add(participations.size() - 1);
      }
    }
    return liste;
  }


  /**
   * Méthode inserant une participation au sein de la liste.
   * 
   * @param participationDto -> Data Transfert Object d'une participation
   * @param jeDto -> Data Transfert Object d'une journée
   * @param entrepriseDto -> Data Transfert Object d'une entreprise
   * @throws FatalException -> Exception lever si problème
   */
  public Participation insererParticipation(ParticipationDto participationDto,
      JourneeEntrepriseDto jeDto, EntrepriseDto entrepriseDto) throws FatalException {
    try {
      if (participationExistante(participationDto) == null && journeeExiste(jeDto)
          && entrepriseExiste(entrepriseDto)) {
        Participation participation =
            initParticipation(participationDto.getIdJournee(), participationDto.getIdEntreprise());
        participations.add(participation);
        return participation;
      } else {
        throw new FatalException("Participation deja presente.");
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode listant les participations.
   * 
   * @param journee -> journée concerné
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public ArrayList<Participation> getParticipations(int journee) throws FatalException {
    ArrayList<Participation> liste = new ArrayList<>();
    try {
      for (Participation participation : participations) {
        if (participation.getIdJournee() == journee) {
          liste.add(participation);
        }
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
    return (liste.size() == 0) ? null : liste;
  }

  /**
   * Méthode listant les entreprises invitable.
   * 
   * @param journee -> journée concerné
   * @throws FatalException -> Exception lever si problème
   */
  public ArrayList<Entreprise> getEntrepriseInvitables(int journee) throws FatalException {
    try {
      ArrayList<Entreprise> entreprisesInvitables = new ArrayList<>();
      for (Participation participation : participations) {
        if (!participation.getEtat().equals(Etat.FACTUREE)
            && journee != participation.getIdJournee()) {
          entreprisesInvitables
              .add((Entreprise) entreprise.getEntrepriseSelonId(participation.getIdEntreprise()));
        }
      }
      if (entreprisesInvitables.size() == 0) {
        return null;
      } else {
        return entreprisesInvitables;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode inserant une personne de contact à une participation.
   * 
   * @param listePdcs -> liste des personnes de contact a ajouté
   * @param idParticipation -> id de la participation
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public List<Integer> insererPersonneContactParticipation(List<PersonneContactDto> listePdcs,
      int idParticipation) {
    if (participations.get(idParticipation) != null && listePdcs != null) {
      List<Integer> list = new ArrayList<>();
      for (PersonneContactDto pdc : listePdcs) {
        if (getPersonneContact(pdc) != null) {
          ParticipationPersonne pp = new ParticipationPersonne(participationsPersonne.size(),
              idParticipation, pdc.getIdPersonneContact());
          participationsPersonne.add(pp);
          list.add(pp.getIdParticipation());
        }
        return list;
      }
    }
    return null;
  }

  /**
   * Méthode renvoyant une personne de contact.
   * 
   * @param pdc -> Data Transfert Object d'une personne de contact
   * @throws FatalException -> Exception lever si problème
   */
  private PersonneContact getPersonneContact(PersonneContactDto pdc) {
    ArrayList<PersonneContactDto> pers = personneContact.getPersonnesContact();
    if (pers.contains(pdc)) {
      return (PersonneContact) pers.get(pdc.getIdPersonneContact());
    } else {
      return null;
    }
  }

  /**
   * Méthode permettant d'update l'etat d'une participation.
   * 
   * @param participationDto -> Data Transfert Object d'une participation contenant l'etat, l'id et
   *        le numero de version
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Participation updateEtatParticipation(ParticipationDto participationDto)
      throws FatalException {
    for (Participation participation : participations) {
      if (participation.getIdEntreprise() == participationDto.getIdEntreprise()
          && participation.getIdJournee() == participationDto.getIdJournee()
          && participationDto.getIdParticipation() == participation.getIdParticipation()) {
        participation.setEtat(participationDto.getEtat());
        return participation;
      }
    }
    return null;
  }

  /**
   * Méthode listant les participation des entreprises à une journée.
   * 
   * @param idJournee -> id de la journée
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Map<ParticipationDto, String> getParticipationsEntreprises(int idJournee) {
    Map<ParticipationDto, String> map = new HashMap<>();
    for (Participation participation : participations) {
      if (participation.getIdJournee() == idJournee) {
        EntrepriseDto entrepriseDto =
            entreprise.getEntrepriseSelonId(participation.getIdEntreprise());
        map.put(participation, "" + entrepriseDto.getNomEntreprise());
      }
    }
    return map;
  }

  /**
   * Méthode listant les participation des personne de contact à une journée.
   * 
   * @param idJournee -> id de la journée
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(int idJournee) {
    Map<Integer, List<PersonneContactDto>> map = new HashMap<>();
    JourneeEntrepriseDto je = journee.getJourneeEntreprise().get(idJournee);
    ArrayList<PersonneContactDto> pers = new ArrayList<>();
    for (Participation participation : participations) {
      if (participation.getIdJournee() == je.getIdJournee()) {
        for (ParticipationPersonne participationPersonne : participationsPersonne) {
          if (participation.getIdParticipation() == participationPersonne.idParticipation) {
            map.put(participation.getIdParticipation(), pers);
          }
        }
      }
    }
    return map;
  }

  /**
   * Méthode ne faisant rien.
   * 
   * @param idEntreprise -> id de l'entreprise
   */
  @Override
  public Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJePers(
      int idEntreprise) {
    return null;
  }

  /**
   * Méthode annulant une participation.
   * 
   * @param participationDto -> Data Transfert Object d'une participation
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Participation annulerParticipation(ParticipationDto participationDto) {
    if (participationExistante(participationDto) != null) {
      Participation participation = participations.get(participationDto.getIdParticipation());
      participation.setAnnulee(true);
      participation.setVersion(participation.getVersion() + 1);
      participations.set(participationDto.getIdParticipation(), participation);
      return participation;
    }
    return null;
  }

  /**
   * Méthode updatant la date de la derniere participation d'une entreprise.
   * 
   * @param participationDto -> Data Transfert Object d'une participation
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Participation updateDateDerniereParticipation(ParticipationDto participationDto) {
    if (participationExistante(participationDto) != null) {
      EntrepriseDto entrepriseDto =
          entreprise.getEntrepriseSelonId(participationDto.getIdParticipation());
      JourneeEntrepriseDto jeDto =
          journee.getJourneeEntreprise().get(participationDto.getIdJournee());
      entrepriseDto.setDateDerniereParticipation(LocalDate.parse(jeDto.getDateJournee()));
      return participations.get(participationDto.getIdParticipation());
    }
    return null;
  }

  /**
   * Méthode listant les id des personne invitée a la participation demandée.
   * 
   * @param idParticipation -> id de la participation
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public List<PersonneContactDto> getIdPersonnesInviteesPourUneParticipation(int idParticipation) {
    ArrayList<PersonneContactDto> pers = new ArrayList<>();
    for (ParticipationPersonne participationPersonne : participationsPersonne) {
      if (participationPersonne.getIdParticipation() == idParticipation) {
        PersonneContactDto pdc =
            personneContact.getPersonnesContact().get(participationPersonne.getIdPersonne());
        pers.add(pdc);
      }
    }
    return pers;
  }

  private static class ParticipationPersonne {

    private int idParticipationPersonne;
    private int idParticipation;
    private int idPersonne;

    ParticipationPersonne(int idParticipationPersonne, int idParticipation, int idPersonne) {
      this.idParticipationPersonne = idParticipationPersonne;
      this.idParticipation = idParticipation;
      this.idPersonne = idPersonne;
    }

    public int getIdParticipationPersonne() {
      return idParticipationPersonne;
    }

    public void setIdParticipationPersonne(int idParticipationPersonne) {
      this.idParticipationPersonne = idParticipationPersonne;
    }

    public int getIdParticipation() {
      return idParticipation;
    }

    public void setIdParticipation(int idParticipation) {
      this.idParticipation = idParticipation;
    }

    public int getIdPersonne() {
      return idPersonne;
    }

    public void setIdPersonne(int idPersonne) {
      this.idPersonne = idPersonne;
    }
  }

  /**
   * Méthode ne faisant rien.
   */
  @Override
  public Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees() {
    return null;
  }
}
