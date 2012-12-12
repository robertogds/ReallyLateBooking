// Generated by CoffeeScript 1.3.3
(function() {
  var ZoneCell;

  ZoneCell = (function() {

    function ZoneCell(row, zone, city, left) {
      var borderView, zoneImage, zoneLabel;
      zoneImage = Titanium.UI.createImageView({
        borderRadius: 0,
        borderWidth: 1,
        borderColor: 'black',
        image: zone.image,
        zoneIndex: zone.index,
        zoneUrl: zone.url,
        city: city,
        left: left,
        width: 79,
        height: 59
      });
      borderView = Titanium.UI.createView({
        backgroundColor: 'black',
        color: 'black',
        height: 80,
        width: 60,
        visible: true,
        left: left
      });
      zoneLabel = Titanium.UI.createLabel({
        text: L(zone.url),
        color: 'black',
        textAlign: 'center',
        font: {
          fontSize: 12,
          fontWeight: 'bold'
        },
        left: left,
        height: 60,
        width: 80
      });
      row.add(borderView);
      row.add(zoneImage);
      row.add(zoneLabel);
      zoneImage.addEventListener('click', function(e) {
        root.showLoading(root.citiesWindow, L('updatingHotels'));
        Ti.API.info('metemos zone' + e.source.zoneName);
        root.zoneIndex = e.source.zoneIndex;
        root.zoneUrl = e.source.zoneUrl;
        return root.loadDeals(e.source.city);
      });
    }

    return ZoneCell;

  })();

  root.zoneCell = ZoneCell;

}).call(this);
