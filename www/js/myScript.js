// Utiliser pour la varification si responsable pour editer pers contact
// Boolean
var responsable = false;

$(function() {
    // Au chargement de la page, on test si l'utilisateur est connecte
    $.ajax({
        url : '/verifierConnexion',
        type : 'POST',
    success : function(data) {
        var utilisateur = data;
        if (! jQuery.isEmptyObject(utilisateur)) {
            $('#nomUtilisateur').html(utilisateur.nom.toUpperCase() + " " + utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
            toggleDiv("wrapper");
            $('#section-dashboard').attr('style', 'display:block');
            loadChart();
            if(utilisateur.responsable == true){
                responsable = true;
                navResponsable();
            }else{
                navUtilisateur();
            }
            chargerProfil(utilisateur);
        } else {
            $('#wrapper').css('display', 'none');
            $('#divInscription').css('display', 'none');
            $('#divConnexion').css('display', 'block');
        }
    },
    error : function(jqXHR, textStatus, errorThrown) {
        notifyErreur(errorThrown);
        deconnexion();
    }
    });
    
    
    // Boutons
    $('#buttonSeConnecter').click(chargerConnexion);
    $('#buttonPasEncoreInscrit').click(function() {
        toggleDiv('divInscription');
        supprimerMessageErreur('formLogin');
    });
    $('#buttonInscriptionRetour').click(function() {
        toggleDiv('divConnexion');
        supprimerMessageErreur('formInscription');
    });
    $('#buttonSInscrire').click(inscrireUtilisateur);
    $('#buttonCreerJE').click(creerJE);
    $('#buttonCloturerJE').click(cloturerJE);
    $('#buttonRechercherContact').click(function() {
        getPersonnesContact(true);
    });
    $('#buttonInsererEntreprise').click(function() {
        insererEntreprise();
    });
    $('#buttonInsererPersonneDeContact').click(function() {
        insererPersonneContact();
    });
    $('#buttonRechercherEntreprise').click(function() {
    	getEntreprises(true);
    });
    
    // Menu
    $('#menuSeDeconnecter').click(deconnexion);

    // Navigation
    $('#navBarDashboard').click(function() {
        affichage('section-dashboard');
        loadChart();
    });

    $('#navBarJourneesU').click(function() {
        affichage('section-journees');
    });

    $('#navBarHistorique').click(function() {
        affichage('section-journees');
        chargerSelectJE();
    });

    $('#navBarGestionJE').click(function() {
        affichage('section-gestionJE');
        isJourneeActive();
        getEntreprisesInvitablesEtInvitees();
    });
    $('#navBarEntreprises').click(function() {
        affichage('section-entreprises');
        getEntreprises(false);
    });
    $('#buttonPCAjouter').click(function() {
        chargerSelectPersonneContact('formInsererPersonneDeContact');
    });
    $('#navBarPersonnesDeContact').click(function() {
        affichage('section-personnesDeContact');
        getPersonnesContact(false);
    }); 
    $('#buttonUpdatePersonnelParticipant').click(function() {
        sauvegarderPersonnelParticipation();
    }); 

    $('#buttonChangerPassword').click(function(){
        updateMdp();
    });

    $('#genererCSV').click(function(){
        sauvegarderPartEtGenererCsv();
    });
    
    /* NAV GESTION INVITATIONS */
    
    $('#lesJE tbody').on('change', '.partSelectEtat', function() {
        updateEtatParticipation($(this).attr('id').substring(23), $(this).find(':selected').val(), $(this).attr('name').substring(15));                 
    });
    
    $('#lesJE tbody').on('click', '.partButtonConfEtat', function() {
        var objpart = $(this).parents('tr').data('objpart');
        updateEtatParticipation(objpart.key.idParticipation, 'CONFIRMEE', objpart.key.version);                 
    });
    
    $('#lesJE tbody').on('click', '.partButtonRefEtat', function() {
        var objpart = $(this).parents('tr').data('objpart');
        updateEtatParticipation(objpart.key.idParticipation, 'REFUSEE', objpart.key.version);               
    });
    
    /* NAV ENTREPRISES */
    
    $('#lesEntreprises tbody').on('click', '.entListePart', function() {
        afficherModalEntrepriseListeParticipations($(this).parents('tr').data('objent').idEntreprise);
    });
    
    $('#lesEntreprises tbody').on('click', '.entListeEmpl', function() {
        afficherModalEntrepriseListeEmployes($(this).parents('tr').data('objent').idEntreprise);
    });
    
    /* NAV PERSONNE DE CONTACT */
    
    $('#lesPersonnesContact tbody').on('click', '.persDesactiver', function() {
        desactiverPersContact($(this).parents('tr').data('objpersonne'));
    });
    
    $('#lesPersonnesContact tbody').on('click', '.persInfoEntreprise', function() {
        afficherInformationEntreprise($(this).parents('tr').data('objpersonne').idEntreprise);
    });
    
    $('#lesPersonnesContact tbody').on('click', '.persEditer', function() {
        afficherModalModifierPersonneContact($(this).parents('tr').data('objpersonne'));
    });
    
    $('#buttonEditPersonneDeContact').on('click', function() {
        modifierPersonneContact();
    });
    
});

/*******************************************************************************
 * FONCTIONS AJAX *
 ******************************************************************************/

/**
 * REQUETE AJAX COMMUNE
 * @param route = url 
 * @param json = donnees envoyees
 * @param idFormulaire = utilise dans le cas d'erreurs input
 * @returns objet
 * SWITCH : 
 *  - obj : retourne un objet, get
 *  - success : insert sans retour d'objet
 *  - input : erreurs d'input
 *  - fail : erreur de la requete
 *  - 400 : erreur session expiree
 *  - 500 : erreur serveur
 */

function requeteAjax(route, json, idFormulaire) {
    var objets = {};
    $.ajax({
        url : route,
        type : 'POST',
        async: false,
        data : json,
    success : function(data, textStatus, jqXHR) {   
        switch (jqXHR.status) {
        case 201:
            // Si message de retour classique
            if (typeof data !== 'object') {
                if (data === "Deconnexion") {
                    $('#wrapper').css('display', 'none');
                    $('#divInscription').css('display', 'none');
                    $('#divConnexion').css('display', 'block');
                } else {
                    objets.success = "ok";
                    $(".modal.in").modal("hide");
                    notifySucces(data);
                }
            } else {
                objets = data;
                return objets;
            }
        }
    },
    error : function(jqXHR, textStatus, errorThrown) {
        switch (jqXHR.status) {
        case 400:
            // JSON.parse lance une syntaxe erreur si c'est pas un json ou si y'a un espace
            var obj = JSON.parse(errorThrown);
            if(obj.fail){
                keyboardListener();
                notifyErreur(obj.fail);
            }else{
                jsonInputErreurs(idFormulaire, obj);
            }
            break;
        case 402:
            // Erreur session expiree
            $('#wrapper').hide();
            toggleDiv('divConnexion');
            keyboardListener();
            notifyErreur(errorThrown);
            break;
        case 500:
            notifyErreurServeur(errorThrown);
            break;
        }
    }
    });
    return objets;
}

/*-------------*
 * UTILISATEUR *
 *-------------*/

function chargerConnexion() {
    supprimerMessageErreur('formLogin');
    var json = $('#formLogin').serialize();
    var inputVide = [];
    if (testInputNonVide('formLogin', inputVide)) {
            var utilisateur = requeteAjax('/connexion', json, 'formLogin');
            if (! jQuery.isEmptyObject(utilisateur)) {
                $('#nomUtilisateur').html(utilisateur.nom.toUpperCase() + " " + utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
                toggleDiv("wrapper");
                notifySucces('Connexion réussie');
                affichage('section-dashboard');
                loadChart();
                if(utilisateur.responsable == true){
                    navResponsable();
                }else{
                    navUtilisateur();
                }
                chargerProfil(utilisateur);
            }
            setTimeout(function() {
                $('#notifSucces').fadeOut(500);
            }, 5000);
    }
}

function deconnexion() {
    requeteAjax('/deconnexion');
    $('#wrapper').hide();
    toggleDiv("divConnexion");
}

// 1 - Test la validite des inputs 2- Test le mdp
function inscrireUtilisateur() {
    supprimerMessageErreur('formInscription');
    var json = $('#formInscription').serialize();
    var test = formToJson('#formInscription');
    var inputVide = [];

    if (testInputNonVide('formInscription', inputVide)) {
        if (test['mdpEntree'] != test['mdpConfirmation']) {
            $('#formInscription input[type="password"]').val('');
            $('#formInscription input[type="password"]').parent().wrap(
            "<div class=\"form-group has-error has-feedback\"></div>");
            $('#formInscription input[type="password"]')
            .after(
            "<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
            messageErreur('formInscription', 'Mot de passe incorrect.');
        } else {
            var retour = requeteAjax('/inscription', json, 'formInscription');
            if (retour.success) {
                toggleDiv("divConnexion");
            }
        }
    }
}

function updateMdp(){
    supprimerMessageErreur('formChangerMdp');
    var retour = requeteAjax('/changerMdp',formToJson($("#formChangerMdp")), 'formChangerMdp');
    if (retour.success) {
        $(".modal.in").modal("hide");
        $('#wrapper').hide();
        toggleDiv("divConnexion");
    }
}

/*-------------*
 *     JE      *
 *-------------*/

function creerJE(){
    supprimerMessageErreur('formCreerJE');
    var json = $('#formCreerJE').serialize();
    var inputVide = [];
    if (testInputNonVide('formCreerJE', inputVide)) {
        var retour = requeteAjax('/creerJE', json, 'formCreerJE');
        if (retour.success) {
            $('#alertIsJeActive').remove();
            $(".modal.in").modal("hide");
            chargerSelectJE();
            emptyInputs("formCreerJE");
        }
    }
}

function cloturerJE(){
    if (confirm('Etes-vous sur de vouloir cloturer la JE ?')) {
        retour = requeteAjax('/cloturerJE');
        if (retour.success) {
            chargerSelectJE();
        }
    } else {

    }
}
// TODO ici
function isJourneeActive(){
    $('#alertIsJeActive').remove();
    var je = requeteAjax('/isJourneeActive');
    if (! je.ouverte) {
        $('#ListeEntreprisesInvitablesPanel').prepend("<div id='alertIsJeActive' " +
                "class='alert alert-danger alert-dismissable fade in' role='alert'>" +
                "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>×</button>" +
                "<p><strong>Attention : </strong>Aucune journée d'entreprise n'est ouverte pour le moment. " +
                "Aucune invitation ne pourra être lancée ! </p></div>");
    }       
}

function chargerSelectJE() {
    var je = requeteAjax('/getJE');
    $('#selectJEaAfficherHistorique').empty();
    if (je.length == 0) {
        var option = $('<option name="idJournee">');
        $(option).val('0').text('Aucune JE d\'enregistrée');
        $('#selectJEaAfficherHistorique').append(option);   
    }else {
        // Prendre l'id de la je ouverte, ou la derniere de creer
        var idJEOuverte = je[je.length-1].idJournee;
        var testJourneeActive = false;
        for (var i=0; i < je.length; i++) {
            var option = $('<option>');
            if (je[i].ouverte == true) {
                testJourneeActive = true;
                idJEOuverte = je[i].idJournee;
                $('#selectJEaAfficherHistorique').attr('name', idJEOuverte);
            }
            $(option).val(je[i].idJournee).text(je[i].dateJournee);
            $('#selectJEaAfficherHistorique').append(option);
            
        }
        if(!testJourneeActive){
            $('#selectJEaAfficherHistorique').attr('name', -1);
        } else {
            $('#selectJEaAfficherHistorique option[value='+ idJEOuverte+']').css("background", "#32CD32");
        }
        
        $('#selectJEaAfficherHistorique').selectpicker('val', idJEOuverte);
    }
    $('#selectJEaAfficherHistorique').selectpicker('refresh');
    getJEParticipations($('#selectJEaAfficherHistorique').find(':selected').val());
}

$( "#selectJEaAfficherHistorique").change(function() {
    getJEParticipations($(this).find(':selected').val());
});

/*
 * 1 - DL toutes les participations + nom de l'entreprises correspondantes
 * 2 - DL l'id + nom + prenom des personnes presentes pour toutes les participations
 */

function getJEParticipations(idJournee) {
    $('#lesJEContainer div[class="alert alert-warning"]').remove();
    var json = 'idJournee=' + idJournee;
    var jes = requeteAjax('/getParticipationsEntreprises', json);
    if (! jQuery.isEmptyObject(jes)) {
        $('#lesJE').show();
        $('#lesJE_wrapper').show();
        $('#lesJE').DataTable( {
            destroy : true,
            data : jes,
            'fnCreatedRow': function (row, objet, index) {
                $(row).attr('data-objpart', JSON.stringify(objet));
            },
            columns: [
                {"data": "key.idParticipation"},
                {"data": "value"},
                {"data": "key.etat", "width" : "20%", "render" : function(data, type, objet) {
                    if (data === "INVITEE") {
                        return "<button type=\"button\" class=\"partButtonConfEtat btn btn-success btn-sm\" " +
                        "aria-labal=\"Center Align\"> " +
                        "<span class=\"glyphicon glyphicon-ok-circle\" aria-hidden=\"true\"></span>" +
                        " Confirmer</button>  <button type=\"button\" class=\"partButtonRefEtat btn btn-danger btn-sm\" " +
                        "aria-labal=\"Center Align\" style=\"width:90px;\"> " +
                        "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
                        " Refuser</button>";
                    } else if (data !== "REFUSEE") {
                        var select = "<select id='selectEtatParticipation"+objet.key.idParticipation+"' name='selectIdVersion"+objet.key.version+ "' class='partSelectEtat selectpicker show-tick' data-width='auto'>"+ 
                            "<option value='CONFIRMEE'>Confirmée</option>" +
                            "<option value='FACTUREE'>Facturée</option>" +
                            "<option value='PAYEE'>Payée</option>" +
                            "<option value='REFUSEE'>Refusée</option>" +
                            "</select>";
                        return select;
                    } else {
                        return "<fieldset disabled>" +
                        "<a type=\"button\" class=\"btn btn-outline btn-danger btn-sm\">" +
                        "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Refusée" +
                        "</a></fieldset>";
                    }
                }},
                {"data": "key.annulee", "render" : function(data, type, objet) {
                    if (data === false) {
                        return "<button type=\"button\" class=\"btn btn-outline btn-danger btn-sm\" " +
                        "aria-labal=\"Center Align\" " +
                        "onclick=\"annulerParticipation("+objet.key.idParticipation+")\" >"+
                    "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
                    " Annuler</button>";
                    } else {
                        return "<fieldset disabled>" +
                        "<button type=\"button\" class=\"btn btn-default btn-sm\">" +
                        "<span class=\"glyphicon glyphicon-check\" aria-hidden=\"true\"></span> Part. annulée" +
                        "</button></fieldset>";
                    }
                }},
                {"data": "key.etat", "render" : function(data, type, objet) {
                    if (data !== "INVITEE" && data !== "REFUSEE") {
                        return "<button type=\"button\" class=\"btn btn-outline btn-primary btn-sm\" " +
                                    "aria-labal=\"Center Align\" onclick=\"afficherPersonnelParticipantJournee("+objet.key.idParticipation+","+objet.key.idEntreprise+")\" " +
                                "data-toggle=\"modal\" data-target=\"#modalParticipantsEntreprise\">" +
                                "<span class=\"fa fa-edit \" aria-hidden=\"true\"></span>" +
                                " Voir participants</button>";
                    }else {
                        return "";
                    }
                }},
                {"data": "key.etat", "render" : function(data, type, objet) {
                    if (data !== "INVITEE" && data !== "REFUSEE") {
                        return "<button type=\"button\" class=\"btn btn-outline btn-primary btn-sm\" " +
                                "aria-labal=\"Center Align\" onclick=\"afficherModalModificationParticipants("+objet.key.idParticipation+","+objet.key.idEntreprise+")\" " +
                            "data-toggle=\"modal\" data-target=\"#modalAjouterPDCaParticipation\">" +
                            "<span class=\"fa fa-edit \" aria-hidden=\"true\"></span>" +
                            " Editer participants</button>";
                    }else {
                        return "";
                    }
                }}
            ],
            "rowCallback" : function(row, objet, index) {
                //$(row).find('option[value=' + objet.key.etat + ']').attr("selected", "selected");
                $(row).find("#selectEtatParticipation"+objet.key.idParticipation).selectpicker('val', objet.key.etat);
                $(row).find("#selectEtatParticipation"+objet.key.idParticipation).selectpicker('refresh');
                // Teste id correspond à id du name du select de la JE, si pas JE Fermee donc on disabled les boutons
                if ($('#selectJEaAfficherHistorique').attr('name') != objet.key.idJournee) {
                    $(row).find('button').attr('disabled', true);
                    $(row).find('select').attr('disabled', true);
                    $(row).find('td button[data-target="#modalParticipantsEntreprise"]').attr('disabled', false);

                }
            }
        });
    } else {
        $('#lesJE').hide();
        $('#lesJE_wrapper').hide();
        $('#lesJEContainer').append("<div class='alert alert-warning'>Aucune participation pour cette journée d'entreprise.</div>");
    }
}

function afficherPersonnelParticipantJournee(idParticipation, idEntreprise){
    $('#tableParticipantsEntrepriseContainer p').remove();
    var json = "idParticipation="+idParticipation+"&idEntreprise="+idEntreprise;
    var personnels = requeteAjax("/getPersonnesEtPersonnesInviteesParticipation", json);
    
    if (! jQuery.isEmptyObject(personnels[1])) {
        $('#tableParticipantsEntreprise').show();
        $('#tableParticipantsEntreprise').DataTable( {          
            destroy : true,
            responsive: {
                display: $.fn.dataTable.Responsive.display.modal()
            },
            data : personnels[1],
            "dom": "rt",        
            columns: [
                {"data": "nom"},
                {"data": "prenom"},
                {"data": "telephone"},
                {"data": "email"},
                {"data": "actif",
                    "render" : function(data, type, objet) {
                        if (data) {
                            return "<fieldset disabled>" +
                            "<button type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
                            "</button></fieldset>";
                        } else {
                            return "<fieldset disabled>" +
                            "<a type=\"button\" class=\"btn btn-outline btn-danger btn-xs\">" +
                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Inactif" +
                            "</a></fieldset>";
                        }
                    }
                }
            ],
        });
    } else {
        $('#tableParticipantsEntreprise').hide();
        $('#tableParticipantsEntrepriseContainer').append("<p>Aucun participant.</p>");

    }
}

function afficherModalModificationParticipants(idParticipation, idEntreprise){ 
    $('#personnelParticipantContainer p').remove();
    var json = "idParticipation="+idParticipation+"&idEntreprise="+idEntreprise;
    var personnels = requeteAjax("/getPersonnesEtPersonnesInviteesParticipation", json);
    
    if (! jQuery.isEmptyObject(personnels[0])) {
        $('#tablePersonnelParticipant').show();
        $('#tablePersonnelParticipant').DataTable( {          
            destroy : true,
            responsive: {
                display: $.fn.dataTable.Responsive.display.modal()
            },
            data : personnels[0],
            "dom": "rt",        
            columns: [
            	{
            		"data": "idPersonneContact", 
            		"render" : function(data, type, objet) {
            			return "<input id=checkIdPers" +data+ " type='checkbox' name='tabPersInvit[]' value=" + data + " />";
            		}
            	},
                {
            		"data": "nom"
            	},
                {
            		"data": "prenom"
            	},
                {
            		"data": "email"
            	},
                {
            		"data": "actif",
                    "render" : function(data, type, objet) {
                        if (data) {
                            return "<fieldset disabled>" +
                            "<button type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
                            "</button></fieldset>";
                        } else {
                            return "<fieldset disabled>" +
                            "<a type=\"button\" class=\"btn btn-outline btn-danger btn-xs\">" +
                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Inactif" +
                            "</a></fieldset>";
                        }
                    }
                }
            ],
        });
        $('#buttonUpdatePersonnelParticipant').attr("idParticipation", idParticipation);

        for (var i = 0; i < personnels[1].length; i++) {
            var idPers = personnels[1][i].idPersonneContact;
            $('#checkIdPers'+idPers).prop('checked', true).attr('disabled', 'true');
        }
        
    } else {
        $('#tablePersonnelParticipant').hide();
        $('#personnelParticipantContainer').append("<p>Aucun participant.</p>");
    }
}

function sauvegarderPersonnelParticipation() {
    var inputCheck = $("input[type='checkbox'][name='tabPersInvit[]']");
    var jsonTab = [];
    for (var i = 0; i < inputCheck.length; i++) {
        if ($(inputCheck[i]).is(':checked')) {
            if ( ! $(inputCheck[i]).attr('disabled')) {
                jsonTab.push($(inputCheck[i]).val());
            }
        }
    }
    var json = 'tabIdPersonneContact=' + JSON.stringify(jsonTab) + "&idParticipation="+$('#buttonUpdatePersonnelParticipant').attr("idParticipation");
    var listeIdEntreprise = requeteAjax('/insererPersonneContactParticipation', json);
    if (listeIdEntreprise.success) {
        $(".modal.in").modal("hide");
    }
}

function annulerParticipation(idParticipation){
    if (confirm('Etes-vous sur de vouloir annuler la participation n°'+idParticipation+" ?")){
        var json = 'idParticipation='+idParticipation;
        var retour = requeteAjax('/annulerParticipation', json);
        if (retour.success) {
            getJEParticipations($('#selectJEaAfficherHistorique').find(':selected').val());
        }
    } else {
    }
}

function updateEtatParticipation(idParticipation, etat, version){
        var json = 'idParticipation='+idParticipation+'&etat='+etat+'&version='+version;
        var retour = requeteAjax('/updateEtatParticipation', json);
        if (retour.success) {
            getJEParticipations($('#selectJEaAfficherHistorique').find(':selected').val());
        }
}


/*-------------*
 * INVITATION  *
 *-------------*/

function getEntreprisesInvitablesEtInvitees(){
    $('#tableInvitationsContainer div[class="alert alert-warning"]').remove();
    var entreprises = requeteAjax("/getEntreprisesInvitablesEtInvitees");
    if (! jQuery.isEmptyObject(entreprises[0])) {
        $('#tableInvitations').show();
        $('#tableInvitations_wrapper').show();
        $('#genererCSV').show();
        $('#tableInvitations').DataTable( {
            destroy : true,
            data : entreprises[0],
            'fnCreatedRow': function (row, objet, index) {
                $(row).attr('data-objent', JSON.stringify(objet));
            },
            columns: [
                {"data": "idEntreprise", "render" : function(data, type, objet) {
                    return "<input id=checkIdEnt" +data+ " type='checkbox' name='tabEntInvit[]' value=" + data + " />";
                }},
                {"data": "nomEntreprise"},
                {"data": "rue", "render" : function(data, type, objet) {
                        var adresse = objet.rue + ", " + objet.numero;
                        if (objet.boite !== "") {
                            adresse += " - Boite : " + objet.boite;
                        }
                        return adresse;
                    }
                },
                {"data": "commune"},
                {"data": "codePostal"},
                {"data": "datePremierContact", "render" : function(data, type, objet) {
                        return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
                    }},
                {"data": "dateDerniereParticipation", "render" : function(data, type, objet) {
                        if (data == null) {
                            return "";
                        } else {
                            return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
                        }
                    }}
            ]
        });
        // Je parcours mtn les id des entreprises déjà invitées
        for (var i = 0; i < entreprises[1].length; i++) {
            var idEnt = entreprises[1][i].idEntreprise;
            $('#checkIdEnt'+idEnt).prop('checked', true).attr('disabled', 'true');
        }
    }
    else{
        $('#tableInvitations').hide();
        $('#tableInvitations_wrapper').hide();
        $('#tableInvitationsContainer').append("<div class='alert alert-warning'>Aucune entreprise ne correspondant aux critères de sélections.</div>");
        $('#genererCSV').hide();
    }
}

function sauvegarderPartEtGenererCsv() {
    var inputCheck = $("input[type='checkbox'][name='tabEntInvit[]']");
    var jsonTab = [];
    for (var i = 0; i < inputCheck.length; i++) {
        if ($(inputCheck[i]).is(':checked')) {
            if ( ! $(inputCheck[i]).attr('disabled')) {
                jsonTab.push($(inputCheck[i]).val());
            }
        }
    }
    var json = 'tabIdEntreprise=' + JSON.stringify(jsonTab);
    var listeIdEntreprise = requeteAjax('/insererParticipation', json);
    var journees = requeteAjax('/getJE');
    var max = -1;
    for(var i = 0; i < journees.length; i++){
        if(max < journees[i].idJournee)
            max = journees[i].idJournee;
    }
    if (! jQuery.isEmptyObject(listeIdEntreprise)) {
        creerCSV(jsonTab, "listeEntreprisesInvitees");
        creerCSV2(max);
        getEntreprisesInvitablesEtInvitees();
    }
}

/*-------------*
 * Entreprise  *
 *-------------*/

function insererEntreprise() {
    supprimerMessageErreur('formInsererEntreprise');
    var json = 'entreprise=' + JSON.stringify(formToJson($('#formInsererEntreprise')));
    var inputVide = ["boite"];
    if (testInputNonVide('formInsererEntreprise', inputVide)) {
        var retour = requeteAjax('/insererEntreprise', json, 'formInsererEntreprise');
        if (retour.success) {
            $(".modal.in").modal("hide");
            getEntreprises(false);
            emptyInputs("formInsererEntreprise");
        }
    }
}

function getEntreprises(isRechercher) {
    $('#lesEntreprisesContainer div[class="alert alert-warning"]').remove();
	var data = "";
	if (isRechercher) {
		var json = 'entreprise=' + JSON.stringify(formToJson($('#formRechercherEntreprise')));
		data = requeteAjax('/rechercherEntreprise', json);
		$(".modal.in").modal("hide");
		emptyInputs('formRechercherEntreprise');
	} else {
		data = requeteAjax('/getEntreprisesEtCreateurs');
	}
    if (! jQuery.isEmptyObject(data)) {
        $('#lesEntreprises').show();
        $('#lesEntreprises_wrapper').show();
        $('#lesEntreprises').DataTable( {
            destroy : true,
            data : data,
            'fnCreatedRow': function (row, objet, index) {
                $(row).attr('data-objent', JSON.stringify(objet.key));
            },
            columns: [
                {"data": "key.nomEntreprise"},
                {"data": "key", "render" : function(data, type, objet) {
                        var adresse = data.rue + ", " + data.numero;
                        if (data.boite !== "") {
                            adresse += " - Boite : " + data.boite;
                        }
                        return adresse;
                    }
                },
                {"data": "key.commune"},
                {"data": "key.codePostal"},
                {"data": "key.datePremierContact", "render" : function(data, type, objet) {
                        return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
                    }},
                {"data": "key.dateDerniereParticipation", "render" : function(data, type, objet) {
                        if (data == null) {
                            return "";
                        } else {
                            return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
                        }
                    }},
                {"data": "key.idEntreprise", "render": function(data, type, objet) {
                        return "<button type=\"button\" class=\"entListeEmpl btn btn-outline btn-primary btn-xs\" " +
                            "aria-labal=\"Center Align\" data-toggle=\"modal\" data-target=\"#modalEntrepriseListeEmployes\">" +
                        "<span class=\"fa fa-file-text-o\" aria-hidden=\"true\"></span> Liste empl.</button>";
                    }},
                {"data": "key.idEntreprise", "render" : function(data, type, objet) {
                        return "<button type=\"button\" class=\"entListePart btn btn-outline btn-primary btn-xs\" " +
                            "aria-labal=\"Center Align\" data-toggle=\"modal\" data-target=\"#modalEntrepriseListeParticipations\">" +
                        "<span class=\"fa fa-file-text-o\" aria-hidden=\"true\"></span> Liste part.</button>";
                    }},
                {"data": "value"}
            ]
        });
    } else {
        $('#lesEntreprises').hide();
        $('#lesEntreprises_wrapper').hide();
        if (isRechercher) {
            $('#lesEntreprisesContainer').append("<div class='alert alert-warning'>Aucune entreprise ne correspond à votre recherche.</div>");
        } else {
        	$('#lesEntreprisesContainer').append("<div class='alert alert-warning'>Aucune entreprise d'enregistrée.</div>");
        }
    }
}

function afficherModalEntrepriseListeEmployes(idEntreprise) {
    $('#bodyEntrepriseEmployes p').remove();
    var json = 'idEntreprise=' + idEntreprise;
    var listePers = requeteAjax('/getPersonnesContactDeLEntreprise', json);
    if (! jQuery.isEmptyObject(listePers)) {
        $('#tableListeEmployes').show();
        $('#tableListeEmployes').DataTable( {
            destroy : true,
            data : listePers,
            "dom": "rt",
            columns: [
                {"data": "nom"},
                {"data": "prenom"},
                {"data": "email"},
                {"data": "telephone"}
            ]
        });
    } else {
        $('#tableListeEmployes').hide();
        $("#bodyEntrepriseEmployes").append("<p>Pas de personne de contact pour cette entreprise !</p>");
    }
}

function afficherModalEntrepriseListeParticipations(idEntreprise) {
    $('#bodyEntrepriseParticipations p').remove();
    var json = 'idEntreprise=' + idEntreprise;
    var listePart = requeteAjax('/getParticipationsJEPers', json);

    if (! jQuery.isEmptyObject(listePart)) {
        $('#tableListePart').show();
        $('#tableListePart').DataTable( {
            destroy : true,
            data : listePart,
            "dom": "rt",
            columns: [
                {"data": "key.idParticipation"},
                {"data": "value[0].key.dateJournee"},
                {"data": "value[0].key.ouverte", "render" : function(data, type, objet) {
                    if (data) {
                        return "En cours";
                    } else {
                        return "Fermée";
                    }
                }},
                {"data": "key.etat"},
                {"data": "key.annulee", "render" : function(data, type, objet) {
                    if (data) {
                        return "Oui";
                    } else {
                        return "Non";
                    }
                }},
                {"data": "value[0].value"}
            ]
        });
    } else {
        $('#tableListePart').hide();
        $("#bodyEntrepriseParticipations").append("<p>Aucune participation recensée pour cette entreprise !</p>");
    }
}

function afficherInformationEntreprise(idEntreprise){
    var json = "idEntreprise=" + idEntreprise;
    var informationEntreprise = requeteAjax('/getEntreprisesParId', json);

    if (! jQuery.isEmptyObject(informationEntreprise)) {
        $('#informationEntrepriseNompc').html(informationEntreprise.nomEntreprise);
        var adresse = informationEntreprise.rue+", "+informationEntreprise.numero+" ";
        if(informationEntreprise.boite!=""){
            adresse += "(N° boîte : "+informationEntreprise.boite+") "
        }
        adresse += informationEntreprise.codePostal+" "+informationEntreprise.commune;
        $('#informationEntrepriseAdressepc').html(adresse);
    }
}

/*                     *
 * PERSONNE DE CONTACT *
 *                     */

function insererPersonneContact() {
    supprimerMessageErreur('formInsererPersonneDeContact');
    var json = 'personne=' + JSON.stringify(formToJson($('#formInsererPersonneDeContact')));
    console.log(json);
    var inputVide = [];
    if (testInputNonVide('formInsererPersonneDeContact', inputVide)) {
        var retour = requeteAjax('/insererPersonneDeContact', json,'formInsererPersonneDeContact');
        if (retour.success) {
            $(".modal.in").modal("hide");
            getPersonnesContact(false);
            emptyInputs("formInsererPersonneDeContact");
        }
    }
}

function afficherModalModifierPersonneContact(objpersonne) {
    $('#formEditPersonneDeContact').data('objpersonne', objpersonne);
    var keys = Object.keys(objpersonne);
    for (var i=0; i<keys.length; i++) {
        var clef = keys[i];
        if (clef === 'idEntreprise') {
            $('#formEditPersonneDeContact select').find('option[value=' +objpersonne.idEntreprise+']').attr('selected','selected');
        }else {
            $('#formEditPersonneDeContact').find('input[name=' +clef+']').val(objpersonne[clef]);
        }
    }
}

function modifierPersonneContact() {
    supprimerMessageErreur('formEditPersonneDeContact');
    var objpersonne = formToJson($('#formEditPersonneDeContact'));
    objpersonne["idPersonneContact"] = $('#formEditPersonneDeContact').data('objpersonne').idPersonneContact;
    objpersonne["version"] = $('#formEditPersonneDeContact').data('objpersonne').version;

    var json = 'personne=' + JSON.stringify(objpersonne);
    var inputVide = [];
    if (testInputNonVide('formEditPersonneDeContact', inputVide)) {
        var retour = requeteAjax('/modifierPersonneContact', json, 'formEditPersonneDeContact');
        if (retour.success) {
            $(".modal.in").modal("hide");
            getPersonnesContact(false);
        }
    }
}

function desactiverPersContact(objPersonne) {
    if (confirm('Etes-vous sur de vouloir désactiver ' + objPersonne.nom + ' ' + objPersonne.prenom + ' ?')) {
        var json = 'idPers=' + objPersonne.idPersonneContact;
        var retour = requeteAjax('/desactiverPersContact', json);
        if (retour.success) {
            getPersonnesContact(false);
        }
    } else {
    }
}

function chargerSelectPersonneContact(idFormulaire) {
    var entreprises = requeteAjax('/getEntreprises');
    $('#'+ idFormulaire + ' select').empty();
    if (entreprises.length == 0) {
        var option = $('<option name="idEntreprise">');
        $(option).val('0').text('Aucune entreprise d\'enregistrée');
        $('#'+ idFormulaire + ' select').append(option);    
    }else {
        for (var i=0; i < entreprises.length; i++) {
            var option = $('<option name="idEntreprise">');
            $(option).val(entreprises[i].idEntreprise).text(entreprises[i].nomEntreprise);
            $('#'+ idFormulaire + ' select').append(option);
        }
    }
    $('#'+ idFormulaire + ' select').selectpicker('refresh');
}

function getPersonnesContact(isRechercher) {
    $('#lesPersonnesContactContainer div[class="alert alert-warning"]').remove();
    var data = "";
    if (isRechercher) {
    	var json = 'personne=' + JSON.stringify(formToJson($('#formRechercherContact')));
		data = requeteAjax('/rechercherPersonneDeContactSelonNomEtPrenom', json);
		$(".modal.in").modal("hide");
		emptyInputs('formRechercherContact');
    } else {
    	data = requeteAjax('/getPersonnesContactEtEntreprise');
    }
    if (! jQuery.isEmptyObject(data)) {
        $('#lesPersonnesContact').show();
        $('#lesPersonnesContact_wrapper').show();
        $('#lesPersonnesContact').DataTable( {
            destroy : true,
            data : data,
            'fnCreatedRow': function (row, objet, index) {
                $(row).attr('data-objpersonne', JSON.stringify(objet.key));
            },
            columns: [
                {"data": "key.nom"},
                {"data": "key.prenom"},
                {"data": "key.telephone"},
                {"data": "key.email"},
                {"data": "value" ,
                    "render": function(data, type, objet) {
                        return "<a id=\"buttonModalInformationEntreprise"+objet.key.idEntreprise+"\"" +
                        " class='persInfoEntreprise' data-toggle=\"modal\" data-target=\"#modalInformationEntreprise\" " +
                        "style=\"cursor: pointer; color: black;\">" +
                        "<i class=\"fa fa-info-circle fa-fw\"></i>"+data+"</a>";
                    }
                }, 
                {"data": "key.actif",
                    "render" : function(data, type, objet) {
                        if (data) {
                            return "<fieldset disabled>" +
                            "<button type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
                            "</button></fieldset>";
                        } else {
                            return "<fieldset disabled>" +
                            "<a type=\"button\" class=\"btn btn-outline btn-danger btn-xs\">" +
                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Inactif" +
                            "</a></fieldset>";
                        }
                    }
                },
                {"data": "key.actif", "width" : "20%",
                    "render": function(data, type, objet) {
                        var retour = "";
                        if (data) {
                            retour += "<button type=\"button\" class=\"persDesactiver btn btn-outline btn-danger btn-sm\" " +
                            "aria-labal=\"Center Align\" \">" +
                            "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
                            " Désactiver</button>";
                        } else {
                            retour += "<button type=\"button\" class=\"btn btn-default btn-sm\" disabled>" +
                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Désactiver" +
                            "</button>";
                        }
                        if (responsable) {
                            retour += " <button type=\"button\" class=\"persEditer btn btn-outline btn-default btn-sm\" " +
                            "data-toggle=\"modal\" data-target=\"#modalEditerPersonneDeContact\">" +
                            "<span class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></span> " +
                            "Editer</button>";
                        }
                        return retour;
                    }   
                }
            ]
        });
    } else {
        $('#lesPersonnesContact').hide();
        $('#lesPersonnesContact_wrapper').hide();
        if (isRechercher) {
        	$('#lesPersonnesContactContainer').append("<div class='alert alert-warning'>Aucune personne de contact ne correspondant à cette recherche.</div>");
        } else {
        	$('#lesPersonnesContactContainer').append("<div class='alert alert-warning'>Aucune personne de contact d'enregistrée.</div>");

        }
    }
}


/*******************************************************************************
 * FONCTIONS TEST INPUT - ERREURS *
 ******************************************************************************/

function supprimerMessageErreur(idFormulaire) {
    $('span[class="glyphicon glyphicon-remove form-control-feedback"]')
    .remove();
    $('#alertInscr').remove();
    $('#' + idFormulaire)
    .find('input')
    .each(
            function(i, el) {
                if ($(el).parent().parent().attr('class') == "form-group has-error has-feedback") {
                    $(el).parent().unwrap();
                }
            })
}

function messageErreur(idFormulaire, message) {
    $('#alertInscr').remove();
    $('#' + idFormulaire + ' div[class="messageErreur"]')
    .append(
            "<div id=\"alertInscr\" class=\"alert alert-danger\"><strong>Erreur !</strong> "
            + message + "</div>");
}

function testInputNonVide(idFormulaire, tabInputVide) {
    var reponse = true;

    $('#' + idFormulaire)
    .find('input[type=text]')
    .each(
            function(i, el) {
                if ($(el).val() == "" && ! tabInputVide.includes($(el).attr('name')) ) {
                    $(el)
                    .parent()
                    .wrap(
                            "<div class=\"form-group has-error has-feedback\"></div>");
                    $(el)
                    .after(
                            "<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
                    reponse = false;
                }
            });

    $('#' + idFormulaire)
    .find('input[type=password]')
    .each(
            function(i, el) {
                if ($(el).val() == "") {
                    $(el)
                    .parent()
                    .wrap(
                            "<div class=\"form-group has-error has-feedback\"></div>");
                    $(el)
                    .parent()
                    .after(
                            "<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
                    reponse = false;
                }
            });
    // Teste pour avoir un email et/ou un tel personne contact
    if (reponse && (idFormulaire === "formInsererPersonneDeContact" || idFormulaire === "formEditPersonneDeContact"))
        if ($('#' + idFormulaire+ ' input[type="tel"]').val() == "" && 
                $('#' + idFormulaire+ ' input[type="email"]').val() == "") {
            messageErreur(idFormulaire, 'L\'email et/où le téléphone doivent être remplie');
            return false;
        }
    
    // Si une erreur j'appelle messageErreur
    if (! reponse) {
        messageErreur(idFormulaire, 'Veuillez remplir les champs encadrés.');
    }
    return reponse;
}

function jsonInputErreurs(idFormulaire, json) {
    var keys = Object.keys(json);
    var messageE = 'Donnée(s) incorrecte(s) : ';
    for (var i = 0; i < keys.length; i++) {
        var clef = keys[i];
        var valeur = json[clef];
        $('#' + idFormulaire + ' input[name=' + clef + ']').parent().wrap(
        "<div class=\"form-group has-error has-feedback\"></div>");
        $('#' + idFormulaire + ' input[name=' + clef + ']')
        .after(
                "<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
        messageE += valeur;
    }
    messageErreur(idFormulaire, messageE);
}

/*******************************************************************************
 * FONCTIONS UTILITAIRES *
 ******************************************************************************/

//Transforme chaque donnee utilisée dans un form en un JSON
function formToJson($form) {
    var cible = {};
    $form.find('input[type=text]').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });
    
    $form.find('input[type=password]').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });
    
    $form.find('input[type=tel]').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });

    $form.find('input[type=hidden]').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });
    
    $form.find('input[type=email]').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });

    $form.find('input[type=radio]:checked').each(function(i, el) {
        cible[$(el).attr('name')] = $(el).val();
    });

    $form.find('input[type=checkbox]:checked').each(function(i, el) {
        cible[$(el).attr('value')] = $(el).val();
    });

    $form.find('select').each(function(i, el) {
        var selected = $(el).find('option:selected');
        cible[$(selected).attr('name')] = $(el).val();
    });
    return cible;
}

