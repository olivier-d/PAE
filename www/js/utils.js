/**
 * ajax(route, json, idFormulaire)
 * showDiv($div)
 * showSection($section)
 * cleanForm($form)
 * notifySucces(data)
 * notifyErreur(data)
 * notifyErreurServeur(data)
 */
var Utils = (function() {
	
	// GESTION DES ERREURS
	
	function supprimerMessageErreur($form) {
	    $('span[class="glyphicon glyphicon-remove form-control-feedback"]')
	    .remove();
	    $('#alertInscr').remove();
	    $form
	    .find('input')
	    .each(
	            function(i, el) {
	                if ($(el).parent().parent().attr('class') == "form-group has-error has-feedback") {
	                    $(el).parent().unwrap();
	                }
	            })
	}

	function messageErreur($form, message) {
	    $('#alertInscr').remove();
	    $form.find('div[class="messageErreur"]')
	    .append("<div id=\"alertInscr\" class=\"alert alert-danger\"><strong>Erreur !</strong> " + message + "</div>");
	}

	// TODO - problème avec l'id du form personnes de contact !! si on supprime les ids
	
	function testInputNonVide($form, tabInputVide) {
	    var reponse = true;

	    $form
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
	    
	    
	    $form
	    .find('input[type=textarea]')
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
	    

	    $form
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
	    if (reponse && ($form.attr('id') === "formInsererPersonneDeContact" || $form.attr('id') === "formEditPersonneDeContact"))
	        if ($form.find(' input[type="tel"]').val() == "" && 
	                $form.find( ' input[type="email"]').val() == "") {
	            messageErreur($form, 'L\'email et/où le téléphone doivent être remplie');
	            return false;
	        }
	    
	    // Si une erreur j'appelle messageErreur
	    if (! reponse) {
	        messageErreur($form, 'Veuillez remplir les champs encadrés.');
	    }
	    return reponse;
	}

	function jsonInputErreurs($form, json) {
	    var keys = Object.keys(json);
	    var messageE = 'Donnée(s) incorrecte(s) : ';
	    for (var i = 0; i < keys.length; i++) {
	        var clef = keys[i];
	        var valeur = json[clef];
	        $form.find('input[name=' + clef + ']').parent().wrap("<div class=\"form-group has-error has-feedback\"></div>");
	        $form.find(' input[name=' + clef + ']').after("<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
	        messageE += valeur;
	    }
	    messageErreur($form, messageE);
	}
	
	
	// UTILISER POUR INSCRIPTION - CONNEXION
	function showDiv($div) {
	    var sectionEnCours = getCurrentDisplaySection();
	    $('#' + sectionEnCours).hide();
	    $div.show();
	    $div.find('input:first').focus();
	}
	
	function getCurrentDisplaySection() {
	    var res;

	    $("div[id^='div']").each(function() {
	        if ($(this).css('display') == "block") {
	            res = $(this).attr('id');
	        }
	    });
	    return res;
	}
	
	function showSection($section) {
		var $sectionEnCours = getSectionAffichee();
		$sectionEnCours.attr('style', 'display:none');
		$section.attr('style', 'display:block');
		$section.find('input:first').focus();
		//$('#' + section + " input:first").focus();
	}
	
	function getSectionAffichee() {
	    var $res;
	    $("#page-wrapper > div[id^='section-']").each(function() {
	        if ($(this).css('display') == "block") {
	            $res = $('#'+ $(this).attr('id'));
	        }
	    });
	    
	    return $res;
	}
	
	function formToJson(formulaire) {
	    var cible = {};
	    $(formulaire).find('input[type=text]').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });
	    
	    $(formulaire).find('input[type=password]').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });
	    
	    $(formulaire).find('input[type=tel]').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });

	    $(formulaire).find('input[type=hidden]').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });
	    
	    $(formulaire).find('input[type=email]').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });

	    $(formulaire).find('input[type=radio]:checked').each(function(i, el) {
	        cible[$(el).attr('name')] = $(el).val();
	    });

	    $(formulaire).find('input[type=checkbox]:checked').each(function(i, el) {
	        cible[$(el).attr('value')] = $(el).val();
	    });

	    $(formulaire).find('select').each(function(i, el) {
	        var selected = $(el).find('option:selected');
	        cible[$(selected).attr('name')] = $(el).val();
	    });
	    return cible;
	}
	
	function cleanForm($form) {
		$form.find(':input').val('');
		supprimerMessageErreur($form);
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
	
	return {
		testInputNonVide: testInputNonVide,
		jsonInputErreurs: jsonInputErreurs,
		supprimerMessageErreur: supprimerMessageErreur,
		messageErreur: messageErreur,
		showSection: showSection,
		getSectionAffichee: getSectionAffichee,
		formToJson: formToJson,
		cleanForm: cleanForm,
		notifySucces: notifySucces,
		notifyErreur: notifyErreur,
		notifyErreurServeur: notifyErreurServeur
	}
})();