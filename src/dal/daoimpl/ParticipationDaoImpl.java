package dal.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bizz.domaine.JourneeEntreprise;
import bizz.domaine.Participation;
import bizz.domaine.ParticipationImpl.Etat;
import bizz.domaine.PersonneContact;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;
import bizz.persistance.EntrepriseFactory;
import bizz.persistance.JourneeEntrepriseFactory;
import bizz.persistance.ParticipationFactory;
import bizz.persistance.PersonneContactFactory;
import dal.DalBackendServices;
import dal.dao.ParticipationDao;
import exception.FatalException;

public class ParticipationDaoImpl implements ParticipationDao {

  private ParticipationFactory participationFactory;
  private EntrepriseFactory entrepriseFactory;
  private PersonneContactFactory pdcFactory;
  private JourneeEntrepriseFactory jeFactory;
  private DalBackendServices dalBackendServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param participationFact -> factory des participations
   * @param backDs -> DalServices coté BackEnd
   * @param entrepriseFact -> factory des entreprises
   * @param pdcFact -> factory des personnes de contact
   * @param jeFact -> factory des journées d'entreprise
   */
  public ParticipationDaoImpl(ParticipationFactory participationFact, DalBackendServices backDs,
      EntrepriseFactory entrepriseFact, PersonneContactFactory pdcFact,
      JourneeEntrepriseFactory jeFact) {
    this.participationFactory = participationFact;
    this.dalBackendServices = backDs;
    this.entrepriseFactory = entrepriseFact;
    this.pdcFactory = pdcFact;
    this.jeFactory = jeFact;
  }

