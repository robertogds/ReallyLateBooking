(function(){var a,b;root.noBookingsView=Titanium.UI.createView({backgroundImage:"/images/Texture.jpg"});b=L("bookingTonight");a=L("yetNoBook");a=(new root.GenericTextView(0,b,a)).view;root.noBookingsView.add(a);root.bookingsWindow.add(root.noBookingsView)}).call(this);