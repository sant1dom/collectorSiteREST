const statistiche_container = $("#statistiche-container");


//Numero di dischi per anno
function getStats() {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    toggleVisibility($('#stats_pubbliche'));
    $("#numero_dischi").text("Numero di dischi totali nel sistema: ");
    $("#numero_tracce").text("Numero di tracce totali nel sistema: ");
    $("#numero_autori").text("Numero di autori totali nel sistema: ");
    $("#numero_etichette").text("Numero di etichette totali nel sistema: ");
    $("#numero_generi").text("Numero di generi totali nel sistema: ");
    $("#numero_collezioni_pubbliche").text("Numero di collezioni pubbliche nel sistema: ");
    $.ajax({
        url: "rest/stats",
        method: "GET",
        success: function (data) {
            $("#numero_dischi").append(data['numero_dischi']);
            $("#numero_tracce").append(data['numero_tracce']);
            $("#numero_autori").append(data['numero_autori']);
            $("#numero_etichette").append(data['numero_etichette']);
            $("#numero_generi").append(data['numero_generi']);
            $("#numero_collezioni_pubbliche").append(data['numero_collezioni_pubbliche']);
        },
        error: function (request, status, error) {
            handleError(request, status, error, "", "Caricamento stats pubbliche fallito.");
        }
    });
}

function getStatsUtenteLoggato() {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    toggleVisibility($('#stats_utente'));
    $.ajax({
        url: "rest/stats/numero_collezioni_private_utente",
        method: "GET",
        success: function (data) {
            $("#numero_collezioni_private_utente").text("Numero di collezioni private dell'utente: ");
            $("#numero_collezioni_private_utente").append(data['numero_collezioni_private_utente']);
        },
        error: (request, status, error) => {
            handleError(request, status, error, "", "Caricamento stats utente fallito.");
        }
    });
    $.ajax({
        url: "rest/stats/numero_collezioni_totali_utente",
        method: "GET",
        success: function (data) {
            $("#numero_collezioni_totali_utente").text("Numero di collezioni totali dell'utente: ");
            $("#numero_collezioni_totali_utente").append(data['numero_collezioni_totali_utente']);
        }
    })
}

function getStatsDischiByEtichetta(val) {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_etichetta/" + val,
            method: "GET",
            success: () => {},
            error: function (request, status, error) {
                handleError(request, status, error, "", "Caricamento stats per etichetta fallito.");
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi di questa etichetta.");
    }
}

function getStatsDischiByAutore(val) {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_autore/" + val,
            method: "GET",
            success: () => {},
            error: function (request, status, error) {
                handleError(request, status, error, "", "Caricamento stats per autore fallito.");
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi di questo autore.");
    }
}

function getStatsDischiByAnno(val) {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_anno/" + val,
            method: "GET",
            success: () => {},
            error: function (request, status, error) {
                handleError(request, status, error, "", "Caricamento stats per anno fallito.");
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi di questo anno.");
    }
}

function getStatsDischiByGenere(val) {
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_genere/" + val,
            method: "GET",
            success: () => {},
            error: function (request, status, error) {
                handleError(request, status, error, "", "Caricamento stats per genere fallito.");
            },
            cache: false,
        });
    } else {
        dischi_result.children().remove();
        dischi_empty.show();
        dischi_empty.text("Non ci sono dischi di questo genere.");
    }
}