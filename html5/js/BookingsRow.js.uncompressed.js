// Generated by CoffeeScript 1.3.3
(function() {
  var BookingsRow;

  BookingsRow = (function() {

    function BookingsRow(booking) {
      this.row = Ti.UI.createTableViewRow({
        hasChild: true,
        rightImage: '/images/grey_arrow.png',
        backgroundGradient: root.bgGradient,
        booking: booking
      });
    }

    return BookingsRow;

  })();

  root.BookingsRow = BookingsRow;

}).call(this);
