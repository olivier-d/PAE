package bizz.domaine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

public class PersonneContactImplTest {
  private EntrepriseImpl entreprise;
  private PersonneContactImpl personneContact;

  @Before
  public void setUp() throws Exception {
    entreprise = new EntrepriseImpl();
    entreprise.setIdEntreprise(10);
    entreprise.setNomEntreprise("IPL");
    entreprise.setCodePostal("1000");
    entreprise.setCommune("Bruxelles");
    entreprise.setRue("Clos la chapelle");
    entreprise.setNumero("23");
    entreprise.setBoite("2");
    entreprise.setDateDerniereParticipation(LocalDate.now());
    entreprise.setDatePremierContact(LocalDate.now());
    entreprise.setVersion(1);
    entreprise.setCreateur(1);

    personneContact = new PersonneContactImpl();
    personneContact.setIdEntreprise(10);
    personneContact.setIdPersonneContact(1);
    personneContact.setNom("Vcp");
    personneContact.setPrenom("Antho");
    personneContact.setTelephone("0488888888");
    personneContact.setEmail("antho@hotmail.com");
    personneContact.setActif(true);
    personneContact.setVersion(1);


    entreprise.ajouterPersonneContact(personneContact);

  }

  @Test
  public void testGetIdpersonneContact() {
    assertEquals(1, personneContact.getIdPersonneContact());
  }

  @Test
  public void testSetIdpersonneContact() {
    try {
      personneContact.setIdPersonneContact(2);
      assertEquals(2, personneContact.getIdPersonneContact());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetNom() {
    try {
      personneContact.setNom("Vancampenhault");
      assertEquals("Vancampenhault", personneContact.getNom());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetPrenom() {
    assertEquals("Antho", personneContact.getPrenom());
  }

  @Test
  public void testSetPrenom() {
    try {
      personneContact.setPrenom("anthony");
      assertEquals("anthony", personneContact.getPrenom());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetTelephone() {
    assertEquals("0488888888", personneContact.getTelephone());
  }

  @Test
  public void testSetTelephone() {
    try {
      personneContact.setTelephone("0471152536");
      assertEquals("0471152536", personneContact.getTelephone());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetEmail() {
    assertEquals("antho@hotmail.com", personneContact.getEmail());
  }

  @Test
  public void testSetEmail() {
    try {
      personneContact.setEmail("a@hotmail.com");
      assertEquals("a@hotmail.com", personneContact.getEmail());
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testGetActif() {
    assertEquals(true, personneContact.getActif());
  }

  @Test
  public void testSetActif() {
    personneContact.setActif(false);
    assertEquals(false, personneContact.getActif());
  }

  @Test
  public void testGetIdEntreprise() {
    assertEquals(10, personneContact.getIdEntreprise());
  }

  @Test
  public void testSetIdEntreprise() {
    try {
      personneContact.setIdEntreprise(5);
      assertEquals(5, personneContact.getIdEntreprise());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetVersion() {
    assertEquals(1, personneContact.getVersion());
  }

  @Test
  public void testSetVersion() {
    try {
      personneContact.setVersion(4);
      assertEquals(4, personneContact.getVersion());
    } catch (Exception e) {
      fail();
    }
  }
}
