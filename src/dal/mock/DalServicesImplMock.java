package dal.mock;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import bizz.persistance.EntrepriseFactoryImpl;
import bizz.persistance.JourneeEntrepriseFactoryImpl;
import bizz.persistance.ParticipationFactoryImpl;
import bizz.persistance.PersonneContactFactoryImpl;
import bizz.persistance.UtilisateurFactoryImpl;
import bizz.uccimpl.EntrepriseUccImpl;
import bizz.uccimpl.JourneeEntrepriseUccImpl;
import bizz.uccimpl.ParticipationUccImpl;
import bizz.uccimpl.PersonneContactUccImpl;
import bizz.uccimpl.UtilisateurUccImpl;
import dal.DalBackendServices;
import dal.DalServices;

public class DalServicesImplMock implements DalServices, DalBackendServices {

  private static DalServicesImplMock instance = null;
  private Map<String, Object> map = null;

  private DalServicesImplMock() {
    map = new HashMap<>();
    System.out.println(
        "****************** Connection à la base de données fictive établie ******************");
    System.out.println();
  }

  private void putValues() {
    UtilisateurFactoryImpl utilisateurFactory = new UtilisateurFactoryImpl();
    UtilisateurDaoMock utilisateurDao = new UtilisateurDaoMock(utilisateurFactory, instance);
    UtilisateurUccImpl utilisateur = new UtilisateurUccImpl(utilisateurDao, instance);
    instance.map.put("utilisateur", utilisateur);

    EntrepriseFactoryImpl entrepriseFactory = new EntrepriseFactoryImpl();
    EntrepriseDaoMock entrepriseDao = new EntrepriseDaoMock(entrepriseFactory, instance);
    EntrepriseUccImpl entreprise = new EntrepriseUccImpl(entrepriseDao, instance);
    instance.map.put("entreprise", entreprise);

    JourneeEntrepriseFactoryImpl journeeEntrepriseFactory = new JourneeEntrepriseFactoryImpl();
    JourneeEntrepriseDaoMock journeeEntrepriseDao =
        new JourneeEntrepriseDaoMock(journeeEntrepriseFactory, instance);
    JourneeEntrepriseUccImpl journeeEntreprise =
        new JourneeEntrepriseUccImpl(journeeEntrepriseDao, instance);
    instance.map.put("je", journeeEntreprise);

    PersonneContactFactoryImpl personneContactFactory = new PersonneContactFactoryImpl();
    PersonneContactDaoMock personneContactDao =
        new PersonneContactDaoMock(personneContactFactory, instance);
    PersonneContactUccImpl personneContact =
        new PersonneContactUccImpl(personneContactDao, instance);
    instance.map.put("pc", personneContact);

    ParticipationFactoryImpl participationFactory = new ParticipationFactoryImpl();
    ParticipationDaoMock participationDao = new ParticipationDaoMock(participationFactory, instance,
        entrepriseFactory, personneContactFactory);
    ParticipationUccImpl participation = new ParticipationUccImpl(participationDao, instance);
    instance.map.put("participation", participation);

  }

  /**
   * 
   * 
   * @param requested -> chaine de caractere
   * @return Object -> une instance
   */
  public static synchronized Object getOType(String requested) {
    if (instance == null) {
      instance = new DalServicesImplMock();
      instance.putValues();
    }
    return instance.map.get(requested);
  }

  @Override
  public void startTransaction() {
    System.out.println("Transaction commencée ");
  }

  @Override
  public void commitTransaction() {
    System.out.println("Transaction commit");
  }

  @Override
  public void rollbackTransaction() {
    System.out.println("Transaction rollback");
  }

  @Override
  public ResultSet prepare(String query) {
    return null;
  }

  @Override
  public void openConnection() {
    System.out.println("Ouverture de connexion");

  }

  @Override
  public void closeConnection() {
    System.out.println("Fermeture de connexion");
  }
}
