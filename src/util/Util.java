package util;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bizz.domaine.ParticipationImpl.Etat;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.PersonneContactDto;
import bizz.dto.UtilisateurDto;

public interface Util {
  /**
   * Méthode checkant si l'objet est null.
   * 
   * @param obj -> object a tester
   */
  static void checkObject(Object obj) {
    if (obj == null) {
      throw new NullPointerException("L'objet ne peut pas etre null");
    }
  }

  /**
   * Méthode checkant que le nom passé en paramètre n'est pas vide et respecte un certain masque.
   * 
   * @param nom -> nom a tester
   * @throws NullPointerException, IllegalArgumentException
   */
  static void checkNom(String nom) {
    if (nom.matches("\\s*") || nom.matches("")) {
      throw new NullPointerException("Chaine vide ");
    }
    if (!nom.matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      throw new IllegalArgumentException("Format de nom incorrect");
    }
  }

  /**
   * Méthode checkant que la rue passée en paramètre n'est pas vide et respecte un certain masque.
   * 
   * @param rue -> rue a tester
   * @throws NullPointerException, IllegalArgumentException
   */
  static void checkRue(String rue) {
    if (rue.matches("\\s*") || rue.matches("")) {
      throw new NullPointerException("Chaine vide");
    }
    if (!rue.matches("((?i)[a-zâäàéèùêëîïôöçñ]['-_\\s]?)+")) {
      throw new IllegalArgumentException("Format de rue incorrect");
    }
  }

  /**
   * Méthode checkant que le mail passé en paramètre n'est pas vide et respecte un certain masque.
   * 
   * @param mail -> mail a tester
   * @throws NullPointerException, IllegalArgumentException
   */
  static void checkEmail(String mail) {
    if (mail.matches("\\s*") || mail.matches("")) {
      throw new NullPointerException("Chaine vide");
    }
    if (!mail.matches("[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+")) {
      throw new IllegalArgumentException("Format d'email incorrect");
    }
  }

  /**
   * Méthode checkant que le mot de passe passé en paramètre n'est pas vide et respecte un certain
   * masque.
   * 
   * @param mdp -> mot de passe a tester
   * @throws NullPointerException, IllegalArgumentException
   */
  static void checkNouveauMdp(String mdp) {
    if (mdp.matches("\\s*") || mdp.matches("")) {
      throw new NullPointerException("Chaine vide");
    }
    if (!mdp.matches(".{4,12}")) {
      throw new IllegalArgumentException("- Mot de passe (min 4 à 12 caractères)");
    }
  }

  /**
   * Méthode checkant que le code postal passé en paramètre n'est pas vide et respecte un certain
   * masque.
   * 
   * @param codePostal -> code postal a tester
   * @throws NullPointerException, IllegalArgumentException
   */
  static void checkCodePostal(String codePostal) {
    if (codePostal.matches("\\s*") || codePostal.matches("")) {
      throw new NullPointerException("Chaine vide");
    }
    if (!codePostal.matches("(\\w[-_\\s]?)+")) {
      throw new IllegalArgumentException("Mauvais code postal");
    }
  }

  /**
   * Méthode checkant que la commune passée en paramètre n'est pas vide et respecte un certain
   * masque.
   * 
   * @param commune -> commune a tester
   * @throws NullPointerException -> Chaine vide
   * @throws IllegalArgumentException -> Mauvaise commune
   */
  static void checkCommune(String commune) {
    if (commune.matches("\\s*") || commune.matches("")) {
      throw new NullPointerException("Chaine vide");
    }
    if (!commune.matches("(?i)[a-zâäàéèùêëîïôöçñ]{2,}((?i)[a-zâäàéèùêëîïôöçñ]?[-_\\s]?)*")) {
      throw new IllegalArgumentException("Mauvaise commune");
    }
  }

  /**
   * Méthode checkant que le numéro de téléphone passé en paramètre n'est pas vide et respecte un
   * certain masque.
   * 
   * @param tel -> tel a tester
   * @throws IllegalArgumentException -> Mauvais telephone
   */
  static void checkTel(String tel) {
    if (!tel.matches("[0-9]{1,4}[/\\s]?([0-9]{2,}[/\\s]?)*")) {
      throw new IllegalArgumentException("Mauvais telephone");
    }
  }

  /**
   * Méthode checkant qu'un string donné n'est pas vide.
   * 
   * @param str -> chaine de caractere a tester
   * @throws IllegalArgumentException -> Chaine vide
   */
  static void checkString(String str) {
    if (str.matches("\\s*") || str.matches("")) {
      throw new IllegalArgumentException("La chaine ne peut pas etre vide");
    }
  }

