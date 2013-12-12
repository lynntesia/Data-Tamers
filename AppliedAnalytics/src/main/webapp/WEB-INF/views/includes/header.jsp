<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
var headerAttributes = { 
	state: '${HEADER.state}'
};

</script>

<header>
	<div id="header">
	
		<!-- Header for Application Pages -->
		<div id="header-application">
			<div class="filter">
				<input type="text" id="start-date" value="${ filter.getActiveStartDate() }" />
				<input type="text" id="end-date" value="${ filter.getActiveEndDate() }" /><br />
				Active Metric: ${ filter.getActiveMetric() }
			</div> 
			<div class="avatar">
				<c:choose>
					<c:when test="${not empty settings.getGoogleUserData().getPicture()}" >
						<img class="profile-image" title="" src="${settings.getGoogleUserData().getPicture()}?sz=50" /><br />
					</c:when>
					<c:otherwise>
						<img class="profile-image" src="<c:url value="/cache/images/default_user_50.png" />" /><br />
					</c:otherwise>
				</c:choose>
			</div>
			<div class="messages"></div>		
		</div>
		
		<!-- Header for Entry Page -->
		<div id="header-entry">
			<div class="entry-wrapper">
				<img src="<c:url value="/cache/images/logo-280.png" />" />
				<div class="right">
					<span class="nav-item"><a href="#">About</a></span>
					<span class="nav-item"><a href="<c:url value="/application" />">Login</a></span>
				</div>
			</div>
		</div>
		
		
	</div>	
</header>
