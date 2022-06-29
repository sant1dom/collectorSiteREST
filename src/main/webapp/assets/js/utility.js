function toggleVisibility(elem) {
    if (elem.css('display') === 'none') {
        elem.css('display', 'block');
    } else {
        elem.css('display', 'none');
    }
}

function message (message, type) {
    const message_span = $('#message');
    message_span.html(message);
    if (type === 'error') {
        message_span.css('color', 'red');
    } else {
        message_span.css('color', 'green');
    }
}

function handleError(request, status, error, table) {
    switch (request.status) {
        case 404:
            table.show();
            table.text("Elemento non presente.");
            break;
        case 401:
            table.show();
            table.text("Non sei autorizzato.");
            break;
        default:
            table.show();
            table.text("Errore generico");
    }
}