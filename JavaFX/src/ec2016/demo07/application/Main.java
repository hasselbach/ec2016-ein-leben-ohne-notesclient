package ec2016.demo07.application;

import java.io.IOException;
import java.util.List;

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
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class Main extends Application {
	
	WebEngine webEngine;
	JavaApp javaApp = new JavaApp();
	
	
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
		 * f�gt Pizza zu AngularJS hinzu
		 */		
		public void addPizza(){
			webEngine.executeScript("addPizzaToScope( 4, 'Pizza Hawaii', 7 );");
		}
		
		/**
		 * callBack methode f�r hinzuf�gen von Pizza zu AngularJS
		 * 
		 * @param id
		 * @param name
		 * @param price
		 */
		public void addPizza(int id, String name, int price){
			webEngine.executeScript("addPizzaToScope( " + id + ", '" + name + "', " + price + " );");
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// lade FXML definition
		BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("/ec2016/resources/Main.fxml"));
				
		// ֶffne Scene in Gr�sse 800x600px
		Scene scene = new Scene(root,800,600);
					
		// wende CSS an
		scene.getStylesheets().add(getClass().getResource("/ec2016/resources/application.css").toExternalForm());
					
		// hole die WebView Komponente
		WebView webView = (WebView) scene.lookup("#webViewer");
					
		// hole WebEngine
		webEngine = webView.getEngine();
					
		// lade lokales Angular JS
		webEngine.load(Main.class.getResource("/ec2016/demo08/application/index.html").toExternalForm());
		
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
		                    
                            // REST Client
                            Retrofit retrofit = new Retrofit.Builder()
                    	    		.baseUrl("http://localhost:8080/pizzapizza.nsf/xsp/")
                    	    		.addConverterFactory(GsonConverterFactory.create())
                    	    		.build();
                    	    
                            // initialisieren des Endpoints
                    	    PizzaService service = retrofit.create(PizzaService.class);
                    	    Call<List<Pizza>> call = service.listPizzas();
                    	    
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
                    	    	javaApp.addPizza( pizza.id, pizza.name, pizza.price );
                    	    }
		            	}
					}
				}
		);
		        
        
        // �ndern der alert Ausgabe
	    webEngine.setOnAlert((WebEvent<String> wEvent) -> {
		      System.out.println("Alert Event  -  Message:  " + wEvent.getData());
		});
	    
	    
	    // Neues Context-Men� f�r rechte Maustaste
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

