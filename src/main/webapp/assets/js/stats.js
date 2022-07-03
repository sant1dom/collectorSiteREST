const statistiche_container = $("#statistiche-container");
let stats_pubbliche = $('#stats_pubbliche')
let stats_utente = $('#stats_utente')
let stats_dischi_autore = $('#stats_dischi_autore')
let stats_dischi_etichetta = $('#stats_dischi_etichetta')
let stats_dischi_anno = $('#stats_dischi_anno')
let stats_dischi_genere = $('#stats_dischi_genere')
let arr = [stats_pubbliche, stats_utente, stats_dischi_autore, stats_dischi_etichetta, stats_dischi_anno, stats_dischi_genere];

//Numero di dischi per anno
function getStats() {
    disableOthers();
    toggleVisibility(stats_pubbliche);
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
    disableOthers();
    toggleVisibility(stats_utente);
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
    disableOthers();
    toggleVisibility(stats_dischi_etichetta);
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_etichetta/" + val,
            method: "GET",
            success: function (data) {
                $("#numero_dischi_per_etichetta").text("Numero di dischi di questa etichetta: ");
                $("#numero_dischi_per_etichetta").append(data['numero_dischi_per_etichetta']);
            },
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
    disableOthers();
    toggleVisibility(stats_dischi_autore);
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_autore/" + val,
            method: "GET",
            success: function (data) {
                $("#numero_dischi_per_autore").text("Numero di dischi di questo autore: ");
                $("#numero_dischi_per_autore").append(data['numero_dischi_per_autore']);
            },
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
    disableOthers();
    toggleVisibility(stats_dischi_anno);
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_anno/" + val,
            method: "GET",
            success: function (data) {
                $("#numero_dischi_per_anno").text("Numero di dischi di questo anno: ");
                $("#numero_dischi_per_anno").append(data['numero_dischi_per_anno']);
            },
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
    disableOthers();
    toggleVisibility(stats_dischi_genere);
    if (val) {
        $.ajax({
            url: "rest/stats/dischi_per_genere/" + val,
            method: "GET",
            success: function (data) {
                $("#numero_dischi_per_genere").text("Numero di dischi di questo genere: ");
                $("#numero_dischi_per_genere").append(data['numero_dischi_per_genere']);
            },
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

function disableOthers(){
    if (statistiche_container.css("display") === "none") {
        statistiche_container.css("display", "block");
    }
    $.each(arr, function(index, value){
        value.css("display", "none");
    });
}

function populateAutoriSelect(){
    let select = $("#getStatsDischiByAutore_e");
    $.ajax({
        url: "rest/autori",
        method: "GET",
        success: function (data) {
            $.each(data, function(index, value){
                $.ajax({
                    url: value,
                    method: "GET",
                    success: function (data) {
                        select.append("<option value='" + data["nome_artistico"] + "'>" + data["nome_artistico"] + "</option>");
                        select.trigger('change');
                        select.selectpicker("refresh");
                    }
                })
            });
        },
        error: function (request, status, error) {
            handleError(request, status, error, "", "Caricamento autori fallito.");
        }
    });
}