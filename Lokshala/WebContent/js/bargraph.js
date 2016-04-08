$(document).ready(function(){
	$.ajax({
		url: 'GraphDetailServlet',
		type : 'POST',
		dataType : 'json',
		data : JSON.stringify({
			"action"   			:	'details'
		}),
		processData : false,
		success : function(data, textStatus,
				jQxhr) {
			console.log(data.infra);
				var infra = data.infra;
				var acad = data.acad;
				var sanit = data.sanitation;
				var mid = data.midday;
				var chart1 = new CanvasJS.Chart("ChartContainer", {
					title:{
						text: "FACILITIES:"              
					},
					axisY:{
					title :"feedback"
					},
					data: [              
					{
						// Change type to "doughnut", "line", "splineArea", etc.
						type: "column",
						dataPoints: [
							{ label: "infrastructure", y: infra  },
							{ label: "sanitation", y: sanit  },
							{ label: "Acadmics", y: acad  },
							{ label: "mid-day meal",  y: mid  },
							{ label: "faculty",  y: 28  }
						]
					}
					]
				});
					chart1.render();
					var dps = new Array();
					var i = 0;
					$.each(data.events,function(){
						dps[i] = {label: this['title'] , y: this['feedback']};
						i++;
					});
					var chart2 = new CanvasJS.Chart("chartContainer", {
						title:{
							text: "EVENTS:"              
						},
						axisY:{
						title :"feedback"
						},
						data: [              
						{
							// Change type to "doughnut", "line", "splineArea", etc.
							type: "column",
							dataPoints: dps
						}
						]
					});
					chart2.render();
		},
		error : function(jqXhr, textStatus,
				errorThrown) {
			console.log(errorThrown);
		}
	});
});

