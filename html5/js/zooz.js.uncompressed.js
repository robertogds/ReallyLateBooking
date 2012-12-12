// Generated by CoffeeScript 1.3.3
(function() {

  root.zoozButton = Ti.UI.createButton({
    title: "Purchase...",
    height: 50,
    width: 250,
    bottom: 20
  });

  root.zoozButton.addEventListener('click', function(e) {
    var zoozmodule;
    Ti.API.info("payBtn clicked");
    zoozmodule = require('com.zooz.ti');
    return zoozmodule.doPayment({
      data: {
        amount: root.cantidadText.value,
        currencyCode: "EUR",
        appKey: "4b71ac79-9338-4cc0-91ad-220572fd54b3",
        isSandbox: false,
        email: ''
      },
      success: function(data) {
        Ti.API.info('Result success!');
        return Ti.UI.createAlertDialog({
          title: "Transaction Completed",
          message: "TransactionId: " + data.transactionId
        }).show();
      },
      error: function(data) {
        Ti.API.info('Callback error called.');
        return Ti.UI.createAlertDialog({
          title: "Transaction Failed",
          message: "Error Code: " + data.errorCode + " ; Error Message: " + data.errorMessage
        }).show();
      },
      cancel: function(data) {
        Ti.API.info('Callback cancel called.');
        return Ti.UI.createAlertDialog({
          title: "Transaction Aborted"
        }).show();
      }
    });
  });

}).call(this);
