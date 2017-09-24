package ihm;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.servlet.DefaultServlet;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.owlike.genson.GenericType;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import bizz.domaine.EntrepriseImpl;
import bizz.domaine.JourneeEntrepriseImpl;
import bizz.domaine.ParticipationImpl;
import bizz.domaine.ParticipationImpl.Etat;
import bizz.domaine.PersonneContactImpl;
import bizz.dto.EntrepriseDto;
import bizz.dto.JourneeEntrepriseDto;
import bizz.dto.ParticipationDto;
import bizz.dto.PersonneContactDto;
import bizz.dto.UtilisateurDto;
import bizz.persistance.EntrepriseFactory;
import bizz.persistance.JourneeEntrepriseFactory;
import bizz.persistance.ParticipationFactory;
import bizz.persistance.PersonneContactFactory;
import bizz.persistance.UtilisateurFactory;
import bizz.ucc.EntrepriseUcc;
import bizz.ucc.JourneeEntrepriseUcc;
import bizz.ucc.ParticipationUcc;
import bizz.ucc.PersonneContactUcc;
import bizz.ucc.UtilisateurUcc;
import exception.BizzException;
import exception.FatalException;

public class MaServlet extends DefaultServlet {

  private static final long serialVersionUID = 1L;

  private String secret;

  private UtilisateurUcc utilisateurUcc;
  private UtilisateurFactory utilisateurFactory;

  private EntrepriseUcc entrepriseUcc;
  private EntrepriseFactory entrepriseFactory;

  private JourneeEntrepriseUcc jeUcc;
  private JourneeEntrepriseFactory jeFactory;

  private PersonneContactUcc personneContactUcc;
  private PersonneContactFactory personneContactFactory;

  private ParticipationUcc participationUcc;
  private ParticipationFactory participationFactory;
  
  private String virtualpath;


  private Genson genson = new GensonBuilder().useDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
      .useIndentation(true).create();

  /**
   * Constructeur de la classe. Initialise les Ucc et les factory 
   * et reçoit le code secret
   * 
   * @param secret
   * @param userUcc
   * @param userFactory
   * @param entrepriseUcc
   * @param entrepriseFactory
   * @param jeUcc
   * @param jeFactory
   * @param personneContactUcc
   * @param personneContactFactory
   * @param partUcc
   * @param partFactory
   */
  public MaServlet(String secret, UtilisateurUcc userUcc, UtilisateurFactory userFactory,
      EntrepriseUcc entrepriseUcc, EntrepriseFactory entrepriseFactory, JourneeEntrepriseUcc jeUcc,
      JourneeEntrepriseFactory jeFactory, PersonneContactUcc personneContactUcc,
      PersonneContactFactory personneContactFactory, ParticipationUcc partUcc,
      ParticipationFactory partFactory, String virtualpath) {
    super();
    this.secret = secret;
    this.utilisateurUcc = userUcc;
    this.utilisateurFactory = userFactory;
    this.entrepriseUcc = entrepriseUcc;
    this.entrepriseFactory = entrepriseFactory;
    this.jeUcc = jeUcc;
    this.jeFactory = jeFactory;
    this.personneContactUcc = personneContactUcc;
    this.personneContactFactory = personneContactFactory;
    this.participationUcc = partUcc;
    this.participationFactory = partFactory;
    this.virtualpath = virtualpath;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    System.out.println(req.getRequestURI());
    if (!req.getRequestURI().equals("/"+virtualpath+"/")) {
      super.doGet(req, resp);

    } else {

      resp.setCharacterEncoding("UTF-8");
      resp.setContentType("text/html");
      String chemin = req.getRequestURI();
      System.out.println("******************");
      System.out.println("Requete perçue dans GET " + chemin);

      String response = "";

      response += readFile("html/header.html", StandardCharsets.UTF_8);
      response += readFile("html/connexion.html", StandardCharsets.UTF_8);
      response += readFile("html/inscription.html", StandardCharsets.UTF_8);
      response += readFile("html/navigation.html", StandardCharsets.UTF_8);
      response += readFile("html/journees.html", StandardCharsets.UTF_8);
      response += readFile("html/gestion.html", StandardCharsets.UTF_8);
      response += readFile("html/entreprises.html", StandardCharsets.UTF_8);
      response += readFile("html/personnes.html", StandardCharsets.UTF_8);
      response += readFile("html/footer.html", StandardCharsets.UTF_8);

      resp.getWriter().print(response);

      System.out.println("******************");
      System.out.println();
    }
  }

