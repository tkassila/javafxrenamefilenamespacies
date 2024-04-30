module com.metait.javafxrenamefilenamespacies {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.metait.javafxrenamefilenamespacies to javafx.fxml;
    exports com.metait.javafxrenamefilenamespacies;
}