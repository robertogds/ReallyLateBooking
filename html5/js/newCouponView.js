(function(){var a,b;root.newCouponView=Titanium.UI.createView({height:80,borderWidth:0,boderColor:"grey",top:0});root.codeText=Titanium.UI.createTextField({backgroundColor:"#fff",top:37,left:140,width:120,height:20,borderRadius:8,color:"#336699",hintText:"Code",clearOnEdit:!0,paddingLeft:10,returnKeyType:Titanium.UI.RETURNKEY_DONE});root.codeText.addEventListener("return",function(){Ti.API.info("Entra en return validar con code = "+root.codeText.value);root.showLoading(root.creditsWindow);return root.validateCoupon(root.codeText.value)});
b=(new root.GenericBlueTitleLabel(5,10,L("have_coupon_code"))).label;a=(new root.GenericTextLabel(40,10,L("enter_code"))).label;root.newCouponView.add(b);root.newCouponView.add(a);root.newCouponView.add(root.codeText)}).call(this);