function getCurrentDisplaySection() {
    // $("div[id^='div'][style='display:block']").attr('id');
    // http://hmkcode.com/jquery-tutorial/jquery-selector-multiple-attribute-selector/
    // https://api.jquery.com/multiple-attribute-selector/
    var res;

    $("div[id^='div']").each(function() {
        if ($(this).css('display') == "block") {
            res = $(this).attr('id');
        }
    });
    return res;
}

function emptyInputs(DisplaySection) {
    $("#" + DisplaySection).find("input").each(function() {
        // test des types d'input à faire afin de faire le bon effacement.
        $(this).val('');
    });
}

function toggleDiv(divToDisplay) {
    var sectionEnCours = getCurrentDisplaySection();
    emptyInputs(sectionEnCours);
    $('#' + sectionEnCours).attr('style', 'display:none');
    $('#' + divToDisplay).attr('style', 'display:block');
    $('#' + divToDisplay + " input:first").focus();
}

function getSectionAffichee() {
    var res;
    $("#page-wrapper > div[id^='section-']").each(function() {
        if ($(this).css('display') == "block") {
            res = $(this).attr('id');
        }
    });
    return res;
}

function affichage(section) {
    var sectionEnCours = getSectionAffichee();
    $('#' + sectionEnCours).attr('style', 'display:none');
    $('#' + section).attr('style', 'display:block');
    $('#' + section + " input:first").focus();
}

