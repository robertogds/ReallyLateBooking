(function(){var e,f;Ti.Geolocation.preferredProvider="gps";Ti.Geolocation.purpose="Get Current Location";root.gpsCountry=void 0;root.gpsCity=void 0;Number.prototype.toDeg=function(){return 180*this/Math.PI};Number.prototype.toRad=function(){return this*Math.PI/180};e=function(a,h){var b,c,d,k,l,e,i,g,j,f;i=1E3;g=void 0;c=root.staticCities.concat(root.staticOtherCities);Ti.API.info("************** LONG de listCities = "+root.listCities.length);c=root.listCities.length>c.length?root.listCities:c;for(j=
0,f=c.length;j<f;j++)d=c[j],b=d.latitude*Math.PI/180,e=a*Math.PI/180,k=(d.latitude-a)*Math.PI/180,l=(d.longitude-h)*Math.PI/180,b=Math.sin(k/2)*Math.sin(k/2)+Math.cos(e)*Math.cos(b)*Math.sin(l/2)*Math.sin(l/2),b=2*Math.atan2(Math.sqrt(b),Math.sqrt(1-b)),b*=6371,Ti.API.info("City: "+d.name+" distance: "+b),b<i&&(g=d,i=b);if(100>i&&void 0!==g)return Ti.API.info("+++ GPS Encontrada NEARCITY = "+g.name),root.loadDeals(g);root.hideLoading(root.citiesWindow);return Ti.UI.createAlertDialog({title:"ReallyLateBooking",
message:L("noDealsGPS")}).show()};f=function(a){if(null===a)return null;switch(a){case Ti.Geolocation.ERROR_LOCATION_UNKNOWN:return"Location unknown";case Ti.Geolocation.ERROR_DENIED:return"Access denied";case Ti.Geolocation.ERROR_NETWORK:return"Network error";case Ti.Geolocation.ERROR_HEADING_FAILURE:return"Failure to detect heading";case Ti.Geolocation.ERROR_REGION_MONITORING_DENIED:return"Region monitoring access denied";case Ti.Geolocation.ERROR_REGION_MONITORING_FAILURE:return"Region monitoring access failure";
case Ti.Geolocation.ERROR_REGION_MONITORING_DELAYED:return"Region monitoring setup delayed"}};root.initializeGPS=function(){var a;root.tabGroup.open();root.startupWindow.close();root.isGPS=!0;if(!1===Titanium.Geolocation.locationServicesEnabled)return Ti.API.info("Entra en geo off"),Titanium.UI.createAlertDialog({title:"ReallyLateBooking",message:L("geoOff")}).show(),root.hideLoading(root.citiesWindow);a=Titanium.Geolocation.locationServicesAuthorization;if(a===Titanium.Geolocation.AUTHORIZATION_DENIED)return Ti.UI.createAlertDialog({title:"ReallyLateBooking",
message:L("youGeoDisallow")}).show(),root.hideLoading(root.citiesWindow);return a===Titanium.Geolocation.AUTHORIZATION_RESTRICTED?(Ti.UI.createAlertDialog({title:"ReallyLateBooking",message:L("systemGeoDisallow")}).show(),root.hideLoading(root.citiesWindow)):root.getGPSData()};root.getGPSData=function(){Titanium.Geolocation.accuracy=Titanium.Geolocation.ACCURACY_HIGH;Titanium.Geolocation.distanceFilter=10;return Titanium.Geolocation.getCurrentPosition(function(a){var h,b,c,d;Ti.API.info("Entra en getCurrentPosition ");
if(!a.success||a.error)Ti.API.info("Code translation: "+f(a.code)),Ti.UI.createAlertDialog({title:"ReallyLateBooking",message:L("geoOff")}).show(),root.hideLoading(root.citiesWindow);else return c=a.coords.longitude,b=a.coords.latitude,h=a.coords.accuracy,d=a.coords.speed,a=a.coords.timestamp,Ti.API.info("speed "+d),Ti.API.info("long:"+c+" lat: "+b),e(b,c),Titanium.API.info("geo - current location: "+new Date(a)+" long "+c+" lat "+b+" accuracy "+h),!0})}}).call(this);