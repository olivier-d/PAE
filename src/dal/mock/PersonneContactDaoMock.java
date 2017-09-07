package dal.mock;

import static util.Util.checkInputPersonneContact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bizz.domaine.PersonneContact;
import bizz.dto.PersonneContactDto;
import bizz.persistance.PersonneContactFactory;
import bizz.uccimpl.EntrepriseUccImpl;
import dal.DalBackendServices;
import dal.dao.PersonneContactDao;
import exception.FatalException;

public class PersonneContactDaoMock implements PersonneContactDao {
  private PersonneContactFactory pdcFactory;
  private EntrepriseUccImpl entreprise;

  // Mocks objets
  private ArrayList<PersonneContact> personnes;

  /**
   * Constructeur initialisant la liste des personnes de contact et la rempli de 3 personnes par
   * defaut.
   * 
   * @param pdcFact -> factory des personnes de contact
   * @param backDs -> DalServices coté BackEnd
   */
  public PersonneContactDaoMock(PersonneContactFactory pdcFact, DalBackendServices backDs) {
    entreprise = (EntrepriseUccImpl) DalServicesImplMock.getOType("entreprise");

    this.pdcFactory = pdcFact;

    // donnees pour tests unitaires
    personnes = new ArrayList<>();
    PersonneContact pdc1 = init(1, "Vcp", "Antho", "0471474455", "antho@hotmail.com");
    personnes.add(pdc1);
    PersonneContact pdc2 = init(1, "Dab", "Virginia", "0478552000", "virgi@gmail.com");
    personnes.add(pdc2);
    PersonneContact pdc3 = init(2, "Blade", "Chase", "0489999999", "chase@outlook.be");
    personnes.add(pdc3);
  }

  /**
   * Methode initialisant les personnes de contact.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @param nom -> nom de la personne de contact
   * @param prenom -> prenom de la personne de contact
   * @param telephone -> telephone de la personne de contact
   * @param email -> email de la personne de contact
   */
  private PersonneContact init(int idEntreprise, String nom, String prenom, String telephone,
      String email) {
    PersonneContact pdc = null;
    pdc = (PersonneContact) pdcFactory.creerPersonneContact();
    pdc.setIdPersonneContact(personnes.size() + 1);
    pdc.setIdEntreprise(idEntreprise);
    pdc.setNom(nom);
    pdc.setPrenom(prenom);
    pdc.setEmail(email);
    pdc.setTelephone(telephone);
    pdc.setVersion(1);
    return pdc;
  }

