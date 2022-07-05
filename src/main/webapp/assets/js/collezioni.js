/*
* File per le operazioni sulle collezioni
* @Author: Davide De Acetis
* @Author: Raluca Mihaela Bujoreanu
*/

//DICHIARAZIONE SELETTORI
const collezione_result = $('#collezione-result');
const collezione_empty = $('#collezione-empty');
const collezioni_container = $('#collezioni-container');
const id_collezione_update = $('#updateDiscoCollezione_c');
const id_disco_update = $('#updateDiscoCollezione_d');
const id_collezione_add = $('#addDiscoCollezione_c');
const update_disco_collezione_form = $('#updateDiscoCollezione_form_2')
const add_disco_collezione_form = $('#addDiscoCollezione_form_2')
//END DICHIARAZIONE SELETTOR

/*
* 2: Elenco collezioni private di un utente
*/
function getCollezioniUtente() {
    message("", "");
    clear();
    toggleVisibility(collezioni_container);

    $.ajax({
        url: "rest/collezioni/private",
        method: "GET",
        success: function (data) {
            //data - lista di url di collezioni
            collezione_result.children().remove();
            if (data.length > 0) {
                getCollezioniUtility(data); //ottengo le collezioni e le inserisco nella tabella
                message("Collezioni caricate.", "success");
            } else {
                handleError("", "", "", "#collezione", "Non ci sono collezioni.");
            }
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#collezione", "Errore nel caricamento delle collezioni.");
        },
        cache: false,
    });
}

/*
* 3: Elenco collezioni condivise con un utente
*/
function getCollezioniCondivise() {
    message("", "");
    clear();
    toggleVisibility(collezioni_container);

    $.ajax({
        url: "rest/collezioni/condivise",
        method: "GET",
        success: function (data) {
            //data - lista di url di collezioni
            collezione_result.children().remove();
            if (data.length > 0) {
                getCollezioniUtility(data); //ottengo le collezioni e le inserisco nella tabella
                message("Collezioni condivise caricate.", "success");
            } else {
                handleError("", "", "", "#collezione", "Non ci sono collezioni condivise.");
            }
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#collezione", "Errore nel caricamento delle collezioni.");
        },
        cache: false,
    });
}

/*
* 4: Singola collezione
* @param {int} val - id della collezione da visualizzare
*/
function getCollezione(val) {
    message("", "");
    clear();
    toggleVisibility(collezioni_container);

    if (val) {
        $.ajax({
            url: "rest/collezioni/" + val,
            method: "GET",
            success: function (data) {
                //data - collezione
                collezione_result.children().remove();
                message("Collezione caricata.", "success");
                populateCollezione(data); //popolo la tabella con la collezione
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
            },
            cache: false,
        });
    } else {
        handleError("", "", "", "#collezione", "Input errato");
    }
}

/*
* 6. Crea e aggiunge un disco esistente in una collezione
* @param {int} c - id della collezione
* @param {int} d - id del disco nella collezione
*/
function addDiscoCollezione(c) {
    message("", "");
    if (c) {
        $.ajax({
            url: "rest/collezioni/" + c + "/dischi",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                titolo: $('#titolo_add').val(),
                anno: $('#anno_add').val(),
                genere: $('#genere_add').val(),
                formato: $('#formato_add').val(),
                stato_conservazione: $('#statoConservazione_add').val(),
                barcode: $('#barcode_add').val(),
                etichetta: $('#etichetta_add').val(),
                autori: $('#autore_add').val()
            }),
            success: function () {
                collezione_result.children().remove();
                clear();
                message("Collezione aggiornata con il nuovo disco.", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
            },
            cache: false,
        });
    } else {
        clear();
        handleError("", "", "", "#collezione", "Input errato");
    }
}

