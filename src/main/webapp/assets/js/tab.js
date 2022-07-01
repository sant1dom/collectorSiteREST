$(document).ready(function () {
    const tab_collezioni = $('#tab-collezioni');
    const tab_autori = $('#tab-autori');
    const tab_dischi = $('#tab-dischi');

    const tab_collezioni_btn = $('#tab-collezioni-btn');
    const tab_autori_btn = $('#tab-autori-btn');
    const tab_dischi_btn = $('#tab-dischi-btn');

    tab_autori.hide();
    tab_dischi.hide();

    function hideAll() {
        clear();
        tab_collezioni.hide();
        tab_autori.hide();
        tab_dischi.hide();

        tab_collezioni_btn.addClass('btn-dark');
        tab_autori_btn.addClass('btn-dark');
        tab_dischi_btn.addClass('btn-dark');
    }

    tab_collezioni_btn.click(function () {
        hideAll();
        tab_collezioni.show();
        if (tab_collezioni_btn.hasClass('btn-dark')) {
            tab_collezioni_btn.removeClass('btn-dark');
            tab_collezioni_btn.addClass('btn-warning');
        } else {
            tab_collezioni_btn.removeClass('btn-warning');
            tab_collezioni_btn.addClass('btn-dark');
        }
    });

    tab_autori_btn.click(function () {
        hideAll();
        tab_autori.show();
        if (tab_autori_btn.hasClass('btn-dark')) {
            tab_autori_btn.removeClass('btn-dark');
            tab_autori_btn.addClass('btn-warning');
        } else {
            tab_autori_btn.removeClass('btn-warning');
            tab_autori_btn.addClass('btn-dark');
        }
    });

    tab_dischi_btn.click(function () {
        hideAll();
        tab_dischi.show();
        if (tab_dischi_btn.hasClass('btn-dark')) {
            tab_dischi_btn.removeClass('btn-dark');
            tab_dischi_btn.addClass('btn-warning');
        } else {
            tab_dischi_btn.removeClass('btn-warning');
            tab_dischi_btn.addClass('btn-dark');
        }
    });
});