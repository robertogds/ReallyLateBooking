{%
	Long timestamp= new java.util.Date().getTime();
%}


<script type="text/javascript" charset="utf-8">

	function hidePrevResponse${timestamp}(formData, jqForm, options){
		try{	
			$('#${_resultDiv}').fadeOut();
			$('#${_errorDiv}').hide()
		 }
		catch(err){}	
	    return true; 
	}
	
	function showResponse${timestamp}(response, statusText){
		${_callback};
		if(statusText == 'success' && (response.status == '200' || response.status == '201' || response.status == '202')){
			$('#${_resultDiv}').html(response.detail);
			$('#${_resultDiv}').fadeIn();
		}else{
			$('#${_errorDiv}').html(response.detail);
			$('#${_errorDiv}').fadeIn();
		}
	}

	$(document).ready(function(){
		var options = { 
	        target:        '#${_resultDiv}',   
	        beforeSubmit:  hidePrevResponse${timestamp}, 
	        success: function(response, textStatus, xhr, form) {
		        console.log("in ajaxForm success");
		        showResponse${timestamp}(response, textStatus);
		    },
		    error: function(xhr, textStatus, errorThrown) {
		        console.log("in ajaxForm error");
		    },
        }
		
		$("#frm${timestamp}").ajaxForm(options); 
	});
</script> 

<form id="frm${timestamp}" method="POST" action="${_action}" class="${_class}" >
	#{doBody /}
</form>