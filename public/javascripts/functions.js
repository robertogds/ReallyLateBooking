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
				 fillItemsList(data);
			}, // body is a string (or if dataType is 'json', a parsed JSON object)
		error: function(xhr, type) {
				alert("error at loadItemsList: " + type + " | " + xhr);
				
			} // type is a string ('error' for HTTP errors, 'parsererror' for invalid JSON)
	});
};

function loadItemDetail(detail_url, item_id){
	$.ajax({
		type: 'GET',
		url: detail_url + item_id, // defaults to window.location
		//data: "{item_id : '" + item_id + "'}",
		dataType: 'json', // what response type you accept from the server ('json', 'xml', 'html', or 'text')
		success: function(data) { 
				fillItemDetail(data);
			}, 
		error: function(xhr, type) {
				alert("error en loadItemDetail: " + type + " | " + xhr);
			} 
	});
};
function fillItemsList(d){
	
	var itemHTML = "";
	var contentLayout = "";
	for (i=0;i<d.length;i++){
		itemHTML 	+= 	"<li class='pf type_" + d[i].roomType + "'>";
		itemHTML 	+=	"	<div class='pf_l'>";
		itemHTML	+=	"		<div><img src='" + d[i].mainImageSmall +"'/></div>";
		itemHTML	+=	"  	</div>";
		itemHTML	+=	"  	<div class='pf_r'>";
		itemHTML 	+=	"	  <h2><a href='#item_" + d[i].id + "' rel='"+d[i].id+"' class='moreInfo'>" + d[i].hotelName + "</a></h2>";
		itemHTML 	+=	"	  <div class='price_mod'>";
	//	itemHTML 	+=	"	    <em>esta noche</em>";
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
	
	$('#items_list').append(itemHTML);
}

function fillItemDetail(data){
	var itemHTML	 =	"	<img src='http://lorempixum.com/300/200/sports/" + data.img +"' width='300' height='200'/>";
	itemHTML 		+=	"	<p>"+  data.desc +"</p>";

	$('#item_'+  data.id +' article').html(itemHTML);
}