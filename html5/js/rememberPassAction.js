(function(){root.xhrRemember=Titanium.Network.createHTTPClient({timeout:15E3});root.xhrRemember.onerror=function(a){root.hideLoading(root.rememberPassWindow);root.showError();return Ti.API.error(a)};root.xhrRemember.onload=function(){var a;root.hideLoading(root.rememberPassWindow);a=JSON.parse(this.responseText);return 200===a.status?(root.rememberPassWindow.close(),Ti.UI.createAlertDialog({title:"ReallyLateBooking",message:a.detail}).show()):alert("Error: "+a.detail)};root.doRememberPass=function(a){root.xhrRemember.open("POST",
root.url+"/users/remember");root.xhrRemember.setRequestHeader("Content-Type","application/json; charset=utf-8");root.xhrRemember.setRequestHeader("Accept-Language",Titanium.Locale.currentLanguage);return root.xhrRemember.send(JSON.stringify({email:a}))}}).call(this);