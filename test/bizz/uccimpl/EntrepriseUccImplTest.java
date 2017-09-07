package bizz.uccimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import bizz.domaine.Entreprise;
import bizz.domaine.EntrepriseImpl;
import bizz.dto.EntrepriseDto;
import dal.mock.DalServicesImplMock;

public class EntrepriseUccImplTest {

  private EntrepriseUccImpl entreprise;
  private EntrepriseImpl company, testCompany;

  @Before
  public void setUp() throws Exception {

    entreprise = (EntrepriseUccImpl) DalServicesImplMock.getOType("entreprise");

    company = new EntrepriseImpl();
    company.setNomEntreprise("Ecole");
    company.setRue("Rue Scolaire");
    company.setNumero("142");
    company.setBoite("2");
    company.setCodePostal("1200");
    company.setCommune("WSL");
    company.setCreateur(2);

    try {
      Entreprise e = (Entreprise) entreprise.insererEntreprise(company);
      company.setIdEntreprise(e.getIdEntreprise());
      company.setDatePremierContact(e.getDatePremierContact());
      company.setDateDerniereParticipation(e.getDateDerniereParticipation());
      company.setVersion(e.getVersion());
    } catch (Exception e) {
      fail("Erreur dans le setUp() à l'ajout d'une entreprise" + e);
    }
  }

  @Test
  public void testInsererEntreprise() {
    // Si le setUp() est OK l'insertion fonctionne d'office
    assertTrue(true);
  }

  @Test
  public void testInsererEntreprise2() {
    try {
      testCompany = new EntrepriseImpl();
      testCompany.setRue("Blv Triomphal");
      testCompany.setNumero("13");
      testCompany.setBoite("0000");
      testCompany.setCodePostal("1000");
      testCompany.setCommune("Bruxelles");
      testCompany.setCreateur(0);
      Entreprise ent = entreprise.insererEntreprise(testCompany);
      if (ent == null) {
        assertTrue(true);
      } else {
        fail("Ajout raté");
      }
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererEntreprise3() {
    try {
      testCompany = new EntrepriseImpl();
      testCompany.setNomEntreprise("Bruxity");
      testCompany.setRue("Blv Triomphal");
      testCompany.setNumero("13");
      testCompany.setBoite("0000");
      testCompany.setCodePostal("1000");
      testCompany.setCommune("");
      testCompany.setCreateur(0);
      Entreprise ent = entreprise.insererEntreprise(testCompany);
      if (ent == null) {
        assertTrue(true);
      } else {
        fail("Ajout raté");
      }
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererEntreprise4() {
    try {
      Entreprise ent = entreprise.insererEntreprise(null);
      if (ent == null) {
        assertTrue(true);
      } else {
        fail("Ajout raté");
      }
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testInsererEntreprise5() {
    try {
      Entreprise ent = entreprise.insererEntreprise(new EntrepriseImpl());
      if (ent == null) {
        assertTrue(true);
      } else {
        fail("Ajout raté");
      }
    } catch (Exception e) {
      assertTrue(true);
    }
  }


  @Test
  public void testGetEntreprises() {
    try {
      ArrayList<EntrepriseDto> es = entreprise.getEntreprises();
      assertNotNull(es);
      for (EntrepriseDto entrepriseDto : es) {
        System.out
            .println(entrepriseDto.getNomEntreprise() + " - " + entrepriseDto.getIdEntreprise());
      }
      assertEquals(20, es.size());
    } catch (Exception e) {
      fail("" + e);
    }
  }

  @Test
  public final void testGetEntreprisesEtCreateurs1() {
    EntrepriseDto e = entreprise.getEntrepriseSelonId(1);
    assertEquals("1", entreprise.getEntreprisesEtCreateurs().get(e));
  }

  @Test
  public final void testGetEntreprisesEtCreateurs2() {
    assertEquals(null, entreprise.getEntreprisesEtCreateurs().get(null));
  }

  @Test
  public final void testGetEntreprisesEtCreateurs3() {
    assertEquals(7, entreprise.getEntreprisesEtCreateurs().size());
  }

  @Test
  public void testGetEntrepriseSelonId() {
    try {
      EntrepriseDto e = entreprise.getEntrepriseSelonId(1);
      assertEquals("IPL", e.getNomEntreprise());
    } catch (Exception e) {
      fail("" + e);
    }
  }

  @Test
  public void testGetEntrepriseSelonId2() {
    try {
      EntrepriseDto entrepriseDto = entreprise.getEntrepriseSelonId(-3);
      assertNull(entrepriseDto);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetEntrepriseSelonId3() {
    try {
      EntrepriseDto e = entreprise.getEntrepriseSelonId(78);
      assertNull(e);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public final void testRechercherEntreprise() {
    assertEquals(15, entreprise.rechercherEntreprise(company).size());
  }

  @Test
  public void testGetNumVersion() {
    try {
      System.out.println("Version " + company.getVersion());
      int num = entreprise.getNumVersion(company);
      assertEquals(1, num);
    } catch (Exception e) {
      fail();
    }
  }


  @Test
  public void testGetNumVersion2() {
    try {
      entreprise.getNumVersion(null);
      fail();
    } catch (Exception e) {
      assertTrue(true);
    }
  }


  @Test
  public void testGetNumVersion3() {
    try {
      int num = entreprise.getNumVersion(new EntrepriseImpl());
      if (num != -1) {
        fail();
      } else {
        assertTrue(true);
      }
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public void testGetNumVersion4() {
    try {
      testCompany = new EntrepriseImpl();
      testCompany.setNomEntreprise("Bruxity");
      testCompany.setRue("Blv Triomphal");
      testCompany.setNumero("13");
      testCompany.setBoite("0000");
      testCompany.setCodePostal("1000");
      testCompany.setCommune("");
      testCompany.setCreateur(0);
      int e = entreprise.getNumVersion(testCompany);
      assertEquals(-1, e);
    } catch (Exception e) {
      assertTrue(true);
    }
  }

  @Test
  public final void testGetEntreprisesInvitables() {
    assertEquals(19, entreprise.getEntreprisesInvitables().size());
  }

  @Test
  public final void testGetEntreprisesInvitees() {
    assertEquals(4, entreprise.getEntreprisesInvitees().size());
  }

}
