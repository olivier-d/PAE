package dal.daoimpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import bizz.domaine.JourneeEntreprise;
import bizz.dto.JourneeEntrepriseDto;
import bizz.persistance.JourneeEntrepriseFactory;
import dal.DalBackendServices;
import dal.dao.JourneeEntrepriseDao;
import exception.FatalException;

public class JourneeEntrepriseDaoImpl implements JourneeEntrepriseDao {
  private JourneeEntrepriseFactory jeFactory;
  private DalBackendServices dalBackendServices;


  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param backDs -> DalServices coté BackEnd
   * @param jeFact -> factory des journées d'entreprise
   */
  public JourneeEntrepriseDaoImpl(JourneeEntrepriseFactory jeFact, DalBackendServices backDs) {
    this.jeFactory = jeFact;
    this.dalBackendServices = backDs;
  }

  /**
   * Méthode verifiant si une journée est ouverte.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public JourneeEntreprise verifierJeNonOuverte() {
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT id_journee, date_journee FROM pae.journees WHERE ouverte = true;");

      if (rs.next()) {
        JourneeEntreprise jeBizz = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
        jeBizz.setIdJournee(rs.getInt(1));
        jeBizz.setDateJournee(rs.getString(2));
        return jeBizz;
      }
      return null;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode inserant une journée au sein de la base de donnée.
   * 
   * @param jeDto -> Data Transfert Object d'une journée d'entreprise
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public JourneeEntreprise insererJourneeEntreprise(JourneeEntrepriseDto jeDto) {
    try {
      ResultSet rs =
          dalBackendServices.prepare("INSERT INTO pae.journees (date_journee, ouverte, version) "
              + "SELECT '" + jeDto.getDateJournee() + "', true, 0 WHERE NOT EXISTS "
              + "(SELECT id_journee FROM pae.journees WHERE date_journee = '"
              + jeDto.getDateJournee() + "') " + "RETURNING *;");

      if (!rs.next()) {
        return null;
      }
      JourneeEntreprise jeBizz = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
      jeBizz.setIdJournee(rs.getInt(1));
      jeBizz.setDateJournee(rs.getDate(2).toString());
      jeBizz.setOuverte(rs.getBoolean(3));
      jeBizz.setVersion(rs.getInt(4));
      return jeBizz;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les journées d'entreprise.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public ArrayList<JourneeEntreprise> getJourneeEntreprise() {
    try {
      ArrayList<JourneeEntreprise> liste = new ArrayList<JourneeEntreprise>();
      ResultSet rs = dalBackendServices.prepare("SELECT * FROM PAE.journees;");
      while (rs.next()) {
        JourneeEntreprise jeBizz = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
        jeBizz.setIdJournee(rs.getInt(1));
        jeBizz.setDateJournee(rs.getDate(2).toString());
        jeBizz.setOuverte(rs.getBoolean(3));
        liste.add(jeBizz);
      }
      return liste;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les journées d'entreprise selon l'id entrée en paramètre.
   * 
   * @param jeDto -> Data transfert Object contenant l'id de la journée
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public JourneeEntreprise getJourneeSelonId(JourneeEntrepriseDto jeDto) {
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT * FROM PAE.journees WHERE id_journee =" + jeDto.getIdJournee() + ";");

      rs.next();
      JourneeEntreprise jeBizz = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
      jeBizz.setIdJournee(rs.getInt(1));
      jeBizz.setDateJournee(rs.getDate(2).toString());
      jeBizz.setOuverte(rs.getBoolean(3));
      return jeBizz;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant le numero de version de la journée voulue.
   * 
   * @param jeDto -> Data transfert Object contenant l'id de la journée
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public int getNumVersion(JourneeEntrepriseDto jeDto) {
    ResultSet rs = null;
    int version = 0;
    try {
      rs = dalBackendServices
          .prepare("SELECT version FROM PAE.journees WHERE version=" + jeDto.getVersion() + ";");
      while (rs.next()) {
        version = rs.getInt(1);
      }
    } catch (SQLException exception) {
      throw new FatalException();
    }
    return version;
  }

  /**
   * Méthode cloturant la journée entrée en paramètre.
   * 
   * @param jeDto -> Data transfert Object contenant le numero de version de la journée active
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public JourneeEntreprise cloturerJourneeEntreprise(JourneeEntrepriseDto jeDto) {
    try {
      ResultSet rs = dalBackendServices.prepare("UPDATE pae.journees SET ouverte = false, version="
          + (jeDto.getVersion() + 1) + " WHERE ouverte = true RETURNING id_journee, date_journee;");
      if (!rs.next()) {
        return null;
      }
      JourneeEntreprise jeBizz = (JourneeEntreprise) jeFactory.creerJourneeEntreprise();
      jeBizz.setIdJournee(rs.getInt(1));
      jeBizz.setDateJournee(rs.getString(2));
      return jeBizz;
    } catch (Exception exception) {
      System.out.println(exception.getMessage());
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant si il y'a une journée active.
   * 
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  @Override
  public boolean isJourneeActive() {
    try {
      ResultSet rs = this.dalBackendServices
          .prepare("SELECT count(id_journee) FROM pae.journees WHERE ouverte = true;");
      rs.next();
      if (rs.getInt(1) == 0) {
        return false;
      } else {
        return true;
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new FatalException();
    }
  }
}