function keyboardListener() {
    $('#formLogin input').keydown(function(event) {
        if (event.keyCode == 13) {
            event.preventDefault();
            chargerConnexion();
        }
    });

    $('#formInscription input').keydown(function(event) {
        if (event.keyCode == 13) {
            event.preventDefault();
            inscrireUtilisateur();
        }
    });
}
/*
function graphDonut(colors) {
    Morris.Donut({
        element: 'donut-example',
        data   : [
            {label: "Download Sales", value: 12},
            {label: "In-Store Sales", value: 30},
            {label: "Mail-Order Sales", value: 20}
        ]
    });
}

graphDonut();
*/

// Graphique DASHBOARD
function loadChart(){
        var option = {
            responsive: true,
        };
        var journees = requeteAjax('/nbrParticipationConfirmeeParJournees');

        if(journees.length > 0){
            var labeldata = new Array();
            var nbEntreprises = new Array();
            for(var i = 0; i < journees.length; i++){
                labeldata.push(journees[i].key.dateJournee);
                nbEntreprises.push(journees[i].value);
            }
            var data2 = {
                labels: labeldata,
                datasets: [
                {
                        label: "Les entreprises",
                        fillColor: "rgba(220,220,220,0.5)",
                        strokeColor: "rgba(220,220,220,0.8)",
                        highlightFill: "rgba(220,220,220,0.75)",
                        highlightStroke: "rgba(220,220,220,1)",
                        data: nbEntreprises
                    }
                ]
            }
        }
        else{
            var data2 = {
                labels: ["12-09-2015", "23-04-2016", "01-01-2017"],
                datasets: [
                    {
                        label: "Exemple",
                        fillColor: "rgba(220,220,220,0.5)",
                        strokeColor: "rgba(220,220,220,0.8)",
                        highlightFill: "rgba(220,220,220,0.75)",
                        highlightStroke: "rgba(220,220,220,1)",
                        data: [4, 10, 15]
                    },
                ]
            }
        }   
            // Get the context of the canvas element we want to select
            var ctx = document.getElementById("myChart").getContext('2d');
            var myBarChart = new Chart(ctx).Bar(data2, option);
            
}


