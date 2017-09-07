package dal;

import static util.Util.checkString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import exception.FatalException;

public class DalServicesImpl implements DalServices, DalBackendServices {

  /* Gestion des transactions et des prepareStatement */
  private BasicDataSource ds;
  private ThreadLocal<Connection> threadConnexions;
  private String url = "";

  /**
   * Constructeur initialisant les parametres de la classe par default.
   * 
   * @param lien -> Lien de la base de données
   * @param utilisateur -> Nom d'utilisateur pour la base de donnée
   * @param mdp -> Mot de passe pour la base de donnée
   */
  public DalServicesImpl(String lien, String utilisateur, String mdp) {
    checkString(lien);
    checkString(utilisateur);
    checkString(mdp);
    this.url = lien + "?user=" + utilisateur + "&password=" + mdp;
    System.out.println("******************");
    System.out.println("Tentative de connection à la base de données");
    System.out.println("Lien ->" + url);
    System.out.println("******************");
    System.out.println();
    // Force le chargement du build path lors de l'execution
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException exception) {
      System.err.println("Driver PostgreSQL manquant !");
      System.exit(1);
    }
    // Ouverture connexion
    ds = new BasicDataSource();
    ds.setDriverClassName("org.postgresql.Driver");
    ds.setUsername(utilisateur);
    ds.setPassword(mdp);
    ds.setUrl(lien);
    threadConnexions = new ThreadLocal<>();

    System.out.println("******************");
    System.out.println("Connection à la base de données établie");
    System.out.println("******************");
    System.out.println();
  }

  @Override
  public void openConnection() {
    try {
      Connection conn = ds.getConnection();
      threadConnexions.set(conn);
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  @Override
  public void closeConnection() {
    try {
      Connection conn = threadConnexions.get();
      threadConnexions.remove();
      conn.close();
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  @Override
  public void startTransaction() {
    if (threadConnexions.get() == null) {
      openConnection();
    }
    try {
      threadConnexions.get().setAutoCommit(false);
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  @Override
  public void commitTransaction() {
    try {
      threadConnexions.get().commit();
      threadConnexions.get().setAutoCommit(true);
      closeConnection();
    } catch (SQLException exception) {
      throw new FatalException();
    }
  }

  @Override
  public void rollbackTransaction() {
    try {
      threadConnexions.get().rollback();
      threadConnexions.get().setAutoCommit(true);
      closeConnection();
    } catch (SQLException exception) {
      throw new FatalException();
    }

  }

  @Override
  public ResultSet prepare(String requete) {
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      ps = threadConnexions.get().prepareStatement(requete);
    } catch (SQLException exception) {
      System.err.println("DALServicesImpl -> Erreur chargement requete : " + requete);
      throw new FatalException();
    }

    try {
      rs = ps.executeQuery();
    } catch (SQLException exception) {
      System.err.println("DALServicesImpl -> Erreur execution requete : " + requete);
      throw new FatalException();
    }

    return rs;
  }

}
