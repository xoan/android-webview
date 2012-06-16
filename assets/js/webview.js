xui.extend({
    data: function(attribute, val) {
        if (arguments.length == 2) {
            return this.each(function(el) {
                if (el.setAttribute) {
                    el.setAttribute('data-' + attribute, val);
                }
            });
        } else {
            var attrs = [];
            this.each(function(el) {
                if (el.getAttribute && el.getAttribute('data-' + attribute)) {
                    attrs.push(el.getAttribute('data-' + attribute));
                }
            });
            return attrs;
        }
    }
});

xui.ready(function() {
    x$(document).on("backbutton", function(event) {
        intern.finish();
    });

    x$(document).on("menuitem", function(event) {
        switch (event.data.item_id) {
            case "close":
                intern.finish();
            break;
            case "help":
                x$(".help").toggleClass("hide");
            break;
        }
    });

    x$("a.toast").on("click", function(event) {
        intern.toast(x$(this).data("message"));
        event.preventDefault();
    });

    x$("a.exit").on("click", function(event) {
        intern.finish();
        event.preventDefault();
    });
});

var extern = {
    fireDOMEvent: function(event, data) {
        x$(document).fire(event, data);
    }
};

