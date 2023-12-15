package csc2040.grosspay;
/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: This class is used for payment creation to store pay rate determine gross pay
 * The class is used by Payroll View (TableView) and the GrossPayController
 * Date: Nov 27, 2023
 */

public class Payment {
    String employeeID;
    String employeeName;
    String checkinLocation;
    Double payRate;
    Double timeWorked;
    Double hoursWorked;
    Double grossPay;

    // Use TimeEntry object for constructor input, as
    // a number of attributes are the same.
    public Payment(TimeEntry timeEntry) {
        this.employeeID = timeEntry.employeeID;
        this.employeeName = timeEntry.employeeName;
        this.checkinLocation = timeEntry.checkinLocation;
        this.payRate = timeEntry.payRate;
        this.timeWorked = timeEntry.timeWorked;
        calculatePay();
    }

    // define getters
    public String getEmployeeID() {
        return employeeID;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public String getCheckinLocation() {
        return checkinLocation;
    }
    public Double getPayRate() {
        return payRate;
    }

    // these appear unused, but the controller needs them
    public Double getHoursWorked() {
        return hoursWorked;
    }
    public Double getGrossPay() {
        return grossPay;
    }


    // this recalculates hoursWorked and grossPay
    private void calculatePay(){
        this.hoursWorked = Math.round((this.timeWorked / 60 )*100)/100D;
        this.grossPay = Math.round((this.payRate * this.hoursWorked)*100)/100D;
    }


    // this adds new time worked to timeWorked as a subtotal
    public void addTimeWorked(Double newTimeWorked){
        this.timeWorked = this.timeWorked + newTimeWorked;
        calculatePay();
    }

    // tbd output for console
    public String toString(){
        return ("EmployeeID: " + this.employeeID + ", Pay Rate: " + this.payRate + ", Time Worked: " + this.timeWorked + ", Gross Pay " + this.grossPay);
    }

    // tbd if used for writing payroll file(s)
    public String toCommaDelimited(){
        return (this.employeeID + ", " + this.payRate + ", " + this.timeWorked + ", " + this.grossPay + "\n");
    }
}