  /**
   * Méthode inserant une participation au sein de la base de donnée.
   * 
   * @param listeIdEntreprises -> liste d'id des entreprises
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<Integer> insererParticipation(List<EntrepriseDto> listeIdEntreprises) {
    List<Integer> liste = new ArrayList<>();
    for (EntrepriseDto entreprise : listeIdEntreprises) {
      try {
        ResultSet rs =
            dalBackendServices.prepare("SELECT id_journee FROM pae.journees WHERE ouverte = true;");
        if (!rs.next()) {
          liste.add(-1);
          return liste;
        }
        int idJournee = rs.getInt(1);
        ResultSet rs2 = dalBackendServices.prepare(
            "SELECT count(p.id_participation) FROM pae.participations p " + "WHERE p.id_journee = "
                + idJournee + " AND p.id_entreprise = " + entreprise.getIdEntreprise() + ";");
        rs2.next();
        if (rs2.getInt(1) > 0) {
          return null;
        }
        ResultSet rs3 = dalBackendServices
            .prepare("INSERT INTO pae.participations VALUES (DEFAULT, 'INVITEE', false, 0,"
                + idJournee + "," + entreprise.getIdEntreprise() + ") RETURNING id_entreprise;");

        rs3.next();
        liste.add(rs3.getInt(1));
      } catch (Exception exception) {
        exception.printStackTrace();
        throw new FatalException();
      }
    }
    return liste;
  }

  /**
   * Méthode listant les participations.
   * 
   * @param journee -> journée concerné
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public ArrayList<?> getParticipations(int journee) {
    ResultSet rs = null;
    ArrayList<Participation> liste = new ArrayList<Participation>();
    try {
      rs = dalBackendServices
          .prepare("SELECT * FROM PAE.participations WHERE id_journee = " + journee + ";");

      while (rs.next()) {
        Participation participation = remplirParticipation(rs);
        liste.add(participation);
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
    return liste;
  }

  /**
   * Méthode inserant une personne de contact à une participation.
   * 
   * @param listePdcs -> liste des personnes de contact a ajouté
   * @param idParticipation -> id de la participation
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<Integer> insererPersonneContactParticipation(List<PersonneContactDto> listePdcs,
      int idParticipation) {
    List<Integer> liste = new ArrayList<>();
    try {
      for (PersonneContactDto pdc : listePdcs) {
        ResultSet rs2 = dalBackendServices.prepare(
            "SELECT count(p.id_participation_personne) FROM pae.participations_personnes p "
                + "WHERE p.id_participation = " + idParticipation + " AND p.id_personne_contact = "
                + pdc.getIdPersonneContact() + ";");
        rs2.next();
        // Alors cet personne a déjà ete ajoutée
        if (rs2.getInt(1) > 0) {
          return null;
        }
        ResultSet rs3 = dalBackendServices
            .prepare("SELECT count(pers.id_personne_contact) FROM pae.personnes_contact pers "
                + "WHERE pers.id_personne_contact = " + pdc.getIdPersonneContact()
                + " AND pers.actif = false;");
        rs3.next();
        if (rs3.getInt(1) > 0) {
          liste.add(-1);
          return liste;
        }

        ResultSet rs = dalBackendServices
            .prepare("INSERT INTO pae.participations_personnes " + "VALUES (DEFAULT, 0,"
                + idParticipation + "," + pdc.getIdPersonneContact() + ") RETURNING *;");

        rs.next();
        liste.add(rs.getInt(1));
      }
      return liste;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }


  /**
   * Méthode listant les participation d'une entreprise.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public List<ParticipationDto> getParticipationsDUneEntreprise(int idEntreprise) {
    List<ParticipationDto> liste = new ArrayList<>();
    try {
      ResultSet rs = dalBackendServices.prepare("SELECT p.* FROM pae.participations p "
          + "WHERE p.id_entreprise = " + idEntreprise + ";");
      while (rs.next()) {
        Participation participation = remplirParticipation(rs);
        liste.add(participation);
      }
      return liste;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les participation des entreprises à une journée.
   * 
   * @param idJournee -> id de la journée
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Map<ParticipationDto, String> getParticipationsEntreprises(int idJournee) {
    Map<ParticipationDto, String> map = new HashMap<ParticipationDto, String>();
    try {
      ResultSet rs = dalBackendServices.prepare("SELECT part.*, ent.nom_entreprise "
          + "FROM pae.participations part, pae.entreprises ent " + "WHERE part.id_journee = "
          + idJournee + " AND part.id_entreprise = ent.id_entreprise;");

      while (rs.next()) {
        Participation participation = remplirParticipation(rs);
        String nomEntreprise = rs.getString(7);
        map.put(participation, nomEntreprise);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new FatalException();
    }
    return map;
  }

  /**
   * Méthode listant les participation des personnes de contact qui ont été présent(e)s.
   * 
   * @param idJournee -> id de la journée
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  // Par participation pour une je, on a la liste des contacts
  @Override
  public Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(int idJournee) {
    Map<Integer, List<PersonneContactDto>> map = new HashMap<>();
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT p.id_participation, pers.id_personne_contact, pers.nom, pers.prenom "
              + "FROM pae.participations p, "
              + "pae.personnes_contact pers, pae.participations_personnes partpers "
              + "WHERE p.id_journee = " + idJournee
              + " AND p.id_participation = partpers.id_participation "
              + "AND partpers.id_personne_contact = pers.id_personne_contact;");
      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) this.pdcFactory.creerPersonneContact();
        pdc.setIdPersonneContact(rs.getInt(2));
        pdc.setNom(rs.getString(3));
        pdc.setPrenom(rs.getString(4));
        int idParticipation = rs.getInt(1);
        if (map.containsKey(idParticipation)) {
          map.get(idParticipation).add(pdc);
        } else {
          List<PersonneContactDto> listePdcs = new ArrayList<>();
          listePdcs.add(pdc);
          map.put(idParticipation, listePdcs);
        }
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
    return map;
  }

  // Pour une participation, j'affiche la je et les pers presentes a cette participation pour cet
  // entreprise et cet je
  // Utiliser dans entreprise pour la liste des participations
  /*
   * public Map<ParticipationDTO, Map<JourneeEntrepriseDTO, PersonneContactDTO>>
   * getParticipationsJEPers( int idEntreprise) { Map<ParticipationDTO, Map<JourneeEntrepriseDTO,
   * PersonneContactDTO>> map = new HashMap<>(); try { ResultSet rs =
   * backDs.prepare("SELECT p.*, j.id_journee, j.date_journee, j.ouverte, pers.* " +
   * "FROM pae.journees j, pae.participations p, pae.personnes_contact pers, " +
   * " pae.participations_personnes partpers " + "WHERE p.id_entreprise = " + idEntreprise +
   * " AND p.id_journee = j.id_journee AND p.id_participation = partpers.id_participation " +
   * "AND partpers.id_personne_contact = pers.id_personne_contact;");
   * 
   * while (rs.next()) { Participation p = (Participation) this.pF.creerParticipation();
   * JourneeEntreprise j = (JourneeEntreprise) this.jF.creerJourneeEntreprise(); PersonneContact
   * pers = (PersonneContact) this.pcF.creerPersonneContact();
   * 
   * p.setIdParticipation(rs.getInt(1)); p.setEtat(Etat.getEtatByAbreviation(rs.getString(2)));
   * p.setAnnulee(rs.getBoolean(3)); p.setVersion(rs.getInt(4)); p.setIdJournee(rs.getInt(5));
   * p.setIdEntreprise(rs.getInt(6));
   * 
   * j.setId_journee(rs.getInt(7)); j.setDate_journee(rs.getDate(8).toString());
   * j.setOuverte(rs.getBoolean(9));
   * 
   * pers.setIdPersonneContact(rs.getInt(10)); pers.setNom(rs.getString(11));
   * pers.setPrenom(rs.getString(12)); pers.setTelephone(rs.getString(13));
   * pers.setEmail(rs.getString(14)); pers.setActif(rs.getBoolean(15));
   * 
   * Map<JourneeEntrepriseDTO, PersonneContactDTO> m = new HashMap<>(); m.put(j, pers); map.put(p,
   * m); } return map; } catch (Exception e) { throw new FatalException(); } }
   */

