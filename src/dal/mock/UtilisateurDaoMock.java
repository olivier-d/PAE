package dal.mock;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import bizz.domaine.Utilisateur;
import bizz.dto.UtilisateurDto;
import bizz.persistance.UtilisateurFactory;
import dal.DalBackendServices;
import dal.dao.UtilisateurDao;
import exception.FatalException;

public class UtilisateurDaoMock implements UtilisateurDao {
  private UtilisateurFactory utilisateurFactory;

  // Mocks objets
  private List<Utilisateur> users;


  /**
   * Constructeur initialisant la liste des utilisateurs et la rempli de 3 utilisateurs par defaut.
   * 
   * @param utilisateurFactory -> factory des utilisateurs
   * @param dalBackendServices -> DalServices coté BackEnd
   */
  public UtilisateurDaoMock(UtilisateurFactory utilisateurFactory,
      DalBackendServices dalBackendServices) {
    this.utilisateurFactory = utilisateurFactory;

    // donnees pour tests unitaires
    users = new ArrayList<>();
    Utilisateur utilisateur = null;
    utilisateur = init("Cottilard", "Marion", "cotcot", "kjhglezhg455", false, "marion.c@free.com");
    Utilisateur utilisateur2 = null;
    utilisateur2 = init("Murphy", "Eddy", "sneddy", "sneddy", true, "sneddy@outlook.com");
    Utilisateur utilisateur3 = null;
    utilisateur3 = init("Dicaprio", "Leonardo", "noscar", "heyhey", false, "olachickas@hot.com");
    users.add(utilisateur);
    users.add(utilisateur2);
    users.add(utilisateur3);
  }

  /**
   * Methode initialisant les utilisateur.
   * 
   * @param nom -> nom de l'utilisateur
   * @param prenom -> prenom de l'utilisateur
   * @param pseudo -> pseudo de l'utilisateur
   * @param password -> password de l'utilisateur
   * @param responsable -> est responsable
   * @param email -> email de l'utilisateur
   */
  // Methode init (+creation) afin d'eviter le duplicate
  private Utilisateur init(String nom, String prenom, String pseudo, String password,
      boolean responsable, String email) {
    Utilisateur utilisateur = null;
    utilisateur = (Utilisateur) utilisateurFactory.createUtilisateur();
    utilisateur.setId(users.size() + 1);
    utilisateur.setPseudo(pseudo);
    utilisateur.setNom(nom);
    utilisateur.setPrenom(prenom);
    utilisateur.setEmail(email);
    utilisateur.setResponsable(responsable);
    utilisateur.setMdp(password);
    utilisateur.setVersion(1);
    utilisateur.setDateInscription(LocalDate.now());
    return utilisateur;
  }

  /**
   * Méthode checkant que utilisateur entré en paramètre existe.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème
   */
  // Verifie si l'utilisateur existe
  public Utilisateur utilisateurExiste(UtilisateurDto utilisateurDto) throws FatalException {
    for (Utilisateur utilisateur : users) {
      if (utilisateur.getNom().equals(utilisateurDto.getNom())
          && utilisateur.getPrenom().equals(utilisateurDto.getPrenom())) {
        return users.get(utilisateurDto.getId());
      }
    }
    return null;
  }

  /**
   * Méthode checkant le doublon d'un pseudo.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème
   */
  // verifie si le pseudo est deja pris
  // @return l'user passe en param si le pseudo n'existe pas encore sinon @return null si pris
  public Utilisateur verifierDoublonPseudo(UtilisateurDto utilisateurDto) throws FatalException {
    for (Utilisateur utilisateur : users) {
      if (utilisateur.getPseudo().equals(utilisateurDto.getPseudo())) {
        return null;
      }
    }
    return (Utilisateur) utilisateurDto;
  }

  /**
   * Méthode inserant un utilisateur au sein de la base de données.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws FatalException -> Exception lever si problème
   */
  public Utilisateur insererUtilisateur(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      if (utilisateurExiste(utilisateurDto) == null
          && verifierDoublonPseudo(utilisateurDto) != null) {
        String motDePasse = utilisateurDto.getMdp();
        String motDePasseCrypte = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Utilisateur user =
            init(utilisateurDto.getNom(), utilisateurDto.getPrenom(), utilisateurDto.getPseudo(),
                motDePasseCrypte, utilisateurDto.getResponsable(), utilisateurDto.getEmail());
        users.add(user);
        return user;
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode changeant le mot de passe d'un utilisateur.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id et le nouveau
   *        mot de passe
   * @throws SQLException -> Exception lever si problème
   */
  public Utilisateur changerMotDePasse(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      Utilisateur utilisateur = getUtilisateurByPseudo(utilisateurDto);
      int indice = users.indexOf(utilisateur);
      String motDePasse = utilisateurDto.getMdp();
      String motDePasseCrypte = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
      utilisateur.setMdp(motDePasseCrypte);
      int version = utilisateur.getVersion();
      utilisateur.setVersion(version++);
      users.set(indice, utilisateur);
      return utilisateur;
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode renvoyant l'utilisateur selon son pseudo.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo
   * @throws SQLException -> Exception lever si problème
   */
  public Utilisateur getUtilisateurByPseudo(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      for (Utilisateur utilisateur : users) {
        if (utilisateur.getPseudo().equals(utilisateurDto.getPseudo())) {
          int indice = users.indexOf(utilisateur);
          return users.get(indice);
        }
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
    return null;
  }

  /**
   * Méthode checkant le numero de version.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id
   * @throws SQLException -> Exception lever si problème
   */
  public int getNumVersion(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      for (Utilisateur utilisateur : users) {
        if (utilisateur.getId() == utilisateurDto.getId()) {
          return utilisateur.getVersion();
        }
      }
      return -1;
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode verifiant un utilisateur.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant l'id et la version
   * @throws SQLException -> Exception lever si problème
   */
  public Utilisateur verifierUtilisateur(UtilisateurDto utilisateurDto) throws FatalException {
    try {
      Utilisateur utilisateur = null;
      utilisateur = users.get(utilisateurDto.getId());
      if (utilisateur == null) {
        return null;
      }
      if (utilisateur.getVersion() == utilisateurDto.getVersion()
          && utilisateurDto.getDateInscription() != null) {
        return utilisateur;
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode renvotant la liste des utilisateurs.
   */
  public List<Utilisateur> getUsers() {
    return this.users;
  }

  /**
   * Méthode checkant la connection d'un utilisateur.
   * 
   * @param utilisateurDto -> Data Transfert Object d'un utilisateur contenant le pseudo et le mot
   *        de passe
   */
  public UtilisateurDto connecterUtilisateur(UtilisateurDto utilisateurDto) {
    for (Utilisateur utilisateur : users) {
      if (utilisateur.getPseudo().equals(utilisateurDto.getPseudo())
          && utilisateur.getMdp().equals(utilisateurDto.getMdp())) {
        return utilisateur;
      }
    }
    return null;
  }
}
