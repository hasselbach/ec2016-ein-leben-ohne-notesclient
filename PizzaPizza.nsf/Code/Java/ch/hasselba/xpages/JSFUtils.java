package ch.hasselba.xpages;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import lotus.domino.Database;
import lotus.domino.Session;

import com.ibm.xsp.acl.RedirectSignal;
import com.ibm.xsp.context.ExternalContextEx;
import com.ibm.xsp.context.FacesContextExImpl;
import com.ibm.xsp.designer.context.ServletXSPContext;
import com.ibm.xsp.webapp.XspHttpServletResponse;

public class JSFUtils {

	private final static String VAR_NAME_DATABASE = "database";
	private final static String VAR_NAME_SESSIONASSIGNER = "sessionAsSigner";
	private final static String VAR_NAME_CONTEXT = "context";
	private final static String VAR_NAME_SESSION = "session";

	public static Object resolveVariable(final String toResolve) {
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();
		VariableResolver vRes = app.getVariableResolver();

		return vRes.resolveVariable(fc, toResolve);
	}

	public static Session getSession() {
		return (Session) resolveVariable(VAR_NAME_SESSION);
	}

	public static ServletXSPContext getContext() {
		return (ServletXSPContext) resolveVariable(VAR_NAME_CONTEXT);
	}

	public static Session getSessionAsSigner() {
		return (Session) resolveVariable(VAR_NAME_SESSIONASSIGNER);
	}

	public static Database getCurrentDatabase() {
		return (Database) resolveVariable(VAR_NAME_DATABASE);
	}

	public static void redirectToPage(final String target) {
		FacesContextExImpl fc = (FacesContextExImpl) FacesContextExImpl
				.getCurrentInstance();
		ExternalContextEx ec = (ExternalContextEx) fc.getExternalContext();
		ServletXSPContext ctx = JSFUtils.getContext();
		((XspHttpServletResponse) ec.getResponse()).setHeader("X-XspRefreshId",
				"@none");
		try {
			ctx.redirectToPage(target);
		} catch (RedirectSignal rs) {
		}

		fc.responseComplete();
	}
}
