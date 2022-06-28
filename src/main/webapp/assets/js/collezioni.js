const collezione_result = $('#collezione-result');
const collezione_empty = $('#collezione-empty');

$(document).ready(function () {
    collezione_result.hide();
});


//2: Elenco collezioni di un utente
function getCollezioniUtente() {
    $.ajax({
        url: "rest/collezioni/all",
        method: "GET",
        success: function (data) {
            getCollezioni(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
        },
        cache: false,
    });
}

//3: Elenco collezioni condivise con un utente
function getCollezioniCondivise() {
    $.ajax({
        url: "rest/collezioni/condivise",
        method: "GET",
        success: function (data) {
            getCollezioni(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
        },
        cache: false,
    });
}

//4: Singola collezione
function getCollezione(val) {
    $.ajax({
        url: "rest/collezioni/" + val,
        method: "GET",
        success: function (data) {
            collezione_result.children().remove();
            populateCollezione(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
        },
        cache: false,
    });
}

//7: Collezioni private di un utente
function getCollezioniPrivate() {
    $.ajax({
        url: "rest/collezioni/all",
        method: "GET",
        success: function (data) {
            getCollezioni(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error);
        },
        cache: false,
    });
}


//Funzioni di utility
function getCollezioni(data) {
    if (data) {
        collezione_result.children().remove();
        $.each(data, function (key) {
            $.ajax({
                url: "rest/collezioni/" + data[key].split("/")[6],
                method: "GET",
                success: function (data) {
                    populateCollezione(data);
                },
                error: function (request, status, error) {
                    handleError(request, status, error);
                },
                cache: false,
            });
        })
    } else {
        collezione_empty.show();
        collezione_empty.text("Non ci sono collezioni.");
    }
}

function populateCollezione (data) {
    if (data) {
        collezione_result.show();
        collezione_empty.hide();
        collezione_empty.text("Non ci sono collezioni.");

        collezione_result.append('<tr>')
        collezione_result.append('<td>' + data['id'] + '</td>')
        collezione_result.append('<td>' + data['titolo'] + '</td>')
        collezione_result.append('<td>' + data['data_creazione'] + '</td>')
        collezione_result.append('<td>' + data['privacy'] + '</td>')
        collezione_result.append('</tr>')
    } else {
        collezione_empty.show();
        collezione_empty.text("Non ci sono collezioni.");
    }
}