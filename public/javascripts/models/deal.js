var Deal = Backbone.Model.extend({

  EMPTY: "You need define this deal",

  initialize: function() {

  },

  validateAttributes: function() {
    return {
        sale_price: this.get("sale_price")
    }
  },

  toggle: function() {
    this.save({activate: !this.get("activate")});
  },

	url : function() {
		return this.id ? '/deal/' + this.id : '/deal';
	},

  clear: function() {
    this.destroy();
    this.view.remove();
  },

    validate:function(attrs){
        if(!attrs.sale_price && !this.get("sale_price")){
            return "Task must have a sale price";
        }
    }
});
