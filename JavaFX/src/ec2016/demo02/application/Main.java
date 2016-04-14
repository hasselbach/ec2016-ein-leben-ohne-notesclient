package ec2016.demo02.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
	
	WebEngine webEngine;

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
		webEngine.load(Main.class.getResource("/ec2016/demo02/application/index.html").toExternalForm());
		
		// Zeige die Stage
		primaryStage.setScene(scene);
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
