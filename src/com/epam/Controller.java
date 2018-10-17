package com.epam;

import com.epam.service.STMService;
import com.epam.service.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    private static final int SHEET_NUM = 2;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label appNameLabel;

    @FXML
    private TextField viewFileTextField;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button templateBuildButton;

    @FXML
    private TextArea infoTextArea;

    private STMService service;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewFileTextField.setEditable(false);
        infoTextArea.setEditable(false);
        service = new STMService();
        infoTextArea.setText(getInitialRules());
    }

   @FXML
    private void handleChooseFileAction(final ActionEvent event){
        addMessage("Choosing file...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls"));
        File file = fileChooser.showOpenDialog(new Stage());
        String filePath = file.getAbsolutePath();
        viewFileTextField.setText(filePath);
        addMessage("Selected file: " + file.getAbsolutePath());
    }

    private void addMessage(String message){
        infoTextArea.appendText("[" + LocalDateTime.now() + "]: " + message + "\n");
    }

    @FXML
    private void handleTemplateBuildAction(final ActionEvent event) {
        String file = viewFileTextField.getText();
        if(!StringUtils.isBlank(file)){
            addMessage("Building templates in directory where STM is stored...");
            try{
                service.buildTemplates(file, SHEET_NUM);
            }catch (Exception e){
                addMessage(e.getLocalizedMessage());
                throw new RuntimeException("An exception occured: " + e.getLocalizedMessage());
            }
            addMessage("Templates are built successfully");
        }
    }

    private String getInitialRules(){
        return "Before parsing every STM please prepare each Excel file corresponding to it: \n" +
                "1. Set 'Data Mapping' sheet (the main sheet with tables' data) to the 3-rd position (if it's order is " +
                "proper do nothing).\n" +
                "2. For RAW table perform changing the following column names: \n" +
                "\t 'Table name' (or else) -> RAW_TN \n" +
                "\t 'Column name' (or else) -> RAW_CN \n" +
                "\t 'Data type' (or else) -> RAW_DT \n" +
                "\t 'Description' (or else) -> RAW_DC \n" +
                "3. For TARGET table perform changing the following column names: \n" +
                "\t 'Structure name' (or else) -> TGT_TN \n" +
                "\t 'Column name' (or else) -> TGT_CN \n" +
                "\t 'Data type' (or else) -> TGT_DT \n" +
                "\t 'Description' (or else) -> TGT_DC \n" +
                "4. If some column is absent do nothing.\n\n";
    }

}
