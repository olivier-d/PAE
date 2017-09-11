package dal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.Entreprise;
import bizz.dto.EntrepriseDto;

public interface EntrepriseDao {

  public Entreprise insererEntreprise(EntrepriseDto entrepriseDto);

  public ArrayList<Entreprise> getEntreprises();

  Map<EntrepriseDto, String> getEntreprisesEtCreateurs();

  public Entreprise getEntreprisesSelonId(int id);

  public Map<EntrepriseDto, String> rechercherEntreprise(EntrepriseDto entrepriseDto);

  public int getNumVersion(EntrepriseDto entrepriseDto);

  List<EntrepriseDto> getEntreprisesInvitables();

  List<EntrepriseDto> getEntreprisesInvitees();
  
  List<EntrepriseDto> getEntreprisesAvecCommentaires();
}
