const autori_result = $("#autori-result");
const autori_empty = $('#autori-empty');

$(document).ready(function () {
    autori_result.hide();
});


//8. Elenco autori del sistema
function getAutori() {
    $.ajax({
        url: "rest/autori",
        method: "GET",
        success: function (data) {
            getAutoriUtility(data);
        },
        error: function (request, status, error) {
            handleError(request, status, error, autori_empty);
        },
        cache: false,
    });
}

function getAutore(val) {
    if (val) {
        $.ajax({
            url: "rest/autori/" + val,
            method: "GET",
            success: function (data) {
                autori_result.children().remove();
                populateAutori(data);
            },
            error: function (request, status, error) {
                handleError(request, status, error, autori_empty);
            },
            cache: false,
        });
    } else {
        autori_result.children().remove();
        autori_empty.show();
        autori_empty.text("Non ci sono autori.");
    }
}

//Tutti gli autori
function getAutoriUtility(data) {
    if (data) {
        autori_result.children().remove();
        $.each(data, function (key) {
            $.ajax({
                url: "rest/autori/" + data[key].split("/")[6],
                method: "GET",
                success: function (data) {
                    populateAutori(data);
                },
                error: function (request, status, error) {
                    handleError(request, status, error, autori_empty);
                },
                cache: false,
            });
        })
    } else {
        autori_result.children().remove();
        autori_empty.show();
        autori_empty.text("Non ci sono autori.");
    }
}

//Popolazione tabella autori
function populateAutori (data) {
    if (data) {
        autori_result.show();
        autori_empty.hide();
        autori_empty.text("Non ci sono autori.");

        autori_result.append('<tr>')
        autori_result.append('<td>' + data['id'] + '</td>')
        autori_result.append('<td>' + data['nome'] + '</td>')
        autori_result.append('<td>' + data['cognome'] + '</td>')
        autori_result.append('<td>' + data['nome_artistico'] + '</td>')
        autori_result.append('<td>' + data['tipologia_autore'] + '</td>')
        autori_result.append('</tr>')
    } else {
        autori_result.children().remove();
        autori_empty.show();
        autori_empty.text("Non ci sono autori.");
    }
}