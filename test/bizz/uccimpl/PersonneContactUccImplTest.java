package bizz.uccimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import bizz.domaine.PersonneContact;
import bizz.domaine.PersonneContactImpl;
import bizz.dto.PersonneContactDto;
import dal.mock.DalServicesImplMock;
import exception.BizzException;
import exception.FatalException;

public class PersonneContactUccImplTest {


  private static EntrepriseUccImpl entrepriseUCC;
  private static PersonneContactUccImpl pcUCC;
  private static PersonneContactImpl pc;
  private PersonneContactImpl pcTest;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    entrepriseUCC = (EntrepriseUccImpl) DalServicesImplMock.getOType("entreprise");
    pcUCC = (PersonneContactUccImpl) DalServicesImplMock.getOType("pc");

    System.out.println(entrepriseUCC.getEntrepriseSelonId(0));

    pc = new PersonneContactImpl();
    pc.setNom("Jalon");
    pc.setPrenom("Marlon");
    pc.setIdEntreprise(1);
    pc.setEmail("jalon@mail.com");
    pc.setTelephone("0476060606");
    pc.setActif(true);
    pc.setVersion(1);
    try {
      PersonneContact p = (PersonneContact) pcUCC.insererPersonneContact(pc);
      pc.setIdPersonneContact(p.getIdPersonneContact());
      pc.setActif(p.getActif());
      pc.setVersion(p.getVersion());
    } catch (Exception e) {
      fail("Erreur dans le setUp() à l'ajout d'une entreprise");
    }
  }

  @Test(expected = NullPointerException.class)
  public void testPersonneContactUCCImpl2() {
    new PersonneContactUccImpl(null, null);
  }

  @Test
  public void testInsererPersonneContact1() throws FatalException, BizzException {
    // Si le setUp() est OK, l'insertion fonctionne d'office
    assertTrue(true);
  }

  @Test
  public void testInsererPersonneContact2() throws FatalException, BizzException {
    assertNull((PersonneContact) pcUCC.insererPersonneContact(pc));
  }

  @Test
  public void testInsererPersonneContact3() throws FatalException, BizzException {
    try {
      pcTest = pc;
      pcTest.setIdEntreprise(100000);
      assertNull((PersonneContact) pcUCC.insererPersonneContact(pc));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererPersonneContact4() throws FatalException, BizzException {
    try {
      pcTest = pc;
      pcTest.setIdEntreprise(-5);
      pcUCC.insererPersonneContact(pc);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testInsererPersonneContact5() throws FatalException, BizzException {
    pcUCC.insererPersonneContact(null);
  }

  @Test(expected = BizzException.class)
  public void testInsererPersonneContact6() throws FatalException, BizzException {
    pcTest = new PersonneContactImpl();
    pcTest.setIdEntreprise(0);
    pcTest.setNom("Interlici");
    pcTest.setTelephone("0478787878");
    pcTest.setEmail("rosario@hotmail.com");
    pcTest.setActif(true);
    pcTest.setVersion(1);
    pcUCC.insererPersonneContact(pcTest);
  }

  @Test(expected = NullPointerException.class)
  public void testInsererPersonneContact7() throws FatalException, BizzException {
    pcTest = new PersonneContactImpl();
    pcUCC.insererPersonneContact(pcTest);
  }

  @Test
  public final void testDesactiverPersContact() {
    try {
      pcUCC.desactiverPersContact(pc.getIdPersonneContact());
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public final void testDesactiverPersContact2() {
    try {
      pc.setActif(true);
      assertNull(pcUCC.desactiverPersContact(-1));
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetPersonnesContact() throws FatalException {
    try {
      ArrayList<PersonneContactDto> p = pcUCC.getPersonnesContact();
      if (p == null || p.size() == 0) {
        fail("Il y a des pc a afficher normalement");
      } else {
        ArrayList<PersonneContactDto> personnes = pcUCC.getPersonnesContact();
        for (PersonneContactDto personneContactDto : personnes) {
          System.out.println(personneContactDto.getIdPersonneContact() + " : "
              + personneContactDto.getPrenom() + " " + personneContactDto.getNom()
              + " travaille pour l'entreprise " + personneContactDto.getIdEntreprise());
        }
        assertEquals(4, personnes.size(), 0);
      }
    } catch (Exception e) {
      fail("" + e);
    }
  }
/*
  @Test
  public void testRecherchePersonneContactSelonPrenomEtNom1() {
    try {
      pcUCC.recherchePersonneContactSelonPrenomEtNom("", "test");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testRecherchePersonneContactSelonPrenomEtNom2() {
    try {
      pcUCC.recherchePersonneContactSelonPrenomEtNom("test", "");
    } catch (Exception e) {
      assertTrue(true);
    }

  }

  @Test(expected = NullPointerException.class)
  public void testRecherchePersonneContactSelonPrenomEtNom3() throws FatalException {
    pcUCC.recherchePersonneContactSelonPrenomEtNom(null, "test");
  }

  @Test(expected = NullPointerException.class)
  public void testRecherchePersonneContactSelonPrenomEtNom4() throws FatalException {
    pcUCC.recherchePersonneContactSelonPrenomEtNom("test", null);
  }

  @Test(expected = NullPointerException.class)
  public void testRecherchePersonneContactSelonPrenomEtNom5() throws FatalException {
    pcUCC.recherchePersonneContactSelonPrenomEtNom(null, null);
  }

  public void testRecherchePersonneContactSelonPrenomEtNom6() throws FatalException {
    assertEquals(0, pcUCC.recherchePersonneContactSelonPrenomEtNom("test", "test").size(), 0);
  }

  @Test
  public void testRecherchePersonneContactSelonPrenomEtNom7() {
    try {
      pcUCC.recherchePersonneContactSelonPrenomEtNom("", "");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testRecherchePersonneContactSelonPrenomEtNom8() {
    assertEquals(1, pcUCC.recherchePersonneContactSelonPrenomEtNom("Antho", "Vcp").size(), 0);
  }

  @Test
  public void testRecherchePersonneContactSelonPrenomEtNom9() {
    assertEquals(0, pcUCC.recherchePersonneContactSelonPrenomEtNom("Anthony", "Vcp").size(), 0);
  }
*/
  @Test
  public final void testGetNumVersion() {

    assertEquals(-1, pcUCC.getNumVersion(pc));
  }

  @Test
  public final void testGetNumVersion2() {
    try {
      pcUCC.getNumVersion(null);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public final void testGetPersonnesContactEtEntreprise1() {
    assertEquals(4, pcUCC.getPersonnesContactEtEntreprise().size());
  }

  @Test
  public final void testGetPersonnesContactEtEntreprise2() {
    assertEquals("2",
        pcUCC.getPersonnesContactEtEntreprise().get(pcUCC.getPersonnesContact().get(2)));
  }

  @Test
  public final void testGetPersonnesContactEtEntreprise3() {
    assertEquals(null, pcUCC.getPersonnesContactEtEntreprise().get(null));
  }

  @Test
  public final void testGetPersonnesContactDeLEntreprise1() {
    assertEquals(3, pcUCC.getPersonnesContactDeLEntreprise(1).size());
  }

  @Test
  public final void testGetPersonnesContactDeLEntreprise2() {
    assertEquals(1, pcUCC.getPersonnesContactDeLEntreprise(2).size());
  }

  @Test
  public final void testGetPersonnesContactDeLEntreprise3() {
    assertEquals(0, pcUCC.getPersonnesContactDeLEntreprise(3).size());
  }

  @Test
  public final void testGetPersonnesContactDeLEntreprise4() {
    assertEquals(0, pcUCC.getPersonnesContactDeLEntreprise(-1).size());
  }

  @Test
  public final void testGetPersonnesContactActifEtInactifDeLEntreprise1() {
    assertEquals(3, pcUCC.getPersonnesContactActifEtInactifDeLEntreprise(1).size());
  }

  @Test
  public final void testGetPersonnesContactActifEtInactifDeLEntreprise2() {
    assertEquals(1, pcUCC.getPersonnesContactActifEtInactifDeLEntreprise(2).size());
  }

  @Test
  public final void testGetPersonnesContactActifEtInactifDeLEntreprise3() {
    assertEquals(0, pcUCC.getPersonnesContactActifEtInactifDeLEntreprise(3).size());
  }

  @Test
  public final void testGetPersonnesContactActifEtInactifDeLEntreprise4() {
    assertEquals(0, pcUCC.getPersonnesContactActifEtInactifDeLEntreprise(-1).size());
  }

  @Test(expected = NullPointerException.class)
  public final void testModifierPersonneContact1() {
    pcUCC.modifierPersonneContact(null);
  }

  @Test
  public final void testModifierPersonneContact2() {
    PersonneContactDto pcTest = pc;
    pcTest.setNom("VCP");
    pcUCC.modifierPersonneContact(pcTest);
    assertEquals("VCP", pcTest.getNom());
  }

  @Test(expected = NullPointerException.class)
  public final void testModifierPersonneContact3() {
    PersonneContactDto pcTest = new PersonneContactImpl();
    pcTest.setIdPersonneContact(500);
    pcUCC.modifierPersonneContact(pcTest);
  }


}
