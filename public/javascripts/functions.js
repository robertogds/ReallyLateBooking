var root_deals;

function loadCitiesList(cities_url){
	$.ajax({
		type: 'POST',
		url: cities_url, 
		dataType: 'json', 
		success: function(data) { 
				 fillCitiesList(data);
			}, 
		error: function(xhr, type) {
				alert("error en loadItemsList: " + type + " | " + xhr);
				
			} 
	});
};

function fillCitiesList(d){
	var itemHTML = "";
	
	for (i=0;i<d.length;i++){
		itemHTML 	+= 	"<li class='city_" + d[i].type + "'>";
		itemHTML 	+=	"	  <h2><a href='#city_" + d[i].id + "' rel='"+d[i].url+"' class='moreInfo'>" + d[i].name + "</a></h2>";
		itemHTML 	+=	"	  <div class='arrow'></div>";
		itemHTML 	+=	"</li>";
		
		var city_list = "";
		city_list += '<section id="city_'+d[i].id+'">';
		city_list += '	<div class="toolbar">';
		//city_list += '		<a href="#cities" class="button">Cities</a>';	
		city_list += '		<h1>'+d[i].name+'</h1>';
		city_list += '	</div>';
		city_list += '	<ul id="city_list_'+d[i].id+'" class="list items_list"></ul>';
		city_list += '</section>';
		
		$("#endOfPage").before(city_list);
	} 
	
	$('#cities_list').append(itemHTML);
}

function setBackButton(prev_element, current_element){
	current_element.addClass("current");
	$(".current .back").remove();
	$(".current .toolbar").prepend("<a href=\""+prev_element+"\" class=\"back\">Back</a>");
};

function loadItemsList(deals_url, city){

	$.ajax({
		type: 'POST',
		url: deals_url, 
		data: "city=" + city,
		dataType: 'json', // ('json', 'xml', 'html', or 'text')
		success: function(data) { 
			     root_deals = data;
				 fillItemsList(data);
			}, // body is a string (or if dataType is 'json', a parsed JSON object)
		error: function(xhr, type) {
				alert("error at loadItemsList: " + type + " | " + xhr);
				
			} // type is a string ('error' for HTTP errors, 'parsererror' for invalid JSON)
	});
};

function loadItemDetail(item_id){
	var deal = findDealById(item_id);
	fillItemDetail(deal);
};

function findDealById(item_id){
	for (i=0;i<root_deals.length;i++){
		if(root_deals[i].id == item_id){
			return root_deals[i];
		}
	}
}

function fillItemsList(d){
	var itemHTML = "";
	var contentLayout = "";
	for (i=0;i<d.length;i++){
		itemHTML     = "";
		itemHTML 	+= 	"<li id='item_list_" + d[i].id + "' class='pf " + d[i].roomType + "'>";
		itemHTML 	+=	"	<div class='pf_l'>";
		itemHTML	+=	"		<div><img src='" + d[i].mainImageSmall +"' width='139' height='97' /></div>";
		itemHTML	+=	"  	</div>";
		itemHTML	+=	"  	<div class='pf_r'>";
		itemHTML 	+=	"	  <h2><a href='#item_" + d[i].id + "' rel='"+d[i].id+"' class='moreInfo'>" + d[i].hotelName + "</a></h2>";
		itemHTML 	+=	"	  <div class='price_mod'>";
		itemHTML 	+=	"	    <strong>" + d[i].salePriceCents + "<em>&euro;</em></strong>";
		itemHTML 	+=	"	    <div>";
		itemHTML 	+=	"	      <em>antes</em>";
		itemHTML 	+=	"	      <strong>" + d[i].priceCents + "<em>&euro;</em></strong>";
		itemHTML 	+=	"	    </div>";
		itemHTML 	+=	"	  </div>";
		itemHTML 	+=	"	  <div class='arrow'></div>";
		itemHTML 	+=	"	  <span class='type'><span></span></span>";
		itemHTML 	+=	"	</div>";
		itemHTML 	+=	"</li>";
		
		if (!$('#city_list_'+d[i].city.id+' li#item_list_'+d[i].id).size()) { 
			$('#city_list_'+d[i].city.id).append(itemHTML);
		}
		contentLayout = "";
		contentLayout += "<section class='detail' id='item_" + d[i].id + "'>";
		contentLayout += "	<div class='toolbar'>";
		contentLayout += "	  <h1>" + d[i].hotelName + "</h1>";
		contentLayout += "	</div>";
		contentLayout += "	<article>";
		contentLayout += "	</article>";
		contentLayout += "</section>";
		
		$("#endOfPage").before(contentLayout);
	} 
	
}

