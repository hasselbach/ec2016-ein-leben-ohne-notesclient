package ec2016;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.xsp.webapp.DesignerFacesServlet;


public class RestServlet extends DesignerFacesServlet implements Serializable {

	private static final long serialVersionUID = -1L;

	@Override
	public void service(final ServletRequest servletRequest,
			final ServletResponse servletResponse) throws ServletException,
			IOException {
		HttpServletResponse res = (HttpServletResponse) servletResponse;
		HttpServletRequest req = (HttpServletRequest) servletRequest;

		ServletOutputStream out = res.getOutputStream();
		FacesContext facesContext = this.getFacesContext(req, res);

		try {

			// Content type, cache & Access-Control Header setzen
			res.setContentType("application/json");
			res.setHeader("Cache-Control", "no-cache");
			res.setHeader("Access-Control-Allow-Origin", "*");
			res.setHeader("Access-Control-Allow-Headers",
					"Origin, X-Requested-With, Content-Type, Accept");
			
			// hole pathInfo
			String pathInfo = req.getPathInfo();

			// regex parsen pathInfo
			Pattern regPizzaAllPattern = Pattern.compile("/pizzas");
			Pattern regPizzaNewPattern = Pattern.compile("/pizza/new");
			Matcher matchPizzasCollection = regPizzaAllPattern.matcher(pathInfo);
			Matcher matchPizzaNew = regPizzaNewPattern.matcher(pathInfo);
			
			// Request Methode überprüfen
			String reqMethod = req.getMethod();
			if (matchPizzasCollection.find()) {
				if (reqMethod.equals("GET")) {
					PizzaCollection.doGet(req, res, facesContext, out);
					return;
				}
			}
			
			if( matchPizzaNew.find() ){
				if (reqMethod.equals("POST")) {
					PizzaRecord.doPost(req, res, facesContext, out);
					return;
				}
			}
			
			res.setStatus(400); 

		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace(new PrintStream(out));
		} finally {
			facesContext.responseComplete();
			facesContext.release();
			out.close();
		}
	}

}