  private String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String chemin = req.getRequestURI();

    System.out.println(chemin);


    if (!chemin.startsWith("/"+virtualpath)) {
      resp.sendError(400, "Route incorrecte");
    }

    resp.setCharacterEncoding("UTF-8");
    resp.setStatus(HttpStatus.CREATED_201);
    resp.setContentType("application/json");

    System.out.println("******************");
    System.out.println("Requete perçue dans POST " + chemin);

    String[] demande = chemin.split("/");
    System.out.println("doPost -> " + demande[2]);

    System.out.println(chemin);

    Object reponse = null;

    try {
      // Demande qui ne demande pas d'etre connecte
      switch (demande[2]) {
      /*
        case "inscription":
          reponse = inscription(req);
          break;
        case "connexion":
          System.out.println("oui connexion");
          reponse = connexion(req, resp);
          System.out.println("reponse -> " + reponse);
          break;
          
        case "verifierConnexion":
          // Verifier la presence de la session
          UtilisateurDto utilisateur = verifierUtilisateurConnexion(req);
          if (utilisateur == null) {
            resp.getWriter().println("{}");
            return;
          } else {
            // Verification de l'id + version de la session
            reponse = getUtilisateurSelonIdEtVersion(utilisateur);
            if (reponse == null) {
              resp.sendError(402, "Session expirée, veuillez-vous reconnecter");
              return;
            }
          }
          break;
          */
        case "creerOuChargerUser":
        	reponse = creerOuChargerUser(req);
        	break;
      }

      // Renverra un success :
      if (reponse != null) {
        resp.getWriter().println(this.genson.serialize(reponse));
        return;
      }

      //UtilisateurDto utilisateurAuthentifie = verifierUtilisateurConnexion(req);
      /* Verifie que l'utilisateur est connecte pour effectuer les autres demandes
      if (utilisateurAuthentifie == null) {
        resp.sendError(402, "Session expirée, veuillez-vous reconnecter");
        return;
      }
      */

      switch (demande[2]) {
      /*
        case "deconnexion":
          deconnexion(req, resp);
          return;
          
        case "changerMdp":
          reponse = changerMdp(utilisateurAuthentifie, req);
          deconnexion(req, resp);
          resp.getWriter().println(this.genson.serialize(reponse));
          return;
          */
        case "creerJE":
          reponse = creerJournee(req);
          break;
        case "getJE":
          reponse = getJournee(req);
          break;
        case "getJESelonId":
          reponse = getJourneeSelonId(req);
          break;
        case "cloturerJE":
          reponse = cloturerJournee(req);
          break;
        case "nbrParticipationConfirmeeParJournees":
          Map<JourneeEntrepriseDto, Integer> mapPart = nbrParticipationConfirmeeParJournees(req);
          resp.getWriter().println(this.genson.serialize(mapPart,
              new GenericType<Map<JourneeEntrepriseImpl, Integer>>() {}));
          return;
        case "insererEntreprise":
          reponse = insererEntreprise(req);
          break;
        case "getEntreprises":
          reponse = getEntreprises(req);
          break;
        case "getEntreprisesEtCreateurs":
          Map<EntrepriseDto, String> map = getEntreprisesEtCreateurs(req);
          resp.getWriter().println(
              this.genson.serialize(map, new GenericType<Map<EntrepriseImpl, String>>() {}));
          return;
        case "getEntreprisesParId":
          reponse = getEntreprisesParId(req);
          break;
        case "rechercherEntreprise":
          Map<EntrepriseDto, String> resultat = rechercherEntreprise(req);
          resp.getWriter().println(
              this.genson.serialize(resultat, new GenericType<Map<EntrepriseImpl, String>>() {}));
          return;
        case "insererPersonneDeContact":
          reponse = insererPersonneDeContact(req);
          break;
        case "desactiverPersContact":
          reponse = desactiverPersContact(req);
          break;
        case "getPersonnesDeContact":
          reponse = getPersonnesDeContact(req);
          break;
        case "getPersonnesContactDeLEntreprise":
          reponse = getPersonnesContactDeLEntreprise(req);
          break;
        case "getPersonnesContactEtEntreprise":
          Map<PersonneContactDto, String> rep = getPersonnesContactEtEntreprise(req);
          resp.getWriter().println(
              this.genson.serialize(rep, new GenericType<Map<PersonneContactImpl, String>>() {}));
          return;
        case "rechercherPersonneDeContactSelonNomEtPrenom":
          Map<PersonneContactDto, String> rep8 = rechercherPersonneDeContactSelonNomEtPrenom(req);
          resp.getWriter().println(
              this.genson.serialize(rep8, new GenericType<Map<PersonneContactImpl, String>>() {}));
          return;
        case "getParticipationsEntreprises":
          Map<ParticipationDto, String> rep2 = getParticipationsEntreprises(req);
          resp.getWriter().println(
              this.genson.serialize(rep2, new GenericType<Map<ParticipationImpl, String>>() {}));
          return;
        case "getParticipationsPersonnesPresentes":
          reponse = getParticipationsPersonnesPresentes(req);
          break;
        case "getParticipationsJEPers":
          Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> rep3 =
              getParticipationsJourneePers(req);
          resp.getWriter().println(this.genson.serialize(rep3,
              new GenericType<Map<ParticipationImpl, Map<JourneeEntrepriseImpl, Integer>>>() {}));
          return;
        case "annulerParticipation":
          reponse = annulerParticipation(req);
          break;
        case "updateEtatParticipation":
          reponse = updateEtatParticipation(req);
          break;
        case "getPersonnesEtPersonnesInviteesParticipation":
          reponse = getPersonnesEtPersonnesInviteesParticipation(req);
          break;
        case "insererPersonneContactParticipation":
          reponse = insererPersonneContactParticipation(req);
          break;
        case "isJourneeActive":
          reponse = isJourneeActive(req);
          break;
        case "insererCommentaire":
      	  reponse = insererCommentaire(req);
      	  break;
      }

      /* Je vérifie l'id + la version + si il est responsable
      utilisateurAuthentifie = getUtilisateurSelonIdEtVersion(utilisateurAuthentifie);
      if (utilisateurAuthentifie == null) {
        resp.sendError(402, "Session expirée, veuillez-vous reconnecter");
        return;
      }
      */
      
      if (reponse == null && isUserResponsable(req)) {
        switch (demande[2]) {
          case "modifierPersonneContact":
            reponse = modifierPersonneContact(req);
            break;
          case "insererParticipation":
            reponse = insererParticipation(req);
            String sourceFile = "Nouveau.csv";
            try {
                FileInputStream inputStream = new FileInputStream(sourceFile);
                String disposition = "attachment; fileName=outputfile.csv";
                resp.setContentType("text/csv");
                resp.setHeader("Content-Disposition", disposition);
                resp.setHeader("content-Length", String.valueOf(stream(inputStream, resp.getOutputStream())));
                return;
            } catch (IOException e) {
            }
            
          case "getCsvToutLeMonde":
        	  List<Integer> listeId = new ArrayList<>();
        	  for(EntrepriseDto entrepriseDto : entrepriseUcc.getEntreprisesInvitees()){
        		  listeId.add(entrepriseDto.getIdEntreprise());
        	  }
        	  creerCSV(listeId);
        	  sourceFile = "Nouveau.csv";
              try {
                  FileInputStream inputStream = new FileInputStream(sourceFile);
                  String disposition = "attachment; fileName=outputfile.csv";
                  resp.setContentType("text/csv");
                  resp.setHeader("Content-Disposition", disposition);
                  resp.setHeader("content-Length", String.valueOf(stream(inputStream, resp.getOutputStream())));

              } catch (IOException e) {
              }
        	  return;
          case "getEntreprisesInvitablesEtInvitees":
            reponse = getEntreprisesInvitablesEtInvitees(req);
            break;
          case "getCommentairesParEntreprise":
        	reponse = getCommentairesParEntreprise(req);
        	break;
        }
      }

      if (reponse != null) {
        resp.getWriter().println(this.genson.serialize(reponse));
      } else {
        throw new FatalException("Autorisation refusée");
      }

    } catch (BizzException exception) {
      resp.sendError(400, exception.getMessage());
    } catch (Exception exception) {
      resp.sendError(500, exception.getMessage());
    }
    System.out.println("******************");
    System.out.println();
  }