  /**
   * Méthode checkant qu'un nombre sous forme de string peut être cast en nombre.
   * 
   * @param nombre -> nombre a tester
   * @throws IllegalArgumentException -> mauvais parametre
   */
  static void checkNumerique(String nombre) {
    checkString(nombre);
    try {
      Long.parseLong(nombre);
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("La chaine doit etre un nombre valide");
    }
  }

  /**
   * Méthode checkant qu'un nombre est supérieur ou égal à zéro.
   * 
   * @param nombre -> nombre a tester
   * @throws IllegalArgumentException -> mauvais parametre
   */
  static void checkPositiveOrZero(double nombre) {
    if (nombre < 0) {
      throw new IllegalArgumentException("La valeur ne peut pas etre negative");
    }
  }

  /**
   * Méthode checkant qu'un nombre est strictement supérieur à zéro.
   * 
   * @param nombre -> nombre a tester
   * @throws IllegalArgumentException -> mauvais parametre
   */
  static void checkPositive(double nombre) {
    if (nombre <= 0) {
      throw new IllegalArgumentException("La valeur ne peut pas etre negative ou nulle");
    }
  }

  /**
   * Méthode checkant qu'un état n'est pas null et représente bien un état existant.
   * 
   * @param etat -> etat a tester
   * @throws IllegalArgumentException -> mauvais parametre
   */
  static void checkEtat(Etat etat) {
    if (etat == null || (!etat.equals(Etat.CONFIRMEE) && !etat.equals(Etat.FACTUREE)
        && !etat.equals(Etat.INVITEE) && !etat.equals(Etat.PAYEE) && !etat.equals(Etat.REFUSEE))) {
      throw new IllegalArgumentException("Ceci est pas un état existant");
    }
  }

