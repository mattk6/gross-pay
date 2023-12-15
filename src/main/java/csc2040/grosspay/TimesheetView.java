package csc2040.grosspay;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: The TimesheetView class stores and manages the lists of timesheet items
 * Date: Nov 27, 2023
 */
public class TimesheetView extends VBox {

    private final String resourcePath = "./src/main/resources/csc2040/grosspay";

    // text fields for adding or editing data
    private String fileName;
    private TextField employeeID;
    private TextField employeeName;
    private TextField checkinLocation;
    private TextField payRate;
    private TextField startTime;
    private TextField endTime;
    private ArrayList<TimeEntry> timeEntries;
    private TableView<TimeEntry> tableView;
    private ObservableList<TimeEntry> timesheetEntriesOl;

    // create hbox for text field inputs
    public ArrayList<TimeEntry> getTimeEntries() {
        return timeEntries;
    }

    // constructor for setting class with the table view
    public TimesheetView(TableView<TimeEntry> tableView) {
        this.tableView = tableView;
        this.timeEntries = new ArrayList<TimeEntry>();
    }

    // Reads a timesheet file and populates the table view with all the records
    // triggered by Pay period combobox selection
    public void loadTimesheetContents(String fileName) {

        this.fileName = fileName + ".csv";

        // empty the array
        this.timeEntries.clear();

        // open the timesheet file for reading all rows
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader( resourcePath +  "/timesheets/" + this.fileName))) {

            // variable to hold line of text
            String record;

            // loop through all file rows
            while ((record = bufferedReader.readLine()) != null) {

                // skip the header row
                if (!record.equals("EmployeeId, CheckInLocation, TimeStampIn, TimeStampOut")) {

                    // split the timesheet record into array elements
                    String[] elements = record.split(",");

                    // create a new TimeEntry object
                    TimeEntry timeEntry;
                    try {
                        timeEntry = new TimeEntry(elements[0],elements[1], elements[2], Double.valueOf(elements[3]), elements[4], elements[5]);
                        // add the time entry to the list
                        this.timeEntries.add(timeEntry);

                    } catch (Exception e) {
                        System.out.println("Invalid Time record, skipping:\n " + record );
                    }

                }
            }
        } catch (IOException error) {
            // todo: error handling
        }

        // establish observable list for the tableview
        this.timesheetEntriesOl = FXCollections.observableArrayList(this.timeEntries);
        this.tableView.setItems(timesheetEntriesOl);
    }

    // triggered instructions when user hits Add Record button
    public void add(TimeEntry timeEntry) {

        // add to the array list
        this.timeEntries.add(timeEntry);

        // update the observable list
        timesheetEntriesOl.add(timeEntry);

        // rewrite the csv with the new record
        updateFile();

        // refresh the table view
        refresh();
    }

    public void delete(TimeEntry timeEntry) {

        this.timeEntries.remove(timeEntry);
        this.timesheetEntriesOl.remove(timeEntry);

        // rewrite the csv excluding the deleted record
        updateFile();

        // refresh the table view
        refresh();
    }

    // method to support filtering
    @FXML
    public void filter(String keyword)
    {
        if (keyword.isEmpty()) {
            // switch back to master observable list if there is no filter keyword
            refresh();
        } else {
            // switch to filtered observable list and populate per user input
            ObservableList<TimeEntry> filteredData = FXCollections.observableArrayList();
            for (TimeEntry entry : getTimeEntries() ) {
                // apply matches on Employee Name
                if(entry.getEmployeeName().toLowerCase().contains(keyword.toLowerCase()))
                    filteredData.add(entry);
                // apply matches on location
                if(entry.getCheckinLocation().toLowerCase().contains(keyword.toLowerCase()))
                    filteredData.add(entry);
                // apply matches on date
                if(String.valueOf(entry.getStartTime()).contains(keyword))
                    filteredData.add(entry);
            }
            this.tableView.setItems(filteredData);
        }
    }

    // reset the view to show data updates after an addition or deletion
    private void refresh() {

        // refresh the tableview with the ol
        this.tableView.setItems(timesheetEntriesOl);

        // repaint the rows - is it all needed?
        this.tableView.getSelectionModel().clearSelection();
        this.tableView.getItems().clear();
        this.tableView.getItems().addAll(getTimeEntries());
    }

    // rewrite the timesheet csv after a list change
    public void updateFile() {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(resourcePath + "/timesheets/" + this.fileName))) {

            for(int i=0; i<this.getTimeEntries().size();i++) {
                bw.write(timeEntries.get(i).toCommaDelimited());
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
