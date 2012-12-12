// Generated by CoffeeScript 1.3.3
(function() {
  var CityCell;

  CityCell = (function() {

    function CityCell(row, city, left) {
      var borderView, moreCitiesLabel;
      this.cityImage = Titanium.UI.createImageView({
        image: city.image,
        city: city,
        left: left,
        width: 105,
        height: 105,
        open: false,
        top: 0
      });
      this.inactiveView = Titanium.UI.createView({
        backgroundColor: '#0d1e28',
        opacity: 1,
        height: 107,
        width: 107,
        visible: false,
        top: 0,
        left: left,
        cityName: L('more_cities')
      });
      moreCitiesLabel = Titanium.UI.createLabel({
        text: L('view_all_cities'),
        width: '90%',
        textAlign: 'center',
        height: 107,
        top: 0,
        color: '#d3d3d3',
        font: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      });
      this.inactiveView.add(moreCitiesLabel);
      borderView = Titanium.UI.createView({
        backgroundColor: 'black',
        color: 'black',
        height: 107,
        width: 107,
        visible: true,
        top: 0,
        left: left
      });
      this.cityLabel = Titanium.UI.createLabel({
        text: L(city.url),
        color: 'white',
        textAlign: 'center',
        font: {
          fontSize: 14,
          fontWeight: 'bold'
        },
        left: left,
        height: 18,
        width: 107,
        top: 88
      });
      this.cityBarLabel = Titanium.UI.createLabel({
        borderWidth: 0,
        backgroundColor: 'black',
        opacity: 0.3,
        color: '#fff',
        left: left,
        height: 18,
        width: 107,
        top: 89
      });
      row.add(borderView);
      row.add(this.cityImage);
      row.add(this.cityBarLabel);
      row.add(this.cityLabel);
      row.add(this.inactiveView);
      this.inactiveView.addEventListener('click', function(e) {
        return root.showAllCities();
      });
      this.cityImage.addEventListener('click', function(e) {
        var cell;
        Ti.API.info('*** Entra en click');
        city = e.source.city;
        cell = root.cell[city.name];
        root.showLoading(root.citiesWindow, L('updatingHotels'));
        Ti.API.info('*** LLama a loadDeals');
        return root.loadDeals(e.source.city);
      });
    }

    return CityCell;

  })();

  root.cityCell = CityCell;

}).call(this);
