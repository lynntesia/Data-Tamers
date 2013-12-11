<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<header>
	<div class="header">
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
</header>
