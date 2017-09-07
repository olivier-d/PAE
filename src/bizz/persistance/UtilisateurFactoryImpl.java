package bizz.persistance;

import bizz.domaine.UtilisateurImpl;
import bizz.dto.UtilisateurDto;

public class UtilisateurFactoryImpl implements UtilisateurFactory {

  @Override
  public UtilisateurDto createUtilisateur() {
    return new UtilisateurImpl();
  }

}
