/*
* File di utility per tutte el operazioni.
* @Author: Davide De Acetis
* @Author: Raluca Mihaela Bujoreanu
*/

const select_picker = $('.selectpicker');

/*
* Al caricamento della pagina nasconde tutte le tabelle e le form
*/
$(document).ready(function () {
    clear();
});

/*
* Utility che mostra un elemento se è nascosto o lo nasconde se è visibile
* @Param {Elem} elem - Elemento che si vuole nascondere o mostrare.
*/
function toggleVisibility(elem) {
    if (elem.css('display') === 'none') {
        elem.css('display', 'block');
    } else {
        elem.css('display', 'none');
    }
}

/*
* Utility che imposta un messaggio di errore o di successo dopo la chiamata di un'operazione.
* @Param {String} message - Messaggio da mostrare.
* @Param {String} type - Tipo di messaggio, può essere error, success o "".
*/
function message (message, type) {
    const message_span = $('#message');
    message_span.html(message);
    switch (type) {
        case "error":
            message_span.css('color', 'red');
            break;
        case "success":
            message_span.css('color', 'green');
            break;
        default:
            message_span.html("");
    }
}

/*
* Utility per la gestione degli errori
* @Param {String} request - Stringa della richiesta contenente lo status html.
* @Param {String} status
* @Param {String} error
* @Param {String} table - Tabella dell'operazione.
* @Param {String} msg - Messaggio da inoltrare alla funzione message().
*/
function handleError(request, status, error, table, msg) {
    const table_empty = $(table + "-empty"); //selettore per la barra empty da mostrare e su cui inserire il messaggio
    const table_result = $(table + "-result"); //selettore per l'interno della tabella da svuotare.

    table_empty.show()
    table_result.children().remove();
    switch (request.status) {
        case 404:   //Elemento non trovato
            table_empty.text("Elemento non presente.");
            message("Elemento non presente.", "error");
            break;
        case 401:   //Non autorizzato
            table_empty.text("Non sei autorizzato.");
            message("Non sei autorizzato.", "error");
            break;
        case 500:   //Server error
            table_empty.text("Errore del server.");
            message("Errore del server.", "error");
            break;
        default:    //Qualsiasi altro messaggio che non sia un errore html
            table_empty.text(msg);
            message(msg, "error");
    }
}

/*
* Utility rimuove tutte le tabelle e le form sullo schermo.
*/
function clear() {
    collezioni_container.css('display', 'none');
    collezione_result.hide();
    update_disco_collezione_form.css('display', 'none');
    add_disco_collezione_form.css('display', 'none');

    autori_container.css('display', 'none');
    autore_result.hide();

    dischi_container.css('display', 'none');
    dischi_result.hide();
}


/*
* Utility che controlla l'esistenza o le autorizzazioni per una collezione
* @param {int} c - Id della collezione
* @return {boolean} - True se l'utente ha i permessi per accedere alla collezione e la collezione esiste, false altrimenti
*/
function checkCollezione(c) {
    let exist;
    $.ajax({
        url: "rest/collezioni/" + c,
        async: false,
        method: "GET",
        success: function () {
            exist = true;
        },
        error: function (request, status, error) {
            switch (request.status) {
                case 404:
                    message("Collezione non trovata.", "error");
                    break;
                case 401:
                    message("Non sei autorizzato a visualizzare questa collezione.", "error");
                    break;
                default:
                    message("Errore nel caricamento della collezione.", "error");
            }
            exist = false;
        },
        cache: false,
    });
    return exist;
}

/*
* Utility che controlla l'esistenza o le autorizzazioni per un disco di una collezione
* @param {int} c - Id della collezione
* @param {int} d - Id del disco
* @return {boolean} - True se l'utente ha i permessi per accedere alla collezione e al disco, false altrimenti
*/
function checkDiscoCollezione(c, d) {
    let exist;
    $.ajax({
        url: "rest/collezioni/" + c + "/dischi/" + d,
        async: false,
        method: "GET",
        success: function () {
            exist = true;
        },
        error: function (request, status, error) {
            switch (request.status) {
                case 404:
                    message("Disco non trovato o non appartenente alla collezione.", "error");
                    break;
                case 401:
                    message("Non sei autorizzato a visualizzare questa collezione.", "error");
                    break;
                default:
                    message("Errore nel caricamento del disco.", "error");
            }
            exist = false;
        },
        cache: false,
    });
    return exist;
}