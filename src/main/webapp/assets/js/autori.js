/*
* File per le operazioni sugli autori.
* @Author: Davide De Acetis
* @Author: Raluca Mihaela Bujoreanu
*/

//DICHIARAZIONE SELETTORI
const autori_container = $('#autori-container');
const autore_result = $("#autore-result");
const autore_empty = $('#autore-empty');
//END DICHIARAZIONE SELETTOR

/*
* 8. Elenco autori del sistema
*/
function getAutori() {
    message("", "");
    clear();
    toggleVisibility(autori_container);

    $.ajax({
        url: "rest/autori",
        method: "GET",
        success: function (data) {
            //data - lista di url di autori
            autore_result.children().remove();
            if (data.length > 0) {
                getAutoriUtility(data);
                message("Autori caricati con successo", "success");
            } else {
                handleError("", "", "", "#autore", "Non ci sono autori.");
            }
        },
        error: function (request, status, error) {
            handleError(request, status, error, "#autore", "Errore nel caricamento degli autori.");
        },
        cache: false,
    });
}

/*
* 8: Singolo autore
* @param {int} val - ID dell'autore
*/
function getAutore(val) {
    message("", "");
    clear();
    toggleVisibility(autori_container);

    if (val) {
        $.ajax({
            url: "rest/autori/" + val,
            method: "GET",
            success: function (data) {
                //data - autore
                autore_result.children().remove();
                populateAutori(data);
                message("Autore caricato con successo", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#autore", "Autore non trovato.");
            },
            cache: false,
        });
    } else {
        handleError("", "", "", "#autore", "Non ci sono autori.");
    }
}

/*-----------------------------------FUNZIONI UTILITY PER GLI AUTORI--------------------------------------------------*/

/*
* Funzione Utility per Ottenere gli autori.
* @param {List<URL>} data - Lista di URL degli autori
*/
function getAutoriUtility(data) {
    if (data) {
        autore_result.children().remove();
        $.each(data, function (key) {
            $.ajax({
                url: "rest/autori/" + data[key].split("/")[6],
                method: "GET",
                success: function (data) {
                    populateAutori(data);
                },
                error: function (request, status, error) {
                    handleError(request, status, error, autore_empty);
                },
                cache: false,
            });
        })
    } else {
        autore_result.children().remove();
        autore_empty.show();
        autore_empty.text("Non ci sono autori.");
    }
}

/*
* Funzione Utility il riempimento della tabella degli autori.
* @param {Collezione} data - Autore da inserire nella tabella
*/
function populateAutori (data) {
    if (data) {
        //Se l'autore non è vuoto viene inserito nella tabella.
        autore_result.show();
        autore_empty.hide();

        autore_result.append('<tr>')
        autore_result.append('<td>' + data['id'] + '</td>')
        autore_result.append('<td>' + data['nome'] + '</td>')
        autore_result.append('<td>' + data['cognome'] + '</td>')
        autore_result.append('<td>' + data['nome_artistico'] + '</td>')
        autore_result.append('<td>' + data['tipologia_autore'] + '</td>')
        autore_result.append('</tr>')
    } else {
        //Se l'autore è vuoto viene mostrato un messaggio di errore.
        autore_result.children().remove();
        autore_empty.show();
    }
    autore_empty.text("Non ci sono autori.");
}