(function(){Ti.API.info("Entra aqui");root.loadUser();Ti.include("/js/faqView.js","/js/facebookButton.js","/js/editAccountView.js","/js/newAccountView.js","/js/loginAction.js","/js/logoutAction.js","/js/newFacebookAccount.js","/js/rememberPassView.js","/js/signInView.js","/js/rememberPassAction.js");root.accountView=Titanium.UI.createView({backgroundColor:"transparent"});root.loginLabelView=new root.Generic2RowsView(20,L("loginLabel"));root.loginLabelView.table.height=44;root.registerLabelView=new root.Generic2RowsView(80,
L("needAccount"));root.registerLabelView.table.height=44;root.isLogged()&&(root.loginLabelView.label1.text=L("signedInAs")+" "+root.user.email,root.registerLabelView.label1.text=L("logout"));root.loginLabelView.label1.addEventListener("click",function(){return root.isLogged()?(root.tabGroup.activeTab.open(root.editAccountWindow,{animated:!0}),root.loadEditLoggedUser(),root.editAccountWindow.setTitle(L("editAccount"))):root.showSignInView("account")});root.registerLabelView.label1.addEventListener("click",
function(){return root.isLogged()?(Ti.API.info("***** Llama a hacer logout"),root.confirmLogout.show()):root.showNewAccount()});root.accountView.add(root.loginLabelView.view);root.accountView.add(root.registerLabelView.view)}).call(this);