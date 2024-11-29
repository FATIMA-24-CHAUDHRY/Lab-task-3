package com.example.forms;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;

public class HelloApplication extends Application {
    private TextField fullNameField, idField;
    private ToggleGroup genderGroup;
    private DatePicker dobPicker;
    private ComboBox<String> provinceDropdown;
    private HashMap<String, String> records = new HashMap<>();
    private File recordsFile = new File("records.txt");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox formLayout = new VBox(10);
        formLayout.setPadding(new Insets(10));

        fullNameField = new TextField();
        fullNameField.setPromptText("Enter Full Name");

        idField = new TextField();
        idField.setPromptText("Enter ID");

        genderGroup = new ToggleGroup();
        RadioButton maleRadio = new RadioButton("Male");
        maleRadio.setToggleGroup(genderGroup);
        RadioButton femaleRadio = new RadioButton("Female");
        femaleRadio.setToggleGroup(genderGroup);

        HBox genderBox = new HBox(10, maleRadio, femaleRadio);

        dobPicker = new DatePicker();

        provinceDropdown = new ComboBox<>();
        provinceDropdown.getItems().addAll("Punjab", "Sindh", "Balochistan", "Khyber Pakhtunkhwa");
        provinceDropdown.setPromptText("Select Province");

        formLayout.getChildren().addAll(
                new Label("Full Name"), fullNameField,
                new Label("ID"), idField,
                new Label("Gender"), genderBox,
                new Label("Date of Birth"), dobPicker,
                new Label("Home Province"), provinceDropdown
        );

        ScrollPane scrollPane = new ScrollPane(formLayout);
        scrollPane.setFitToWidth(true); // Ensure the form is fully visible within the scroll pane
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show the vertical scrollbar

        VBox buttonsLayout = new VBox(10);
        buttonsLayout.setPadding(new Insets(10));
        buttonsLayout.setAlignment(Pos.CENTER);

        Button newButton = new Button("New");
        Button deleteButton = new Button("Delete");
        Button restoreButton = new Button("Restore");
        Button findButton = new Button("Find");
        Button closeButton = new Button("Close");

        buttonsLayout.getChildren().addAll(
                newButton, deleteButton, restoreButton, findButton, closeButton
        );

        newButton.setOnAction(e -> saveRecord());
        deleteButton.setOnAction(e -> deleteRecord());
        restoreButton.setOnAction(e -> restoreRecord());
        findButton.setOnAction(e -> findRecord());
        closeButton.setOnAction(e -> primaryStage.close());

        HBox mainLayout = new HBox(20, scrollPane, buttonsLayout);
        Scene scene = new Scene(mainLayout, 600, 400);

        primaryStage.setTitle("User Records");
        primaryStage.setScene(scene);
        primaryStage.show();

        loadRecords();
    }

    private void saveRecord() {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String gender = genderGroup.getSelectedToggle() != null ? ((RadioButton) genderGroup.getSelectedToggle()).getText() : "";
        String province = provinceDropdown.getValue();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().toString() : "";

        if (id.isEmpty() || fullName.isEmpty() || gender.isEmpty() || province == null || dob.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            return;
        }

        String record = String.format("%s|%s|%s|%s|%s", id, fullName, gender, province, dob);
        records.put(id, record);
        saveRecordsToFile();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Record saved successfully.");
        clearForm();
    }

    private void deleteRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an ID to delete.");
            return;
        }

        boolean recordFound = records.containsKey(id);
        if (recordFound) {
            records.remove(id);
            saveRecordsToFile();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Record deleted successfully.");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Record with ID " + id + " not found.");
        }
    }

    private void restoreRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an ID to restore.");
            return;
        }

        String record = records.get(id);
        if (record != null) {
            populateForm(record);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Record with ID " + id + " not found.");
        }
    }

    private void findRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID field cannot be empty.");
            return;
        }

        String record = records.get(id);
        if (record != null) {
            populateForm(record);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Record not found with ID: " + id);
        }
    }

    private void loadRecords() {
        if (recordsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(recordsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split("\\|");
                    if (fields.length == 5) {
                        records.put(fields[0], line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveRecordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recordsFile))) {
            for (String record : records.values()) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        fullNameField.clear();
        idField.clear();
        genderGroup.selectToggle(null);
        dobPicker.setValue(null);
        provinceDropdown.setValue(null);
    }

    private void populateForm(String record) {
        String[] fields = record.split("\\|");
        if (fields.length == 5) {
            idField.setText(fields[0]);
            fullNameField.setText(fields[1]);
            genderGroup.selectToggle(fields[2].equals("Male") ? genderGroup.getToggles().get(0) : genderGroup.getToggles().get(1));
            provinceDropdown.setValue(fields[3]);
            dobPicker.setValue(LocalDate.parse(fields[4]));
        }
    }
}
