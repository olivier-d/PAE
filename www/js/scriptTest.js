var app;

(function() {
	
	var App = function() {
		
		// Private Object
		var user;
		
		function getUser() {
			return user;
		}
		
		function setUser(newUser) {
			user = newUser;
		}
		
		function start(){
			getSession();
		}
		
		function isUser() {
			return user !== undefined && user.responsable === false;
		}
		
		function isResponsable() {
			return user !== undefined && user.responsable === true;
		}
		
		function getSession() {
			$.ajax({
                url: '/verifierConnexion',
                type: 'POST',
                success: function(resp) {
	                if (! jQuery.isEmptyObject(resp)) {
	                    setUser(resp);
	                    Nav.init();
	                } else {
	                    Nav.destroy();
	                }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                	notifyErreur(errorThrown);
                    deconnexion();
                }
            });
		}
		
		return {
			start: start,
			getUser: getUser,
			setUser: setUser,
			isUser: isUser,
			isResponsable: isResponsable
		}
	}
	
	var dataTablesLanguage = {
        search: 'Rechercher :',
        lengthMenu: 'Afficher _MENU_ entrées',
        info: "Entrée _START_ à _END_  sur  _TOTAL_ entrées au total",
        infoEmpty: "",
        infoFiltered: " - Recherche sur _MAX_ entrées",
        zeroRecords: "Aucune donnée ne correspond à cette recherche",
        paginate: {
            sPrevious: 'Précédent',
            sNext: 'Suivant'
        },
        emptyTable: 'Il n\'y a pas de données à afficher'
    };
	
	
	var Connexion = (function() {
		
		var $div = $('#divConnexion');
		var $form = $div.find('form');
		
		var $divInscription = $('#divInscription');
		var $butInscription = $div.find('#buttonPasEncoreInscrit');
		
		function bindAll() {
			$form.on('submit', submitHandler)
			$butInscription.on('click', function() {
				destroy();
				Inscription.init();
			});
		}
		
		function unbindAll() {
			$form.off('submit');
			$butInscription.off('click');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
		    var json = $form.serialize();
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
	            var utilisateur = Ajax.ajax('/connexion', json, $form);
	            if (! jQuery.isEmptyObject(utilisateur)) {
	            	console.log(utilisateur);
	            	app.setUser(utilisateur);
	            	destroy();
	            	Nav.init();
	                Utils.notifySucces('Connexion réussie');
	            }
		    }
		}
		
		function init() {
			bindAll();
			$div.show();
			$form.find('input:first').focus();
		}
		
		function destroy() {
			$div.hide();
			unbindAll();
			Utils.cleanForm($form);
		}
		
		return {
			init: init,
			destroy: destroy
		}
		
	})();
	
	var Inscription = (function() {
		
		var $div = $('#divInscription');
		var $form = $div.find('form');
		
		var $divConnexion = $('#divConnexion');
		var $butConnexion = $div.find('#buttonInscriptionRetour');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$butConnexion.on('click', function() {
				destroy();
				Connexion.init();
			});
		}
		
		function unbindAll() {
			$form.off('submit');
			$butConnexion.off('click');
		}
		
		function submitHandler(e) {
			e.preventDefault();
					
			Utils.supprimerMessageErreur($form);
		    var json = $form.serialize();
		    var inputVide = [];

		    if (Utils.testInputNonVide($form, inputVide)) {
		        if ($form.find('input[name="mdpEntree"]').val() !== $form.find('input[name="mdpConfirmation"]').val()) {
		            $form.find('input[type="password"]').val('');
		            $form.find('input[type="password"]').parent().wrap("<div class=\"form-group has-error has-feedback\"></div>");
		            $form.find('input[type="password"]').after("<span class=\"glyphicon glyphicon-remove form-control-feedback\" aria-hidden=\"true\"></span>");
		            Utils.messageErreur($form, 'Mot de passe incorrect.');
		        } else {
		            var retour = Ajax.ajax('/inscription', json, $form);
		            if (retour.success) {
		                destroy();
		                Connexion.init();
		            }
		        }
		    }
		}
		
		function init() {
			bindAll();
			$div.show();
			$form.find('input:first').focus();
		}
		
		function destroy() {
			$div.hide();
			unbindAll();
			Utils.cleanForm($form);
		}
		
		return {
			init: init,
			restroy: destroy
		}
	})();
	
	var Nav = (function() {
		
		var $navDashboard = $('#navBarDashboard');
		var $navJournee = $('#navBarJournees');
		var $navJEHistorique = $('#navBarHistorique');
		// Responsable
		var $navJEGestion = $('#navBarGestionJE');
		var $navEntreprise = $('#navBarEntreprises');
		var $navPersonne = $('#navBarPersonnesDeContact');
		
		var $butDeconnection = $('#menuSeDeconnecter');
		
		var $wrapper = $('#wrapper');

		
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
		
		function chargerProfil() {
			var utilisateur = app.getUser();
		    $('#modalChangerPasswordLabel').html("Profil de l'utilisateur : "+ utilisateur.pseudo);
		    $('#disabledNom').attr("placeholder", utilisateur.nom.toUpperCase());
		    $('#disabledPrenom').attr("placeholder",utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
		    $('#disabledMail').attr("placeholder", utilisateur.email);
            $('#nomUtilisateur').html(utilisateur.nom.toUpperCase() + " " + utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
		}
		
		function objetDestroy() {
			var $section = Utils.getSectionAffichee();
			switch ($section.attr('id')) {
				case 'section-dashboard':
					Dashboard.destroy();
					break;
				case 'section-journees':
					Journee.destroy();
					break;
				case 'section-gestionJE':
					Gestion.destroy();
					break;
				case 'section-entreprises':
					Entreprise.destroy();
					break;
				case 'section-personnesDeContact':
					Personne.destroy();
					break;
			}
		}
		
		function bindAll() {
			$navDashboard.on('click', function() {
				objetDestroy();
				Dashboard.init();
			});
			$navJournee.on('click', function() {
				objetDestroy();
				Journee.init();
			});
			$navJEHistorique.on('click', function() {
				objetDestroy();
				Journee.init();
			});
			$navJEGestion.on('click', function() {
				objetDestroy();
				Gestion.init();
			});
			$navEntreprise.on('click', function() {
				objetDestroy();
				Entreprise.init();
			});
			$navPersonne.on('click', function() {
				objetDestroy();
				Personne.init();
			});
			
			$butDeconnection.on('click', destroy);
			
			// TODO - rajouter tous les modals
			$('#section-entreprises').find('button[data-target="#modalCreerEntreprise"]').on('click', function() {
				ModalCreerEntreprise.init();
			});
			
			$('#section-personnesDeContact').find('button[data-target="#modalCreerPersonneDeContact"]').on('click', function() {
				ModalCreerPersonneDeContact.init();
			});
			
		}
		
		function unbindAll() {
			$navDashboard.off('click');
			$navJournee.off('click');
			$navJEHistorique.off('click');
			$navJEGestion.off('click');
			$navEntreprise.off('click');
			$navPersonne.off('click');
			$butDeconnection.off('click');
		}
		
		function init() {
			bindAll();
			chargerProfil();
			
			if (app.isUser()) {
				navUtilisateur();
			}
			if (app.isResponsable()) {
				navResponsable();
			}
			$wrapper.show();
			Dashboard.init();
		}
		
		function destroy() {
			unbindAll();
			objetDestroy();
			$wrapper.hide();
			app.setUser(undefined);
			Connexion.init();
		}
		
		return {
			init: init,
			destroy: destroy
		}
		
	})();
	
	
	var Dashboard = (function() {
		
		var $div = $('#section-dashboard');
		
		function loadChart(){
	        var option = {
	            responsive: true,
	        };
	        var journees = Ajax.ajax('/nbrParticipationConfirmeeParJournees');
	
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
		
		
		function init() {
			loadChart();
			$div.show();
		}
		
		function destroy() {
			$div.hide();
		}
		
		return {
			init: init,
			destroy: destroy
		}
	})();
	
	var Journee = (function() {
		var table;
				
		var $div = $('#section-journees');
		var $table = $div.find('#lesJE');
		var $selectJE = $div.find('#selectJE');
		
		var $buttonCreerJE = $div.find('#buttonNouvelleJE');
		var $buttonCloturerJE = $div.find('#buttonCloturerJE');
				
		function bindAll() {
			$selectJE.on('change', reloadTable);	
			
			$table.on('click', 'tbody td .annuler-participation', function() {
				annulerParticipation($(this).closest('tr').data('objet').key.idParticipation);
			});
			$table.on('click', 'tbody td .confirmer-participation', function() {
				$tr = $(this).closest('tr');
				modifierEtatParticipation($tr.data('objet').key.idParticipation, 'CONFIRMEE', $tr.data('objet').key.version);
			});
			$table.on('click', 'tbody td .refuser-participation', function() {
				$tr = $(this).closest('tr');
				modifierEtatParticipation($tr.data('objet').key.idParticipation, 'REFUSEE', $tr.data('objet').key.version);
			});
			$table.on('change', 'tbody td .etat-participation', function() {
				$tr = $(this).closest('tr');
				modifierEtatParticipation($tr.data('objet').key.idParticipation, $(this).find(':selected').val(), $tr.data('objet').key.version);
			});
				
			$table.on('click', 'tbody td .voir-participants', function() {
				var objet = $(this).closest('tr').data('objet');
				ModalAfficherParticipants.setInformation(objet.key.idParticipation, objet.key.idEntreprise);
			});
				
			$buttonCreerJE.on('click', function() {
				ModalCreerJE.init();
			});
			$buttonCloturerJE.on('click', cloturerJE);
		}
		
		function unbindAll() {
			$selectJE.off('change');
			$table.off('click');
			$buttonCreerJE.off('click');
			$buttonCloturerJE.off('click');
		}
		
		function modifierEtatParticipation(idParticipation, etat, version) {
			var json = 'idParticipation='+idParticipation+'&etat='+etat+'&version='+version;
	        var retour = Ajax.ajax('/updateEtatParticipation', json);
	        if (retour.success) {
	        	updateTable();
	        }
		}
		
		function annulerParticipation(idParticipation) {
			if (confirm('Etes-vous sur de vouloir annuler la participation n°'+idParticipation+" ?")){
		        var json = 'idParticipation='+idParticipation;
		        var retour = Ajax.ajax('/annulerParticipation', json);
		        if (retour.success) {
		        	updateTable();
		        }
		    } else {
		    }
		}
		
		function cloturerJE() {
			if (confirm('Etes-vous sur de vouloir cloturer la JE ?')) {
		        retour = Ajax.ajax('/cloturerJE');
		        if (retour.success) {
		        	refreshSelectJE();
		        }
		    } else {
		    }
		}
		
		function refreshSelectJE() {
			loadSelectJE();
		}
		
		function loadSelectJE() {
			$selectJE.empty();
			var je = Ajax.ajax('/getJE');
		    
			if (je.length == 0) {
		        var option = $('<option name="idJournee">');
		        $(option).val('0').text('Aucune JE d\'enregistrée');
		        $selectJE.append(option);   
		    }else {
		        // Prendre l'id de la je ouverte, ou la derniere de creer
		        var idJEOuverte = je[je.length-1].idJournee;
		        var testJourneeActive = false;
		        for (var i=0; i < je.length; i++) {
		            var option = $('<option>');
		            if (je[i].ouverte == true) {
		                testJourneeActive = true;
		                idJEOuverte = je[i].idJournee;
		                $selectJE.attr('name', idJEOuverte);
		            }
		            $(option).val(je[i].idJournee).text(je[i].dateJournee);
		            $selectJE.append(option);
		            
		        }
		        if(!testJourneeActive){
		        	$selectJE.attr('name', -1);
		        } else {
		        	$selectJE.find('option[value='+ idJEOuverte+']').css("background", "#32CD32");
		        }
		        
		        $selectJE.selectpicker('val', idJEOuverte);
		    }
			$selectJE.selectpicker('refresh');
		}
		
		// createdRow - info : idEntreprise
		function initializeTable() {
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: '/getParticipationsEntreprises',
	                type: "POST",
	                data: function ( d ) {
	                    d.idJournee = $selectJE.find(':selected').val();
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            createdRow: function (row, objet, index) {
	                $(row).attr('data-objet', JSON.stringify(objet));
	            },
	            columns: [
	            	{
	            		data: "key.idParticipation"
	            	},
	                {
	            		data: "value"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
		                    if (row.key.etat === "INVITEE") {
		                        return "<button type=\"button\" class=\"confirmer-participation btn btn-success btn-sm\" " +
			                        "aria-labal=\"Center Align\"> " +
			                        "<span class=\"glyphicon glyphicon-ok-circle\" aria-hidden=\"true\"></span>" +
			                        " Confirmer</button>  " +
			                        "<button type=\"button\" class=\"refuser-participation btn btn-danger btn-sm\" " +
			                        "aria-labal=\"Center Align\" style=\"width:90px;\"> " +
			                        "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
			                        " Refuser</button>";
		                    } else if (row.key.etat !== "REFUSEE") {
		                        var select = "<select class='etat-participation selectpicker show-tick' data-width='auto'>"+ 
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
	            		},
	            		width: "20%"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
		                    if (row.key.annulee === false) {
		                        return "<button type=\"button\" class=\"annuler-participation btn btn-outline btn-danger btn-sm\" " +
			                        "aria-labal=\"Center Align\"><span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span> Annuler</button>";
		                    } else {
		                        return "<fieldset disabled>" +
			                        "<button type=\"button\" class=\"btn btn-default btn-sm\">" +
			                        "<span class=\"glyphicon glyphicon-check\" aria-hidden=\"true\"></span> Part. annulée" +
			                        "</button></fieldset>";
		                    }
	            		}
	            	},
	                {
	            		data: function ( row, type, val, meta ) { 
		                    if (row.key.etat !== "INVITEE" && row.key.etat !== "REFUSEE") {
		                        return "<button type=\"button\" class=\"voir-participants btn btn-outline btn-primary btn-sm\" " +
		                                    "aria-labal=\"Center Align\" " +
		                                "data-toggle=\"modal\" data-target=\"#modalAfficherParticipants\">" +
		                                "<span class=\"fa fa-edit \" aria-hidden=\"true\"></span>" +
		                                " Voir participants</button>";
		                    }else {
		                        return "";
		                    }
	            		}
	            	},
	                {
	            		data: function ( row, type, val, meta ) { 
		                    if (row.key.etat !== "INVITEE" && row.key.etat !== "REFUSEE") {
		                        return "<button type=\"button\" class=\"btn btn-outline btn-primary btn-sm\" " +
		                                "aria-labal=\"Center Align\" onclick=\"afficherModalModificationParticipants("+row.key.idParticipation+","+row.key.idEntreprise+")\" " +
		                            "data-toggle=\"modal\" data-target=\"#modalAjouterPDCaParticipation\">" +
		                            "<span class=\"fa fa-edit \" aria-hidden=\"true\"></span>" +
		                            " Editer participants</button>";
		                    }else {
		                        return "";
		                    }
	            		}
	            	}
	            ],
	            rowCallback: function(row, objet, index) {
	                $(row).find('select').selectpicker('val', objet.key.etat);
	                $(row).find('select').selectpicker('refresh');
	                // Teste id correspond à id du name du select de la JE, si pas JE Fermee donc on disabled les boutons
	                if ($selectJE.attr('name') != objet.key.idJournee) {
	                    $(row).find('button').attr('disabled', true);
	                    $(row).find('select').attr('disabled', true);
	                    $(row).find('td button[data-target="#modalAfficherParticipants"]').attr('disabled', false);
	                }
	            }
	        });
		}
		
		function reloadTable(e) {
            e.preventDefault();
            table.destroy();
            initializeTable();
        }
		
		// Ne sera normalement jamais utilise...
	    function updateTable() {
            table.ajax.reload();
        }
		
		
		function init() {
			bindAll();
			loadSelectJE();
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
			$div.show();
		}
		
		function destroy() {
			unbindAll();
			$div.hide();
		}
		
		return {
			init: init,
			refreshSelectJE: refreshSelectJE,
			destroy: destroy
		}
		
	})();
	
	var Gestion = (function() {
		
		var table;
		
		var $div = $('#section-gestionJE');
		var $table = $div.find('#tableInvitations');
		
		var $buttonSaveAndCSV = $div.find('#genererCSV');
		
				
		function bindAll() {						
			$buttonSaveAndCSV.on('click', sauvegarderInvitations);
		}
		
		function unbindAll() {
			$buttonSaveAndCSV.off('click');
		}
		
		function sauvegarderInvitations() {
		    var inputCheck = $table.find('input[name="tabInvitations[]"]');
		    var jsonTab = [];
		    for (var i = 0; i < inputCheck.length; i++) {
		        if ($(inputCheck[i]).is(':checked')) {
		            if ( ! $(inputCheck[i]).attr('disabled')) {
		                jsonTab.push($(inputCheck[i]).val());
		            }
		        }
		    }
		    var json = 'tabIdEntreprise=' + JSON.stringify(jsonTab);
		    var listeIdEntreprise = Ajax.ajax('/insererParticipation', json);
		    
		    // TODO - remplacer par l'id de la je ouverte + Refaire les csv
		    /*
		    var journees = requeteAjax('/getJE');
		    var max = -1;
		    for(var i = 0; i < journees.length; i++){
		        if(max < journees[i].idJournee)
		            max = journees[i].idJournee;
		    }
		    if (! jQuery.isEmptyObject(listeIdEntreprise)) {
		        creerCSV(jsonTab, "listeEntreprisesInvitees");
		        creerCSV2(max);
		        reloadTable();
		    }
		    */
		}
		
		// Obliger de destroy et de rappeller la methode pour avoir les data
		function initializeTable() {
			var data = Ajax.ajax('/getEntreprisesInvitablesEtInvitees');
			table = $table.DataTable( {
	            serverSide: false,
	            data: data[0],
	            autoWidth: false,
	            language: dataTablesLanguage,
	            createdRow: function (row, objet, index) {
	                $(row).attr('data-objet', JSON.stringify(objet));
	            },
	            columns: [
	            	{
	            		data: function ( row, type, val, meta ) {
	            			return "<input id=checkIdEnt" + row.idEntreprise + " type='checkbox' name='tabInvitations[]' value=" + row.idEntreprise + " />";
	            		}
	            	},
	                {
	            		data: "nomEntreprise"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
	                        var adresse = row.rue + ", " + row.numero;
	                        if (row.boite !== "") {
	                            adresse += " - Boite : " + row.boite;
	                        }
	                        return adresse;
	                    }
	                },
	                {
	                	data: "commune"
	                },
	                {
	                	data: "codePostal"
	                },
	                {
	                	data: "datePremierContact", 
	                	render : function(data, type, objet) {
	                        return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
	                    }
	                },
	                {
	                	data: "dateDerniereParticipation", 
	                	render : function(data, type, objet) {
	                        if (data == null) {
	                            return "";
	                        } else {
	                            return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
	                        }
	                    }
	                }
	            ]
	        });
			cocherInvitations(data);
		}
		
		function cocherInvitations(data) {
			for (var i = 0; i < data[1].length; i++) {
	            var idEnt = data[1][i].idEntreprise;
	            $('#checkIdEnt'+idEnt).prop('checked', true).attr('disabled', 'true');
	        }
		}
		
		function reloadTable() {
            table.destroy();
            initializeTable();
        }
		
		// Ne sera normalement jamais utilise...
	    function updateTable() {
            table.ajax.reload();
        }
		
		
		function init() {
			bindAll();
			if (!table) {
				initializeTable();
			} else {
				reloadTable();
			}
			$div.show();
		}
		
		function destroy() {
			unbindAll();
			$div.hide();
		}
		
		return {
			init: init,
			destroy: destroy
		}
	})();
	
	var Entreprise = (function() {
		
		var table;
		
		var $div = $('#section-entreprises');
		var $table = $div.find('#lesEntreprises');
				
		function bindAll() {
			$table.on('click', 'tbody td .entListeEmpl', function() {
				ModalListeEmployes.setIdEntreprise($(this).closest('tr').data('info'));
			});
			
			$table.on('click', 'tbody td .entListePart', function() {
				ModalListeParticipations.setIdEntreprise($(this).closest('tr').data('info'));
			});
			
		}
		
		function unbindAll() {
			$table.off('click');
		}
		
		function reloadTable(e) {
			e.preventDefault();
			table.destroy();
			initializeTable();
		}
		
		// createdRow - info : idEntreprise
		function initializeTable() {	       
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: '/getEntreprisesEtCreateurs',
	                type: "POST",
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            createdRow: function (row, objet, index) {
	                $(row).attr('data-info', objet.key.idEntreprise);
	            },
	            columns: [
	                {
	                	data: "key.nomEntreprise"
	                },
	                {
	                	data:  function ( row, type, val, meta ) {
	                        var adresse = row.key.rue + ", " + row.key.numero;
	                        if (row.key.boite !== "") {
	                            adresse += " - Boite : " + row.key.boite;
	                        }
	                        return adresse;
	                    }
	                },
	                {
	                	data: "key.commune"
	                },
	                {
	                	data: "key.codePostal"
	                },
	                {
	                	data: "key.datePremierContact",
	                	render : function(data, type, objet) {
	                        return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
	                    }
	                },
	                {
	                	data: "key.dateDerniereParticipation", 
	                	render : function(data, type, objet) {
	                        if (data == null) {
	                            return "";
	                        } else {
	                            return data.dayOfMonth + "/" + data.monthValue + "/" + data.year;
	                        }
	                    }
	                },
	                {
	                	data: function ( row, type, val, meta ) { 
	                        return '<button type="button" class="entListeEmpl btn btn-outline btn-primary btn-xs" ' +
	                            'aria-labal="Center Align" data-toggle="modal" data-target="#modalEntrepriseListeEmployes">' +
	                            '<span class="fa fa-file-text-o" aria-hidden="true"></span> Liste empl.</button>';
	                    }
	                },
	                {
	                	data: function ( row, type, val, meta ) { 
	                        return '<button type="button" class="entListePart btn btn-outline btn-primary btn-xs" ' +
	                            'aria-labal="Center Align" data-toggle="modal" data-target="#modalEntrepriseListeParticipations">' +
	                            '<span class="fa fa-file-text-o" aria-hidden="true"></span> Liste part.</button>';
	                    }
	                },
	                {
	                	data: "value"
	                }
	            ]
	        });
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function init() {
			bindAll();
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
			$div.show();
		}
		
		function destroy() {
			unbindAll();
			$div.hide();
		}
		
		return {
			init: init,
			updateTable: updateTable,
			destroy: destroy
		}
	})();
	
	var Personne = (function() {
		
		var table;
		
		var $div = $('#section-personnesDeContact');
		var $table = $div.find('#lesPersonnesContact');
				
		function bindAll() {
			$table.on('click', '.infoEntreprise', function() {
				ModalInformationEntreprise.setIdEntreprise($(this).closest('tr').data('objet').idEntreprise);
			});
			
			$table.on('click', '.persDesactiver', function() {
				desactiverPersonne($(this).closest('tr').data('objet'));
			});
			
			$table.on('click', 'tbody td button[data-target="#modalEditerPersonneDeContact"]', function() {
				ModalEditerPersonneDeContact.setPersonne($(this).closest('tr').data('objet'));
			});
		}
		
		function unbindAll() {
			$table.off('click');
		}
		
		function desactiverPersonne(objet) {
			if (confirm('Etes-vous sur de vouloir désactiver ' + objet.nom + ' ' + objet.prenom + ' ?')) {
		        var json = 'idPers=' + objet.idPersonneContact;
		        var retour = Ajax.ajax('/desactiverPersContact', json);
		        if (retour.success) {
		        	updateTable();
		        }
		    } else {
		    }
		}
		
		// createdRow - info : idEntreprise
		function initializeTable() {
            if (app.isResponsable()) {
				table = $table.DataTable( {
		            serverSide: false,
		            ajax: {
		                url: '/getPersonnesContactEtEntreprise',
		                type: "POST",
		                dataSrc: ""
		            },
		            autoWidth: false,
		            language: dataTablesLanguage,
		            createdRow: function (row, objet, index) {
		                $(row).attr('data-objet', JSON.stringify(objet.key));
		            },
		            columns: [
		            	{
		            		data: "key.nom",
		            		title: 'Nom'
		            	},
		                {
		            		data: "key.prenom",
		            		title: 'Prénom'
		            	},
		                {
		            		data: "key.telephone",
		            		title: 'Téléphone'
		            	},
		                {
		            		data: "key.email",
		            		title: 'E-mail'
		            	},
		                {
		                	data: function ( row, type, val, meta ) { 
		                		return '<a class="infoEntreprise" data-toggle="modal" data-target="#modalInformationEntreprise"' +
		                        	' style="cursor: pointer; color: black;"><i class="fa fa-info-circle fa-fw"></i>'+row.value+'</a>';
		                    },
		                    title: 'Nom entreprise'
		                }, 
		                {
		                	data: function ( row, type, val, meta ) { 
		                        if (row.key.actif) {
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
		                    },
		                    title: 'Actif'
		                },
		                {
		                	data: function ( row, type, val, meta ) {
		                		if (row.key.actif) {
		                            return "<button type=\"button\" class=\"persDesactiver btn btn-outline btn-danger btn-sm\" " +
			                            "aria-labal=\"Center Align\" \">" +
			                            "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
			                            " Désactiver</button>";
		                        } else {
		                            return "<button type=\"button\" class=\"btn btn-default btn-sm\" disabled>" +
			                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Désactiver" +
			                            "</button>";
		                        }
		                	},
		                	title: 'Désactiver',
		                	orderable: false 
		                }, 
		                {
		                	data: function ( row, type, val, meta ) {
	                            return " <button type=\"button\" class=\"btn btn-outline btn-default btn-sm\" " +
		                            "data-toggle=\"modal\" data-target=\"#modalEditerPersonneDeContact\">" +
		                            "<span class=\"fa fa-pencil-square-o\" aria-hidden=\"true\"></span> " +
		                            "Editer</button>";
		                	},
		                	title: 'Editer',
		                	orderable: false 
		                }
		            ]
		        });
            } else {
            	table = $table.DataTable( {
		            serverSide: false,
		            ajax: {
		                url: '/getPersonnesContactEtEntreprise',
		                type: "POST",
		                dataSrc: ""
		            },
		            autoWidth: false,
		            language: dataTablesLanguage,
		            createdRow: function (row, objet, index) {
		                $(row).attr('data-objet', JSON.stringify(objet.key));
		            },
		            columns: [
		            	{
		            		data: "key.nom",
		            		title: 'Nom'
		            	},
		                {
		            		data: "key.prenom",
		            		title: 'Prénom'
		            	},
		                {
		            		data: "key.telephone",
		            		title: 'Téléphone'
		            	},
		                {
		            		data: "key.email",
		            		title: 'E-mail'
		            	},
		                {
		                	data: function ( row, type, val, meta ) { 
		                		return '<a class="infoEntreprise"  data-toggle="modal" data-target="#modalInformationEntreprise"' +
		                        ' style="cursor: pointer; color: black;"><i class="fa fa-info-circle fa-fw"></i>'+row.value+'</a>';
		                    },
		                    title: 'Nom entreprise'
		                }, 
		                {
		                	"data": function ( row, type, val, meta ) { 
		                        if (row.key.actif) {
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
		                    },
		                    title: 'Actif'
		                },
		                {
		                	"data": function ( row, type, val, meta ) {
		                        if (row.key.actif) {
		                            return "<button type=\"button\" class=\"persDesactiver btn btn-outline btn-danger btn-sm\" " +
		                            "aria-labal=\"Center Align\" \">" +
		                            "<span class=\"glyphicon glyphicon-remove-circle\" aria-hidden=\"true\"></span>" +
		                            " Désactiver</button>";
		                        } else {
		                            return "<button type=\"button\" class=\"btn btn-default btn-sm\" disabled>" +
		                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Désactiver" +
		                            "</button>";
		                        }
		                    },
		                    title: 'Désactiver'
		                }  
		            ]
		        });
            }
		}
		
		function reloadTable() {
			table.destroy();
			initializeTable();
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function init() {
			bindAll();
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
			$div.show();
		}
		
		function destroy() {
			unbindAll();
			$div.hide();
		}
		
		return {
			init: init,
			updateTable: updateTable,
			destroy: destroy
		}
		
		
	})();
	
	/**
	 * MODAL
	 */
	
	var ModalCreerJE = (function() {
		
		var isDisplayed = false;
		
		var $modal = $('#modalCreerJE');
		var $form = $modal.find('form');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$modal.on('shown.bs.modal', function () {
				$form.find('input:first').focus();
			});
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function unbindAll() {
			$form.off('submit');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
		    var json = $form.serialize();
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('/creerJE', json, $form);
		        if (retour.success) {
		        	destroy();
		        	Journee.refreshSelectJE();
		        }
		    }
		}
		
		function init() {
			if (isDisplayed) return ;
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
		}
		
		return {
			init: init,
			destroy: destroy
		}
	})();
	
	var ModalAfficherParticipants = (function() {
		var table;
		var idParticipation;
		var idEntreprise;
		
		var $modal = $('#modalAfficherParticipants');
		var $table = $modal.find('table');
		
		
		function initializeTable() {		
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: '/getPersonnesEtPersonnesInviteesParticipation',
	                type: "POST",
	                data: function ( d ) {
	                    d.idParticipation = idParticipation,
	                    d.idEntreprise = idEntreprise
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	            	{
	            		"data": function ( row, type, val, meta ) {
	            			console.log(row);
	            			return row[1].nom;
	            		}
	            	},
	                {
	            		"data": function ( row, type, val, meta ) {
	            			return row[1].prenom;
	            		}
	            	},
	                {
	            		"data": function ( row, type, val, meta ) {
	            			return row[1].telephone;
	            		}
	            	},
	                {
	            		"data": function ( row, type, val, meta ) {
	            			return row[1].email;
	            		}
	            	},
	                {
	            		"data": function ( row, type, val, meta ) {
	            			if (row[1].actif) {
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
	            ]
	        });
	        
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function setInformation(newParticipation, newIdEntreprise) {
			idParticipation = newParticipation;
			idEntreprise = newIdEntreprise;
			console.log('-> ' + idParticipation + ' ' + idEntreprise);
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
		}
		
		return {
			setInformation: setInformation
		}
	})();
	
	var ModalCreerEntreprise = (function() {
		
		var isDisplayed = false;
		
		var $modal = $('#modalCreerEntreprise');
		var $form = $modal.find('form');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$modal.on('shown.bs.modal', function () {
				$form.find('input:first').focus();
			});
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function unbindAll() {
			$form.off('submit');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
		    var json = 'entreprise=' + JSON.stringify(Utils.formToJson($form));
		    var inputVide = ["boite"];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('/insererEntreprise', json, $form);
		        if (retour.success) {
		            Entreprise.updateTable();
		        	destroy();
		        }
		    }
		}
		
		function init() {
			if (isDisplayed) return ;
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
		}
		
		return {
			init: init,
			destroy: destroy
		}
	})();
	
	var ModalListeEmployes = (function() {
		
		var table;
		var idEntreprise;
		
		var $modal = $('#modalEntrepriseListeEmployes');
		var $table = $modal.find('table');
		
		
		function initializeTable() {		
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: '/getPersonnesContactDeLEntreprise',
	                type: "POST",
	                data: function ( d ) {
	                    d.idEntreprise = getIdEntreprise()
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	            	{"data": "nom"},
	                {"data": "prenom"},
	                {"data": "email"},
	                {"data": "telephone"}
	            ]
	        });
	        
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function getIdEntreprise() {
			return idEntreprise;
		}
		
		function setIdEntreprise(newIdEntreprise) {
			idEntreprise = newIdEntreprise;
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
		}
		
		return {
			setIdEntreprise: setIdEntreprise
		}
	})();
	
	var ModalListeParticipations = (function() {
		
		var table;
		var idEntreprise;
		
		var $modal = $('#modalEntrepriseListeParticipations');
		var $table = $modal.find('table');
		
			
		function initializeTable() {		
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: '/getParticipationsJEPers',
	                type: "POST",
	                data: function ( d ) {
	                    d.idEntreprise = getIdEntreprise()
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	            	{
	            		data: "key.idParticipation"
	            			
	            	},
	                {
	            		data: "value[0].key.dateJournee"
	            	},
	            	{
	            		data: function ( row, type, val, meta ) {
		                    if (row.value[0].key.ouverte) {
		                        return "En cours";
		                    } else {
		                        return "Fermée";
		                    }
	            		}
	                },
	                {
	            		data: "key.etat"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
		                    if (row.key.annulee) {
		                        return "Oui";
		                    } else {
		                        return "Non";
		                    }
	            		}
	            	},
	                {
	            		data: "value[0].value"
	            	}
	            ]
	        });
	        
		}	        
		
		function reloadTable(e) {
			e.preventDefault();
			table.destroy();
			initializeTable();
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function getIdEntreprise() {
			return idEntreprise;
		}
		
		function setIdEntreprise(newIdEntreprise) {
			idEntreprise = newIdEntreprise;
			if (!table) {
				initializeTable();
			} else {
				updateTable();
			}
		}
		
		return {
			setIdEntreprise: setIdEntreprise
		}
	})();
	
	
	var ModalCreerPersonneDeContact = (function() {
		var isDisplayed = false;
		
		var $modal = $('#modalCreerPersonneDeContact');
		var $form = $modal.find('form');
		var $select = $modal.find('select');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$modal.on('shown.bs.modal', function () {
				$form.find('input:first').focus();
			});
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function unbindAll() {
			$form.off('submit');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
		    var json = 'personne=' + JSON.stringify(Utils.formToJson($form));
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('/insererEntreprise', json, $form);
		        if (retour.success) {
		            Personne.updateTable();
		        	destroy();
		        }
		    }
		}
		
		function refreshSelectEntreprises() {
			loadSelectEntreprises();
		}
		
		function loadSelectEntreprises() {
			$select.empty();
			var entreprises = Ajax.ajax('/getEntreprises');
		    
			if (entreprises.length == 0) {
		        var option = $('<option name="idEntreprise">');
		        $(option).val('0').text('Aucune entreprise d\'enregistrée');
		        $select.append(option);    
		    }else {
		        for (var i=0; i < entreprises.length; i++) {
		            var option = $('<option name="idEntreprise">');
		            $(option).val(entreprises[i].idEntreprise).text(entreprises[i].nomEntreprise);
		            $select.append(option);
		        }
		    }
		    $select.selectpicker('refresh');
		}
		
		function init() {
			refreshSelectEntreprises();
			if (isDisplayed) return ;
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
		}
		
		return {
			init: init,
			destroy: destroy
		}
	})();
	
	
	var ModalInformationEntreprise = (function() {
		var idEntreprise;
		
		var $modal = $('#modalInformationEntreprise');
		
		var $divNomEntreprise = $modal.find('#informationEntrepriseNompc');
		var $divAdresse = $modal.find('#informationEntrepriseAdressepc');
		
		
		function setIdEntreprise(newIdEntreprise) {
			idEntreprise = newIdEntreprise;
			afficherInfo();
		}
		
		function afficherInfo() {
			var json = "idEntreprise=" + idEntreprise;
		    var informationEntreprise = Ajax.ajax('/getEntreprisesParId', json);
		    
			$divNomEntreprise.html(informationEntreprise.nomEntreprise);
	        var adresse = informationEntreprise.rue + ", " + informationEntreprise.numero + " ";
	        if(informationEntreprise.boite !== ""){
	            adresse += "(N° boîte : " + informationEntreprise.boite + ") ";
	        }
	        adresse += informationEntreprise.codePostal + " " + informationEntreprise.commune;
	        $divAdresse.html(adresse);
		}
		
		return {
			setIdEntreprise: setIdEntreprise
		}
	})();
	
	var ModalEditerPersonneDeContact = (function() {
		var isDisplayed = false;
		var personne;
		
		var $modal = $('#modalEditerPersonneDeContact');
		var $form = $modal.find('form');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$modal.on('shown.bs.modal', function () {
				$form.find('input:first').focus();
			});
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function unbindAll() {
			$form.off('submit');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
			var personne = Utils.formToJson($form);
			personne["idPersonneContact"] = personne.idPersonneContact;
			personne["version"] = personne.version;

		    var json = 'personne=' + JSON.stringify(personne);
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('/modifierPersonneContact', json, $form);
		        if (retour.success) {
		            Personne.reloadTable();
		            destroy();
		        }
		    }
		}
		
		function loadFormEdit() {
		    var keys = Object.keys(personne);
		    for (var i=0; i<keys.length; i++) {
		        var clef = keys[i];
		        $form.find('input[name=' +clef+']').val(personne[clef]); 
		    }
		}
		
		function setPersonne(newPersonne) {
			if (!isDisplayed) {
				init();
			}
			personne = newPersonne;
			loadFormEdit();
		}
		
		
		function init() {
			if (isDisplayed) return ;
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
		}
		
		return {
			setPersonne: setPersonne
		}
	})();
	
	var Ajax = (function() {
		
		function ajax(route, json, $form) {
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
			                    Nav.destroy();
			                } else {
			                    objets.success = "ok";
			                    $(".modal.in").modal("hide");
			                    Utils.notifySucces(data);
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
			                Utils.notifyErreur(obj.fail);
			            }else{
			                Utils.jsonInputErreurs($form, obj);
			            }
			            break;
			        case 402:
			            // Erreur session expiree
			            Nav.destroy();
			            Utils.notifyErreur(errorThrown);
			            break;
			        case 500:
			            Utils.notifyErreurServeur(errorThrown);
			            break;
			        }
			    }
		    });
		    return objets;
		}
		
		return {
			ajax: ajax
		}
	})();
	
	app = new App();
	app.start();
	
})();