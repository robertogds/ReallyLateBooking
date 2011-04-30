Sprint.Views.EditDeal = Backbone.View.extend({
    
    events: {
        "submit form": "save"
    },
    
    initialize: function() {
        _.bindAll(this, 'render');
		this.model.bind('change', this.render);
		this.render();
    },
	
	// Generate the attributes for a new Deal item.
	newAttributes: function() {
	    return {
		  hotel_name: this.$('[name=hotel_name]').val(),
		  sale_price: this.$('[name=sale_price]').val(),
          normal_price: this.$('[name=normal_price]').val(),
	      active:    false
	    };
	},

    editAttributes: function() {
	    return {
		  title: this.$('[name=title]').val(),
		  content: this.$('[name=content]').val(),
          estimate: this.$('[name=estimate]').val()
	    };
	},
    
    save: function() {
        var self = this;

        var msg = this.model.isNew() ? 'Successfully created!' : "Saved!";
        var attributes = this.editAttributes();
        if (this.model.isNew()) { attributes = this.newAttributes(); }

        this.model.save(attributes, {
           success: function(model, resp) {
				window.location.hash = '#';
            },

            error: function(model,e) {
				alert(e);
            //    new App.Views.Error();
            }
        });
        
        return false;
    },
    
    render: function() {
	        $(this.el).html(JST.deal({ model: this.model }));
            //this.$( "#dialog" ).dialog();
	        $('#newdeal').html(this.el);
	        // use val to fill in title, for security reasons
	        this.$('[name=hotel_name]').val(this.model.get('hotel_name'));

	        this.delegateEvents();
    }
});