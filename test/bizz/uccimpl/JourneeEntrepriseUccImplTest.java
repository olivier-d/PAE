package bizz.uccimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import bizz.domaine.JourneeEntrepriseImpl;
import bizz.dto.JourneeEntrepriseDto;
import dal.mock.DalServicesImplMock;
import exception.BizzException;
import exception.FatalException;

public class JourneeEntrepriseUccImplTest {

  private static JourneeEntrepriseUccImpl journee;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    journee = (JourneeEntrepriseUccImpl) DalServicesImplMock.getOType("je");
  }

  @Test(expected = NullPointerException.class)
  public void testJourneeEntrepriseUCCImpl3() {
    new JourneeEntrepriseUccImpl(null, null);
  }

  @Test
  public void testInsererJourneeEntreprise1() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setDateJournee("29-03-2017");
    je.setIdJournee(1);
    je.setOuverte(true);
    je.setVersion(1);
    try {
      journee.insererJourneeEntreprise(je);
      fail("Une journee etait deja ouverte...");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererJourneeEntreprise2() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setDateJournee("29-03-2017");
    je.setOuverte(true);
    je.setVersion(1);
    try {
      journee.insererJourneeEntreprise(je);
      fail("Manque information.");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererJourneeEntreprise3() throws BizzException, FatalException {
    try {
      journee.insererJourneeEntreprise(null);
      fail("Null.");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererJourneeEntreprise4() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setDateJournee("29-03-2017");
    je.setIdJournee(1);
    je.setOuverte(true);
    je.setVersion(1);
    try {
      journee.insererJourneeEntreprise(je);
      assertTrue(true);
      journee.insererJourneeEntreprise(je);
      fail("ajout de 2 je");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererJourneeEntreprise5() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    try {
      journee.insererJourneeEntreprise(je);
      fail("je vide");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererJourneeEntreprise6() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(2);
    je = journee.getJourneeSelonId(je);
    assertTrue(je.isOuverte());
    je.setIdJournee(2);
    je.setDateJournee("04-04-2019");
    journee.cloturerJourneeEntreprise(je);
    assertEquals(false, je.isOuverte());
    je.setDateJournee("25-04-2019");
    journee.insererJourneeEntreprise(je);
    assertTrue(true);
  }

  @Test
  public void testInsererJourneeEntreprise7() throws BizzException, FatalException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    try {
      journee.insererJourneeEntreprise(je);
      fail("je vide");
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetJourneeEntreprise() {
    try {
      for (JourneeEntrepriseDto j : journee.getJourneeEntreprise()) {
        System.out.println("Journee du " + j.getDateJournee() + " num " + j.getIdJournee()
            + " est " + j.isOuverte());
      }
      assertEquals(4, journee.getJourneeEntreprise().size(), 0);
    } catch (Exception e) {
      fail(e + "");
    }
  }

  public void testGetJESelonId1() {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(1);
    assertNotNull(journee.getJourneeSelonId(je));
  }

  @Test
  public void testGetJESelonId2() {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(5);
    assertNull(journee.getJourneeSelonId(je));
  }

  @Test
  public void testGetNumVersion1() {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(1);
    assertEquals(1, journee.getNumVersion(je));
  }

  @Test
  public void testGetNumVersion2() {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(5);
    assertEquals(-1, journee.getNumVersion(je));
  }

  @Test(expected = FatalException.class)
  public void testGetNumVersion4() {
    journee.getNumVersion(null);
  }

  @Test
  public void testCloturerJourneeEntreprise2() throws FatalException, BizzException {
    try {
      JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
      je.setIdJournee(1);
      journee.cloturerJourneeEntreprise(je);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test(expected = NullPointerException.class)
  public void testCloturerJourneeEntreprise3() throws FatalException, BizzException {
    journee.cloturerJourneeEntreprise(null);
  }

  @Test(expected = NullPointerException.class)
  public void testCloturerJourneeEntreprise4() throws FatalException, BizzException {
    JourneeEntrepriseDto je = new JourneeEntrepriseImpl();
    je.setIdJournee(8);
    je = journee.getJourneeSelonId(je);
    je = journee.cloturerJourneeEntreprise(je);
    assertEquals(false, je.isOuverte());
  }

  @Test
  public void testIsJourneeActive1() {
    assertTrue(journee.isJourneeActive());
  }

}
