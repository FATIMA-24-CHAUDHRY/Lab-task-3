module com.example.forms {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.forms to javafx.fxml;
    exports com.example.forms;
}