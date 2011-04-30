var App = {
    Views: {},
    Controllers: {},
    Collections: {},
    init: function() {
        // Create our global collection of Deals.
        App.Deals = new App.Collections.Deals();
        new App.Controllers.BacklogCtrl();
        Backbone.history.start();
    }
};








