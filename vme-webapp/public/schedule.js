$( document ).delegate("#schedule", "pageinit", function() {
	
	jQuery.validator.setDefaults({
		debug: true,
		success: "valid"
	});

    var rules = {
    	name: { required: true},
    	longitude: {required: true, number: true},
    	latitude: {required: true, number: true}
    };

    $("#scheduleForm").validate({rules: rules, submitHandler: submitForm});

	function submitForm() {
		$.post("/schedule.html", $("#scheduleForm").serialize(), function(data){  
		    console.log("submitted");
		    console.log("response", data);
		    $.mobile.changePage("#thankyou")
		  }); 
	    console.log("done submittingx");
	};

	function success(position) {
		var lat = position.coords.latitude;
		var lon = position.coords.longitude;
		document.getElementById('latitude').value = lat;
		document.getElementById('longitude').value = lon;
	};
	function fail(error) {
		var myTextField = document.getElementById('error');
		myTextField.innerHTML = error.message;
	};
	navigator.geolocation.getCurrentPosition(success, fail);


});
