package ec2016.demo10.application;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import retrofit.Call;
import retrofit.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.sun.webkit.dom.HTMLDivElementImpl;

@SuppressWarnings("restriction")
public class Main extends Application {
	
	WebEngine webEngine;
	JavaApp javaApp = new JavaApp();
	RESTClient client = new RESTClient();
    Connection connection = null;
    
	public class JavaApp {
		/**
		 * schliesst die Applikation
		 */
		public void exit() {
	        Platform.exit();
	    }
		
		/**
		 * calls JS back
		 */
		public void callBack(){
			webEngine.executeScript("foo();");
		}
		
		/**
		 * fügt Pizza zu AngularJS hinzu
		 */		
		public void addPizza2(){
			webEngine.executeScript("addPizzaToScope( 4, 'Pizza Hawaii', 7 );");
		}
		
		/**
		 * callBack methode für hinzufügen von Pizza zu AngularJS
		 * 
		 * @param id
		 * @param name
		 * @param price
		 */
		public void addPizza(int id, String name, int price){
			webEngine.executeScript("addPizzaToScope( " + id + ", '" + name + "', " + price + " );");
		}
		
		/**
		 * callBack methode für hinzufügen in SQLite DB & Update des Frontends
		 * 
		 * @param id
		 * @param name
		 * @param price
		 */
		public void savePizza(int id, String name, int price){
			addPizza( id , name , price );
			
			Statement statement = null;
            try {
				statement = connection.createStatement();
	        	statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        	statement.executeUpdate("insert into pizzas values(" + id + ", '" + name +  "', " + price + ", 1)");
            } catch (SQLException e) {
            	e.printStackTrace();
            }
		}
		/**
		 * callBack methode für hinzufügen via REST
		 * 
		 * @param id
		 * @param name
		 * @param price
		 */
		public void replicate(){
			
			Statement statement = null;
            try {
            	statement = connection.createStatement();
            	statement.setQueryTimeout(30);  // set timeout to 30 sec.
            	ResultSet rs = statement.executeQuery("select * from pizzas where isnew = 1");
            	while(rs.next()){
            		Pizza pizza = new Pizza();
        			pizza.id = rs.getInt("id");
        			pizza.name = rs.getString("name");
        			pizza.price = rs.getInt("price");
        			
        			try {
        				client.service.createPizza(pizza).execute();
        			} catch (IOException e) {
        				e.printStackTrace();
        			}

            	}
            	
            	statement.executeUpdate("update pizzas set isnew = 0 where isnew = 1");
            } catch (SQLException e) {
            	e.printStackTrace();
            }
           
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// lade FXML definition
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/ec2016/resources/Main.fxml"));
				
		// Öffne Scene in Grösse 800x800px
		Scene scene = new Scene(root,800,800);
					
		// wende CSS an
		scene.getStylesheets().add(getClass().getResource("/ec2016/resources/application.css").toExternalForm());
					
		// hole die WebView Komponente
		WebView webView = (WebView) scene.lookup("#webViewer");
					
		// hole WebEngine
		webEngine = webView.getEngine();
					
		// lade lokales Angular JS
		webEngine.load(Main.class.getResource("/ec2016/demo10/application/index.html").toExternalForm());
		
    	// Zeige die Stage
		primaryStage.setScene(scene);
		primaryStage.show();
		
		// Integration Java App
		// ChangeListener wartet, bis WebEngine initialisiert wurde
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<State>() {
					@Override
		            public void changed(ObservableValue<? extends State> ov,
		            	State oldState, State newState) {
						
						// warte auf Status, dass alles ok ist
		            	if (newState == State.SUCCEEDED) {
		                        	
		                	// stelle Java App Instanz als app Objekt bereits
		                    JSObject win = (JSObject) webEngine.executeScript("window");
		                    win.setMember( "app", javaApp );
		                    
		                    
		                    // lade Daten von SQLite Datenbank
		                    Statement statement = null;
		                    try {
		                    	statement = connection.createStatement();
		                    	statement.setQueryTimeout(30);  // set timeout to 30 sec.
		                    	ResultSet rs = statement.executeQuery("select * from pizzas");
		                    	while(rs.next()){
		                    		javaApp.addPizza(rs.getInt("id"), rs.getString("name"), rs.getInt("price"));
		                    	}
		                    } catch (SQLException e) {
		                    	e.printStackTrace();
		                    }
	 
		            	}
					}
				}
		);
		