// Creer le csv avec l
function creerCSV(json, nom){
    var entete = 'Nom Entreprise;Nom Personne;Prenom Personne; E-mail Personne';
    var csv="";
    csv += entete;
    csv += "\r\n";
    for(var i = 0; i < json.length; i++){
        var entreprise = requeteAjax('/getEntreprisesParId', "idEntreprise="+json[i]);
        var listePers = requeteAjax('/getPersonnesContactDeLEntreprise', "idEntreprise="+json[i]);
        for(var j = 0; j < listePers.length; j++){
            if(listePers[j].email != ""){
                csv +=  entreprise.nomEntreprise + ';' + listePers[j].nom + ";" + listePers[j].prenom + ";" + listePers[j].email + '\r\n';
            }
        }
    }
    creerDownload(csv, nom, "csv");
}

function creerCSV2(idJournee){
    var entreprises = requeteAjax('/getParticipationsEntreprises', "idJournee="+idJournee);
    var json = [];
    for(var i = 0; i < entreprises.length; i++){
        var entreprise = entreprises[i].key.idEntreprise;
        json.push(entreprise);
    }
    creerCSV(json, "listeEntreprisesParticipantes");
}

// Permettre download d'un fichier
function creerDownload(string, nom, type){
    var a         = document.createElement('a');
    a.href        = 'data:attachment/'+type+',' +  encodeURIComponent(string);
    a.target      = '_blank';
    a.download    = nom+"."+type;
    a.click();
}

