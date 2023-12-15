package csc2040.grosspay;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.LocalDateTimeStringConverter;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: This is the controller for the Gross Pay Application
 * All UI components are initialized here, with fxml associations and event handlers
 * Date: Nov 27, 2023
 */
public class GrossPayController implements Initializable {

    // declare objects on the pane

    // header objects
    @FXML
    private ComboBox<String> cbPayPeriod;

    // center objects - Timesheet tab
    @FXML
    private TextField tsFilter;
    @FXML
    private TableView<TimeEntry> tvTimesheet;
    @FXML
    private TableColumn<TimeEntry, String> tEmployeeIDCol;
    @FXML
    private TableColumn<TimeEntry, String> tEmployeeNameCol;
    @FXML
    private TableColumn<TimeEntry, String> tCheckinLocationCol;
    @FXML
    private TableColumn<TimeEntry, LocalDateTime> tStartTimeCol;
    @FXML
    private TableColumn<TimeEntry, LocalDateTime> tEndTimeCol;
    @FXML
    private TableColumn<TimeEntry, Double> tTimeWorkedCol;

    // center objects - Payroll tab
    @FXML
    private TextField prFilter;
    @FXML
    private TableView<Payment> tvPayroll;
    @FXML
    private TableColumn<Payment, String> pEmployeeIDCol;
    @FXML
    private TableColumn<Payment, String> pEmployeeNameCol;
    @FXML
    private TableColumn<Payment, Double> pHourlyRateCol;
    @FXML
    private TableColumn<Payment, Double> pHoursWorkedCol;
    @FXML
    private TableColumn<Payment, Double> pGrossPayCol;
    @FXML
    private HBox hbox;
    @FXML
    private Button addBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button deleteBtn;


    // return the list of main time sheets available for processing / review
    // triggered by initialize
    private static ArrayList<String> getTimeSheetList() {
        ArrayList<String> list = new ArrayList<>();

        // inspect directory of where the timesheets are
        File timesheets = new File("./src/main/resources/csc2040/grosspay/timesheets");
        File[] files = timesheets.listFiles();

        for (File file : files) {
            // add only the file name without extension
            list.add(file.getName().substring(0, file.getName().indexOf(".")));
        }
        return list;
    }

    ObservableList<String> timesheetNames = FXCollections.observableArrayList(getTimeSheetList());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // set properties on initialize
        hbox.setVisible(false);
        deleteBtn.setDisable(true);

        // populate the time period combobox, and create event handler
        Collections.sort(timesheetNames);
        cbPayPeriod.setItems(timesheetNames);

        TimesheetView timesheetView = new TimesheetView(tvTimesheet);

        PayrollView payrollView = new PayrollView(tvPayroll);

