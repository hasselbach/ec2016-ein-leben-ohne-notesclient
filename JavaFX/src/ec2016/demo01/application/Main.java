package ec2016.demo01.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			// lade FXML definition
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("Main.fxml"));
		
			// Öffne Scene in Größe 800x600px
			Scene scene = new Scene(root,800,600);
			
			// wende CSS an
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			// hole die WebView Komponente
			WebView webview = (WebView) scene.lookup("#webViewer");
			
			// hole WebEngine
			WebEngine webEngine = webview.getEngine();
			webEngine.load("http://localhost:8080");
			
			// Zeige die Stage
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}