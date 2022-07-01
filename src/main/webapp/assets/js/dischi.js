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
    if (val) {
        message("", "");
        $.ajax({
            url: "rest/collezioni/" + val + "/dischi",
            method: "GET",
            success: function (data) {
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
    if (c && d) {
        $.ajax({
            url: "rest/collezioni/" + c + "/dischi/" + d,
            method: "GET",
            success: function (data) {
                getDischiUtility(data);
                message("Disco caricato con successo", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#dischi", "errore nel caricamento del disco");
            },
            cache: false,
        });
    } else {
        handleError("", "", "", "#dischi", "input errato");

    }
}

/*
* 7. Dischi delle collezioni private dell'utente
*/
function getDischiUtente() {
    message("", "");
    $.ajax({
        url: "rest/collezioni/private/dischi",
        method: "GET",
        success: function (data) {
            getDischiUtility(data);
            message("Dischi caricati con successo", "success");
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#dischi", "errore nel caricamento dei dischi");
        },
        cache: false,
    });
}

//Dischi delle collezioni condivise con l'utente
function getDischiCondivisiUtente(){
    message("", "");
    $.ajax({
        url: "rest/collezioni/condivise/dischi",
        method: "GET",
        success: function (data) {
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
    toggleVisibility(autori_container);

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


//Tutti i dischi
function getDischiUtility(data) {
    if (data) {
        dischi_result.children().remove();
        $.each(data, function (key) {
            $.ajax({
                url: data[key],
                method: "GET",
                success: function (data) {
                    populateDischi(data);
                },
                error: function (request, status, error) {
                    handleError(request, status, error, dischi_empty);
                },
                cache: false,
            });
        })
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi.");
    }
}

//Popolazione tabella dischi
function populateDischi(data) {
    if (data) {
        dischi_result.show();
        dischi_empty.hide();
        dischi_empty.text("Non ci sono dischi.");

        dischi_result.append('<tr>')
        dischi_result.append('<td>' + data['id'] + '</td>')
        dischi_result.append('<td>' + data['titolo'] + '</td>')
        dischi_result.append('<td>' + data['barcode'] + '</td>')
        dischi_result.append('<td>' + data['etichetta'] + '</td>')
        dischi_result.append('<td>' + data['anno'] + '</td>')
        dischi_result.append('<td>' + data['genere'] + '</td>')
        dischi_result.append('<td>' + data['formato'] + '</td>')
        dischi_result.append('<td>' + data['stato_conservazione'] + '</td>')
        dischi_result.append('<td>' + data['data_inserimento'] + '</td>')
        dischi_result.append('</tr>')
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi.");
    }
}