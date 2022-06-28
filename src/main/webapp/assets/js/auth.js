$(document).ready(function () {
    const login = $('#login');
    const login_form = $('#login-form');
    const login_btn = $('#login-btn');
    const login_send = $('#login-send');
    const logout_send = $('#logout-send');
    const logout = $('#logout');

    if (!(document.cookie.indexOf('token') === null)) {
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
            },
            errors: function (data) {
                alert("Errore di login");
            }
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
            },
            errors: function (data) {
                alert("Errore di logout");
            }
        });
    });
});
