package io.analytics.site;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.view.InternalResourceView;

public class PageRenderer extends InternalResourceView {	
	
	@Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String dispatcherPath = prepareForRendering(request, response);

        // TODO Title logic	
        request.setAttribute("TITLE", "Applied Analytics");
        
        
        request.setAttribute("SIDEPANEL", model.get("SIDEPANEL"));
        
        // Set the header and footer if applicable
        request.setAttribute("HEADER", model.get("HEADER"));
        request.setAttribute("SETTINGS", model.get("SETTINGS"));
        request.setAttribute("FOOTER", model.get("FOOTER"));
        
        request.setAttribute("model", model);
        
        String uri = dispatcherPath.substring(0, dispatcherPath.lastIndexOf("/"));
        String page = uri.substring(uri.lastIndexOf("/") + 1);

    	// set the content
        request.setAttribute("BODY", dispatcherPath.substring(dispatcherPath.lastIndexOf("/") + 1));

        // route to page-template
        //if(state.equals("application"))
        RequestDispatcher rd = request.getRequestDispatcher(getMasterPage(page));
        
        rd.include(request, response);
        
	 }
	
	private String getMasterPage(String page)
	{
		return (page.equals("application")) ? "/WEB-INF/views/pages/application/application-master.jsp" 
											: "/WEB-INF/views/pages/home/home-master.jsp";
	}

}
