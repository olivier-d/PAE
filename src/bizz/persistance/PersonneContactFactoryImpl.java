package bizz.persistance;

import bizz.domaine.PersonneContactImpl;
import bizz.dto.PersonneContactDto;

public class PersonneContactFactoryImpl implements PersonneContactFactory {

  @Override
  public PersonneContactDto creerPersonneContact() {
    return new PersonneContactImpl();
  }



}
