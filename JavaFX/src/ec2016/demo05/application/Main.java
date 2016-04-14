package ec2016.demo05.application;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Main extends Application {
	WebEngine webEngine;

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
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// lade FXML definition
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/ec2016/resources/Main.fxml"));
				
		// Öffne Scene in Grösse 800x600px
		Scene scene = new Scene(root,800,600);
					
		// wende CSS an
		scene.getStylesheets().add(getClass().getResource("/ec2016/resources/application.css").toExternalForm());
					
		// hole die WebView Komponente
		WebView webView = (WebView) scene.lookup("#webViewer");
					
		// hole WebEngine
		webEngine = webView.getEngine();
					
		// lade lokales Angular JS
		webEngine.load(Main.class.getResource("/ec2016/demo05/application/index.html").toExternalForm());
		
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
		                    win.setMember( "app", new JavaApp() );
		                    
		                    // Script programmatisch hinzufügen
                            Document doc = webEngine.getDocument();
                    		Element scriptElement = doc.createElement("script");
                    		scriptElement.setAttribute( "src", Main.class.getResource("/ec2016/demo05/application/dom.js").toExternalForm()  );
                    		NodeList body = doc.getElementsByTagName("body");
                    		body.item(0).appendChild( scriptElement );
		            	}
					}
				}
		);
		        
        
        // Ändern der alert Ausgabe
	    webEngine.setOnAlert((WebEvent<String> wEvent) -> {
		      System.out.println("Alert Event  -  Message:  " + wEvent.getData());
		});
	    
	    
	    // Neues Context-Menü für rechte Maustaste
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
        
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

