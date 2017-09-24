package dal.dao;


import bizz.domaine.Utilisateur;
import bizz.dto.UtilisateurDto;

// Transforme demande en SQL et resultSet en objets
public interface UtilisateurDao {
  public Utilisateur utilisateurExiste(UtilisateurDto utilisateurDto);

  public Utilisateur verifierDoublonPseudo(UtilisateurDto utilisateurDto);

  public Utilisateur insererUtilisateur(UtilisateurDto utilisateurDto);

  public Utilisateur changerMotDePasse(UtilisateurDto utilisateurDto);

  public Utilisateur getUtilisateurByPseudo(UtilisateurDto utilisateurDto);

  public int getNumVersion(UtilisateurDto utilisateurDto);

  public Utilisateur verifierUtilisateur(UtilisateurDto utilisateurDto);
    
  Utilisateur isEmailExiste(UtilisateurDto utilisateurDto);
  
  Utilisateur insererUserEmail(UtilisateurDto utilisateurDto);
}
