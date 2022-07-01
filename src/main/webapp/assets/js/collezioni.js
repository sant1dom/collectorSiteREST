const collezione_result = $('#collezione-result');
const collezione_empty = $('#collezione-empty');

$(document).ready(function () {
    collezione_result.hide();
});


//2: Elenco collezioni private di un utente
function getCollezioniUtente() {
    message("", "");
    $.ajax({
        url: "rest/collezioni/all",
        method: "GET",
        success: function (data) {
            collezione_result.children().remove();
            if (data.length > 0) {
                getCollezioni(data);
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

//3: Elenco collezioni condivise con un utente
function getCollezioniCondivise() {
    message("", "");
    $.ajax({
        url: "rest/collezioni/condivise",
        method: "GET",
        success: function (data) {
            collezione_result.children().remove();
            if (data.length > 0) {
                getCollezioni(data);
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

//4: Singola collezione
function getCollezione(val) {
    message("", "");
    console.log(val);
    if (val) {
        $.ajax({
            url: "rest/collezioni/" + val,
            method: "GET",
            success: function (data) {
                collezione_result.children().remove();
                message("Collezione caricata.", "success");
                populateCollezione(data);
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

//10. Aggiungi disco esistente in una collezione
function updateDiscoCollezione(c, d) {
    message("", "");
    if (c && d) {
        $.ajax({
            url: "rest/collezioni/" + c + "/dischi/" + d,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify({
                id: d,
                titolo: $('#titolo').val(),
                anno: $('#anno').val(),
                genere: $('#genere').val(),
                formato: $('#formato').val(),
                stato_conservazione: $('#statoConservazione').val(),
                barcode: $('#barcode').val(),
                etichetta: $('#etichetta').val(),
                autori: $('#autore').val()
            }),
            success: function (data) {
                collezione_result.children().remove();
                message("Collezione aggiornata con il nuovo disco.", "success");
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

//Funzioni di utility
function getCollezioni(data) {
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


function populateCollezione(data) {
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
        collezione_result.children().remove();
        collezione_empty.show();
        collezione_empty.text("Non ci sono collezioni.");
    }
}

function discoForm(id_disco) {
    message("", "");
    toggleVisibility($('#collezioni-container'));
    if (id_disco === undefined) {
        toggleVisibility($('#addDiscoCollezione_form_2'));
        $.ajax({
            url: "rest/autori",
            method: "GET",
            success: function (data) {
                $.each(data, function (key) {
                        $.ajax({
                            url: "rest/autori/" + data[key].split("/")[6],
                            method: "GET",
                            success: function (data) {
                                $('#autore').append('<option value="' + data['id'] + '">' + data['nome_artistico'] + '</option>');
                            }
                        })
                    }
                )
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento degli autori.");
            },
            cache: false,
        });
    } else {
        toggleVisibility($('#updateDiscoCollezione_form_2'));
        let disco;
        $.ajax({
            url: "rest/dischi/" + id_disco,
            method: "GET",
            success: function (data) {
                $('#titolo').val(data['titolo']);
                $('#anno').val(data['anno']);
                $('#barcode').val(data['barcode']);
                $('#etichetta').val(data['etichetta']);
                $('#genere').val(data['genere']).trigger('change');
                $('#formato').val(data['formato']).trigger('change');
                if ($('#formato').val() === 'DIGITALE') {
                    disableStatoConservazione();
                } else {
                    $('#statoConservazione').val(data['stato_conservazione']).trigger('change');
                }
                $('.selectpicker').selectpicker('refresh');
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento degli autori.");
            },
            cache: false,
        });
        $.ajax({
            url: "rest/autori",
            method: "GET",
            success: function (data) {
                $.each(data, function (key) {
                        $.ajax({
                            url: "rest/autori/" + data[key].split("/")[6],
                            method: "GET",
                            success: function (data) {
                                $('#autore').append('<option value="' + data['id'] + '">' + data['nome_artistico'] + '</option>');
                                $('.selectpicker').selectpicker('refresh');
                            }
                        })
                    }
                );
            },
            error: function (request, status, error) {
                handleError(request, status, error, "#collezione", "Errore nel caricamento degli autori.");
            },
            cache: false,
        });
    }

    $('#formato').on('change', function () {
        disableStatoConservazione();
    });

    function disableStatoConservazione() {
        let selected = $('#formato option:selected');
        let statoConservazione = $('#statoConservazione');
        if (selected.val() === 'DIGITALE') {
            statoConservazione.val('').trigger('change');
            statoConservazione.prop('disabled', true);
            statoConservazione.prop('required', false);
            statoConservazione.parent().parent().addClass('off');
        } else {
            statoConservazione.prop('disabled', false);
            statoConservazione.prop('required', true);
            statoConservazione.parent().parent().removeClass('off');
        }
        $('.selectpicker').selectpicker('refresh');
    }
}