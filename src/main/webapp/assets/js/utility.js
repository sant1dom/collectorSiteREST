function toggleVisibility(elem) {
    if (elem.css('display') === 'none') {
        elem.css('display', 'block');
    } else {
        elem.css('display', 'none');
    }
}

function handleError(request, status, error) {
    switch (request.status) {
        case 404:
            collezione_empty.show();
            collezione_empty.text("Collezione inesistente");
            break;
        case 401:
            collezione_empty.show();
            collezione_empty.text("Non sei autorizzato a visualizzare questa collezione");
            break;
        default:
            collezione_empty.show();
            collezione_empty.text("Errore generico");

    }
}