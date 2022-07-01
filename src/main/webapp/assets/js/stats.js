const stats_result = $("#dischi-result");
const stats_empty = $('#dischi-empty');

$(document).ready(function () {
    stats_result.hide();
});


//Numero di dischi per anno
function getDischiPerAnno(val) {
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_anno/" + val,
            method: "GET",
            success: function (data) {
                getDischiUtility(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, dischi_empty);
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi.");
    }
}

// prendi un disco di una collezione
//#5 numerazione yml
function getDiscoCollezione(id_c, id_d) {
    if (id_c && id_d) {
        $.ajax({
            url: "rest/collezioni/" + id_c + "/dischi/" + id_d,
            method: "GET",
            success: function (data) {
                getDischiUtility(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, dischi_empty);
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Disco non trovato nella collezione.");

    }
}

// Dischi delle collezioni private dell'utente
//#7 numerazione yml
function getDischiUtente() {
    $.ajax({
        url: "rest/collezioni/private/dischi",
        method: "GET",
        success: function (data) {
            getDischiUtility(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error, dischi_empty);
        },
        cache: false,
    });
}

//Dischi delle collezioni condivise con l'utente
function getDischiCondivisiUtente(){
    $.ajax({
        url: "rest/collezioni/condivise/dischi",
        method: "GET",
        success: function (data) {
            getDischiUtility(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error, dischi_empty);
        },
        cache: false,
    });

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
//Dischi di un autore
function getDischiByAutore(val) {
    if (val) {
        $.ajax({
            url: "rest/autori/" + val + "/dischi",
            method: "GET",
            success: function (data) {
                getDischiUtility(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, dischi_empty);
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi di questo autore.");
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