  /**
   * Méthode checkant si une personne de contact existe.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact contenant nom, prenom et id de
   *        l'entreprise
   * @return true -> existe
   * @return false -> n'existe pas
   */
  private boolean existe(PersonneContactDto pdcDto) {
    for (PersonneContact personneContact : personnes) {
      if (personneContact.getPrenom().equals(pdcDto.getPrenom())
          && personneContact.getNom().equals(pdcDto.getNom())
          && personneContact.getIdEntreprise() == pdcDto.getIdEntreprise()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Méthode inserant les personne de contact.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public PersonneContact insererPersonneContact(PersonneContactDto pdcDto) throws FatalException {
    try {
      if (checkInputPersonneContact(pdcDto).equals("") && !existe(pdcDto) && pdcDto.getActif()
          && entreprise.getEntrepriseSelonId(pdcDto.getIdEntreprise()) != null) {
        PersonneContact pdc = init(pdcDto.getIdEntreprise(), pdcDto.getNom(), pdcDto.getPrenom(),
            pdcDto.getTelephone(), pdcDto.getEmail());
        personnes.add(pdc);
        return pdc;
      } else {
        return null;
      }
    } catch (Exception exception) {
      throw new FatalException(exception);
    }
  }

  /**
   * Méthode listant les personne de contact.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public ArrayList<PersonneContact> getPersonnesContact() throws FatalException {
    try {
      return personnes;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }
  
  /**
   * Méthode renvoyant le numero de version.
   * 
   * @param pdcDto -> Data Transfert Object d'une personne de contact contenant le numero de version
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public int getNumVersion(PersonneContactDto pdcDto) {
    try {
      for (PersonneContact pdc : personnes) {
        if (pdc.getPrenom().equals(pdcDto.getPrenom()) && pdc.getNom().equals(pdcDto.getNom())
            && pdc.getIdEntreprise() == pdcDto.getIdEntreprise()) {
          return pdc.getVersion();
        }
      }
      return -1;
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les personnes de contact active d'une entreprises.
   * 
   * @param id -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  public ArrayList<PersonneContact> getPersonneContact(int id) throws FatalException {
    try {
      ArrayList<PersonneContact> listePdcs = new ArrayList<>();
      for (PersonneContact pdc : personnes) {
        if (pdc.getActif() && pdc.getIdEntreprise() == id) {
          listePdcs.add(pdc);
        }
      }
      if (listePdcs.size() == 0) {
        return null;
      } else {
        return listePdcs;
      }
    } catch (Exception exception) {
      throw new FatalException();
    }
  }

  /**
   * Méthode listant les personnes de contact ainsi que son entreprise.
   * 
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public Map<PersonneContactDto, String> getPersonnesContactEtEntreprise() {
    Map<PersonneContactDto, String> map = new HashMap<>();
    for (PersonneContactDto pdc : personnes) {
      map.put(pdc, pdc.getIdEntreprise() + "");
    }
    return map;
  }

  /**
   * Méthode listant les personnes de contact active d'une entreprises.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public List<PersonneContactDto> getPersonnesContactDeLEntreprise(int idEntreprise) {
    List<PersonneContactDto> listePdcs = new ArrayList<>();
    for (PersonneContactDto pdc : personnes) {
      if (pdc.getIdEntreprise() == idEntreprise && pdc.getActif()) {
        listePdcs.add(pdc);
      }
    }
    return listePdcs;
  }

  /**
   * Méthode desactivant une personne de contact.
   * 
   * @param idPersContact -> id de la personne de contact
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public PersonneContact desactiverPersContact(int idPersContact) {
    for (PersonneContactDto pdc : personnes) {
      if (pdc.getIdPersonneContact() == idPersContact) {
        pdc.setActif(false);
        return (PersonneContact) pdc;
      }
    }
    return null;
  }

  /**
   * Méthode listant les personnes de contact active et inactive d'une entreprises.
   * 
   * @param idEntreprise -> id de l'entreprise
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public List<PersonneContactDto> getPersonnesContactActifEtInactifDeLEntreprise(int idEntreprise) {
    List<PersonneContactDto> listePdcs = new ArrayList<>();
    for (PersonneContactDto pdc : personnes) {
      if (pdc.getIdEntreprise() == idEntreprise) {
        listePdcs.add(pdc);
      }
    }
    return listePdcs;
  }

  /**
   * Méthode modifiant une personne de contact.
   * 
   * @param pcD -> Data Transfert Object d'une personne de contact
   * @throws FatalException -> Exception lever si problème
   */
  @Override
  public PersonneContact modifierPersonneContact(PersonneContactDto pcD) {
    for (PersonneContact personneContact : personnes) {
      if (personneContact.getIdPersonneContact() == pcD.getIdPersonneContact()) {
        personneContact.setIdEntreprise(pcD.getIdEntreprise());
        personneContact.setNom(pcD.getNom());
        personneContact.setPrenom(pcD.getPrenom());
        personneContact.setEmail(pcD.getEmail());
        personneContact.setTelephone(pcD.getTelephone());
        personneContact.setVersion(personneContact.getVersion() + 1);
        return personneContact;
      }
    }
    return null;
  }

  @Override
  public Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(
      PersonneContactDto p) {
    return null;
  }
}

