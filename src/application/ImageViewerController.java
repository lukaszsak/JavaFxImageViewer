package application;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageViewerController {
	//picture resources default catalog
	private final static String FILE = "c:/projects/starterkit/pictures";
	
	private int index = 0;
	private double zoom = 1.0;
	private File defaultDirectory = new File(FILE);
	private List<File> selectedFiles;

	@FXML
	private GridPane gridPane;

	@FXML
	private ScrollPane imagesScrollPane;

	@FXML
	private GridPane imageFilesViewerGridPane;

	@FXML
	private Button selectFileButton;

	@FXML
	private Button slideShowButton;

	@FXML
	private Button prevImageButton;

	@FXML
	private Button nextImageButton;

	@FXML
	private Button zoomInButton;

	@FXML
	private Button zoomOutButton;

	@FXML
	private ListView<Image> imageListViewer = new ListView<Image>();

	@FXML
	private ScrollPane showPane;

	@FXML
	private ImageView imageViewer;

	@FXML
	private Image image;

	@FXML
	private String imagePath;

	@FXML
	private Thread th;

	private FileChooser fileChooser = new FileChooser();

	@FXML
	private void initialize() throws InterruptedException {

		double prefWidth = gridPane.getWidth() - 2 * showPane.getLayoutX();
		double prefHeight = gridPane.getHeight() - 2 * showPane.getLayoutY();

		showPane.setPrefSize(prefWidth, prefHeight);

		FileChooser.ExtensionFilter extFilterJpeg = new FileChooser.ExtensionFilter("jpg files (*.jpg, *.jpeg)",
				"*.jpg", "*.jpeg");
		FileChooser.ExtensionFilter extFilterGif = new FileChooser.ExtensionFilter("gif files (*.gif)", "*.gif");
		FileChooser.ExtensionFilter extFilterPng = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
		FileChooser.ExtensionFilter extFilterAllSupported = new FileChooser.ExtensionFilter("all supported files",
				"*.jpg", "*.jpeg", "*.gif", "*.png");

		fileChooser.getExtensionFilters().add(extFilterJpeg);
		fileChooser.getExtensionFilters().add(extFilterGif);
		fileChooser.getExtensionFilters().add(extFilterPng);
		fileChooser.getExtensionFilters().add(extFilterAllSupported);
	}

	private Service<Void> backgroundThread = new Service<Void>() {
		@Override
		protected Task<Void> createTask() {
			return new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					while (index < selectedFiles.size()) {
						imagePath = selectedFiles.get(index).toURI().toURL().toString();
						Image image = new Image(imagePath);
						imageViewer.setImage(image);
						Thread.sleep(1000);

						if (index == selectedFiles.size() - 1) {
							index = -1;
						}
						index++;
					}
					return null;
				}
			};
		}
	};

	@FXML
	void slideShowButtonAction() throws MalformedURLException, InterruptedException {
		showPane.setVisible(true);
		imageViewer.setVisible(true);
		if (selectedFiles != null) {
			if (slideShowButton.getText().equals("start slide show")) {
				selectFileButton.setDisable(true);
				zoomInButton.setDisable(false);
				zoomOutButton.setDisable(false);

				backgroundThread.restart();
				slideShowButton.setText("stop slide show");
			} else if (slideShowButton.getText().equals("stop slide show")) {
				backgroundThread.cancel();
				slideShowButton.setText("start slide show");
				selectFileButton.setDisable(false);

			}
		}
	}

	@FXML
	File selectFile() {
		imageViewer.setVisible(true);
		fileChooser.setInitialDirectory(defaultDirectory);
		fileChooser.setTitle("Choose File to display");
		return fileChooser.showOpenDialog((Stage) selectFileButton.getScene().getWindow());
	}

	@FXML
	List<File> selectMultipleFiles() {
		imageViewer.setVisible(true);
		fileChooser.setInitialDirectory(defaultDirectory);
		fileChooser.setTitle("Choose File to display");
		return fileChooser.showOpenMultipleDialog((Stage) selectFileButton.getScene().getWindow());
	}

	@FXML
	void selectFileButtonAction() throws MalformedURLException {

		if (backgroundThread != null) {
			slideShowButton.setText("start slide show");
			backgroundThread.cancel();
		}

		List<File> temporarySelectedFiles = selectedFiles;
		selectedFiles = selectMultipleFiles();

		if (selectedFiles != null) {
			index = 0;
			slideShowButton.setDisable(false);
			GridPane imageFilesViewerGridPane = new GridPane();
			imagesScrollPane.setContent(imageFilesViewerGridPane);

			File imageFile;
			for (int i = 0; i < selectedFiles.size(); i++) {
				imageFile = selectedFiles.get(i);
				String imagePath = imageFile.toURI().toURL().toString();
				Image image = new Image(imagePath);
				ImageView viewer = new ImageView();
				viewer.setId("" + i);
				viewer.setOnMouseClicked(new EventHandler<Event>() {

					@Override
					public void handle(Event event) {
						ImageView iv = (ImageView) event.getSource();
						iv.getImage();

						index = Integer.parseInt(iv.getId());

						imageViewer.setImage(iv.getImage());
						zoomInButton.setDisable(false);
						zoomOutButton.setDisable(false);
						prevImageButton.setDisable(false);
						nextImageButton.setDisable(false);

						// resize the new picture to 100% size
						zoom = 1.0;
						imageViewer.setFitWidth(zoom * imageViewer.getImage().getWidth());
						imageViewer.setFitHeight(zoom * imageViewer.getImage().getHeight());
					}
				});
				viewer.setImage(image);
				viewer.setFitHeight(100);
				viewer.setFitWidth(100);
				ColumnConstraints column = new ColumnConstraints(100);
				imageFilesViewerGridPane.getColumnConstraints().add(i, column);
				imageFilesViewerGridPane.addColumn(i, viewer);
			}
		} else {
			selectedFiles = temporarySelectedFiles;
		}
	}

	@FXML
	void prevImageButtonAction() throws MalformedURLException {
		index--;
		if (index <= -1) {
			index = selectedFiles.size() - 1;
		}

		imagePath = selectedFiles.get(index).toURI().toURL().toString();
		Image image = new Image(imagePath);
		imageViewer.setImage(image);
		
		imageViewer.setFitWidth(zoom * imageViewer.getImage().getWidth());
		imageViewer.setFitHeight(zoom * imageViewer.getImage().getHeight());
		
	}

	@FXML
	void nextImageButtonAction() throws MalformedURLException {
		index++;
		if (index >= selectedFiles.size()) {
			index = 0;
		}

		imagePath = selectedFiles.get(index).toURI().toURL().toString();
		Image image = new Image(imagePath);
		imageViewer.setImage(image);

		imageViewer.setFitWidth(zoom * imageViewer.getImage().getWidth());
		imageViewer.setFitHeight(zoom * imageViewer.getImage().getHeight());
	}

	@FXML
	void zoomInButtonAction() {
		zoom = 1.25 * zoom;
		imageViewer.setFitWidth(zoom * imageViewer.getImage().getWidth());
		imageViewer.setFitHeight(zoom * imageViewer.getImage().getHeight());
	}

	@FXML
	void zoomOutButtonAction() {
		zoom = 0.8 * zoom;
		imageViewer.setFitHeight(zoom * imageViewer.getImage().getHeight());
	}

}
