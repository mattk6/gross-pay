package csc2040.grosspay;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: The PayrollView class stores and manages the lists of payroll items
 * with a rule that there is 1 record per employee
 * Date: Nov 27, 2023
 */
public class PayrollView extends VBox {

    private ArrayList<Payment> payments;
    private TableView<Payment> tableView;
    private ObservableList<Payment> paymentEntriesOl;

    // constructor to build the view with a payment tableview object
    public PayrollView(TableView<Payment> tableView) {

        this.tableView = tableView;

        this.payments = new ArrayList<Payment>();
    }

    // populate the table view based on all timesheet entries for the selected pay period
    // Triggered by Pay period combobox selector
    public void populateView(ArrayList<TimeEntry> entries) {

        this.payments.clear();
        // build an empty ArrayList to store payroll records
        //ArrayList<Payment> payroll = new ArrayList<>();

        // update payment records based on each time entry
        entries.forEach(timeEntry -> {

            // update gross pay for target employee
            updateGrossPay(timeEntry);
        });
    }

    // create a record in payments for each individual employee with a payment record
    // keep a subtotal for time worked in minutes
    // when a record already exists, increase the subtotal amount
    private void updateGrossPay(TimeEntry timeEntry) {

        // variable to determine if employee payment record already exists
        AtomicBoolean found = new AtomicBoolean(false);

        // Determine which employee payroll record the timesheet entry belongs to and apply if
        // there is a match
        this.payments.forEach( payment -> {

            // see if there is a matching employee id in the payroll arraylist
            if(payment.getEmployeeID().equals(timeEntry.getEmployeeID())) {
                found.set(true);

                // existing payroll entry is found, add more time worked to the subtotal
                payment.addTimeWorked(timeEntry.getTimeWorked());
            }
        });

        // if record has not been found, create a new payment
        if (!found.get()) {

            // create new payment object from the timeEntry object
            Payment payment = new Payment(timeEntry);

            // add the new payroll entry to the array list
            this.payments.add(payment);
        }
        this.paymentEntriesOl = FXCollections.observableArrayList(payments);

        this.tableView.setItems(this.paymentEntriesOl);
    }

    // method to support filtering
    @FXML
    public void filter(String keyword)
    {
        // switch back to master observable list if there is no filter keyword
        if (keyword.isEmpty()) {
            this.tableView.setItems(paymentEntriesOl);
        } else {
            // switch to filtered observable list and populate per user input
            ObservableList<Payment> filteredData = FXCollections.observableArrayList();
            for (Payment entry : payments ) {
                // apply matches on Employee Name
                if(entry.getEmployeeName().toLowerCase().contains(keyword.toLowerCase()))
                    filteredData.add(entry);
            }
            this.tableView.setItems(filteredData);
        }
    }
}
