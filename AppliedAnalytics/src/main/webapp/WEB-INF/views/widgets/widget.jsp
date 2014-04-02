<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="widget widgetView ${widget.getHTMLClass()}">
	<c:if test="${widget.getViewCount() > 1 }">
		<div class="w-prev"><span class="glyphicon glyphicon-chevron-left"></span></div>
		<div class="w-next"><span class="glyphicon glyphicon-chevron-right"></span></div>
	</c:if>
	<div class="widget_header">
		<div class="widget_title"><fmt:message key="${widget.getTitle()}" /></div>
	</div>
	<div class="widget-content">	
		<div class = "spinner-content" align = "center"><img src="<c:url value="/cache/images/spinner.gif" />" width = "30" height = "30" /></div>
			
		<c:import url="${view}" />
		
	</div>
</div> 