(function(){Ti.include("/js/supportView.js","/js/testWindow.js","/js/optionsView.js");root.optionsWindow.addEventListener("focus",function(){Ti.API.info("Entra en focus accountWindow");return root.loadAccountLabels()});root.loadAccountLabels=function(){if(root.isLogged())return root.loginLabelView.label1.text=L("signedInAs")+" "+root.user.email,root.registerLabelView.label1.text=L("logout");root.loginLabelView.label1.text=L("loginLabel");return root.registerLabelView.label1.text=L("needAccount")}}).call(this);