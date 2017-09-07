package bizz.domaine;

import bizz.dto.UtilisateurDto;

public interface Utilisateur extends UtilisateurDto {
  // Methodes business
  public boolean verifierPassword(String password);
}