		// laden des SQLite Treibers
		Class.forName("org.sqlite.JDBC");
		
		// initialisieren von lokaler db
		connection = DriverManager.getConnection("jdbc:sqlite:pizzapizza.db");
	    Statement statement = connection.createStatement();
	    statement.setQueryTimeout(30);  // set timeout to 30 sec.

	    // löschen bestehender Daten
	    statement.executeUpdate("drop table if exists pizzas");
	    
	    // initialisieren der Daten
	    statement.executeUpdate("create table pizzas (id integer, name string, price integer, isnew integer)");
	   
	    // lade Daten von REST service
  	    Call<List<Pizza>> call = client.service.listPizzas();
  	    
  	    // Daten abrufen
  	    Response<List<Pizza>> response = null;
			try {
				response = call.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
  	    
		// Ergebnis verarbeiten
  	    List<Pizza> pizzas = response.body();
  	    
  	    for( Pizza pizza : pizzas ){
  	    	// In lokale DB eintragen
  	    	statement.executeUpdate("insert into pizzas values(" + pizza.id + ", '" + pizza.name +  "', " + pizza.price + ", 0)");
  	    }
	
        
        // Ändern der alert Ausgabe
	    webEngine.setOnAlert((WebEvent<String> wEvent) -> {
		      System.out.println("Alert Event  -  Message:  " + wEvent.getData());
		});
	    
	    
	    // Neues Context menü für rechte Maustaste
	    webView.setContextMenuEnabled(false);
        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem nabMenu = new MenuItem("Go To NAB");
        nabMenu.setOnAction(e -> webEngine.load("http://localhost:8080/names.nsf"));
        contextMenu.getItems().addAll(nabMenu);
        
   
        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
     
        // onDragPver Event abfangen
        webView.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != root && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();
        });

       
        // onDragDropped verarbeiten
        webView.setOnDragDropped((DragEvent event) -> {

        	// Dragboard von Event auslesen
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {

                for( File file : db.getFiles() ){
					try {
						
						// lade das PDF
						PDDocument document = PDDocument.load( file );
						
						// hole PDF Informationen
						PDDocumentInformation info = document.getDocumentInformation();
						
						String pdfInfo = "Page Count=" + document.getNumberOfPages() + "<br>";	
						pdfInfo += "Title=" + info.getTitle() + "<br>";
						pdfInfo += "Author=" + info.getAuthor() + "<br>";
						pdfInfo += "Subject=" + info.getSubject() + "<br>";
						pdfInfo += "Keywords=" + info.getKeywords() + "<br>";
						pdfInfo += "Creator=" + info.getCreator() + "<br>";
						pdfInfo += "Producer=" + info.getProducer() + "<br>";
						pdfInfo += "Creation Date=" + info.getCreationDate() + "<br>";
						pdfInfo += "Modification Date=" + info.getModificationDate() + "<br>";
						pdfInfo += "Trapped=" + info.getTrapped() + "<br>";
						
						// PDF schlieÃŸen
						document.close();
						
						// ins Dokument übertragen
			            Document doc = webEngine.getDocument();
			            
			            Element elem = doc.getElementById("dndInfo");
			            if( elem instanceof HTMLDivElementImpl ){
			            	HTMLDivElementImpl div = (HTMLDivElementImpl) elem;
			            	div.setInnerHTML( pdfInfo );
			            }
        				
					} catch (Exception e) {
						e.printStackTrace();
					}
                	
                }
                success = true;
            }
            
            // markiere Event als verarbeitet
            event.setDropCompleted(success);
            event.consume();

        });
	}
	
	/**
	 * SQL connection schliessen
	 */
	@Override
	public void stop(){
	      try
	      {
	        if(connection != null)
	          connection.close();
	      }
	      catch(SQLException e)
	      {
	        e.printStackTrace();
	      }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

