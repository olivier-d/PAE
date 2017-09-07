package bizz.domaine;

import java.time.LocalDate;
import java.util.ArrayList;

import bizz.dto.PersonneContactDto;

public class EntrepriseImpl implements Entreprise {
  private int idEntreprise;
  private String codePostal;
  private int createur;
  private String nomEntreprise;
  private String rue;
  private String numero;
  private String boite;
  private String commune;
  private LocalDate datePremierContact;
  private LocalDate dateDerniereParticipation;
  private int version;
  private ArrayList<PersonneContactDto> listePersonnes;

  /**
   * Constructeur initialisant certain paramètre de la classe par defaut (date de la dernière
   * participation à null, version à 1 et la liste des personnes).
   */
  public EntrepriseImpl() {
    this.dateDerniereParticipation = null;
    this.listePersonnes = new ArrayList<PersonneContactDto>();
    this.version = 1;
  }


  @Override
  public int getIdEntreprise() {
    return idEntreprise;
  }

  @Override
  public void setIdEntreprise(int idEntreprise) {
    this.idEntreprise = idEntreprise;
  }

  @Override
  public String getNomEntreprise() {
    return nomEntreprise;
  }

  @Override
  public void setNomEntreprise(String nomEntreprise) {
    this.nomEntreprise = nomEntreprise;
  }

  @Override
  public String getRue() {
    return rue;
  }

  @Override
  public void setRue(String rue) {
    this.rue = rue;
  }

  @Override
  public String getNumero() {
    return numero;
  }

  @Override
  public void setNumero(String numero) {
    this.numero = numero;
  }

  @Override
  public String getBoite() {
    return boite;
  }

  @Override
  public void setBoite(String boite) {
    this.boite = boite;
  }

  @Override
  public String getCodePostal() {
    return codePostal;
  }

  @Override
  public void setCodePostal(String codePostal) {
    this.codePostal = codePostal;
  }

  @Override
  public String getCommune() {
    return commune;
  }

  @Override
  public void setCommune(String commune) {
    this.commune = commune;
  }

  @Override
  public LocalDate getDatePremierContact() {
    return datePremierContact;
  }

  @Override
  public void setDatePremierContact(LocalDate date) {
    this.datePremierContact = date;
  }

  @Override
  public LocalDate getDateDerniereParticipation() {
    return dateDerniereParticipation;
  }

  @Override
  public void setDateDerniereParticipation(LocalDate dateDerniereParticipation) {
    this.dateDerniereParticipation = dateDerniereParticipation;
  }

  @Override
  public int getCreateur() {
    return createur;
  }

  @Override
  public void setCreateur(int createur) {
    this.createur = createur;
  }

  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public ArrayList<PersonneContactDto> getPersonnesContact() {
    return this.listePersonnes;
  }

  @Override
  public void ajouterPersonneContact(PersonneContactDto pdcDto) {
    this.listePersonnes.add(pdcDto);
  }
}
