(function(){Ti.include("/js/CityHeaderView.js","/js/OtherCityRow.js");root.allCitiesTable=Titanium.UI.createTableView({data:[],backgroundColor:"transparent",separatorColor:"#1b3c50"});root.allCitiesWindow.add(root.allCitiesTable);root.allCitiesTable.addEventListener("click",function(e){root.showLoading(root.allCitiesWindow,L("updatingHotels"));return root.loadDeals(e.row.city)});root.populateCitiesTable=function(e){var a,h,b,g,c,d,f,i;Ti.API.info("Entra en all cities");b=[];c="empty";g=!0;d=Titanium.UI.createTableViewSection();
for(f=0,i=e.length;f<i;f++)a=e[f],Ti.API.info("Entra en city "+a.name+" Country "+a.country),a.isRoot&&(h=new root.OtherCityRow(a),a.country!==c&&(!0!==g&&b.push(d),c=new root.CityHeaderView(a.country),g=!1,d=Titanium.UI.createTableViewSection({headerView:c.view})),d.add(h.row),c=a.country);b.push(d);0===b.length?root.allCitiesWindow.close():(root.allCitiesTable.setData(b),root.allCitiesTable.footerView=root.footerView);root.hideLoading(root.allCitiesWindow);return Ti.API.info("*** fin populateDealsZoneTable")};
root.showAllCities=function(){return root.fetchCities("ALLCITIES")}}).call(this);