  /**
   * Méthode listant les participation d'une entreprise.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJePers(
      int idEntreprise) {
    Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> map = new LinkedHashMap<>();
    try {
      ResultSet rs =
          dalBackendServices.prepare("SELECT p.*, j.id_journee, j.date_journee, j.ouverte , "
              + "count(partpers.id_participation_personne) "
              + "FROM pae.journees j, pae.participations p"
              + " LEFT OUTER JOIN pae.participations_personnes partpers "
              + "ON p.id_participation = partpers.id_participation WHERE p.id_entreprise = "
              + idEntreprise
              + " AND p.id_journee = j.id_journee GROUP BY p.id_participation, j.id_journee "
              + "ORDER BY p.id_participation DESC;");

      while (rs.next()) {
        JourneeEntreprise je = (JourneeEntreprise) this.jeFactory.creerJourneeEntreprise();
        je.setIdJournee(rs.getInt(7));
        je.setDateJournee(rs.getDate(8).toString());
        je.setOuverte(rs.getBoolean(9));

        Participation participation = remplirParticipation(rs);

        Map<JourneeEntrepriseDto, Integer> mapJe = new HashMap<>();
        mapJe.put(je, rs.getInt(10));
        map.put(participation, mapJe);
      }
      return map;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode annulant une participation.
   * 
   * @param participationDto -> Data Transfert Object contenant l'id de la participation
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Participation annulerParticipation(ParticipationDto participationDto) {
    try {
      ResultSet rs = dalBackendServices
          .prepare("UPDATE pae.participations SET annulee = true " + "WHERE id_participation = "
              + participationDto.getIdParticipation() + " AND annulee = false RETURNING *;");
      if (!rs.next()) {
        return null;
      }
      participationDto.setIdEntreprise(rs.getInt(1));
      participationDto.setVersion(rs.getInt(4));
      return (Participation) participationDto;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode permettant d'update l'etat d'une participation.
   * 
   * @param participationDto -> Data Transfert Object d'une participation contenant l'etat, l'id et
   *        le numero de version
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Participation updateEtatParticipation(ParticipationDto participationDto) {
    try {
      ResultSet rs = dalBackendServices.prepare("UPDATE pae.participations SET etat = '"
          + participationDto.getEtat() + "', version = version + 1 WHERE id_participation = "
          + participationDto.getIdParticipation() + " AND version = "
          + participationDto.getVersion() + " RETURNING *;");

      if (!rs.next()) {
        return null;
      }

      Participation p = remplirParticipation(rs);

      return p;
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
      throw new FatalException();
    }
  }

  /**
   * Méthode permettant d'update l'etat d'une participation si l'etat était "a payée".
   * 
   * @param participationDto -> Data Transfert Object d'une participation contenant l'id
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  // Update si l'état était a payée
  @Override
  public Participation updateDateDerniereParticipation(ParticipationDto participationDto) {
    try {
      ResultSet rs = dalBackendServices.prepare(
          "UPDATE pae.entreprises " + "SET version = version + 1, date_derniere_participation = "
              + "(SELECT j.date_journee FROM pae.journees j WHERE j.ouverte = true) "
              + "WHERE id_entreprise = (SELECT p.id_entreprise FROM pae.participations p "
              + "WHERE p.id_participation = " + participationDto.getIdParticipation()
              + ") RETURNING id_entreprise;");
      if (!rs.next()) {
        return null;
      }
      return (Participation) participationDto;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les id des personne invitée a la participation demandée.
   * 
   * @param idParticipation -> id de la participation
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<PersonneContactDto> getIdPersonnesInviteesPourUneParticipation(int idParticipation) {
    try {
      List<PersonneContactDto> listePdcs = new ArrayList<>();
      ResultSet rs = dalBackendServices
          .prepare("SELECT pers.* FROM pae.participations_personnes p, pae.personnes_contact pers "
              + "WHERE p.id_participation = " + idParticipation
              + " AND p.id_personne_contact = pers.id_personne_contact "
              + "ORDER BY pers.actif DESC, pers.nom, pers.prenom;");
      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) this.pdcFactory.creerPersonneContact();
        pdc.setIdPersonneContact(rs.getInt(1));
        pdc.setNom(rs.getString(2));
        pdc.setPrenom(rs.getString(3));
        pdc.setTelephone(rs.getString(4));
        pdc.setEmail(rs.getString(5));
        pdc.setActif(rs.getBoolean(6));
        pdc.setIdEntreprise(rs.getInt(7));
        pdc.setVersion(rs.getInt(8));
        listePdcs.add(pdc);
      }
      return listePdcs;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode verifiant le numero de version.
   * 
   * @param participationDto -> Data Transfert Object d'une participation
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public boolean verifierVersion(ParticipationDto participationDto) {
    try {
      // ResultSet rs = backDs.prepare("SELECT p.*")
      return true;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant la liste des journée d'entreprise ainsi que le nombre de participation.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees() {
    Map<JourneeEntrepriseDto, Integer> map = new LinkedHashMap<>();
    try {
      ResultSet rs = this.dalBackendServices.prepare(
          "SELECT j.*, count(p.id_participation) FROM pae.journees j , pae.participations p "
              + "WHERE j.id_journee = p.id_journee AND p.etat <> "
              + "'INVITEE' AND p.etat <> 'REFUSEE' GROUP BY j.id_journee "
              + "ORDER BY j.date_journee;");
      while (rs.next()) {
        JourneeEntreprise je = (JourneeEntreprise) this.jeFactory.creerJourneeEntreprise();
        je.setIdJournee(rs.getInt(1));
        je.setDateJournee(rs.getDate(2).toString());
        je.setOuverte(rs.getBoolean(3));
        je.setVersion(rs.getInt(4));
        map.put(je, rs.getInt(5));
      }
      return map;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }


  /**
   * Méthode permettant d'update l'etat d'une participation.
   * 
   * @param rs -> ResultSet contenant les information du query
   * @throws SQLException -> Exception lever si problème dans l'ajout de parametre
   */
  public Participation remplirParticipation(ResultSet rs) throws SQLException {
    Participation participation = (Participation) this.participationFactory.creerParticipation();
    participation.setIdParticipation(rs.getInt(1));
    participation.setEtat(Etat.getEtatByAbreviation(rs.getString(2)));
    participation.setAnnulee(rs.getBoolean(3));
    participation.setVersion(rs.getInt(4));
    participation.setIdJournee(rs.getInt(5));
    participation.setIdEntreprise(rs.getInt(6));
    return participation;
  }

}
