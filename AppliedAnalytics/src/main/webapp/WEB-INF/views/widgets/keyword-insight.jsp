<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<head>
<style>
table, th, td
{
background-color:#DCDCDC;
border:1px solid black;
}
h3{color:#DCDCDC;}
th, td
{
padding:3px;
}
</style>
<script>
var dataPoints = ('${ kiModel.getDataPoints() }');

// if no data display error message
if (dataPoints == null) {
	("#keywordInsightSettings").append('<h2><fmt:message key="keywordinsight.gaOverQuotaError"/></h2>');
}
else { // else parse and display data
	var data =JSON.parse('${ kiModel.getDataPoints() }');
	console.log(data);
	var table = $('<table><tbody>');
	var tr = $('<tr>');
	$('<th>Keywords</th>').appendTo(tr);
	$('<th>Visits (%)</th>').appendTo(tr);
	$('<th>Bounce Rate (%)</th>').appendTo(tr);
	tr.appendTo(table);
	for(var r = 0; r < data.removeKeywords.length; r++)
	{
	    var tr = $('<tr>');
	    var key = '<td>'+data.removeKeywords[r]+'</td>';
	    var visits = '<td>'+data.removeVisitsPercent[r]+'</td>';
	    var bounceRate = '<td>'+data.removeBounceRate[r]+'</td>';
	    
	    $(key).appendTo(tr);
	    $(visits).appendTo(tr);
	    $(bounceRate).appendTo(tr);
	    tr.appendTo(table);
	}
	
	table.appendTo("#keywordInsightSettings");
}
</script>
</head>
<body>
<div class="widget_wrapper widgetView">
	<form id="keywordInsightSettings">
		<div class="widget_header">			
			<div class="widget_title"><fmt:message key="keywordinsight.title" /></div>			
		    <h3><fmt:message key="keywordinsight.removeSuggestion"/></h3>
		</div>
	</form>
	<br>
	<br>
	<h3><fmt:message key="keywordinsight.addSuggestion"/></h3>	
	<br>
	<table>
	<tr>
			<th> Organic Keyword </th>
			<th> Visits (%)</th>
			<th> Bounce Rate (%)</th>
	</tr>
	<tr>
			<td> </td>
			<td> </td>
			<td> </td>
	</tr>	
	</table>
	<br>
	<br>
	<h3><fmt:message key="keywordinsight.changeSuggestion"/></h3>
	<br>
	<table>
	<tr>
			<th> Keyword </th>
			<th> Type</th>
			<th> Visits (%)</th>
			<th> Bounce Rate (%)</th>
	</tr>
	<tr>
			<td> </td>
			<td> </td>
			<td> </td>
			<td> </td>
	</tr>
	</table>
	<br>
	<br>
</div>
</body>