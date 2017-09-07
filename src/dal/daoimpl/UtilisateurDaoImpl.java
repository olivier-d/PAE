package dal.daoimpl;

import static util.Util.checkObject;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import bizz.domaine.Utilisateur;
import bizz.domaine.UtilisateurImpl;
import bizz.dto.UtilisateurDto;
import bizz.persistance.UtilisateurFactory;
import dal.DalBackendServices;
import dal.dao.UtilisateurDao;
import exception.FatalException;

// Transforme demande en SQL et resultSet en objets
public class UtilisateurDaoImpl implements UtilisateurDao {
  private UtilisateurFactory utilisateurFactory;
  private DalBackendServices dalBackendServices;

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param backDs -> DalServices coté BackEnd
   * @param utilisateurFact -> factory des utilisateurs
   */
  public UtilisateurDaoImpl(UtilisateurFactory utilisateurFact, DalBackendServices backDs) {
    this.utilisateurFactory = utilisateurFact;
    this.dalBackendServices = backDs;
  }

  /**
   * Méthode checkant que utilisateur entré en paramètre existe.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur utilisateurExiste(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      checkObject(utilisateurDto);
      ResultSet rs = null;
      Utilisateur utilisateur = null;
      rs = dalBackendServices.prepare(
          "SELECT * FROM pae.utilisateurs WHERE pseudo LIKE '" + utilisateurDto.getPseudo() + "';");

      if (!rs.next()) {
        return null;
      }

      utilisateur = remplirUtilisateur(rs);
      return utilisateur;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode checkant le doublon d'un pseudo.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur verifierDoublonPseudo(UtilisateurDto utilisateurDto) {
    try {
      ResultSet rs = dalBackendServices
          .prepare("SELECT count(*) FROM pae.utilisateurs WHERE lower(pseudo) LIKE lower('"
              + utilisateurDto.getPseudo() + "');");
      rs.next();
      if (rs.getInt(1) > 0) {
        return null;
      }
      Utilisateur utilisateur = (Utilisateur) utilisateurFactory.createUtilisateur();
      utilisateur.setId(0);
      return utilisateur;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode inserant un utilisateur au sein de la base de données.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur insererUtilisateur(UtilisateurDto utilisateurDto) {
    try {
      String motDePasse = utilisateurDto.getMdp();
      String motDePasseCrypter = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
      ResultSet rs = dalBackendServices
          .prepare("INSERT INTO pae.utilisateurs VALUES (DEFAULT, '" + utilisateurDto.getPseudo()
              + "','" + utilisateurDto.getNom() + "','" + utilisateurDto.getPrenom() + "','"
              + utilisateurDto.getEmail() + "',CURRENT_DATE," + utilisateurDto.getResponsable()
              + ",'" + motDePasseCrypter + "'," + utilisateurDto.getVersion() + ") RETURNING *;");

      Utilisateur utilisateur = null;
      rs.next();
      utilisateur = remplirUtilisateur(rs);
      return utilisateur;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode changeant le mot de passe d'un utilisateur.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id et le nouveau
   *        mot de passe
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur changerMotDePasse(UtilisateurDto utilisateurDto) {
    try {
      Utilisateur utilisateur = null;
      String motDePasse = utilisateurDto.getMdp();
      String motDePasseCrypter = BCrypt.hashpw(motDePasse, BCrypt.gensalt());

      ResultSet rs = dalBackendServices.prepare("UPDATE pae.utilisateurs SET mdp = '"
          + motDePasseCrypter + "', version = '" + (utilisateurDto.getVersion() + 1)
          + "'  WHERE id_utilisateur=" + utilisateurDto.getId() + " AND version = "
          + utilisateurDto.getVersion() + " RETURNING *;");

      rs.next();
      utilisateur = remplirUtilisateur(rs);
      return utilisateur;
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode renvoyant l'utilisateur selon son pseudo.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur getUtilisateurByPseudo(UtilisateurDto utilisateurDto) {
    ResultSet rs = null;
    Utilisateur utilisateur = null;
    // 1
    try {
      rs = dalBackendServices
          .prepare("SELECT * FROM pae.utilisateurs WHERE lower(pseudo) LIKE lower('"
              + utilisateurDto.getPseudo() + "');");

      rs.next();
      utilisateur = remplirUtilisateur(rs);

    } catch (SQLException exception) {
      throw new FatalException();
    }
    return utilisateur;
  }

  /**
   * Méthode checkant le numero de version.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public int getNumVersion(UtilisateurDto utilisateurDto) {
    ResultSet rs = null;
    int version = 0;
    try {
      rs = dalBackendServices
          .prepare("SELECT version FROM PAE.utilisateurs WHERE id=" + utilisateurDto.getId() + ";");
      if (rs == null) {
        return -1;
      }
      while (rs.next()) {
        version = rs.getInt(1);
      }
    } catch (SQLException exception) {
      System.err.println(exception.getMessage());
      return -1;
    }
    return version;
  }

  /**
   * Méthode verifiant un utilisateur.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id et la version
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public Utilisateur verifierUtilisateur(UtilisateurDto utilisateurDto) {
    try {
      ResultSet rs = dalBackendServices.prepare("SELECT * FROM PAE.utilisateurs WHERE "
          + "id_utilisateur=" + utilisateurDto.getId() + ";");
      // alors l'id n'existe pas
      if (!rs.next()) {
        return null;
      }
      // Alors la version a été modifie !
      if (utilisateurDto.getVersion() < rs.getInt(9)) {
        return null;
      }
      Utilisateur utilisateur = (Utilisateur) remplirUtilisateur(rs);
      return utilisateur;
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode remplissant un Dto sur base d'un ResulSet.
   * 
   * @param rs -> ResultSet d'un query
   * @throws SQLException -> Exception lever si problème dans le ResultSet
   */
  public UtilisateurImpl remplirUtilisateur(ResultSet rs) throws SQLException {
    UtilisateurImpl utilisateur = (UtilisateurImpl) utilisateurFactory.createUtilisateur();
    utilisateur.setId(rs.getInt(1));
    utilisateur.setPseudo(rs.getString(2));
    utilisateur.setNom(rs.getString(3));
    utilisateur.setPrenom(rs.getString(4));
    utilisateur.setEmail(rs.getString(5));
    utilisateur.setDateInscription(rs.getDate(6).toLocalDate());
    utilisateur.setResponsable(rs.getBoolean(7));
    utilisateur.setMdp(rs.getString(8));
    utilisateur.setVersion(rs.getInt(9));
    return utilisateur;
  }
}
