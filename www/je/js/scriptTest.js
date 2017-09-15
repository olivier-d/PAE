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
                url: 'verifierConnexion',
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
                	//Utils.notifyErreur(errorThrown);
                    Nav.destroy();
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
	            var utilisateur = Ajax.ajax('connexion', json, $form);
	            if (! jQuery.isEmptyObject(utilisateur)) {
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
		            var retour = Ajax.ajax('inscription', json, $form);
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
		
		//var $navDashboard = $('#navBarDashboard');
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
            $('#nomUtilisateur').html(utilisateur.nom.toUpperCase() + " " + utilisateur.prenom.charAt(0).toUpperCase() + utilisateur.prenom.slice(1));
		}
		
		function objetDestroy() {
			var $section = Utils.getSectionAffichee();
			if ($section !== undefined) {
				switch ($section.attr('id')) {
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
		}
		
		function bindAll() {
			
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
			
			$('#section-entreprises').find('button[data-target="#modalCreerEntreprise"]').on('click', function() {
				ModalCreerEntreprise.init();
			});
			
			$('#section-personnesDeContact').find('button[data-target="#modalCreerPersonneDeContact"]').on('click', function() {
				ModalCreerPersonneDeContact.init();
			});
			
		}
		
		function unbindAll() {
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
			Journee.init();
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
				
			$table.on('click', 'tbody td .voir-participants', function() {
				var objet = $(this).closest('tr').data('objet');
				ModalAfficherParticipants.setInformation(objet.key.idParticipation, objet.key.idEntreprise);
			});
			
			$table.on('click', 'tbody td .editer-participants', function() {
				var objet = $(this).closest('tr').data('objet');
				ModalEditerParticipants.setInformation(objet.key.idParticipation, objet.key.idEntreprise);
			});
			
			// COMMENTAIRE
			$table.on('click', 'tbody td .ajouter-commentaire', function() {
				var objet = $(this).closest('tr').data('objet');
				ModalCommentaire.setInformation(objet.key.idParticipation, undefined, objet.key.version);
			});
			
			$table.on('click', 'tbody td .modifier-commentaire', function() {
				var objet = $(this).closest('tr').data('objet');
				ModalCommentaire.setInformation(objet.key.idParticipation, objet.key.commentaire, objet.key.version);
			});
				
			$buttonCreerJE.on('click', function() {
				ModalCreerJE.init();
			});
			
			$buttonCloturerJE.on('click', cloturerJE);
		}
		
		function unbindAll() {
			$selectJE.off('change');
			$table.off('click');
			$table.off('change');
			$buttonCreerJE.off('click');
			$buttonCloturerJE.off('click');
		}
		
		function modifierEtatParticipation(idParticipation, etat, version) {
			console.log(idParticipation + " - " + etat + " - " + version);
			var json = 'idParticipation='+idParticipation+'&etat='+etat+'&version='+version;
	        var retour = Ajax.ajax('updateEtatParticipation', json);
	        if (retour.success) {
	        	updateTable();
	        }
		}
		
		function annulerParticipation(idParticipation) {
			if (confirm('Etes-vous sur de vouloir annuler la participation n°'+idParticipation+" ?")){
		        var json = 'idParticipation='+idParticipation;
		        var retour = Ajax.ajax('annulerParticipation', json);
		        if (retour.success) {
		        	updateTable();
		        }
		    } else {
		    }
		}
		
		function cloturerJE() {
			if (confirm('Etes-vous sur de vouloir cloturer la JE ?')) {
		        retour = Ajax.ajax('cloturerJE');
		        if (retour.success) {
		        	refreshSelectJE();
		        }
		    } else {
		    }
		}
		
		function refreshSelectJE() {
			loadSelectJE();
			reloadTable();
		}
		
		function loadSelectJE() {
			$selectJE.empty();
			var je = Ajax.ajax('getJE');
		    
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
	                url: 'getParticipationsEntreprises',
	                type: "POST",
	                data: function ( d ) {
	                    d.idJournee = $selectJE.find(':selected').val();
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            scrollX: true,
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
		                        return '<button type="button" class="confirmer-participation btn btn-success btn-sm" aria-labal="Center Align"><span class="glyphicon glyphicon-ok-circle" aria-hidden="true"></span> Confirmer</button> ' +
		                        		'<button type="button" class="refuser-participation btn btn-danger btn-sm" aria-labal="Center Align" style="width:90px;">' +
		                        		'<span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span> Refuser</button>';
		                    } else if (row.key.etat !== "REFUSEE") {
		                        return '<select class="etat-participation selectpicker show-tick" data-width="auto"><option value="CONFIRMEE">Confirmée</option><option value="FACTUREE">Facturée</option><option value="PAYEE">Payée</option><option value="REFUSEE">Refusée</option></select>';
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
		                        return '<button type="button" class="annuler-participation btn btn-outline btn-danger btn-sm" "aria-labal="Center Align">' +
		                        		'<span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span> Annuler</button>';
		                    } else {
		                        return '<fieldset disabled><button type="button" class="btn btn-default btn-sm"><span class="glyphicon glyphicon-check" aria-hidden="true"></span> Part. annulée</button></fieldset>';
		                    }
	            		}
	            	},
	                {
	            		data: function ( row, type, val, meta ) { 
		                    if (row.key.etat !== "INVITEE" && row.key.etat !== "REFUSEE") {
		                        return "<button type=\"button\" class=\"voir-participants btn btn-outline btn-primary btn-sm\" \"aria-labal=\"Center Align\" " +
		                                "data-toggle=\"modal\" data-target=\"#modalAfficherParticipants\"><span class=\"fa fa-edit \" aria-hidden=\"true\"></span> Voir participants</button>";
		                    }else {
		                        return "";
		                    }
	            		}
	            	},
	                {
	            		data: function ( row, type, val, meta ) { 
		                    if (row.key.etat !== "INVITEE" && row.key.etat !== "REFUSEE") {
		                        return '<button type="button" class="editer-participants btn btn-outline btn-primary btn-sm" "aria-labal="Center Align" data-toggle="modal" data-target="#modalEditerParticipants">' +
		                            	'<span class="fa fa-edit " aria-hidden="true"></span> Editer participants</button>';
		                    }else {
		                        return "";
		                    }
	            		}
	            	},
	            	{
	            		data: function ( row, type, val, meta ) { 
		                    if (row.key.commentaire === "") {
		                        return '<button type="button" class="ajouter-commentaire btn btn-outline btn-primary btn-sm" "aria-labal="Center Align" data-toggle="modal" data-target="#modalCommentaire">' +
		                            	'<span class="fa fa-plus-square fa-fw " aria-hidden="true"></span> Ajouter</button>';
		                    }else {
		                        return '<button type="button" class="modifier-commentaire btn btn-primary btn-sm" "aria-labal="Center Align" data-toggle="modal" data-target="#modalCommentaire">' +
                            			'<span class="fa fa-edit " aria-hidden="true"></span> Afficher</button>';
		                    }
	            		}
	            	}
	            ],
	            rowCallback: function(row, objet, index) {
	                $(row).find('select').selectpicker('val', objet.key.etat);
	                $(row).find('select').selectpicker('refresh');
	                
	                // Teste id correspond à id du name du select de la JE, si pas JE Fermee donc on disabled les boutons
	                if ($selectJE.attr('name') != objet.key.idJournee) {
	                    $(row).find('button').not('.voir-participants').not('.ajouter-commentaire').not('.modifier-commentaire').attr('disabled', true);
	                    $(row).find('select').attr('disabled', true);
	                }
	                var $select = $(row).find('select');
	                $select.on('change', function() {
	    				$tr = $(this).closest('tr');
	    				modifierEtatParticipation($tr.data('objet').key.idParticipation, $(this).find(':selected').val(), $tr.data('objet').key.version);
	    			});
	            }
	        });
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
			updateTable: updateTable,
			destroy: destroy
		}
		
	})();
	
	var Gestion = (function() {
		
		var table;
		
		var $div = $('#section-gestionJE');
		var $table = $div.find('#tableInvitations');
		
		var $buttonSaveAndCSV = $div.find('#genererCSV');
		
		var $checkboxAll = $div.find('#allCheckbox');
		
				
		function bindAll() {						
			$buttonSaveAndCSV.on('click', sauvegarderInvitations);
			
			$checkboxAll.on('change',function() {
				if ($(this).is(":checked")) {
					$('input[name="tabInvitations[]"]').not('[disabled]').prop('checked', true);
				} else {
					$('input[name="tabInvitations[]"]').not('[disabled]').prop('checked', false);
				}
			});
			
			$table.on('click', 'tbody td .voir-commentaire', function() {
				var $tr = $(this).closest('tr');
				ModalListeCommentaires.setIdEntreprise($tr.data('objet').idEntreprise);
			})
		}
		
		function unbindAll() {
			$buttonSaveAndCSV.off('click');
			$checkboxAll.off('change');
			$checkboxAll.prop('checked', false);
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
		    
			$.ajax({
                url: 'insererParticipation',
                type: 'POST',
                data: json,
                success: function(resp) {
					var element = document.createElement('a');
					element.setAttribute('href', 'data:text/csv;charset=UTF-8,' + encodeURIComponent(resp));
					element.setAttribute('download', 'NouvellesEntreprisesInvitees.csv');

					element.style.display = 'none';
					document.body.appendChild(element);

					element.click();

					document.body.removeChild(element);
					updateTable();
					Utils.notifySucces('Sauvegarde réussie');
                },
                error: function(jqXHR, textStatus, errorThrown) {
                	notifyErreur(errorThrown);
                }
            });

            $.ajax({
		        url: 'getCsvToutLeMonde',
		        type: 'POST',
				success: function(resp2) {
					var element2 = document.createElement('a');
					element2.setAttribute('href', 'data:text/csv;charset=UTF-8,' + encodeURIComponent(resp2));
					element2.setAttribute('download', 'ToutesLesEntreprisesInvitees.csv');

					element2.style.display = 'none';
					document.body.appendChild(element2);

					element2.click();

					document.body.removeChild(element2);
					},
	            error: function(jqXHR, textStatus, errorThrown) {
	        	    notifyErreur(errorThrown);
	            }
	        });
		}
		
		// Obliger de destroy et de rappeller la methode pour avoir les data
		function initializeTable() {
			var tabIdInvitables;
			var tabIdCommentaires;
			var tabIdInvitees;
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: 'getEntreprisesInvitablesEtInvitees',
	                type: "POST",
	                dataSrc: function ( json ) {
	                	tabIdInvitables = json[1];
	                	tabIdCommentaires = json[2];
	                	tabIdInvitees = json[3];
	                	return json[0];
	                }
	            },
	            autoWidth: false,
	            scrollX: true,
	            language: dataTablesLanguage,
	            createdRow: function (row, objet, index) {
	                $(row).attr('data-objet', JSON.stringify(objet));
	            },
	            columns: [
	            	{
	            		orderable: false,
	            		data: function ( row, type, val, meta ) {
	            			return '<div class="checkbox checkbox-primary"><input id=checkIdEnt' + row.idEntreprise + ' type="checkbox" name="tabInvitations[]" value=' + row.idEntreprise + ' class="styled styled-primary"><label></label></div>';
	            			//return "<input id=checkIdEnt" + row.idEntreprise + " type='checkbox' name='tabInvitations[]' value=" + row.idEntreprise + " />";
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
	                },
	                {
	            		orderable: false,
	            		data: function ( row, type, val, meta ) {
	                        return "";
	                    }
	                }
	                
	            ],
	            rowCallback: function(row, objet, index) {
	         	    
	            	// On regarde pour les entreprises non invitables
        			$node = this.api().row(row).nodes().to$();

            		for (var i=0; i<tabIdInvitables.length; i++ ) {
	            		if (objet.idEntreprise === tabIdInvitables[i].idEntreprise) {
	            			$node.addClass('success');
	            		}
	            	}
	            	
	            	// On regarde pour les commentaires
	            	for (var i=0; i<tabIdCommentaires.length; i++ ) {
	            		if (objet.idEntreprise === tabIdCommentaires[i].idEntreprise) {
	            			$('td:eq(7)', row).html('<button type="button" class="voir-commentaire btn btn-danger btn-sm" "aria-labal="Center Align" data-toggle="modal" data-target="#modalListeCommentaires"><span class="fa fa-exclamation-circle " aria-hidden="true"></span> Commentaire(s)</button>' );
	            		}
	            	}
	            	
	            	// Table invitees
	            	for (var i=0; i<tabIdInvitees.length; i++) {
        				if (objet.idEntreprise === tabIdInvitees[i].idEntreprise) {
        					$(row).find('#checkIdEnt' + objet.idEntreprise).attr('disabled', true).prop('checked', true);
        				}
        			}
	            },
	            order: [[1, 'asc']]
	        });
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
	                url: 'getEntreprisesEtCreateurs',
	                type: "POST",
	                dataSrc: ""
	            },
	            autoWidth: false,
	            language: dataTablesLanguage,
	            scrollX: true,
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
		        var retour = Ajax.ajax('desactiverPersContact', json);
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
		                url: 'getPersonnesContactEtEntreprise',
		                type: "POST",
		                dataSrc: ""
		            },
		            autoWidth: false,
		            scrollX: true,
		            language: dataTablesLanguage,
		            createdRow: function (row, objet, index) {
		                $(row).attr('data-objet', JSON.stringify(objet.key));
		            },
		            columns: [
		            	{
		            		data: "key.sexe",
		            		render : function(data, type, objet) {
		            			if (data === 'H') {
		            				return '<i class="fa fa-male"></i>';
		            			} else {
		            				return '<i class="fa fa-female"></i>';
		            			}
		            		},
		            		title: '',
		            		orderable: false
		            	},
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
			                            "<a type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
			                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
			                            "</a></fieldset>";
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
		            ],
		            order: [[1, 'asc']]
		        });
            } else {
            	table = $table.DataTable( {
		            serverSide: false,
		            ajax: {
		                url: 'getPersonnesContactEtEntreprise',
		                type: "POST",
		                dataSrc: ""
		            },
		            autoWidth: false,
		            scrollX: true,
		            language: dataTablesLanguage,
		            createdRow: function (row, objet, index) {
		                $(row).attr('data-objet', JSON.stringify(objet.key));
		            },
		            columns: [
		            	{
		            		data: "key.sexe",
		            		render : function(data, type, objet) {
		            			console.log(data);
		            			if (data === 'H') {
		            				return '<i class="fa fa-male"></i>';
		            			} else {
		            				return '<i class="fa fa-female"></i>';
		            			}
		            		},
		            		title: '',
		            		orderable: false
		            	},
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
		                            "<a type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
		                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
		                            "</a></fieldset>";
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
		            ],
		            order: [[1, 'asc']]
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
		        var retour = Ajax.ajax('creerJE', json, $form);
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
	                url: 'getPersonnesEtPersonnesInviteesParticipation',
	                type: "POST",
	                data: function ( d ) {
	                    d.idParticipation = idParticipation,
	                    d.idEntreprise = idEntreprise
	                },
	                dataSrc: function ( json ) {
	                	return json[1];
	                }
	            },
	            autoWidth: false,
	            scrollX: true,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	                {
	            		data: "nom"
	            	},
	                {
	            		data: "prenom"
	            	},
	            	{
	            		data: "telephone"
	            	},
	                {
	            		data: "email"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
	                        if (row.actif) {
	                            return "<fieldset disabled>" +
	                            "<a type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
	                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
	                            "</a></fieldset>";
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
	
	
	var ModalEditerParticipants = (function() {
		var table;
		var idParticipation;
		var idEntreprise;
		
		var isDisplayed = false;
		var $modal = $('#modalEditerParticipants');
		var $form = $modal.find('form');
		var $table = $modal.find('table');
		
		var $checkboxAll2 = $modal.find('#allCheckbox2');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			
			$checkboxAll2.on('change',function() {
				if ($(this).is(":checked")) {
					$('input[name="tabPersInvit[]"]').not('[disabled]').prop('checked', true);
				} else {
					$('input[name="tabPersInvit[]"]').not('[disabled]').prop('checked', false);
				}
			});
			
			
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			var inputCheck = $modal.find("input[type='checkbox'][name='tabPersInvit[]']");
		    var jsonTab = [];
		    for (var i = 0; i < inputCheck.length; i++) {
		        if ($(inputCheck[i]).is(':checked')) {
		            if ( ! $(inputCheck[i]).attr('disabled')) {
		                jsonTab.push($(inputCheck[i]).val());
		            }
		        }
		    }
		    var json = 'tabIdPersonneContact=' + JSON.stringify(jsonTab) + "&idParticipation=" + idParticipation;
		    console.log(json);
		    var listeIdEntreprise = Ajax.ajax('insererPersonneContactParticipation', json);
		    if (listeIdEntreprise.success) {
		        
		    }
		}
		
		function initializeTable() {
			var tabPersInvitees;
			table = $table.DataTable( {
	            serverSide: false,
	            ajax: {
	                url: 'getPersonnesEtPersonnesInviteesParticipation',
	                type: "POST",
	                data: function ( d ) {
	                    d.idParticipation = idParticipation,
	                    d.idEntreprise = idEntreprise
	                },
	                dataSrc: function ( json ) {
	                	tabPersInvitees = json[1];
	                	return json[0];
	                }
	            },
	            autoWidth: false,
	            scrollX: true,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	            	{	
	            		orderable: false,
	            		data: function ( row, type, val, meta ) { 
	            			//return "<input id=checkIdPers" +row.idPersonneContact+ " type='checkbox' name='tabPersInvit[]' value=" + row.idPersonneContact + " />";
	            			return '<div class="checkbox checkbox-primary"><input id=checkIdPers' + row.idPersonneContact + ' type="checkbox" name="tabPersInvit[]" value=' + row.idPersonneContact + ' class="styled styled-primary"><label></label></div>';

	            		}
	            	},
	            	{
	            		data: "nom"
	            	},
	                {
	            		data: "prenom"
	            	},
	                {
	            		data: "telephone"
	            	},
	                {
	            		data: "email"
	            	},
	                {
	            		data: function ( row, type, val, meta ) {
	            			if (row.actif) {
	                            return "<fieldset disabled>" +
	                            "<a type=\"button\" class=\"btn btn-outline btn-success btn-xs\">" +
	                            "<span class=\"fa fa-check\" aria-hidden=\"true\"></span> Actif" +
	                            "</a></fieldset>";
	                        } else {
	                            return "<fieldset disabled>" +
	                            "<a type=\"button\" class=\"btn btn-outline btn-danger btn-xs\">" +
	                            "<span class=\"fa fa-times\" aria-hidden=\"true\"></span> Inactif" +
	                            "</a></fieldset>";
	                        }
	                    }
	                }
	            ],
	            rowCallback: function(row, objet, index) {
	            	// Table invitees
	            	for (var i=0; i<tabPersInvitees.length; i++) {
        				if (objet.idPersonneContact === tabPersInvitees[i].idPersonneContact) {
        					$(row).find('#checkIdPers' + objet.idPersonneContact).attr('disabled', true).prop('checked', true);
        				}
        			}
	            },
	            order: [[1, 'asc']]
	        });  
		}
		
		function updateTable() {
            table.ajax.reload();
        }
		
		function setInformation(newParticipation, newIdEntreprise) {
			idParticipation = newParticipation;
			idEntreprise = newIdEntreprise;
			if (! isDisplayed) {
				init();
				initializeTable();
			} else {
				updateTable();
			}
		}
		
		function init() {
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
			$checkboxAll2.prop('checked', false);
		}
		
		return {
			setInformation: setInformation
		}
	})();
	
	var ModalCommentaire = (function() {
		
		var idParticipation;
		var version;
		
		var isDisplayed = false;
		
		var $modal = $('#modalCommentaire');
		var $form = $modal.find('form');
		var $commentaire = $modal.find('textarea');
		
		function bindAll() {
			$form.on('submit', submitHandler);
			$modal.on('shown.bs.modal', function () {
				$form.find('textarea:first').focus();
			});
			$modal.on('hidden.bs.modal', destroy);
		}
		
		function unbindAll() {
			$form.off('submit');
		}
		
		function submitHandler(e) {
			e.preventDefault();
			
			Utils.supprimerMessageErreur($form);
		    var json = 'idParticipation=' + idParticipation + '&commentaire=' + $commentaire.val() + '&version=' + version;
		    console.log(json);
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('insererCommentaire', json, $form);
		        if (retour.success) {
		        	destroy();
		        	Journee.updateTable();
		        }
		    }
		}
		
		function setInformation(newIdParticipation, commentaire, newVersion) {
			idParticipation = newIdParticipation;
			version = newVersion;
			if (! isDisplayed) {
				init();
			}
			
			if (commentaire !== undefined) {
				$commentaire.val(commentaire);
			}
		}
		
		function init() {
			bindAll();
			isDisplayed = true;
		}
		
		function destroy() {
            $(".modal.in").modal("hide");
            Utils.cleanForm($form);
		}
		
		return {
			setInformation: setInformation,
			destroy: destroy
		}
	})();
	
	 var ModalModifierCommentaire = (function() {
		 
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
		        var retour = Ajax.ajax('insererEntreprise', json, $form);
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
	                url: 'getPersonnesContactDeLEntreprise',
	                type: "POST",
	                data: function ( d ) {
	                    d.idEntreprise = getIdEntreprise()
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            scrollX: true,
	            language: dataTablesLanguage,
	            dom: "rt",
	            columns: [
	            	{
	            		data: "sexe",
	            		render : function(data, type, objet) {
	            			if (data === 'H') {
	            				return '<i class="fa fa-male"></i>';
	            			} else {
	            				return '<i class="fa fa-female"></i>';
	            			}
	            		},
	            		orderable: false
	            	},
	            	{"data": "nom"},
	                {"data": "prenom"},
	                {"data": "email"},
	                {"data": "telephone"}
	            ],
	            order: [[1, 'asc']]
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
	                url: 'getParticipationsJEPers',
	                type: "POST",
	                data: function ( d ) {
	                    d.idEntreprise = getIdEntreprise()
	                },
	                dataSrc: ""
	            },
	            autoWidth: false,
	            scrollX: true,
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
		        var retour = Ajax.ajax('insererPersonneDeContact', json, $form);
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
			var entreprises = Ajax.ajax('getEntreprises');
		    
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
		    var informationEntreprise = Ajax.ajax('getEntreprisesParId', json);
		    
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
			var objet = Utils.formToJson($form);
			objet["idPersonneContact"] = personne.idPersonneContact;
			objet["version"] = personne.version;
			
		    var json = 'personne=' + JSON.stringify(objet);
		    console.log(json);
		    var inputVide = [];
		    if (Utils.testInputNonVide($form, inputVide)) {
		        var retour = Ajax.ajax('modifierPersonneContact', json, $form);
		        if (retour.success) {
		            Personne.updateTable();
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
	
	var ModalListeCommentaires = (function() {
		
		var idEntreprise;
		
		var $modal = $('#modalListeCommentaires');
		var $body = $modal.find('div.modal-body');
		
		var $div = $modal.find('#infoListeCommentaires');
		
		function setIdEntreprise(newIdEntreprise) {
			idEntreprise = newIdEntreprise;
			afficherInfo();
		}
		
		function afficherInfo() {
			$div.empty();
			
			var json = "idEntreprise=" + idEntreprise;
		    var listeCommentaires = Ajax.ajax('getCommentairesParEntreprise', json);
	    	
		    for (var i=1; i <= listeCommentaires.length; i++) {
				var $texte = '<blockquote>' + listeCommentaires[i-1] + '</blockquote>';
				
				$div.append($texte);
		    }
		}
		
		return {
			setIdEntreprise: setIdEntreprise
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