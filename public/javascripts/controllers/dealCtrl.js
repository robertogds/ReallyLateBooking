// Controller for the backlog items column
App.Controllers.dealCtrl = Backbone.Controller.extend({
    routes: {
        "":              "index",
        "deal/:id":      "edit",
        "savedeal":      "saveDeal",
		"new":           "newDeal"
    },
    
	edit: function(id) {
        var deal = new Deal({ id: id });
        deal.fetch({
            success: function(model, resp) {
                new App.Views.EditDeal({ model: deal });
            },
            error: function() {
                new Error({ message: 'Could not find that deal.' });
                window.location.hash = '#';
            }
        });
    },
      index: function() {
	        App.Deals.fetch({
	            success: function() {
                    new App.Views.DealView;
	            },
	            error: function() {
	                new Error({ message: "Error loading deals." });
	            }
	        });
	    },

        saveDeal: function() {
          //alert("salvando tarea");
            this.index();
        },
		
		newDeal: function() {
			var deal = new Deal();
	        new App.Views.EditDeal({ model: deal, collection: App.Deals });

	    }
});