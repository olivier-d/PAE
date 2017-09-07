package ihm.main;

import static util.Util.checkString;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import exception.FatalException;

public class Configuration {

  public static final String DEFAULT_EVIRONNEMENT = "prod.properties";
  private static Properties proprietes = new Properties();

  /* OUVERTURE DU FICHIER */

  /**
   * Constructeur de la classe.
   * 
   * @param fileName
   * @exception FileNotFoundException
   * @exception IOException
   */
  public Configuration(String fileName) {
    // Verification fichier valide
    if (fileName == null || fileName.equals("")) {
      fileName = DEFAULT_EVIRONNEMENT;
    }

    System.out.println("\n******************");
    System.out.println("Chargement fichier " + fileName);
    System.out.println("******************\n");

    FileInputStream fichier = null;

    try {
      fichier = new FileInputStream(fileName);
      proprietes.load(fichier);
    } catch (FileNotFoundException err) {
      throw new InternalError("Fichier inconnu");
    } catch (IOException err) {
      throw new InternalError("Impossible d'ouvrir le fichier");
    } finally {
      if (fichier != null) {
        try {
          fichier.close();
        } catch (IOException err) {
          throw new InternalError("Erreur lors de la fermeture du fichier");
        }

      }
    }
  }

  /**
   * Va chercher la propriete dans le fichier properties qui correspond à la clef
   * 
   * @param clef
   * @return la propriete correspondant a la clef
   */
  public String getConfiguration(String clef) {
    checkString(clef);
    return proprietes.getProperty(clef);
  }

  /**
   * Méthode d'injection de la configuration.
   * 
   * @param interfaceName
   * @return
   * @throws FatalException
   */
  public Constructor<?> inject(String interfaceName) throws FatalException {
    String className = getConfiguration(interfaceName);

    Constructor<?> constructor = null;

    try {
      constructor = Class.forName(className).getDeclaredConstructors()[0];
    } catch (Exception exception) {
      throw new FatalException(exception.getMessage());
    }

    return constructor;
  }
}
