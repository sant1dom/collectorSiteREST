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

function handleError(request, status, error, table, msg) {
    const table_empty = $(table + "-empty");
    const table_result = $(table + "-result");

    table_empty.show()
    table_result.children().remove();
    switch (request.status) {
        case 404:
            table_empty.text("Elemento non presente.");
            message("Elemento non presente.", "error");
            break;
        case 401:
            table_empty.text("Non sei autorizzato.");
            message("Non sei autorizzato.", "error");
            break;
        default:
            table_empty.text(msg);
            message(msg, "error");
    }
}