import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jdom2.JDOMException;

import java.io.File;
import java.io.IOException;

public class GUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        setup(primaryStage);
    }

    public void build() {
        launch();
    }
    void setup(Stage stage){
        File file = null;
        file = getFile(stage, file, "xml");
        Parser parser = new Parser();
        try {
            parser.setup(file);
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
        


        File saveFile = null;
        saveFile = getFile(stage, saveFile, "json");

        parser.saveJSON(saveFile);

    }

    private File getFile(Stage stage, File file, String fileFormat) {
        while (file == null){
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open " + fileFormat + " file");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileFormat.toUpperCase() + " File", "*." + fileFormat));
            file = fileChooser.showOpenDialog(stage);
        }
        return file;
    }

}
