package dal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bizz.domaine.Participation;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;

public interface ParticipationDao {

  List<Integer> insererParticipation(List<EntrepriseDto> listeEntreprises);

  ArrayList<?> getParticipations(int journee);

  List<Integer> insererPersonneContactParticipation(List<PersonneContactDto> listePdcs,
      int idParticipation);

  Map<ParticipationDto, String> getParticipationsEntreprises(int idJournee);

  Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(int idJournee);

  Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJePers(
      int idEntreprise);

  Participation annulerParticipation(ParticipationDto participationDto);

  Participation updateEtatParticipation(ParticipationDto participationDto);

  Participation updateDateDerniereParticipation(ParticipationDto participationDto);

  List<PersonneContactDto> getIdPersonnesInviteesPourUneParticipation(int idParticipation);

  Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees();
  
  String setCommentaire(int version, int idParticipation, String commentaire);


}
