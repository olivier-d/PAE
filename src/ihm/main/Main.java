package ihm.main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import bizz.persistance.EntrepriseFactory;
import bizz.persistance.JourneeEntrepriseFactory;
import bizz.persistance.ParticipationFactory;
import bizz.persistance.PersonneContactFactory;
import bizz.persistance.UtilisateurFactory;
import bizz.ucc.EntrepriseUcc;
import bizz.ucc.JourneeEntrepriseUcc;
import bizz.ucc.ParticipationUcc;
import bizz.ucc.PersonneContactUcc;
import bizz.ucc.UtilisateurUcc;
import dal.DalBackendServices;
import dal.DalServicesImpl;
import dal.dao.EntrepriseDao;
import dal.dao.JourneeEntrepriseDao;
import dal.dao.ParticipationDao;
import dal.dao.PersonneContactDao;
import dal.dao.UtilisateurDao;
import ihm.MaServlet;

public class Main {

  private static String lien = null;
  private static String utilisateur = null;
  private static String mdp = null;
  private static String secret = null;
  private static DalServicesImpl dal = null;

  private static UtilisateurUcc userUcc = null;
  private static UtilisateurFactory userFactory = null;
  private static UtilisateurDao userDao = null;

  private static EntrepriseUcc entrepriseUcc = null;
  private static EntrepriseFactory entrepriseFactory = null;
  private static EntrepriseDao entrepriseDao = null;

  private static JourneeEntrepriseUcc jeUcc = null;
  private static JourneeEntrepriseFactory jeFactory = null;
  private static JourneeEntrepriseDao jeDao = null;

  private static PersonneContactUcc personneContactUcc = null;
  private static PersonneContactFactory personneContactFactory = null;
  private static PersonneContactDao personneContactDao = null;

  private static ParticipationUcc partUcc = null;
  private static ParticipationFactory partFactory = null;
  private static ParticipationDao partDao = null;

  public static String url;
  public static Configuration configuration;
  
  private static String virtualpath;

  /**
   * Méthode main. Chargement du fichier prod.properties avec 
   * injections et initialisation des classes nécessaire + creation
   * servlet
   * 
   * @param args
   */
  public static void main(String[] args) {
    // Args
    System.out.println("Chargement du fichier : " + Main.url);
    Main.url = (args.length == 0) ? "prod.properties" : args[0];

    // Fichier propreties
    configuration = new Configuration(Main.url);

    // injection
    try {
      lien = Main.configuration.getConfiguration("Lien");
      utilisateur = Main.configuration.getConfiguration("Utilisateur");
      mdp = Main.configuration.getConfiguration("Mdp");
      secret = Main.configuration.getConfiguration("Secret");
      dal = (DalServicesImpl) Main.configuration.inject("dal").newInstance(lien, utilisateur, mdp);
      virtualpath = Main.configuration.getConfiguration("virtualpath");

      // utilisateur
      userFactory =
          (UtilisateurFactory) Main.configuration.inject("utilisateurFactory").newInstance();
      userDao = (UtilisateurDao) Main.configuration.inject("daoUtilisateur")
          .newInstance(userFactory, (DalBackendServices) dal);
      userUcc =
          (UtilisateurUcc) Main.configuration.inject("uccUtilisateur").newInstance(userDao, dal);

      // entreprise
      entrepriseFactory =
          (EntrepriseFactory) Main.configuration.inject("entrepriseFactory").newInstance();
      entrepriseDao = (EntrepriseDao) Main.configuration.inject("daoEntreprise")
          .newInstance(entrepriseFactory, (DalBackendServices) dal);
      entrepriseUcc = (EntrepriseUcc) Main.configuration.inject("uccEntreprise")
          .newInstance(entrepriseDao, (DalBackendServices) dal);

      // journee entreprise
      jeFactory = (JourneeEntrepriseFactory) Main.configuration.inject("journeeEntrepriseFactory")
          .newInstance();
      jeDao = (JourneeEntrepriseDao) Main.configuration.inject("daoJourneeEntreprise")
          .newInstance(jeFactory, (DalBackendServices) dal);
      jeUcc = (JourneeEntrepriseUcc) Main.configuration.inject("uccJE").newInstance(jeDao, dal);

      // personne de contact
      personneContactFactory = (PersonneContactFactory) Main.configuration
          .inject("personneContactFactory").newInstance();
      personneContactDao = (PersonneContactDao) Main.configuration.inject("daoPersonneContact")
          .newInstance(personneContactFactory, (DalBackendServices) dal);
      personneContactUcc = (PersonneContactUcc) Main.configuration.inject("uccPersonneContact")
          .newInstance(personneContactDao, dal);

      // participation
      partFactory =
          (ParticipationFactory) Main.configuration.inject("participationFactory").newInstance();
      partDao =
          (ParticipationDao) Main.configuration.inject("daoParticipation").newInstance(partFactory,
              (DalBackendServices) dal, entrepriseFactory, personneContactFactory, jeFactory);
      partUcc = (ParticipationUcc) Main.configuration.inject("uccParticipation")
          .newInstance(partDao, dal);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    MaServlet servlet =
        new MaServlet(secret, userUcc, userFactory, entrepriseUcc, entrepriseFactory, jeUcc,
            jeFactory, personneContactUcc, personneContactFactory, partUcc, partFactory,virtualpath);

    // Context
    WebAppContext context = new WebAppContext();
    context.setResourceBase("www");
    context.setContextPath("/");
    ServletHolder sh= new ServletHolder(servlet);
    context.addServlet(sh, "/"+virtualpath);
    context.addServlet(sh, "/"+virtualpath+"/");
    context.addServlet(sh, "/"+virtualpath+"/*");
    context.setInitParameter("cacheControl", "no-store,no-cache,must-revalidate");
    context.setClassLoader(Thread.currentThread().getContextClassLoader());

    System.out.println("******************");
    System.out.println("Démarrage du serveur");
    System.out.println("******************");
    System.out.println();

    // Serveur
    Server serveur = new Server(8080);
    serveur.setHandler(context);
    try {
      serveur.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    System.out.println();
    System.out.println("******************");
    System.out.println("Serveur prêt et à l'écoute sur le port 8080");
    System.out.println("******************");
    System.out.println();
  }
}
