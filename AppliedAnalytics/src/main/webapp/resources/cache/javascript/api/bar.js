/**
 * bar.js
 */

(function ($) {
		
	/* global variables */
	var defaults = {
			'id'	: ''
	};
	
	/* function declaration */
	$.fn.bar = function (params) {
		return this.each(function() {			
			var svg = d3.select("#" + this.id).append("svg");
			var $this = $(this);
			var settings = $.extend({}, defaults, params);
			var sdata = settings.data;			
			
			nv.addGraph(function() {
				  var chart = nv.models.multiBarChart()
				      .staggerLabels(true)    //Too many bars and not enough room? Try staggering labels.
				      //.tooltips(false)        //Don't show tooltips
				      .transitionDuration(350)
				      .width($this.width() * 2)
				      .height($this.height())
				      .showXAxis(false)				      
				      ;
				  
				  chart.xAxis.tickFormat(function (d) { return sdata["Webpage Title/Link"][d];});

				  svg.datum(formatData(sdata))
				     .call(chart);

				  nv.utils.windowResize(chart.update);

				  return chart;
				});
			
				//Each bar represents a single discrete quantity.
				function formatData(sdata) {
					var p = sdata["Webpage Title/Link"];
					var paths = [];
					var keys= [];					
					var numKeys = sdata.keys.length;
					var numVals = sdata["Visits (%)"].length;					
									
					for(i=1; i < numKeys; i++){
						keys[i-1]=(sdata.keys[i]);
					}
										
					for(i=0; i < paths.length; i++){
						paths[i] = ('"'+p[i]+'"');
					}
					
				var data = [];						
					for(i=0; i < 3; i++){
						data.push({
							key: keys[i],
							values:[]
						});
						
						for(j=0; j < sdata["Webpage Title/Link"].length; j++){
							data[i].values.push({
								x:j,
								y:sdata[sdata.keys[i+1]][j]
							});
						}
						
					}	
				 return data;
					
					
				}
			
			
			// end chart 
		}); 
	};
}(jQuery));