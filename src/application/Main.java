package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	@FXML
	GridPane gridPane;

	@FXML
	ScrollPane showPane;

	@Override
	public void start(Stage primaryStage) {
		try {
			gridPane = (GridPane) FXMLLoader.load(getClass().getResource("imageViewer.fxml"));
			Scene scene = new Scene(gridPane);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);

			primaryStage.titleProperty()
					.bind(scene.widthProperty().asString().concat(" : ").concat(scene.heightProperty().asString()));

			primaryStage.show();
			ScrollPane showPane = (ScrollPane) scene.lookup("#showPane");

			showPane.setPrefHeight(scene.getHeight() - showPane.getLayoutX() - showPane.getLayoutY());
			showPane.setPrefWidth(scene.getWidth() - 2 * showPane.getLayoutX());

			showPane.widthProperty().add(scene.getHeight());
			showPane.heightProperty().add(scene.getHeight());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
