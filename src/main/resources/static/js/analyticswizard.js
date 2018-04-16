$(function() {
	$("#submit").click(function() {
			$.ajax({
				type : "GET",
				url : "analyticsWizardSetup",
				success : function() {
          console.log("Getting configuration parameters!");
				}
			});
	});
});

$(document).ready(function () {
	console.log("Starting Analytics wizard plugin...");
});