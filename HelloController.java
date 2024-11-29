package com.example.forms;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    private TextField fullNameField, idField, genderField, provinceField, dobField;

    private final File dataFile = new File("records.txt");
    private List<String> records = new ArrayList<>();

    @FXML
    public void initialize() {
        loadRecords();
    }

    @FXML
    private void clearForm() {
        fullNameField.clear();
        idField.clear();
        genderField.clear();
        provinceField.clear();
        dobField.clear();
    }

    @FXML
    private void saveRecord() {
        String fullName = fullNameField.getText();
        String id = idField.getText();
        String gender = genderField.getText();
        String province = provinceField.getText();
        String dob = dobField.getText();

        if (id.isEmpty() || fullName.isEmpty() || gender.isEmpty() || province.isEmpty() || dob.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            return;
        }

        // Check if the ID already exists
        for (String record : records) {
            if (record.startsWith(id + "|")) {
                showAlert(Alert.AlertType.ERROR, "Error", "ID already exists.");
                return;
            }
        }

        String record = id + "|" + fullName + "|" + gender + "|" + province + "|" + dob;
        records.add(record);
        saveRecordsToFile();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Record saved successfully.");
        clearForm();
    }

    @FXML
    private void deleteRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an ID to delete.");
            return;
        }

        boolean recordFound = false;
        for (int i = 0; i < records.size(); i++) {
            String record = records.get(i);
            if (record.startsWith(id + "|")) {
                records.remove(i);
                saveRecordsToFile();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Record deleted successfully.");
                clearForm();
                recordFound = true;
                break;
            }
        }

        if (!recordFound) {
            showAlert(Alert.AlertType.ERROR, "Error", "Record with ID " + id + " not found.");
        }
    }

    @FXML
    private void restoreRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an ID to restore.");
            return;
        }

        for (String record : records) {
            if (record.startsWith(id + "|")) {
                populateForm(record);
                return;
            }
        }

        showAlert(Alert.AlertType.ERROR, "Error", "Record with ID " + id + " not found.");
    }

    @FXML
    private void findRecord() {
        String id = idField.getText();

        if (id.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "ID field cannot be empty.");
            return;
        }

        boolean recordFound = false;
        for (String record : records) {
            if (record.startsWith(id + "|")) {
                populateForm(record);
                recordFound = true;
                break;
            }
        }

        if (!recordFound) {
            showAlert(Alert.AlertType.ERROR, "Error", "Record not found with ID: " + id);
        }
    }

    @FXML
    private void closeApp() {
        System.exit(0);
    }

    private void loadRecords() {
        if (!dataFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRecordsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (String record : records) {
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
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void populateForm(String record) {
        String[] fields = record.split("\\|");
        if (fields.length == 5) {
            idField.setText(fields[0]);
            fullNameField.setText(fields[1]);
            genderField.setText(fields[2]);
            provinceField.setText(fields[3]);
            dobField.setText(fields[4]);
        }
    }
}
