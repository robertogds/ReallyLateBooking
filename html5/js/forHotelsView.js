(function(){var a,b;b=L("forHotelsTitle");a=L("forHotelsText");b=(new root.GenericTextView(0,b,a)).view;a=new root.Generic2RowsView(200,"hoteles@reallylatebooking.com","Llamar ");a.table.height=44;a.label1.addEventListener("click",function(){var a;a=Titanium.UI.createEmailDialog();a.subject=L("forHotelsSubject");a.toRecipients=["hoteles@reallylatebooking.com"];return a.open()});root.forHotelsWindow.add(b);root.forHotelsWindow.add(a.view)}).call(this);