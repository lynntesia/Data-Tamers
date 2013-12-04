package io.analytics.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.ui.Model;

@Aspect
public class SiteAspect {
	
	// @HeaderFooter
	@After("@annotation(io.analytics.aspect.HeaderFooter)")
	public void HeaderFooterAdvice(JoinPoint joinPoint) throws Exception
	{
		Model model = this.getModel(joinPoint.getArgs());
		
		model.addAttribute("HEADER", "/WEB-INF/views/includes/header.jsp");
		model.addAttribute("FOOTER", "/WEB-INF/views/includes/footer.jsp");
	}
	

	// @SidePanel
	/*@After("@annotation(io.analytics.aspect.SidePanel)")
	public void SidePanelAdvice(JoinPoint joinPoint) throws Exception
	{
		Model model = this.getModel(joinPoint.getArgs());
		
		model.addAttribute("SIDEPANEL", "");
	}*/

	
	
	/**
	 * Returns the Model object from a list of parameters.
	 * @param args
	 * @return The Model.
	 * @throws Exception if there is no Model.
	 */
	private Model getModel(Object[] args) throws Exception {
		
		for(Object arg : args) {
			if (arg instanceof Model)
				return (Model)arg;
		}
		
		throw new Exception("No Model parameter");
		
	}

}