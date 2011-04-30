// User Stories Collection
  // ---------------
  App.Collections.Deals = Backbone.Collection.extend({

    // Reference to this collection's model.
    model: Deal,

    parse: function(response) {
      return response;
    },

	url :'/deal',

    // Deals are sorted by their original insertion order.
    comparator: function(task) {
      return deal.get('sale_price');
    }

  });