function notifySucces(data) {
    $.notify({
        icon: 'fa fa-check-circle-o',
        title: '<b>Succès</b> : ',
        message: data
    },{
        type: 'success',
        allow_dismiss: true,
        placement: {
            from: "bottom",
            align: "right"
        },
        offset: {
            x: 20,
            y: 40
        },
        spacing: 10,
        z_index: 1031,
        delay: 5000,
    });
}

function notifyErreur(data) {
    $.notify({
        icon: 'fa fa-times-circle-o',
        title: '<b>ERREUR</b> : ',
        message: data
    },{
        type: 'danger',
        allow_dismiss: true,
        placement: {
            from: "bottom",
            align: "right"
        },
        offset: {
            x: 20,
            y: 40
        },
        spacing: 10,
        z_index: 1031,
        delay: 5000,
    });
}

function notifyErreurServeur(data) {
    $.notify({
        icon: 'glyphicon glyphicon-warning-sign',
        title: '<b>ERREUR serveur</b> : ',
        message: data
    },{
        type: 'danger',
        allow_dismiss: true,
        placement: {
            from: "bottom",
            align: "right"
        },
        offset: {
            x: 20,
            y: 40
        },
        spacing: 10,
        z_index: 1031,
        delay: 5000,
    });
}

function navUtilisateur() {
    $('#divStatutResponsable').attr('style', 'display:none');
    $('#divStatutUtilisateur').attr('style', 'display:block');
    $('#listeEntreprisesInvitables').attr('disabled',true);
    $('#genererCSV').attr("style", "display:none");
    $('#ListeEntreprisesInvitablesPanel').attr("style", "display:none");
    $("#StatutEntreprisesPanel").attr('class', 'col-lg-12');
    $('#navBarGestionJE').hide();
    $('#genererCSV').hide();
    $('#divListeEntreprisesInvitables').hide();
    $("#divStatutEntreprises").attr('class', 'col-lg-12');
}

function navResponsable() {
    $('#divStatutUtilisateur').attr('style', 'display:none');
    $('#divStatutResponsable').attr('style', 'display:block');
    $('#listeEntreprisesInvitables').attr('disabled',false);
    $('#genererCSV').attr("style", "display:block");
    $('#ListeEntreprisesInvitablesPanel').attr("style", "display:block");
    $("#StatutEntreprisesPanel").attr('class', 'col-lg-6');
    $('#navBarGestionJE').show();

}

function chargerProfil(utilisateur) {
    $('#modalChangerPasswordLabel').html("Profil de l'utilisateur : "+ utilisateur.pseudo);
    $('#disabledNom').attr("placeholder", utilisateur.nom.toUpperCase());
    $('#disabledPrenom').attr("placeholder",utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
    $('#disabledMail').attr("placeholder", utilisateur.email);
}