/********************* METHODE UTILITAIRE **********************/

  protected String creerToken(String login, String ip) {
    Map<String, Object> claims = new HashMap<String, Object>();
    claims.put("user", login);
    claims.put("ip", ip);
    return new JWTSigner(secret).sign(claims);
  }

  protected Cookie creerCookie(String nom, String token) {
    Cookie cookie = new Cookie(nom, token);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24 * 365);
    return cookie;
  }

  /**
   * 1 - Verifie la presence de l'idSession dans la session, si ok, utilisateur connecte 2 - Si
   * idSession inexistant, on va chercher le cookie user qui contient le token 3 - On verifie la
   * signature du token 4 - Je retiens dans la session id recupere du token 5 - Test session
   * 
   * @return utilisateur
   * @return null si non connecte
   */
  protected UtilisateurDto verifierUtilisateurConnexion(HttpServletRequest req) {
    UtilisateurDto utilisateur = utilisateurFactory.createUtilisateur();
    HttpSession session = req.getSession();

    if (session.getAttribute("idSession") != null) {
      String[] data = session.getAttribute("idSession").toString().split("-");
      utilisateur.setId(Integer.parseInt(data[0]));
      utilisateur.setVersion(Integer.parseInt(data[1]));
      return utilisateur;
    } else {
      Cookie[] cookies = req.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("user".equals(cookie.getName())) {
            String token = cookie.getValue();
            Object userId = null;
            Object userVersion = null;
            try {
              Map<String, Object> map = new JWTVerifier(secret).verify(token);
              userId = map.get("id");
              userVersion = map.get("version");
              session.setAttribute("idSession", userId + "-" + userVersion);
            } catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException
                | SignatureException | IOException | JWTVerifyException exception) {
              // Je fais rien, je reteste apres via la session
            }
            // Je verifie que la map a pu etre dechifre
            if (userId == null || userVersion == null) {
              return null;
            } else {
              String[] data = session.getAttribute("idSession").toString().split("-");
              utilisateur.setId(Integer.parseInt(data[0]));
              utilisateur.setVersion(Integer.parseInt(data[1]));
              return utilisateur;
            }
          }
        } // Si on ne trouve pas le cookie user + pas de session
        return null;
      } else {
        return null; // Aucun cookie + Pas de session
      }
    }
  }


  /****************
   * REQUETE POST *
   ****************/

  /* GESTION UTILISATEUR */

  protected String inscription(HttpServletRequest req) {
    UtilisateurDto utilisateur = utilisateurFactory.createUtilisateur();
    String nom = req.getParameter("nom");
    utilisateur.setNom(nom);
    String prenom = req.getParameter("prenom");
    utilisateur.setPrenom(prenom);
    String email = req.getParameter("mail");
    utilisateur.setEmail(email);
    String pseudo = req.getParameter("login");
    utilisateur.setPseudo(pseudo);
    String mdp = req.getParameter("mdpEntree");
    String mdpC = req.getParameter("mdpConfirmation");

    if (mdp != null && mdpC != null && !mdp.equals(mdpC)) {
      throw new BizzException(
          "{\"mdpEntree\":\"Mot de passe différents\",\"mdpConfirmation\":\"\"}");
    }
    utilisateur.setMdp(mdp);

    utilisateur = utilisateurUcc.inscrireUtilisateur(utilisateur);
    return "Inscription réussie";
  }

  protected UtilisateurDto connexion(HttpServletRequest req, HttpServletResponse resp) {

    String login = req.getParameter("login");
    String mdp = req.getParameter("mdp");

    UtilisateurDto utilisateur = utilisateurFactory.createUtilisateur();
    utilisateur.setPseudo(login);
    utilisateur.setMdp(mdp);

    utilisateur = utilisateurUcc.connecterUtilisateur(utilisateur);

    // Creation JWT
    Map<String, Object> map = new HashMap<>();
    map.put("version", utilisateur.getVersion());
    map.put("pseudo", utilisateur.getPseudo());
    map.put("id", utilisateur.getId());
    String token = new JWTSigner(secret).sign(map);

    // Cookies
    resp.addCookie(creerCookie("user", token));
    resp.addCookie(creerCookie("id", Integer.toString(utilisateur.getId())));
    resp.addCookie(creerCookie("pseudo", utilisateur.getPseudo()));
    resp.addCookie(creerCookie("nom", utilisateur.getNom()));
    resp.addCookie(creerCookie("prenom", utilisateur.getPrenom()));
    DateTimeFormatter dateFormater = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    resp.addCookie(
        creerCookie("dateInscription", utilisateur.getDateInscription().format(dateFormater)));
    resp.addCookie(creerCookie("responsable", String.valueOf(utilisateur.getResponsable())));

    // Sessions
    HttpSession session = req.getSession();
    session.setMaxInactiveInterval(10 * 60);
    session.setAttribute("idSession", utilisateur.getId() + "-" + utilisateur.getVersion());

    return utilisateur;
  }

  protected void deconnexion(HttpServletRequest req, HttpServletResponse resp) {
    // Destruction cookies
    Cookie[] cookies = req.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        cookie.setValue("");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
      }
    }
    // Destruction sessions
    HttpSession session = req.getSession();
    if (session != null) {
      session.invalidate();
    }
  }

  // On ne teste pas encore si le nv mdp = ancien
  private String changerMdp(UtilisateurDto utilisateur, HttpServletRequest req) {

    String mdp = req.getParameter("nouveauMdp");
    String confirmationMdp = req.getParameter("nouveauMdpConfirmation");
    // Les 2 mdp identiques
    if (!mdp.equals(confirmationMdp)) {
      throw new BizzException(
          "{\"nouveauMdp\":\"Mot de passe différents\",\"nouveauMdpConfirmation\":\"\"}");
    }

    // Appel UCC
    utilisateur.setMdp(mdp);
    utilisateurUcc.changerMdp(utilisateur);

    return "Modification mdp réussie, veuillez-vous reconnecter";
  }

  protected UtilisateurDto getUtilisateurSelonIdEtVersion(UtilisateurDto utilisateurDto) {
    UtilisateurDto utilisateur = utilisateurUcc.verifierUtilisateur(utilisateurDto);
    if (utilisateur == null) {
      return null;
    } else {
      return utilisateur;
    }
  }

  /* GESTION JE */

  protected String creerJournee(HttpServletRequest req) {
    String date = req.getParameter("dateJE");
    JourneeEntrepriseDto je = jeFactory.creerJourneeEntreprise();
    je.setDateJournee(date);
    je = jeUcc.insererJourneeEntreprise(je);
    return "Insertion journée entreprise du " + je.getDateJournee() + " réussie";
  }

  protected List<JourneeEntrepriseDto> getJournee(HttpServletRequest req) {
    List<JourneeEntrepriseDto> listeJe = jeUcc.getJourneeEntreprise().stream()
        .sorted(comparing(JourneeEntrepriseDto::getIdJournee)).collect(toList());
    return listeJe;
  }

  protected JourneeEntrepriseDto getJourneeSelonId(HttpServletRequest req) {
    String idJe = req.getParameter("idJE");
    JourneeEntrepriseDto je = jeFactory.creerJourneeEntreprise();
    je.setIdJournee(Integer.parseInt(idJe));
    je = jeUcc.getJourneeSelonId(je);
    return je;
  }

  protected JourneeEntrepriseDto isJourneeActive(HttpServletRequest req) {
    boolean reponse = this.jeUcc.isJourneeActive();
    JourneeEntrepriseDto je = this.jeFactory.creerJourneeEntreprise();
    if (reponse) {
      je.setOuverte(true);
    } else {
      je.setOuverte(false);
    }
    return je;
  }

  protected String cloturerJournee(HttpServletRequest req) {
    JourneeEntrepriseDto je = jeFactory.creerJourneeEntreprise();
    je = jeUcc.cloturerJourneeEntreprise(je);
    return "Journée entreprise du " + je.getDateJournee() + " fermée.";
  }


  /* GESTION ENTREPRISE */
  // Avant dans parametre, UtilisateurDto utilisateurDto
  protected String insererEntreprise(HttpServletRequest req) {
    String entreprise = req.getParameter("entreprise");
    EntrepriseDto entrepriseDto = this.genson.deserialize(entreprise, EntrepriseImpl.class);
    entrepriseDto.setCreateur(Integer.parseInt(req.getParameter("idUtilisateur")));
    entrepriseDto = entrepriseUcc.insererEntreprise(entrepriseDto);
    return "Insertion entreprise reussie.";
  }

  // Utiliser pour le select dans l'insertion d'une personne de contact
  protected List<EntrepriseDto> getEntreprises(HttpServletRequest req) {
    List<EntrepriseDto> listeEntreprise = entrepriseUcc.getEntreprises().stream()
        .sorted(comparing(EntrepriseDto::getNomEntreprise)).collect(toList());
    return listeEntreprise;
  }

  // Utiliser pour l'affichage des entreprises
  protected Map<EntrepriseDto, String> getEntreprisesEtCreateurs(HttpServletRequest req) {
    return this.entrepriseUcc.getEntreprisesEtCreateurs();
  }

  // Utiliser pour le modal d'info entreprise
  protected EntrepriseDto getEntreprisesParId(HttpServletRequest req) {
    return this.entrepriseUcc
        .getEntrepriseSelonId(Integer.parseInt(req.getParameter("idEntreprise")));
  }

  protected Map<ParticipationDto, Map<JourneeEntrepriseDto, Integer>> getParticipationsJourneePers(
      HttpServletRequest req) {
    return this.participationUcc
        .getParticipationsJePers(Integer.parseInt(req.getParameter("idEntreprise")));
  }

  /**
   * 0 : Liste toutes les entreprises (Objet)
   * 1 : Liste entreprises invitables (ID)
   * 2 : Liste entreprises avec commentaires (ID)
   * 3 : Liste entreprises invitees
   */
  protected Map<Integer, List<EntrepriseDto>> getEntreprisesInvitablesEtInvitees(
      HttpServletRequest req) {
	List<EntrepriseDto> l0 = this.entrepriseUcc.getEntreprises();
    List<EntrepriseDto> l1 = this.entrepriseUcc.getEntreprisesInvitables();
    List<EntrepriseDto> l2 = this.entrepriseUcc.getEntreprisesAvecCommentaires();
    List<EntrepriseDto> l3 = this.entrepriseUcc.getEntreprisesInvitees();
    Map<Integer, List<EntrepriseDto>> map = new HashMap<>();
    map.put(0, l0);
    map.put(1, l1);
    map.put(2, l2);
    map.put(3, l3);
    return map;
  }

  protected Map<EntrepriseDto, String> rechercherEntreprise(HttpServletRequest req) {
    String entreprise = req.getParameter("entreprise");
    EntrepriseDto entrepriseDto = this.genson.deserialize(entreprise, EntrepriseImpl.class);
    return entrepriseUcc.rechercherEntreprise(entrepriseDto);
  }

  /* GESTION PERSONNE DE CONTACT */

  protected String insererPersonneDeContact(HttpServletRequest req) {
    String pers = req.getParameter("personne");
    PersonneContactDto pcD = this.genson.deserialize(pers, PersonneContactImpl.class);
    personneContactUcc.insererPersonneContact(pcD);
    return "Insertion personne de contact réussie.";
  }

  protected String modifierPersonneContact(HttpServletRequest req) {
    String pers = req.getParameter("personne");
    System.out.println(pers);
    PersonneContactDto pcD = this.genson.deserialize(pers, PersonneContactImpl.class);
    personneContactUcc.modifierPersonneContact(pcD);
    return "Modification personne de contact réussie";
  }

  protected String desactiverPersContact(HttpServletRequest req) {
    PersonneContactDto pdc =
        this.personneContactUcc.desactiverPersContact(Integer.parseInt(req.getParameter("idPers")));
    return "Désactivation de " + pdc.getNom().toUpperCase() + " " + pdc.getPrenom() + " réussie";
  }

  protected List<PersonneContactDto> getPersonnesDeContact(HttpServletRequest req) {
    List<PersonneContactDto> listePersonneContact = personneContactUcc.getPersonnesContact()
        .stream().sorted(comparing(PersonneContactDto::getNom)).collect(toList());
    return listePersonneContact;
  }

  protected Map<PersonneContactDto, String> getPersonnesContactEtEntreprise(
      HttpServletRequest req) {
    Map<PersonneContactDto, String> mapPers = personneContactUcc.getPersonnesContactEtEntreprise();
    return mapPers;
  }

  protected Map<PersonneContactDto, String> rechercherPersonneDeContactSelonNomEtPrenom(
      HttpServletRequest req) {
    String personne = req.getParameter("personne");
    PersonneContactDto p = this.genson.deserialize(personne, PersonneContactImpl.class);
    Map<PersonneContactDto, String> mapPers =
        personneContactUcc.rechercherPersonneDeContactSelonNomEtPrenom(p);
    return mapPers;
  }

  protected List<PersonneContactDto> getPersonnesContactDeLEntreprise(HttpServletRequest req) {
    List<PersonneContactDto> listePersonneContact = personneContactUcc
        .getPersonnesContactDeLEntreprise(Integer.parseInt(req.getParameter("idEntreprise")));
    return listePersonneContact;
  }

  /* GESTION PARTICIPATION */

  protected Map<JourneeEntrepriseDto, Integer> nbrParticipationConfirmeeParJournees(
      HttpServletRequest req) {
    List<JourneeEntrepriseDto> listeJe1 = this.jeUcc.getJourneeEntreprise();
    Map<JourneeEntrepriseDto, Integer> map =
        this.participationUcc.nbrParticipationConfirmeeParJournees();
    for (JourneeEntrepriseDto je : listeJe1) {
      // JE sans participations confirmee ou sans participations
      if (!map.containsKey(je)) {
        map.put(je, 0);
      }
    }
    Map<JourneeEntrepriseDto, Integer> mapTrieParDate = new LinkedHashMap<>();
    map.entrySet().stream()
        .sorted(Map.Entry.<JourneeEntrepriseDto, Integer>comparingByKey(
            Comparator.comparing(JourneeEntrepriseDto::getDateJournee)))
        .forEachOrdered(elem -> mapTrieParDate.put(elem.getKey(), elem.getValue()));
    return mapTrieParDate;
  }

  protected Map<ParticipationDto, String> getParticipationsEntreprises(HttpServletRequest req) {
    int idJournee = Integer.parseInt(req.getParameter("idJournee"));
    Map<ParticipationDto, String> map =
        this.participationUcc.getParticipationsEntreprises(idJournee);
    return map;
  }

  protected Map<Integer, List<PersonneContactDto>> getParticipationsPersonnesPresentes(
      HttpServletRequest req) {
    int idJournee = Integer.parseInt(req.getParameter("idJournee"));
    Map<Integer, List<PersonneContactDto>> map =
        this.participationUcc.getParticipationsPersonnesPresentes(idJournee);
    return map;
  }

  protected String annulerParticipation(HttpServletRequest req) {
    int idParticipation = Integer.parseInt(req.getParameter("idParticipation"));
    ParticipationDto participation = this.participationFactory.creerParticipation();
    participation.setIdParticipation(idParticipation);
    participation = this.participationUcc.annulerParticipation(participation);
    return "Annulation participation n°" + participation.getIdEntreprise() + " réussie";
  }

  protected String updateEtatParticipation(HttpServletRequest req) {
    ParticipationDto participation = this.participationFactory.creerParticipation();
    participation.setIdParticipation(Integer.parseInt(req.getParameter("idParticipation")));
    participation.setEtat(Etat.getEtatByAbreviation(req.getParameter("etat")));
    participation.setVersion(Integer.parseInt(req.getParameter("version")));
    participation = this.participationUcc.updateEtatParticipation(participation);
    return "Participation n°" + participation.getIdParticipation() + " est à l'état "
        + participation.getEtat().toString();
  }

  /**
   * Cle : 0 avec la liste des personnes de l'entreprise Cle : 1 avec la liste des ID des personnes
   * invitees Recoit l'id de la participation et l'id de l'entreprise
   */
  protected Map<Integer, List<PersonneContactDto>> getPersonnesEtPersonnesInviteesParticipation(
      HttpServletRequest req) {
    Map<Integer, List<PersonneContactDto>> map = new HashMap<>();
    List<PersonneContactDto> listePdcs1 =
        this.personneContactUcc.getPersonnesContactActifEtInactifDeLEntreprise(
            Integer.parseInt(req.getParameter("idEntreprise")));
    List<PersonneContactDto> listePdcs2 =
        this.participationUcc.getIdPersonnesInviteesPourUneParticipation(
            Integer.parseInt(req.getParameter("idParticipation")));
    map.put(0, listePdcs1);
    map.put(1, listePdcs2);
    return map;
  }

  /****************
   * GESTION PARTICIPATION
   ****************/

  protected List<Integer> insererParticipation(HttpServletRequest req) {
    String tab = req.getParameter("tabIdEntreprise");
    List<Integer> liste = this.genson.deserialize(tab, new GenericType<List<Integer>>() {});
    if (liste.isEmpty()) {
      throw new BizzException(
          "{\"fail\":\"Veuillez au moins sélectionner une entreprise à inviter\"}");
    }
    List<EntrepriseDto> listeEntreprises = new ArrayList<>();
    for (Integer idEntreprise : liste) {
      EntrepriseDto entreprise = this.entrepriseFactory.creerEntreprise();
      entreprise.setIdEntreprise(idEntreprise);
      listeEntreprises.add(entreprise);
    }
    List<Integer> listeRetour = this.participationUcc.insererParticipation(listeEntreprises);
    creerCSV(listeRetour);
    return listeRetour;
  }

  protected String insererPersonneContactParticipation(HttpServletRequest req) {
    String tab = req.getParameter("tabIdPersonneContact");
    int idParticipation = Integer.parseInt(req.getParameter("idParticipation"));
    List<Integer> liste = this.genson.deserialize(tab, new GenericType<List<Integer>>() {});
    if (liste.isEmpty()) {
      throw new BizzException("{\"fail\":\"Veuillez au moins sélectionner une pers. de contact\"}");
    }
    List<PersonneContactDto> listePdcs = new ArrayList<>();
    for (Integer idPers : liste) {
      PersonneContactDto pdc = this.personneContactFactory.creerPersonneContact();
      pdc.setIdPersonneContact(idPers);
      listePdcs.add(pdc);
    }
    this.participationUcc.insererPersonneContactParticipation(listePdcs, idParticipation);
    return "Insertion pers. de contact pour la participation n°" + idParticipation + " réussie";
  }
  
  protected String insererCommentaire(HttpServletRequest req) {
	  String commentaire = req.getParameter("commentaire");
	  int version = Integer.parseInt(req.getParameter("version"));
	  int idParticipation = Integer.parseInt(req.getParameter("idParticipation"));
	  this.participationUcc.updateCommentaire(version, idParticipation, commentaire);
	  return "Ajout commentaire réussie";
  }
  
  protected List<String> getCommentairesParEntreprise(HttpServletRequest req) {
	  int idEntreprise = Integer.parseInt(req.getParameter("idEntreprise"));
	  List<String> listeDeCommentaires = this.participationUcc.getCommentairesParEntreprise(idEntreprise);
	  return listeDeCommentaires;
  }
  
  protected void creerCSV(List<Integer> tabIdEntreprises){
	  try{
		    PrintWriter writer = new PrintWriter("Nouveau.csv", "UTF-8");
		    writer.println("Entreprise;Nom;Prenom;E-mail");
		    for(int i : tabIdEntreprises){
		    	String nom = entrepriseUcc.getEntrepriseSelonId(i).getNomEntreprise();
		    	for(PersonneContactDto personne : personneContactUcc.getPersonnesContactDeLEntreprise(i)){
		    		if(!("").equals(personne.getEmail())){
		    			writer.println(nom+";"+personne.getNom()+";"+personne.getPrenom()+";"+personne.getEmail());
		    		}
		    	}
			}
		    writer.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
  }
  
  private long stream(InputStream input, OutputStream output) throws IOException {

	    try (ReadableByteChannel inputChannel = Channels.newChannel(input); WritableByteChannel outputChannel = Channels.newChannel(output)) {
	        ByteBuffer buffer = ByteBuffer.allocate(10240);
	        long size = 0;

	        while (inputChannel.read(buffer) != -1) {
	            buffer.flip();
	            size += outputChannel.write(buffer);
	            buffer.clear();
	        }
	        return size;
	    }
  }
  
  /*
   * On va vérifier si l'email est present en DB
   * -> Si présent = renvoie l'user
   * -> Sinon on cree l'user + renvoie
   */
  protected UtilisateurDto creerOuChargerUser(HttpServletRequest req) {
	String email = req.getHeader("X-Forwarded-User");
	if (email == null || email.equals("")) {
		throw new BizzException("{\"fail\":\"X-Forwarded-User introuvable\"}");
	}
	UtilisateurDto user = this.utilisateurFactory.createUtilisateur();
	user.setEmail(email);
	user = this.utilisateurUcc.creerOuChargerUser(user);
	
	if (isUserResponsable(req)) {
		user.setResponsable(true);
	} else {
		user.setResponsable(false);
	}
	return user;
  }
  

  protected boolean isUserResponsable(HttpServletRequest req) {
	  String droit = req.getHeader("X-Forwarded-Groups");
		if (droit == null || droit.equals("")) {
			throw new BizzException("{\"fail\":\"X-Forwarded-Groups introuvable\"}");
		}
	  return (droit.equals("adminje")) ? true : false;
  }
}