  /**
   * Methode vérifiant les informations remplies dans le DTO d'une entreprise.
   * 
   * @param entreprise | entrepriseDto -> entreprise a tester
   * @return String | entreprise sous format JSON
   */
  static String checkInputEntreprise(EntrepriseDto entreprise) {
    Map<String, String> inputErreurs = new LinkedHashMap<String, String>();
    if (entreprise.getNomEntreprise() == null
        || !entreprise.getNomEntreprise().matches("((?i)[a-zâäàéèùêëîïôöçñ]['-_\\s]?)+")) {
      inputErreurs.put("nomEntreprise", "- Nom entreprise ");
    }
    if (entreprise.getRue() == null
        || !entreprise.getRue().matches("((?i)[a-zâäàéèùêëîïôöçñ]['-_\\s]?)+")) {
      inputErreurs.put("rue", "- Rue ");
    }
    if (entreprise.getNumero() == null || !entreprise.getNumero().matches("\\w+")) {
      inputErreurs.put("numero", "- Numero ");
    }
    if (!"".equals(entreprise.getBoite())) {
      if (!entreprise.getBoite().matches("\\w+")) {
        inputErreurs.put("boite", "- Boîte ");
      }
    }
    if (entreprise.getCodePostal() == null
        || !entreprise.getCodePostal().matches("(\\w[-_\\s]?)+")) {
      inputErreurs.put("codePostal", "- Code postal ");
    }
    if (entreprise.getCommune() == null || !entreprise.getCommune()
        .matches("(?i)[a-zâäàéèùêëîïôöçñ]{2,}((?i)[a-zâäàéèùêëîïôöçñ]?[-_\\s]?)*")) {
      inputErreurs.put("commune", "- Commune");
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Methode vérifiant les informations remplies dans le DTO d'une personne de contact.
   * 
   * @param pdc | personneContactDto -> personne de contact a tester
   * @return String | personne de contact sous format JSON
   */
  static String checkInputPersonneContact(PersonneContactDto pdc) {
    Map<String, String> inputErreurs = new LinkedHashMap<String, String>();
    if (pdc.getNom() == null || !pdc.getNom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("nom", "- Nom ");
    }
    if (pdc.getPrenom() == null || !pdc.getPrenom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("prenom", "- Prénom ");
    }
    if ("".equals(pdc.getEmail()) && "".equals(pdc.getTelephone())) {
      inputErreurs.put("email", "- Indiquer un email et/ou un téléphone");
    } else {
      if (!"".equals(pdc.getTelephone())
          && !pdc.getTelephone().matches("[0-9]{1,4}[/\\s]?([0-9]{2,}[/\\s]?)*")) {
        inputErreurs.put("telephone", "- Téléphone ");
      }
      if (!"".equals(pdc.getEmail())
          && !pdc.getEmail().matches("[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+")) {
        inputErreurs.put("email", "- Email ");
      }
    }
    if (pdc.getIdEntreprise() <= 0) {
      inputErreurs.put("idEntreprise", "- Entreprise incorrecte, veuillez en enregistrer une ");
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Methode vérifiant les informations remplies dans le DTO d'une personne de contact.
   * 
   * @param pdc | personneContactDto -> nouvelles information de la personne de contact
   * @return String | personne de contact sous format JSON
   */
  static String checkInputModifPersonneContact(PersonneContactDto pdc) {
    Map<String, String> inputErreurs = new LinkedHashMap<String, String>();
    if (pdc.getNom() == null || !pdc.getNom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("nom", "- Nom ");
    }
    if (pdc.getPrenom() == null || !pdc.getPrenom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("prenom", "- Prénom ");
    }
    if ("".equals(pdc.getEmail()) && "".equals(pdc.getTelephone())) {
      inputErreurs.put("email", "- Indiquer un email et/ou un téléphone");
    } else {
      if (!"".equals(pdc.getTelephone())
          && !pdc.getTelephone().matches("[0-9]{1,4}[/\\s]?([0-9]{2,}[/\\s]?)*")) {
        inputErreurs.put("telephone", "- Téléphone ");
      }
      if (!"".equals(pdc.getEmail())
          && !pdc.getEmail().matches("[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+")) {
        inputErreurs.put("email", "- Email ");
      }
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Methode vérifiant les informations remplies dans le DTO d'un utilisateur.
   * 
   * @param utilisateur | utilisateurDto -> utilisateur a tester
   * @return String | utilisateur sous format JSON.
   */
  static String checkInputInscription(UtilisateurDto utilisateur) {
    Map<String, String> inputErreurs = new HashMap<String, String>();
    if (!utilisateur.getNom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("nom", "- Nom ");
    }
    if (!utilisateur.getPrenom().matches("((?i)[a-zâäàéèùêëîïôöçñ][-\\s]?)+")) {
      inputErreurs.put("prenom", "- Prenom ");
    }
    if (!utilisateur.getEmail().matches("[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+")) {
      inputErreurs.put("mail", "- Mail ");
    }
    if (!utilisateur.getPseudo().matches("\\w+")) {
      inputErreurs.put("login", "- Pseudo ");
    }
    if (!utilisateur.getMdp().matches(".{4,12}")) {
      inputErreurs.put("mdpEntree", "- Mot de passe (min 4 à 12 caractères) ");
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Méthode checkant le format du mot de passe d'un utilisateur.
   * 
   * @param utilisateur -> utilisateur a tester
   */
  static String checkMdp(UtilisateurDto utilisateur) {
    Map<String, String> inputErreurs = new HashMap<String, String>();
    if (!utilisateur.getPseudo().matches("\\w+") || !utilisateur.getMdp().matches(".{4,12}")) {
      inputErreurs.put("login",
          "Login et/ou mot de passe impossible, mot de passe de minimum 4 caractères");
      inputErreurs.put("mdp", "");
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Méthode vérifiant qu'un second mot de passe est différent du premier.
   * 
   * @param utilisateur -> utilisateur a tester
   */
  static String checkChangementMdp(UtilisateurDto utilisateur) {
    Map<String, String> inputErreurs = new HashMap<String, String>();
    if (!utilisateur.getMdp().matches(".{4,12}")) {
      inputErreurs.put("nouveauMdp",
          "Mot de passe impossible, mot de passe de minimum 4 caractères");
      inputErreurs.put("nouveauMdpConfirmation", "");
    }
    return mapToJson(inputErreurs);
  }

  /**
   * Méthode checkant le format de la date d'une JE.
   * 
   * @param je -> journée a tester
   */
  static String checkInputJe(JourneeEntrepriseDto je) {
    if ((!je.getDateJournee().matches("[0-2]?[0-9]-[0]?[1-9]-[2][0][0-9][0-9]")
        && !je.getDateJournee().matches("[0-2]?[0-9]-[1][0-2]-[2][0][0-9][0-9]")
        && !je.getDateJournee().matches("[3][0-1]-[0]?[1-9]-[2][0][0-9][0-9]")
        && !je.getDateJournee().matches("[3][0-1]-[1][0-2]-[2][0][0-9][0-9]"))
        || je.getDateJournee() == null) {
      return "{\"dateje\":\"- Mauvais format, ex: 02-03-2017 \"}";
    }
    return "";
  }

  /**
   * Méthode tranformant une map en un String au format JSON.
   * 
   * @param inputErreurs -> input d'erreur a tester
   */
  static String mapToJson(Map<String, String> inputErreurs) {
    if (inputErreurs.isEmpty()) {
      return "";
    }
    String jsonMap = "";
    try {
      jsonMap = new ObjectMapper().writeValueAsString(inputErreurs);
    } catch (JsonGenerationException exception) {
      exception.printStackTrace();
    } catch (JsonMappingException exception) {
      exception.printStackTrace();
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    System.out.println(jsonMap);
    return jsonMap;
  }

}
