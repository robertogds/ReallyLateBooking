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
	
	function showResponse${timestamp}(responseText, statusText){
		${_callback};
		if(statusText == 'success' && (responseText.status == '200' || responseText.status == '201' || responseText.status == '202')){
			$('#${_resultDiv}').html(responseText.detail);
			$('#${_resultDiv}').fadeIn();
		}else{
			$('#${_errorDiv}').html(responseText.detail);
			$('#${_errorDiv}').fadeIn();
		}
	}

	$(document).ready(function(){
		var options = { 
	        target:        '#${_resultDiv}',   
	        beforeSubmit:  hidePrevResponse${timestamp}, 
	        success:       showResponse${timestamp}
        }
		
		$("#frm${timestamp}").ajaxForm(options); 
	});
</script> 

<form id="frm${timestamp}" method="POST" action="${_action}">
	#{doBody /}
</form>