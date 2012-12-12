// Generated by CoffeeScript 1.3.3
(function() {
  var GenericTextView;

  GenericTextView = (function() {

    function GenericTextView(top, title, text) {
      this.view = Titanium.UI.createView({
        backgroundImage: '/images/Texture.jpg',
        borderWidth: 0,
        width: Ti.UI.FILL,
        height: Ti.UI.SIZE,
        top: top
      });
      this.titleLabel = Titanium.UI.createLabel({
        text: title,
        height: Ti.UI.SIZE,
        top: 20,
        color: '#fff',
        left: 10,
        font: {
          fontSize: 24,
          fontWeight: 'bold'
        }
      });
      this.textLabel = Titanium.UI.createLabel({
        text: text + '\n\n',
        borderWidth: 0,
        top: 80,
        height: Ti.UI.SIZE,
        color: '#fff',
        left: 10,
        right: 10,
        font: {
          fontSize: 14,
          fontWeight: 'bold'
        }
      });
      this.view.add(this.titleLabel);
      this.view.add(this.textLabel);
    }

    return GenericTextView;

  })();

  root.GenericTextView = GenericTextView;

}).call(this);
