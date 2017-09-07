package dal.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bizz.domaine.Entreprise;
import bizz.dto.EntrepriseDto;
import bizz.persistance.EntrepriseFactory;
import dal.DalBackendServices;
import dal.dao.EntrepriseDao;
import exception.FatalException;

public class EntrepriseDaoImpl implements EntrepriseDao {
  private EntrepriseFactory entrepriseFactory;
  private DalBackendServices dalBackendServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param backDs -> DalServices coté BackEnd
   * @param entFact -> factory des entreprises
   */
  public EntrepriseDaoImpl(EntrepriseFactory entFact, DalBackendServices backDs) {
    this.entrepriseFactory = entFact;
    this.dalBackendServices = backDs;
  }

  /**
   * Méthode inserant une entreprise dans la base de données.
   * 
   * @param entrepriseDto -> Data Transfert Object d'une entreprise
   * @throws FatalException -> Exception lever si problème dans l'insertion du ResultSet
   */
  public Entreprise insererEntreprise(EntrepriseDto entrepriseDto) {
    try {
      String nomEntreprise = entrepriseDto.getNomEntreprise().replaceAll("'", "''");
      String rue = entrepriseDto.getRue().replaceAll("'", "''");
      ResultSet rs = dalBackendServices
          .prepare("INSERT INTO pae.entreprises VALUES " + "(DEFAULT, '" + nomEntreprise + "','"
              + rue + "','" + entrepriseDto.getNumero() + "','" + entrepriseDto.getBoite() + "','"
              + entrepriseDto.getCodePostal() + "','" + entrepriseDto.getCommune()
              + "',CURRENT_DATE," + entrepriseDto.getDateDerniereParticipation() + ","
              + entrepriseDto.getCreateur() + "," + entrepriseDto.getVersion() + ") RETURNING *;");

      rs.next();
      Entreprise entreprise = (Entreprise) remplirDonnees(rs);
      return entreprise;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public ArrayList<Entreprise> getEntreprises() {
    try {
      ArrayList<Entreprise> liste = new ArrayList<Entreprise>();
      ResultSet rs = dalBackendServices.prepare("SELECT * FROM pae.entreprises;");

      while (rs.next()) {
        Entreprise entreprise = (Entreprise) remplirDonnees(rs);
        liste.add(entreprise);
      }
      return liste;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises avec le créateur de l'entreprise au sein de la base
   * de donnée.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Map<EntrepriseDto, String> getEntreprisesEtCreateurs() {
    try {
      Map<EntrepriseDto, String> map = new HashMap<>();
      ResultSet rs = dalBackendServices
          .prepare("SELECT e.*, u.nom, u.prenom FROM pae.entreprises e, pae.utilisateurs u "
              + "WHERE u.id_utilisateur = e.createur;");
      while (rs.next()) {
        Entreprise entreprise = (Entreprise) remplirDonnees(rs);
        String infoUser = rs.getString(12) + " " + rs.getString(13);
        map.put(entreprise, infoUser);
      }
      return map;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant l'entreprise selon l'id.
   * 
   * @param id -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Entreprise getEntreprisesSelonId(int id) {
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT * FROM PAE.entreprises WHERE id_entreprise = " + id + ";");

      rs.next();
      return (Entreprise) remplirDonnees(rs);
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises selon les parametre non null de l'entrepriseDto.
   * 
   * @param entrepriseDto -> Data Transfert Object de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Map<EntrepriseDto, String> rechercherEntreprise(EntrepriseDto entrepriseDto) {
    ResultSet rs = null;

    String requete = "SELECT e.*, u.nom, u.prenom FROM pae.entreprises e, pae.utilisateurs u "
        + "WHERE u.id_utilisateur = e.createur";


    if (!entrepriseDto.getNomEntreprise().isEmpty()) {
      requete +=
          " AND lower(nom_entreprise) LIKE lower('%" + entrepriseDto.getNomEntreprise() + "%')";
    }
    if (!entrepriseDto.getBoite().isEmpty()) {
      requete += " AND lower(boite) LIKE lower('%" + entrepriseDto.getBoite() + "%')";
    }
    if (!entrepriseDto.getCodePostal().isEmpty()) {
      requete += " AND code_postal = '" + entrepriseDto.getCodePostal() + "'";
    }
    if (!entrepriseDto.getCommune().isEmpty()) {
      requete += " AND lower(commune) LIKE lower('%" + entrepriseDto.getCommune() + "%')";
    }
    if (!entrepriseDto.getRue().isEmpty()) {
      requete += " AND lower(rue) LIKE lower('%" + entrepriseDto.getRue() + "%')";
    }
    if (!entrepriseDto.getNumero().isEmpty()) {
      requete += " AND lower(numero) LIKE lower('%" + entrepriseDto.getNumero() + "%')";
    }

    requete += ";";

    try {
      rs = dalBackendServices.prepare(requete);

      Map<EntrepriseDto, String> map = new HashMap<>();

      while (rs.next()) {
        Entreprise entreprise = (Entreprise) remplirDonnees(rs);
        String infoUser = rs.getString(12) + " " + rs.getString(13);
        map.put(entreprise, infoUser);
      }
      return map;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant de la version de l'entreprise entré en parametre.
   * 
   * @param entrepriseDto -> Data Transfert Object de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public int getNumVersion(EntrepriseDto entrepriseDto) {
    ResultSet rs = null;
    int version = 0;
    try {
      rs = dalBackendServices.prepare(
          "SELECT version FROM PAE.entreprises WHERE version=" + entrepriseDto.getVersion() + ";");
      while (rs.next()) {
        version = rs.getInt(1);
      }
    } catch (SQLException exception) {
      throw new FatalException();
    }
    return version;
  }

  /**
   * Méthode listant l'ensemble des entreprises invitable a la derniere journée d'entreprise active.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<EntrepriseDto> getEntreprisesInvitables() {
    List<EntrepriseDto> listeEntreprises = new ArrayList<>();
    try {
      ResultSet rs = dalBackendServices.prepare("SELECT DISTINCT e.* FROM pae.entreprises e "
          + "WHERE e.id_entreprise NOT IN " + "(SELECT e2.id_entreprise FROM pae.entreprises e2,"
          + " pae.participations p, pae.journees j "
          + "WHERE e2.id_entreprise = p.id_entreprise AND"
          + " j.id_journee = p.id_journee AND j.ouverte = false "
          + "AND (p.etat <> 'PAYEE' AND p.etat <> 'REFUSEE') "
          + "OR date_part('year', CURRENT_DATE) - "
          + "date_part('year', e2.date_derniere_participation) > 4) "
          + "OR date_part('year', e.date_premier_contact) = date_part('year', CURRENT_DATE) "
          + "AND e.date_derniere_participation IS NULL;");
      while (rs.next()) {
        Entreprise entreprise = (Entreprise) remplirDonnees(rs);
        listeEntreprises.add(entreprise);
      }
      return listeEntreprises;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }


  // JE ne prends que les id ici, pas besoin de reprendre les infos de l'entreprise
  /**
   * Méthode listant l'ensemble des id des entreprises invitée a la derniere journée d'entreprise
   * active.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<EntrepriseDto> getEntreprisesInvitees() {
    List<EntrepriseDto> listeEntreprises = new ArrayList<>();
    try {
      ResultSet rs = this.dalBackendServices.prepare("SELECT e.id_entreprise "
          + "FROM pae.entreprises e, pae.participations p , pae.journees j "
          + "WHERE e.id_entreprise = p.id_entreprise AND p.id_journee = "
          + "j.id_journee AND j.ouverte = true;");

      while (rs.next()) {
        Entreprise entreprise = (Entreprise) this.entrepriseFactory.creerEntreprise();
        entreprise.setIdEntreprise(rs.getInt(1));
        listeEntreprises.add(entreprise);
      }
      return listeEntreprises;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode remplissant un Dto sur base d'un ResulSet.
   * 
   * @param rs -> ResultSet d'un query
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public EntrepriseDto remplirDonnees(ResultSet rs) throws SQLException {
    Entreprise entreprise = (Entreprise) this.entrepriseFactory.creerEntreprise();
    entreprise.setIdEntreprise(rs.getInt(1));
    entreprise.setNomEntreprise(rs.getString(2));
    entreprise.setRue(rs.getString(3));
    entreprise.setNumero(rs.getString(4));
    entreprise.setBoite(rs.getString(5));
    entreprise.setCodePostal(rs.getString(6));
    entreprise.setCommune(rs.getString(7));
    entreprise.setDatePremierContact(rs.getDate(8).toLocalDate());
    if (rs.getDate(9) != null) {
      entreprise.setDateDerniereParticipation(rs.getDate(9).toLocalDate());
    }
    entreprise.setCreateur(rs.getInt(10));
    entreprise.setVersion(rs.getInt(11));
    return entreprise;
  }
}
