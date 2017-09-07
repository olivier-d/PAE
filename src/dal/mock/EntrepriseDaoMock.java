package dal.mock;

import static util.Util.checkObject;

import java.time.LocalDate;
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

public class EntrepriseDaoMock implements EntrepriseDao {
  private EntrepriseFactory entrepriseFactory;

  // Mocks objets
  private ArrayList<Entreprise> entreprises;

  /**
   * Constructeur initialisant la liste des entreprises et la rempli de 3 entreprises par defaut.
   * 
   * @param entrepriseFactory -> factory des entreprises
   * @param dalBackendServices -> DalServices coté BackEnd
   */
  public EntrepriseDaoMock(EntrepriseFactory entrepriseFactory,
      DalBackendServices dalBackendServices) {
    this.entrepriseFactory = entrepriseFactory;
    // donnees pour test unitaires
    entreprises = new ArrayList<>();
    Entreprise entreprise = init("IPL", "Clos Chapelle", "5", "1", "1000", "Bruxelles", 1);
    entreprises.add(entreprise);
    Entreprise entreprise2 = init("ISEI", "Clos Chapelle", "6", "2", "1100", "Woluwe", 1);
    entreprises.add(entreprise2);
    Entreprise entreprise3 = init("ECAM", "Clos Chapelle", "7", "3", "1300", "StPierre", 0);
    entreprises.add(entreprise3);
  }

  /**
   * Methode initialisant les entreprises.
   * 
   * @param nom -> nom de l'entreprise
   * @param rue -> rue de l'entreprise
   * @param numero -> numero de l'entreprise
   * @param boite -> boite de l'entreprise
   * @param codePostal -> code postal de l'entreprise
   * @param commune -> commune de l'entreprise
   * @param createur -> id du créateur
   */
  private Entreprise init(String nom, String rue, String numero, String boite, String codePostal,
      String commune, int createur) {
    Entreprise entrepriseInit = (Entreprise) entrepriseFactory.creerEntreprise();
    entrepriseInit.setIdEntreprise(entreprises.size() + 1);
    entrepriseInit.setNomEntreprise(nom);
    entrepriseInit.setRue(rue);
    entrepriseInit.setNumero(numero);
    entrepriseInit.setBoite(boite);
    entrepriseInit.setCodePostal(codePostal);
    entrepriseInit.setCommune(commune);
    entrepriseInit.setDatePremierContact(LocalDate.now());
    entrepriseInit.setDateDerniereParticipation(null);
    entrepriseInit.setCreateur(createur);
    entrepriseInit.setVersion(1);
    return entrepriseInit;
  }

  /**
   * Méthode checkant si l'entreprise existe.
   * 
   * @param entrepriseDto -> Data Transfert Object d'une entreprise contenant l'id et le nom
   */
  private Entreprise entrepriseExiste(EntrepriseDto entrepriseDto) throws FatalException {
    for (Entreprise entreprise : entreprises) {
      if (entreprise.getIdEntreprise() == entrepriseDto.getIdEntreprise()
          && entreprise.getNomEntreprise().equals(entrepriseDto.getNomEntreprise())) {
        return entreprise;
      }
    }
    return null;
  }

