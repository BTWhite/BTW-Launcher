package org.bitcoinwhite.launcher;

import java.io.IOException;

import org.bitcoinwhite.launcher.core.tools.Node;
import org.bitcoinwhite.launcher.gui.particles.index.IndexController;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	
	private static Stage stage;
	private static Main instance;
	public static final String version = "0.0.1b";
	
	public static void main(String[] args) {
		
		
		if(args.length > 0) Console.main(args);
		else {
			System.out.println("Starting UI interface");
			launch(args);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		instance = this;
		this.stage = stage;
		Scene scene = showUpdater(stage);
        String css = this.getClass().getResource("gui/particles/main.css").toExternalForm(); 
        scene.getStylesheets().add(css);
        
        stage.setTitle("BitcoinWhite launcher");
        stage.setResizable(false);
        
        stage.show();
        
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	
	public Main hide() {
		this.stage.hide();
		return this;
	}
	
	public Main show() {
		this.stage.show();
		return this;
	}
	
	public Scene showUpdater() throws IOException { return showUpdater(this.stage); }
	public Scene showUpdater(Stage stage) throws IOException {
		
		Parent root = FXMLLoader.load(getClass().getResource("gui/particles/index/index.fxml"));
		Scene scene = new Scene(root, 500, 375);
		stage.setScene(scene);
		stage.setOnShowing(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				// TODO
				
			}
			
		});
		
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {                        
				Node node = IndexController.getNode();
				if(node != null) node.stop();
				System.exit(0);
	        }
	    });
		return scene;
	}
	
	
	/*public Scene showDashboard() throws IOException { return showDashboard(this.stage); }
	public Scene showDashboard(Stage stage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("particles/dashboard/index.fxml"));
		Scene scene = new Scene(root, 500, 375);
		stage.setScene(scene);
		return scene;
	}*/
	
	
}
