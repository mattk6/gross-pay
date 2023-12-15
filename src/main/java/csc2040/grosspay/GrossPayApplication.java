package csc2040.grosspay;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.*;
import java.text.ParseException;

/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: This is a program that lets HR or Payroll employees review timesheet entries
 * submitted by hourly employees, and make corrections upon request. A gross payment payroll
 * file can then be generated.
 * Date: Nov 27, 2023
 */

public class GrossPayApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        // use the fxml document as the model for the app
        Parent root = FXMLLoader.load(getClass().getResource("timesheets.fxml"));
        Scene scene = new Scene(root, 780, 450);

        stage.setTitle("Welcome to the Gross Pay App!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException, ParseException {
        // launch the app
        launch();
    }
}