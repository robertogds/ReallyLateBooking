<script type="text/javascript" charset="utf-8">
 $(function() {
	$('${_observed}').change(function() {
		$('${_target}').empty();
		var obs_name = $('${_observed}').attr('name');
		var obs_val = $('${_observed}').attr('value');
		//alert(obs_name+" - "+obs_val);
		$.getJSON('${_url}/'+obs_val,
	        function(data){
	        	var options = '';
	          $.each(data, function(i,item){
	           options += '<option value="' +  item.${_value} + '">' + item.${_label} + '</option>';
	          });
	          $('${_target}').html(options);
        });	
	});
});

</script>