  /**
   * Méthode inserant une entreprise dans la base de données.
   * 
   * @param entrepriseDto -> Data Transfert Object d'une entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Entreprise insererEntreprise(EntrepriseDto entrepriseDto) throws FatalException {
    try {
      checkObject(entrepriseDto);
      if (entrepriseExiste(entrepriseDto) == null) {
        Entreprise entrepriseInsert = (Entreprise) entrepriseFactory.creerEntreprise();
        entrepriseInsert.setIdEntreprise(entreprises.size() + 1);
        entrepriseInsert.setNomEntreprise(entrepriseDto.getNomEntreprise());
        entrepriseInsert.setRue(entrepriseDto.getRue());
        entrepriseInsert.setNumero(entrepriseDto.getNumero());
        entrepriseInsert.setBoite(entrepriseDto.getBoite());
        entrepriseInsert.setCodePostal(entrepriseDto.getCodePostal());
        entrepriseInsert.setCommune(entrepriseDto.getCommune());
        entrepriseInsert.setDatePremierContact(LocalDate.now());
        entrepriseInsert.setDateDerniereParticipation(null);
        entrepriseInsert.setCreateur(entrepriseDto.getCreateur());
        entrepriseInsert.setVersion(1);
        entreprises.add(entrepriseInsert);
        return entrepriseInsert;
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public ArrayList<Entreprise> getEntreprises() throws FatalException {
    try {
      if (entreprises.size() == 0) {
        return null;
      } else {
        return entreprises;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises avec le créateur de l'entreprise au sein de la base
   * de donnée.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Map<EntrepriseDto, String> getEntreprisesEtCreateurs() {
    Map<EntrepriseDto, String> createursDentreprise = new HashMap<>();
    for (EntrepriseDto entrepriseGet : entreprises) {
      // On ne peut pas recuperer la liste des utilisateur ici on fait donc un stub
      createursDentreprise.put(entrepriseGet, entrepriseGet.getCreateur() + "");
    }
    return createursDentreprise;
  }

  /**
   * Méthode renvoyant l'entreprise selon l'id.
   * 
   * @param id -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Entreprise getEntreprisesSelonId(int id) throws FatalException {
    try {
      for (Entreprise entreprise : entreprises) {
        if (entreprise.getIdEntreprise() == id) {
          return entreprise;
        }
      }
      return null;
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises selon les parametre non null de l'entrepriseDto.
   * 
   * @param entrepriseDto -> Data Transfert Object de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Map<EntrepriseDto, String> rechercherEntreprise(EntrepriseDto entrepriseDto)
      throws FatalException {
    if (entrepriseDto == null) {
      return null;
    }
    Map<EntrepriseDto, String> match = new HashMap<>();
    for (EntrepriseDto e : entreprises) {
      match.put(e, "John Doe");
      if (entrepriseDto.getNomEntreprise() != null
          && !e.getNomEntreprise().contains(entrepriseDto.getNomEntreprise())) {
        match.remove(e);
      }
      if (entrepriseDto.getRue() != null && !e.getRue().contains(entrepriseDto.getRue())) {
        match.remove(e);
      }
      if (entrepriseDto.getNumero() != null && !e.getNumero().contains(entrepriseDto.getNumero())) {
        match.remove(e);
      }
      if (entrepriseDto.getBoite() != null && !e.getBoite().contains(entrepriseDto.getBoite())) {
        match.remove(e);
      }
      if (entrepriseDto.getCodePostal() != null
          && !e.getCodePostal().contains(entrepriseDto.getCodePostal())) {
        match.remove(e);
      }
      if (entrepriseDto.getCommune() != null
          && !e.getCommune().contains(entrepriseDto.getCommune())) {
        match.remove(e);
      }
    }
    return match;
  }

  /**
   * Méthode renvoyant de la version de l'entreprise entré en parametre.
   * 
   * @param entrepriseDto -> Data Transfert Object de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public int getNumVersion(EntrepriseDto entrepriseDto) {
    try {
      Entreprise entreprise = entrepriseExiste(entrepriseDto);
      if (entreprise == null) {
        return -1;
      } else {
        return entreprise.getVersion();
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant l'ensemble des entreprises invitable a la derniere journée d'entreprise active.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public List<EntrepriseDto> getEntreprisesInvitables() {
    ArrayList<EntrepriseDto> liste = new ArrayList<>();
    for (Entreprise entreprise : entreprises) {
      // stub renvoie toute la liste
      liste.add(entreprise);
    }
    return liste;
  }

  /**
   * Méthode listant l'ensemble des entreprises invitée a la derniere journée d'entreprise active.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override // entreprises invitees a la journee ouverte
  public List<EntrepriseDto> getEntreprisesInvitees() {
    ArrayList<EntrepriseDto> liste = new ArrayList<>();
    for (Entreprise entreprise : entreprises) {
      // stub renvoie toute la liste
      liste.add(entreprise);
    }
    return liste;
  }
}
