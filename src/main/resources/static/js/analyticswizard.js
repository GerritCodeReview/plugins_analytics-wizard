function submitDetailsForm() {
	$("#analytics-dashboard-form").click(function(e) {

	  //prevent Default functionality
    e.preventDefault();

    //get the action-url of the form
    var actionurl = e.currentTarget.action;
    $.ajax({
      type : "PUT",
      //XXX Read current project!
      url : "/projects/elasticsearch/analytics-wizard~stack",
      dataType: 'application/json',
      data: $('#analytics-dashboard-form').serialize(),
      success : function() {
        console.log("Getting configuration parameters!");
      }
    });
	});
}

function showConfigDetails() {
    $.ajax({
            url: "/projects/elasticsearch/analytics-wizard~stack",
            type: "GET",
            dataType: "json",
            success: function(data) {
                  console.log("data -->" + JSON.stringify(data));
            },
            error: function(e) {
            	console.log("error -->" + JSON.stringify(e));
            }
        });
}

$(document).ready(function () {
	console.log("Starting Analytics wizard plugin...");
	$.ajaxSetup({
  		dataFilter: function(data, type) {
  		  //Strip out Gerrit API prefix
  			var prefixes = [")]}'"];

  			if (type != 'json' && type != 'jsonp') {
  				return data;
  			}

  			for (i = 0, l = prefixes.length; i < l; i++) {
  				pos = data.indexOf(prefixes[i]);
  				if (pos === 0) {
  					return data.substring(prefixes[i].length);
  				}
  			}

  			console.log("Parsed data: " + JSON.stringify(data))
  			return data;
  		}
  });
});