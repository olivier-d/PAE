package dal.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bizz.domaine.PersonneContact;
import bizz.dto.PersonneContactDto;
import bizz.persistance.PersonneContactFactory;
import dal.DalBackendServices;
import dal.dao.PersonneContactDao;
import exception.FatalException;

public class PersonneContactDaoImpl implements PersonneContactDao {
  private PersonneContactFactory pdcFactory;
  private DalBackendServices dalBackendServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param backDs -> DalServices coté BackEnd
   * @param pdcFact -> factory des personnes de contact
   */
  public PersonneContactDaoImpl(PersonneContactFactory pdcFact, DalBackendServices backDs) {
    this.pdcFactory = pdcFact;
    this.dalBackendServices = backDs;
  }

  /**
   * Méthode inserant une personne de contact.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public PersonneContact insererPersonneContact(PersonneContactDto pdcDto) {
    try {
      ResultSet rs = dalBackendServices
          .prepare("INSERT INTO pae.personnes_contact VALUES (DEFAULT, '" + pdcDto.getNom() + "','"
              + pdcDto.getPrenom() + "','" + pdcDto.getTelephone() + "','" + pdcDto.getEmail()
              + "','" + pdcDto.getActif() + "','" + pdcDto.getIdEntreprise() + "',0) RETURNING *;");

      rs.next();
      PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
      return pdc;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode modifiant une personne de contact.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public PersonneContact modifierPersonneContact(PersonneContactDto pdcDto) {
    try {
      ResultSet rs = dalBackendServices.prepare("UPDATE pae.personnes_contact SET nom='"
          + pdcDto.getNom() + "', prenom='" + pdcDto.getPrenom() + "', telephone='"
          + pdcDto.getTelephone() + "', email='" + pdcDto.getEmail() + "', version= 1 +"
          + pdcDto.getVersion() + " WHERE id_personne_contact=" + pdcDto.getIdPersonneContact()
          + " AND version=" + pdcDto.getVersion() + " RETURNING *;");

      if (!rs.next()) {
        return null;
      }
      PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
      return pdc;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode desactivant une personne de contact.
   * 
   * @param idPersContact -> id de la personne de contact
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public PersonneContact desactiverPersContact(int idPersContact) {
    try {
      ResultSet rs = dalBackendServices.prepare("UPDATE pae.personnes_contact SET actif = false "
          + "WHERE id_personne_contact = " + idPersContact + " AND actif = true RETURNING *;");
      if (!rs.next()) {
        return null;
      }
      PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
      return pdc;
    } catch (Exception exception) {
      exception.printStackTrace();
      System.out.println(exception.getMessage());
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les personne de contact.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public ArrayList<PersonneContact> getPersonnesContact() {
    ArrayList<PersonneContact> listePdcs = new ArrayList<PersonneContact>();
    try {
      ResultSet rs = dalBackendServices.prepare("SELECT * FROM pae.personnes_contact;");

      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
        listePdcs.add(pdc);
      }
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      throw new FatalException();
    }
    return listePdcs;
  }

  /**
   * Méthode renvoyant le numero de version.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact contenant le numero de version
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public int getNumVersion(PersonneContactDto pdcDto) {
    ResultSet rs = null;
    int version = 0;
    try {
      rs = dalBackendServices.prepare(
          "SELECT version FROM PAE.personnes_contact WHERE version=" + pdcDto.getVersion() + ";");

      while (rs.next()) {
        version = rs.getInt(1);
      }
    } catch (SQLException exception) {
      throw new FatalException();
    }
    return version;
  }

  /**
   * Méthode listant les personnes de contact ainsi que son entreprise.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public Map<PersonneContactDto, String> getPersonnesContactEtEntreprise() {
    Map<PersonneContactDto, String> mapPers = new HashMap<PersonneContactDto, String>();
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT p.*, e.nom_entreprise FROM pae.personnes_contact p, pae.entreprises e "
              + "WHERE p.id_entreprise = e.id_entreprise;");
      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
        mapPers.put(pdc, rs.getString(9));
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
    return mapPers;
  }
  
  @Override
  public Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(
      PersonneContactDto p) {
    Map<PersonneContactDto, String> map = new HashMap<>();
    try {
      String query = "SELECT p.*, e.nom_entreprise FROM pae.personnes_contact p, pae.entreprises e "
              + "WHERE p.id_entreprise = e.id_entreprise ";
      if (!p.getNom().isEmpty()) {
        query += " AND lower(nom) LIKE lower('%" + p.getNom()+ "%')";
      }
      if (!p.getPrenom().isEmpty()) {
        query += " AND lower(prenom) LIKE lower('%" + p.getPrenom() + "%')";
      }
      query += ";";
      ResultSet rs = dalBackendServices.prepare(query);
      
      while (rs.next()) {
        PersonneContact pers = (PersonneContact) remplirPersonne(rs);
        map.put(pers, rs.getString(9));
      }
      return map;
    } catch(Exception e) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les personnes de contact active d'une entreprises.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  // On ne sélectionne que les personnes actives !
  @Override
  public List<PersonneContactDto> getPersonnesContactDeLEntreprise(int idEntreprise) {
    try {
      List<PersonneContactDto> listePdcs = new ArrayList<>();
      ResultSet rs = dalBackendServices
          .prepare("SELECT p.* FROM pae.personnes_contact p " + "WHERE p.id_entreprise = "
              + idEntreprise + " AND p.actif = true ORDER BY p.nom, p.prenom;");
      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
        listePdcs.add(pdc);
      }
      return listePdcs;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les personnes de contact active et inactive d'une entreprises.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public List<PersonneContactDto> getPersonnesContactActifEtInactifDeLEntreprise(int idEntreprise) {
    try {
      List<PersonneContactDto> listePdcs = new ArrayList<>();
      ResultSet rs = dalBackendServices
          .prepare("SELECT p.* FROM pae.personnes_contact p " + "WHERE p.id_entreprise = "
              + idEntreprise + " ORDER BY p.actif DESC, p.nom ASC, p.prenom ASC;");
      while (rs.next()) {
        PersonneContact pdc = (PersonneContact) remplirPersonne(rs);
        listePdcs.add(pdc);
      }
      return listePdcs;
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
  public PersonneContactDto remplirPersonne(ResultSet rs) throws SQLException {
    PersonneContact pdc = (PersonneContact) this.pdcFactory.creerPersonneContact();
    pdc.setIdPersonneContact(rs.getInt(1));
    pdc.setNom(rs.getString(2));
    pdc.setPrenom(rs.getString(3));
    pdc.setTelephone(rs.getString(4));
    pdc.setEmail(rs.getString(5));
    pdc.setActif(rs.getBoolean(6));
    pdc.setIdEntreprise(rs.getInt(7));
    pdc.setVersion(rs.getInt(8));
    return pdc;
  }
}

