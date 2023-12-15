module csc2040.grosspay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens csc2040.grosspay to javafx.fxml;
    exports csc2040.grosspay;
}