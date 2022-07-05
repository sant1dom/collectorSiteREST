/*
* File per l'autenticazione.
* @Author: Davide De Acetis
* @Author: Raluca Mihaela Bujoreanu
*/

$(document).ready(function () {
    const login = $('#login');
    const login_form = $('#login-form');
    const login_btn = $('#login-btn');
    const login_send = $('#login-send');
    const logout_send = $('#logout-send');
    const logout = $('#logout');

    if (!(document.cookie.indexOf('token'))) {
        login.css('display', 'none');
        login_form.css('display', 'none');
        logout.css('display', 'block');
    }


    // Toggle visibility of login form
    login_btn.click(function () {
        toggleVisibility(login_form);
    });

    // Send login request
    login_send.click(function () {
        const username = $('#username').val();
        const password = $('#password').val();
        $.ajax({
            url: 'rest/auth/login',
            type: 'POST',
            data: {
                username: username,
                password: password
            },
            success: function (data) {
                document.cookie = "token=" + data;
                login.css('display', 'none');
                login_form.css('display', 'none');
                logout.css('display', 'block');
                message("Login effettuato con successo.", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "", "Errore in fase di login.");
            },
            cache: false,
        });
    });

    // Send logout request
    logout_send.click(function () {
        $.ajax({
            url: 'rest/auth/logout',
            type: 'DELETE',
            success: function () {
                // noinspection JSUnresolvedFunction
                $.removeCookie('token');
                login.css('display', 'block');
                logout.css('display', 'none');
                message("Logout effettuato con successo.", "success");
            },
            error: function (request, status, error) {
                handleError(request, status, error, "", "Errore in fase di logout.");
            },
            cache: false,
        });
    });
});