function fillItemDetail(d){
	var itemHTML =	"	<div class='image'>";
	itemHTML 	+=	"	  <a href='#'>";
	itemHTML 	+=	"	    <img src="+ d.mainImageBig +" width='100%' border='0'/>";
	itemHTML 	+=	"	  </a>";
	itemHTML 	+=	"	</div>";
	itemHTML 	+=	"	<div class='buy'>";
	itemHTML 	+=	"	  <div class='price_mod'>";
	itemHTML 	+=	"	    <strong>" + d.salePriceCents + "<em>&euro;</em></strong>";
	itemHTML 	+=	"	    <div>";
	itemHTML 	+=	"	      <em>antes</em>";
	itemHTML 	+=	"	      <strong>" + d.priceCents + "<em>&euro;</em></strong>";
	itemHTML 	+=	"	    </div>";
	itemHTML 	+=	"	  </div>";
	itemHTML 	+=	"	  <a href='#deal' id='buy_button' ><span>buy</span></a>";
	itemHTML 	+=	"	</div>";
	itemHTML 	+=	"	<div class='address'>";
	itemHTML 	+=	"	  <span class='icon'></span>";
	itemHTML 	+=	"	  <strong>" + d.address + "</strong>";
	itemHTML 	+=	"	  <span class='city'>" + d.city.name + "</span>";
	itemHTML 	+=	"	  <a hrf='#' class='map'>ver en mapa<span></span></a>";
	itemHTML 	+=	"	</div>";
	itemHTML 	+=	"	<div class='descr'>";
	itemHTML 	+=	"	  <span class='icon'></span>";
	itemHTML 	+=	"	  <strong>Descripci&oacute;n del hotel</strong>";
	itemHTML 	+=	"	  <div class='content'>" + d.hotelText + "</div>";
	itemHTML 	+=	"	</div>";

	$('#item_'+  d.id +' article').html(itemHTML);
};

function insertFooter(section){
	var itemHTML =	"<div class='footer'>";
	itemHTML 	+=	"	<ul>";
	itemHTML 	+=	"		<li class='cities'>";
	itemHTML 	+=	"			<a href='#'>";
	itemHTML 	+=	"				<span></span>Ciudades";
	itemHTML 	+=	"			</a>";
	itemHTML 	+=	"		</li>";
	itemHTML 	+=	"		<li class='myItems'>";
	itemHTML 	+=	"			<a href='#'>";
	itemHTML 	+=	"				<span></span>Reservas";
	itemHTML 	+=	"			</a>";
	itemHTML 	+=	"		</li>";
	itemHTML 	+=	"		<li class='myData'>";
	itemHTML 	+=	"			<a href='#'>";
	itemHTML 	+=	"				<span></span>Mis Datos";
	itemHTML 	+=	"			</a>";
	itemHTML 	+=	"		</li>";
	itemHTML 	+=	"	</ul>";
	itemHTML 	+=	"</div>";
	
	$('section#'+section).append(itemHTML);
}

function showWelcomeScreen(window_height){
	
	var next_screen = '#login';
	$('img.welcome_bg_img').css('height', window_height+'px');
	
	// show welcome screen 3 seconds
	$('#welcome').show().removeClass('hidden');
	setTimeout(function(){
		  $(next_screen).addClass("current");
		  $('#welcome').remove();
	},500); 
	
	// remove this line when welcome screen is show
	//$(next_screen).addClass("current");
}

function doLogin(login_url, email,password, curTaget, link){
	var user = "{'email':'"+ email + "','password':'" + password + "'}";
	$.ajax({
		type: 'POST',
		url: login_url, 
		dataType: 'json', 
		data: user,
		success: function(data) { 
			if (data.status == 200){
				var section = curTaget.closest('section');
				var prev_element = "#"+(section.removeClass("current").addClass("reverse").attr("id"));
				var t = $(link.attr("href"));
				var t_id = link.attr("rel");
				$(t_id).addClass("current");
				setBackButton(prev_element, t);
			}
			else{
				$('.loginError').show();
			}
		}, 
		error: function(xhr, type) {
			alert("error en login: " + type + " | " + xhr);
		} 
	});
};

function validateLogin(data){
	
	  
}