        cbPayPeriod.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {

                String timesheetName = cbPayPeriod.getValue();

                timesheetView.loadTimesheetContents(timesheetName);
                hbox.setVisible(true);

                payrollView.populateView(timesheetView.getTimeEntries());
            }
        });

        // enable the delete button when a timesheet record is clicked
        tvTimesheet.getSelectionModel().selectedItemProperty().addListener((a, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteBtn.setDisable(false);
            }
        });

        // add button to save what is in input fields to new time entry
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                Alert alert;

                // validate employeeID
                String employeeID = ((TextField) hbox.getChildren().get(0)).getText();
                if (employeeID.isEmpty()) {
                    alert = new Alert(Alert.AlertType.ERROR, "Employee ID cannot be empty.");
                    alert.show();
                    return;
                }

                // validate employeeName
                String employeeName = ((TextField) hbox.getChildren().get(1)).getText();
                if (employeeName.isEmpty()) {
                    alert = new Alert(Alert.AlertType.ERROR, "Employee Name cannot be empty.");
                    alert.show();
                    return;
                }

                String location = ((TextField) hbox.getChildren().get(2)).getText();
                if (location.isEmpty()) {
                    alert = new Alert(Alert.AlertType.ERROR, "Check In Location cannot be empty.");
                    alert.show();
                    return;
                }

                String targetDate = ((TextField) hbox.getChildren().get(3)).getText();
                String startTime = targetDate + "T" + ((TextField) hbox.getChildren().get(4)).getText() + ":00";

                try {
                    LocalDateTime.parse(startTime);
                } catch(Exception e) {
                    alert = new Alert(Alert.AlertType.ERROR, "Work Date must be formatted as yyyy-dd-mm\n" +
                            "Start time must be formatted as HH:MM e.g. 09:00 or 13:30");
                    alert.show();
                    return;
                }

                String endTime = targetDate + "T" + ((TextField) hbox.getChildren().get(5)).getText() + ":00";
                try {
                    LocalDateTime.parse(endTime);
                } catch(Exception e) {
                    alert = new Alert(Alert.AlertType.ERROR, "Work Date must be formatted as yyyy-dd-mm\n" +
                            "End time must be formatted with leading zero as HH:MM e.g. 15:00 for 3PM");
                    alert.show();
                    return;
                }

                double payRate;

                try {
                    payRate = Double.parseDouble(((TextField) hbox.getChildren().get(6)).getText());
                } catch (Exception e) {
                    alert = new Alert(Alert.AlertType.ERROR, "Pay Rate must be numeric");
                    alert.show();
                    return;
                }

                // all inputs passed validation, create the time entry
                TimeEntry timeEntry;
                try {
                    timeEntry = new TimeEntry(employeeID,employeeName,location,payRate,startTime,endTime);
                } catch (Exception e) {
                    alert = new Alert(Alert.AlertType.ERROR, "End Time cannot be earlier than Start Time");
                    alert.show();
                    return;
                }
                System.out.println(timeEntry.toCommaDelimited());

                // add entry to the timesheet
                timesheetView.add(timeEntry);
                payrollView.populateView(timesheetView.getTimeEntries());
            }
        });

        // cancel button to clear text fields and hide hbox
        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                hbox.getChildren().forEach( node -> {
                    TextField field = (TextField) node;
                    field.clear();
                });
            }
        });

        // delete button to remove record from list, remove row from tableview, and refresh payroll
        deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                TimeEntry timeEntry = tvTimesheet.getSelectionModel().getSelectedItem();

                timesheetView.delete(timeEntry);
                payrollView.populateView(timesheetView.getTimeEntries());

                deleteBtn.setDisable(true);
            }
        });

        // tie tableview columns to TimeEntry class attributes
        tEmployeeIDCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, String>("employeeID"));
        tEmployeeNameCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, String>("employeeName"));
        tCheckinLocationCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, String>("checkinLocation"));
        tStartTimeCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, LocalDateTime>("startTime"));
        tStartTimeCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        tEndTimeCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, LocalDateTime>("endTime"));
        tEndTimeCol.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter()));
        tTimeWorkedCol.setCellValueFactory(new PropertyValueFactory<TimeEntry, Double>("timeWorked"));

        // tie tableview columns to the Payment class attributes
        pEmployeeIDCol.setCellValueFactory(new PropertyValueFactory<Payment, String>("employeeID"));
        pEmployeeNameCol.setCellValueFactory(new PropertyValueFactory<Payment, String>("employeeName"));
        pHourlyRateCol.setCellValueFactory(new PropertyValueFactory<Payment, Double>("payRate"));
        pHoursWorkedCol.setCellValueFactory(new PropertyValueFactory<Payment, Double>("hoursWorked"));
        pGrossPayCol.setCellValueFactory(new PropertyValueFactory<Payment, Double>("grossPay"));

        // add listener for the time entry filter
        tsFilter.textProperty().addListener((obs, oldText, newText) -> {
            timesheetView.filter(newText);
        });
        // add listener for the payroll filter
        prFilter.textProperty().addListener((obs, oldText, newText) -> {
            payrollView.filter(newText);
        });
    }
}