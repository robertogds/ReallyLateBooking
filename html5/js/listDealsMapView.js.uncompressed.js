// Generated by CoffeeScript 1.3.3
(function() {

  root.createMap = function(deals) {
    var a, deal, region, subtitle, _i, _len;
    root.annotations = [];
    region = {
      latitudeDelta: 0.07,
      longitudeDelta: 0.07,
      latitude: root.city.latitude,
      longitude: root.city.longitude
    };
    a = 0;
    for (_i = 0, _len = deals.length; _i < _len; _i++) {
      deal = deals[_i];
      Ti.API.info('****** COORD CIUDAD =  ' + deal.city.longitude + ' ' + deal.city.latitude);
      subtitle = L('tonight') + ': ' + deal.salePriceCents + '€';
      root.mbv[a] = new root.GenericMapRightButtonView(deal).view;
      root.mbv[a].addEventListener('click', function(e) {
        root.showDealView(e.source.deal);
        return root.oneDealWindow.open();
      });
      root.annotations[a] = new root.GenericMapAnnotation(deal.id, deal.latitude, deal.longitude, deal.hotelName, subtitle, root.mbv[a]).annotation;
      root.listDealsMapView.addAnnotation(root.annotations[a]);
      a++;
    }
    return root.listDealsMapView.region = region;
  };

}).call(this);
