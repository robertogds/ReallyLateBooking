// This is the deals view
App.Views.DealView = Backbone.View.extend({
    events: {
        "click .delete_deal": "delete_deal",
        "click .edit_deal": "edit_deal"
    },

    initialize: function() {
        this.render();
    },

    render: function() {
        $(this.el).html(JST.deals_collection({ collection: App.Deals}));
        $('#items').html(this.el);
        this.delegateEvents();
        return this;
    },

    delete_deal: function(event){
        var deal = new Deal();
        deal = App.Deals.get(event.currentTarget.id);
        App.Deals.remove(deal);
        deal.destroy();
        this.render();
    },

    edit_deal: function(event){
        window.location="/#deal/"+ event.currentTarget.id;
    }

});