/*
* File per le operazioni sui dischi
* @Author: Davide De Acetis
* @Author: Raluca Mihaela Bujoreanu
*/

//DICHIARAZIONE SELETTORI
const dischi_container = $("#dischi-container");
const dischi_result = $("#dischi-result");
const dischi_empty = $('#dischi-empty');
//END DICHIARAZIONE SELETTORI

/*
* 3. Elenco dischi in una collezione
* @param {int} val - ID della collezione
*/
function getDischiCollezione(val) {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    if (val) {
        $.ajax({
            url: "rest/collezioni/" + val + "/dischi",
            method: "GET",
            success: function (data) {
                dischi_result.children().remove();
                getDischiUtility(data);
                message("Dischi caricati con successo", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#dischi", "errore nel caricamento dei dischi");

            },
            cache: false,
        });
    } else {
        handleError("", "", "", "#dischi", "input errato");
    }
}

/*
* 5. Disco di una collezione
* @param {int} d - ID del disco
* @param {int} c - ID della collezione
*/
function getDiscoCollezione(c, d) {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    if (c && d) {
        $.ajax({
            url: "rest/collezioni/" + c + "/dischi/" + d,
            method: "GET",
            success: function (data) {
                dischi_result.children().remove();
                populateDischi(data);
                message("Disco caricato con successo", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#dischi", "Errore nel caricamento del disco");
            },
            cache: false,
        });
    } else {
        clear();
        handleError("", "", "", "#dischi", "Input errato");
    }
}

/*
* 7. Dischi delle collezioni private dell'utente
*/
function getDischiUtente() {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    $.ajax({
        url: "rest/collezioni/private/dischi",
        method: "GET",
        success: function (data) {
            dischi_result.children().remove();
            getDischiUtility(data);
            message("Dischi caricati con successo", "success");
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#dischi", "errore nel caricamento dei dischi");
        },
        cache: false,
    });
}

/*
* 3. Dischi delle collezioni condivise con l'utente
* 7. Ricerca di un disco tra le collezioni condivise con criteri di ricerca
* @param {string} titolo - Titolo del disco
* @param {string} anno - Anno di produzione del disco
* @param {string} genere - Genere del disco
* @param {string} formato - Artista del disco
* @param {string} autore - Casa discografica del disco
*/
function getDischiCondivisiUtente(titolo, anno, genere, formato, autore) {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    $.ajax({
        url: "rest/collezioni/condivise/dischi?titolo=" + titolo + "&anno=" + anno + "&genere=" + genere + "&formato=" + formato + "&autore=" + autore,
        method: "GET",
        success: function (data) {
            dischi_result.children().remove();
            getDischiUtility(data);
            message("Dischi caricati con successo", "success");
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#dischi", "errore nel caricamento dei dischi");
        },
        cache: false,
    });
}

/*
* 7. Ricerca di un disco tra le collezioni pubbliche con criteri di ricerca
* @param {string} titolo - Titolo del disco
* @param {string} anno - Anno di produzione del disco
* @param {string} genere - Genere del disco
* @param {string} formato - Artista del disco
* @param {string} autore - Casa discografica del disco
*/
function ricercaDiscoCollezioniPubbliche(titolo, anno, genere, formato, autore) {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    $.ajax({
        url: "/rest/collezioni/dischi?titolo=" + titolo + "&anno=" + anno + "&genere=" + genere + "&formato=" + formato + "&autore=" + autore,
        method: "GET",
        success: function (data) {
            dischi_result.children().remove();
            getDischiUtility(data);
            message("Dischi caricati con successo", "success");
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#dischi", "errore nel caricamento dei dischi");
        },
        cache: false,
    });
}

/*
* 9: Elenco di tutti i dischi di un'autore.
* @param {int} val - ID dell'autore
*/
function getDischiByAutore(val) {
    message("", "");
    clear();
    toggleVisibility(dischi_container);

    if (val) {
        $.ajax({
            url: "/rest/autori/" + val + "/dischi",
            method: "GET",
            success: function (data) {
                //data - dischi
                dischi_result.children().remove();
                populateDischi(data);
                message("Dischi caricati con successo", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#dischi", "Autore non trovato.");
            },
            cache: false,
        });
    } else {
        handleError("", "", "", "#dischi", "Non ci sono autori.");
    }
}

/*----------------------------------FUNZIONI UTILITY PER LE COLLEZIONI------------------------------------------------*/

/*
* Funzione Utility per ottenere i dischi.
* @param {List<URL>} data - Lista di URL dei dischi
*/
function getDischiUtility(data) {
    $.each(data, function (key) {
        $.ajax({
            url: data[key],
            method: "GET",
            success: function (data) {
                populateDischi(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#dischi", "Errore generico");
            },
            cache: false,
        });
    })
}

/*
* Funzione Utility il riempimento della tabella delle collezioni
* @param {Disco} data - Disco da inserire nella tabella
*/
function populateDischi(data) {
    if (data) {
        dischi_result.show();
        dischi_empty.hide();

        dischi_result.append('<tr>')
        dischi_result.append('<td>' + data['id'] + '</td>')
        dischi_result.append('<td>' + data['titolo'] + '</td>')
        dischi_result.append('<td>' + data['barcode'] + '</td>')
        dischi_result.append('<td>' + data['etichetta'] + '</td>')
        dischi_result.append('<td>' + data['anno'] + '</td>')
        dischi_result.append('<td>' + data['genere'] + '</td>')
        dischi_result.append('<td>' + data['formato'] + '</td>')
        dischi_result.append('<td>' + (data['stato_conservazione'] === '' || data['stato_conservazione'] == null ? "-" : data['stato_conservazione']) + '</td>')
        dischi_result.append('<td>' + data['data_inserimento'] + '</td>')
        dischi_result.append('</tr>')
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
    }
    dischi_empty.text("Non ci sono dischi.");
}