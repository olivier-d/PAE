package dal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.PersonneContact;
import bizz.dto.PersonneContactDto;

public interface PersonneContactDao {

  PersonneContact insererPersonneContact(PersonneContactDto pcD);
  
  PersonneContact modifierPersonneContact(PersonneContactDto pcD);
  
  PersonneContact desactiverPersContact(int idPersContact);
 
  ArrayList<PersonneContact> getPersonnesContact();

  int getNumVersion(PersonneContactDto pcD);
  
  Map<PersonneContactDto, String> getPersonnesContactEtEntreprise();
  
  Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(PersonneContactDto p);
  
  List<PersonneContactDto> getPersonnesContactDeLEntreprise(int idEntreprise);
  
  List<PersonneContactDto> getPersonnesContactActifEtInactifDeLEntreprise(int idEntreprise);
}

