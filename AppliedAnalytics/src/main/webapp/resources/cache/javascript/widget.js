/**
 *  widget.js
 */


/**
 * calls each widgets model and view and appends
 * the widget to the associated page.
 * 
 * @param $content - the jQuery element the widget will be appended too.
 * @param widgetIdArray - array of widgetIds
 */
//TODO: Accommodate widget specifics (like filter).
function loadWidgets($content, widgets, callback) {
	
	for(var i = 0; i < widgets.length; i++) {

		var widgetTypeId = widgets[i].widgetTypeId;
		var widgetId = widgets[i].id;
		
		// load in each widget and fire the callback
		loadWidget($content, widgetTypeId, widgetId, i, callback);
		
	}
	
	// store number of widgets loaded
	// widget will not be dragged while user clicks on content
	$content.data("n", widgets.length);
}

/**
 * Loads in the specified widget
 * 
 * @param $content - the jQuery element the widget will be appended too.
 * @param widgetTypeId - Database Id of the widget type.
 * @param widgetId - Database Id of the widget instance.
 * @param i - an arbitrary number.
 */
function loadWidget($content, widgetTypeId, widgetId, i, callback)
{
	//Create an empty widget div.
	var $div = $("<div>").addClass("w_container")
						 .data({
							 	"widgetTypeId": widgetTypeId, 
							 	"widgetId": widgetId 
							   })
						 .appendTo($content);
	
	$div.data("pos", $div.index());
	
	var elementId;
	
	switch(widgetTypeId)
	{
		case 1: 
			elementId = "dataForecastWidget" + i;
			$div.attr("id", elementId);
			loadDataForecast(elementId);
			break;
			
		case 2:
			elementId = "websitePerformanceWidget" + i;
			$div.attr("id", elementId);
			loadWebsitePerformanceWidget("websitePerformanceWidget" + i);
			break;
			
		case 7:
			elementId = "keyContributingFactorsWidget" + i;
			$div.attr("id", elementId);
			loadKeyContributingFactorsWidget(elementId);
			break;
		
		case 4:
			elementId = "keywordInsightWidget" + i;
			$div.attr("id", elementId);
			loadKeywordInsight(elementId);
			break;
			
		case 5:
			elementId = "growingProblemsWidget" + i;
			$div.attr("id", elementId);
			loadGrowingProblemsWidget(elementId);
			break;
			
		case 6: 
			elementId = "boostPerformanceWidget" + i;
			$div.attr("id", elementId);
			loadBoostPerformanceWidget(elementId);
			break;
		
	}
	
	/** Global Widget Events **/
	
	// collapse
	$div.on("dblclick", ".widget_title", function() {
		$(this).parent().siblings(".widget-content").slideToggle("fast");
	});
	
	if (callback)
		callback(elementId);

}


/**
 * Updates each widget"s visualization based on new data 
 * without reloading the entire widget.
 */
function updateWidgets(){
	
	var widgets = $.map($(".w_container"), function(widget) {
						return { "elementId": widget.id, "widgetTypeId" : $(widget).data("widgetTypeId") };
					});
	
	for(var i = 0; i < widgets.length; i++) {
		
		switch(widgets[i].widgetTypeId)
		{
			case 1:
				updateDataForecast(widgets[i].elementId);
				break;
			case 4:
				updateKeywordInsight(widgets[i].elementId);
				break;
		}
	}
		
}




function addWidget(widget, li) 
{
	if (li.length) {
		var id = widget.attr("id");
		var widgetTypeId = widget.data("widgetTypeId");
		var widgetName = $("#" + id + " .widget_title").html();
		//var nWidgets = $(".dashboard-content").data("n");

		$.post(applicationRoot + "addWidget", {widgetTypeId: widgetTypeId, dashboardId: li.attr("id")},
				function(response) {
					var result = $.parseJSON(response);
					Modal.alert({
						"title" : "Add Widget",
						"content": widgetName + " has been added to dashboard \"" + li.children("a").html()  + "\"!"
					});
					
					// update the number of widgets <-- no longer needed
					//$(".dashbaord-content").data("n", nWidgets);
				});

	}

}


/**
 * Removes a widget from the dashboard and deletes it from the database.
 * @param id - the element id of the widget as on the current page.
 */
function removeWidget(id) {

	if (id) {
		var widget = $("#" + id);
		var widgetId = widget.data("widgetId");
		var nWidgets = $(".dashboard-content").data("n");
		var widgetName = $("#" + id + " .widget_title").html();

		$.post(applicationRoot + "removeWidget", {widgetId: widgetId}, 
				function(response) {
					if (response)
						widget.remove();
					// decrement the number of widgets on the page.
					$(".dashboard-content").data("n", --nWidgets);
						console.warn("removed widget: " + id);
						
						//TODO: Make the JSON response the title and content so we can use string properties.
						Modal.alert({
							"title" : "Remove Widget",
							"content": widgetName + " has been removed!"
						});
				});

	}

}





/**
 * Updates the database of the new position of the widgets.
 * Checks all widgets to see if its index() is different than
 * its original position.
 */
function updateWidgetPosition() {
	
	var result = [];
	var widgets = $(".w_container");
	
	// look for the widgets that have changed positions
	$.each(widgets, function() {
		var $widget = $(this);
		var idx = $widget.index();
		if (idx == parseInt($widget.data("pos")))
			return;
		
		var obj = {"widgetId": $widget.data("widgetId"), "pos": idx};
		result.push(obj);
		$widget.data("pos", idx);
	});
	
	if(result.length > 0) {
		$.post(applicationRoot + "updateWidgetPosition", {"widgets": JSON.stringify(result) },
				function() {
		});
	}
}