/*
* 10. Aggiorna un disco all'interno di una collezione
* @param {int} c - id della collezione
* @param {int} d - id del disco nella collezione
*/
function updateDiscoCollezione(c, d) {
    message("", "");
    if (c && d) {
        $.ajax({
            url: "rest/collezioni/" + c + "/dischi/" + d,
            method: "PUT",
            contentType: "application/json",
            //vengono passati i dati della form update_disco_collezione_form
            data: JSON.stringify({
                id: d,
                titolo: $('#titolo_update').val(),
                anno: $('#anno_update').val(),
                genere: $('#genere_update').val(),
                formato: $('#formato_update').val(),
                stato_conservazione: $('#statoConservazione_update').val(),
                barcode: $('#barcode_update').val(),
                etichetta: $('#etichetta_update').val(),
                autori: $('#autore_update').val()
            }),
            success: function () {
                collezione_result.children().remove();
                clear();
                message("Collezione aggiornata con il nuovo disco.", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento della collezione.");
            },
            cache: false,
        });
    } else {
        clear();
        handleError("", "", "", "#collezione", "Input errato");
    }
}

/*----------------------------------FUNZIONI UTILITY PER LE COLLEZIONI------------------------------------------------*/

/*
* Utility che permette di visualizzare le form per le operazioni di add e update di un disco in una collezione
* @param {String} op - operazione da eseguire (add o update)
*/
function discoForm(op) {
    message("", "");
    clear();
    if (op === 'add') { //ADD DI UN DISCO IN UNA COLLEZIONE
        //Se i campi non sono compilati non viene mostrata la form e si esce dalla funzione.
        if (id_collezione_add.val() === '') {
            message('Inserire tutti i campi obbligatori.', 'error')
            return;
        } else if (checkCollezione(id_collezione_add.val()) === false) {
            return;
        }
        //Mostra la form per creare e aggiungere un disco alla collezione specificata
        toggleVisibility(add_disco_collezione_form);
        //Ottiene tutti gli autori del sistema e li inserisce nella select degli autori
        getAutoriForm($('#autore_add'));

    } else if (op === 'update') { //UPDATE DEL DISCO IN UNA COLLEZIONE
        if (id_collezione_update.val() === '' || id_disco_update.val() === '') {
            message('Inserire tutti i campi obbligatori.', 'error')
            return;
        } else if (checkDiscoCollezione(id_collezione_update.val(), id_disco_update.val()) === false) {
            return;
        }
        //Mostra la form per aggiornare un disco della collezione specificata
        toggleVisibility(update_disco_collezione_form);
        //Popola i campi della form con i dati del disco selezionati
        populateDiscoCollezioneUpdateForm();
    }
}

/*
* Funzione Utility per Ottiene tutti gli autori del sistema e li inserisce nella select degli autori
* @param {Selectpicker} select - La select che deve essere riempita con gli autori
*/
function getAutoriForm(select) {

    $.ajax({
        url: "rest/autori",
        method: "GET",
        success: function (data) {
            $.each(data, function (key) {
                $.ajax({
                    url: "rest/autori/" + data[key].split("/")[6],
                    method: "GET",
                    success: function (data) {
                        select.append('<option value="' + data['id'] + '">' + data['nome_artistico'] + '</option>');
                        select.trigger('change');
                        select_picker.selectpicker('refresh');
                    }
                })
            })
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#collezione", "Errore nel caricamento degli autori.");
        },
        cache: false,
    });
}

/*
* Funzione Utility per popolare i campi della form per l'aggiornamento di un disco di una collezione
*/
function populateDiscoCollezioneUpdateForm() {
    const titolo = $('#titolo_update');
    const anno = $('#anno_update');
    const genere = $('#genere_update');
    const formato = $('#formato_update');
    const statoConservazione = $('#statoConservazione_update');
    const barcode = $('#barcode_update');
    const etichetta = $('#etichetta_update');

    $.ajax({
        url: "rest/dischi/" + id_disco_update.val(),
        method: "GET",
        success: function (data) {
            titolo.val(data['titolo']);
            anno.val(data['anno']);
            barcode.val(data['barcode']);
            etichetta.val(data['etichetta']);
            genere.val(data['genere']).trigger('change');
            formato.val(data['formato']).trigger('change');
            if (formato.val() === 'DIGITALE') {
                disableStatoConservazione(statoConservazione, formato);
            } else {
                statoConservazione.val(data['stato_conservazione']).trigger('change');
            }
            getAutoriForm($('#autore_update'));
        },
        error: function (request, status, error) {
            clear()
            handleError(request, status, error, "#collezione", "Errore nel caricamento del disco.");
        },
        cache: false,
    });
}

/*
* Funzione Utility per Ottenere le collezioni
* @param {List<URL>} data - Lista di URL delle collezioni
*/
function getCollezioniUtility(data) {
    $.each(data, function (key) {
        $.ajax({
            url: "rest/collezioni/" + data[key].split("/")[6],
            method: "GET",
            success: function (data) {
                populateCollezione(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore generico");
            },
            cache: false,
        });
    })
}

/*
* Funzione Utility il riempimento della tabella delle collezioni
* @param {Collezione} data - Collezione da inserire nella tabella
*/
function populateCollezione(data) {
    if (data) {
        //Se la collezione non è vuota viene inserita nella tabella.
        collezione_result.show();
        collezione_empty.hide();

        collezione_result.append('<tr>')
        collezione_result.append('<td>' + data['id'] + '</td>')
        collezione_result.append('<td>' + data['titolo'] + '</td>')
        collezione_result.append('<td>' + data['data_creazione'] + '</td>')
        collezione_result.append('<td>' + data['privacy'] + '</td>')
        collezione_result.append('</tr>')
    } else {
        //Se la collezione è vuota viene svuotata la tabella e viene mostrato un messaggio.
        collezione_result.children().remove();
        collezione_empty.show();
    }
    collezione_empty.text("Non ci sono collezioni.");
}

/*
* Alla selezione di un formato nella form update_disco_collezione_form viene disabilitato il campo statoConservazione_update se il formato è digitale
*/
$('#formato_update').on('change', function () {
    disableStatoConservazione($('#statoConservazione_update'), $('#formato_update option:selected'));
});

/*
* Alla selezione di un formato nella form add_disco_collezione_form viene disabilitato il campo statoConservazione_add se il formato è digitale
*/
$('#formato_add').on('change', function () {
    disableStatoConservazione($('#statoConservazione_add'), $('#formato_add option:selected'));
});

/*
* Utility che disabilita il campo statoConservazione se il formato è digitale
* @param {Selectpicker} statoConservazione - Il campo statoConservazione da disabilitare
* @param {Option} selected - L'opzione selezionata in formato
*/
function disableStatoConservazione(statoConservazione, selected) {
    if (selected.val() === 'DIGITALE') {
        statoConservazione.val('').trigger('change');
        statoConservazione.prop('disabled', true);
        statoConservazione.prop('required', false);
        statoConservazione.parent().addClass('off');
    } else {
        statoConservazione.prop('disabled', false);
        statoConservazione.prop('required', true);
        statoConservazione.parent().removeClass('off');
    }
    select_picker.selectpicker('refresh');
}


