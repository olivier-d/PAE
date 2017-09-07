package bizz.ucc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.dto.PersonneContactDto;

public interface PersonneContactUcc {

  PersonneContactDto insererPersonneContact(PersonneContactDto pdcDto);
  
  PersonneContactDto modifierPersonneContact(PersonneContactDto pdcDto);
  
  PersonneContactDto desactiverPersContact(int idPersContact);

  ArrayList<PersonneContactDto> getPersonnesContact();

  int getNumVersion(PersonneContactDto pdcDto);
  
  Map<PersonneContactDto, String> getPersonnesContactEtEntreprise();
  
  Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(PersonneContactDto p);
  
  List<PersonneContactDto> getPersonnesContactDeLEntreprise(int idEntreprise);
  
  List<PersonneContactDto> getPersonnesContactActifEtInactifDeLEntreprise(int idEntreprise);
  
}