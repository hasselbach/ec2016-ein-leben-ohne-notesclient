package ec2016.demo03.application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
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
		webEngine.load(Main.class.getResource("/ec2016/demo03/application/index.html").toExternalForm());
		
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
		                    
		            	}
					}
				}
		);
        
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
