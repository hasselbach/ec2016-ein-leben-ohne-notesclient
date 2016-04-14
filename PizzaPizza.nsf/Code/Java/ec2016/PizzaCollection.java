package ec2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.View;
import lotus.domino.ViewEntry;
import lotus.domino.ViewNavigator;
import ch.hasselba.xpages.JSFUtils;

import com.google.gson.Gson;

public class PizzaCollection {
	
	public static void doGet(HttpServletRequest req, HttpServletResponse res,
			FacesContext facesContext, ServletOutputStream out) throws IOException {
		
		try {
			ArrayList<HashMap<String,Object>> dataAr = new ArrayList<HashMap<String,Object>>();
			
			// hole Datenbank
			Database db = JSFUtils.getCurrentDatabase();
			
			// Suche nach Einträgen
			View vw = db.getView("pizzas");
			ViewNavigator nav = vw.createViewNav();
			ViewEntry viewEntry = nav.getFirstDocument();
			while( viewEntry != null ) {
				
				Vector<?> colVals = viewEntry.getColumnValues();
				
				// Daten in neue HashMap legen
				HashMap<String,Object> curOb = new HashMap<String,Object>();

				curOb.put("id", colVals.get(0));
				curOb.put("name", colVals.get(1));
				curOb.put("price", colVals.get(2));
				
				// Daten merken
				dataAr.add(curOb);
				
				// nächsten Eintrag holen
				ViewEntry tmpEnt = nav.getNext(viewEntry);
				viewEntry.recycle();
				viewEntry = tmpEnt;
			}
			
			// Mittels GSON umwandeln
			Gson g = new Gson();
			out.print(g.toJson(dataAr));
			
			// HTTP Status & Header setzen
			res.setStatus(200); // OK
			res.addHeader("Allow", "GET");
			
		} catch (Exception e) {
			res.setStatus(500); // Error
			res.addHeader("Allow", "GET");
		}
	}
	
}
