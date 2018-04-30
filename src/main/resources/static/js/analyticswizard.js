function submitDetailsForm() {
    var projectName = $("#input-project-name").val();
    $.ajax({
      type : "PUT",
      url : `/a/projects/${projectName}/analytics-wizard~stack`,
      dataType: 'application/json',
      // Initially project-dashboard is a 1 to 1 relationship
      data: "{'dashboard_name': '" + projectName + "}'}",
      contentType:"application/json; charset=utf-8",
      success : function() {
      console.log("Getting configuration parameters!");
      }
    });
}

function showConfigDetails() {
    var projectName = $("#input-project-name").val();
    $.ajax({
            url: `/projects/${projectName}/analytics-wizard~stack`,
            type: "GET",
            dataType: "json",
            success: function(data) {
                  console.log("data -->" + JSON.stringify(data));
                  $('#config-section').show();
                  $('#config-file').text( $('#config-file').text() + data.config_file_name);
                  $('#project-name').text($('#project-name').text() + data.name);
            },
            error: function(e) {
              console.error(JSON.stringify(e));
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