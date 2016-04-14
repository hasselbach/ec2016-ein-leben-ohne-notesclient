package ec2016;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lotus.domino.Database;
import lotus.domino.Document;

import org.apache.commons.io.IOUtils;

import ch.hasselba.xpages.JSFUtils;

import com.google.gson.Gson;

public class PizzaRecord {
	
	public static void doPost(HttpServletRequest req, HttpServletResponse res,
			FacesContext facesContext, ServletOutputStream out) throws IOException {
		
		try {
			
			// Hole Datenbank
			Database db = JSFUtils.getCurrentDatabase();
			
			// Erstelle neues Dokument
			Document doc = db.createDocument();
			
			// lade POST Daten zu Pizza Objekt
			ServletInputStream is = req.getInputStream();
			Gson gson = new Gson();
			Pizza pizza = (Pizza) gson.fromJson(IOUtils.toString(is), Pizza.class);
			
			// speichere die Werte
			doc.replaceItemValue( "Form", "pizza" );
			doc.replaceItemValue( "id", pizza.id );
			doc.replaceItemValue( "name", pizza.name );
			doc.replaceItemValue( "price", pizza.price );
			doc.save();
			
			res.setStatus(200); // OK
			res.addHeader("Allow", "POST");
			
		} catch (Exception e) {
			e.printStackTrace();
			res.setStatus(500); // Error
			res.addHeader("Allow", "POST");
		}
	}
	
}
