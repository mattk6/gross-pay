package csc2040.grosspay;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Random;

/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: This class is used for time entry creation
 * The class is used by TimesheetView View (TableView) and the GrossPayController
 * Date: Nov 27, 2023
 */
public class TimeEntry {

    // define attributes
    String employeeID;
    String employeeName;
    String checkinLocation;
    Double payRate;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Double timeWorked;

    // define getters
    public String getEmployeeID() {
        return employeeID;
    }
    public String getEmployeeName() {
        return employeeName;
    }

    public void setCheckinLocation(String checkinLocation) {
        this.checkinLocation = checkinLocation;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getCheckinLocation() {
        return checkinLocation;
    }
    public Double getPayRate() {
        return payRate;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    } //.toString()
    public LocalDateTime getEndTime() {
        return endTime;
    } //.toString()
    public Double getTimeWorked() {
        return timeWorked;
    }

    // constructor for evaluating any time entry through the gross pay program
    public TimeEntry(String employeeID, String employeeName, String checkinLocation, Double payRate, String startTime, String endTime) throws Exception {

        try {
            this.employeeID = employeeID;
            this.employeeName = employeeName;
            this.checkinLocation = checkinLocation;
            this.payRate = payRate;

            this.startTime = LocalDateTime.parse(startTime);
            this.endTime = LocalDateTime.parse(endTime);

            this.timeWorked = this.calculateTimeWorked();

            if (this.timeWorked <0 ) {
                throw new Exception("End Time cannot be before Start Time");
            }

        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("", new CharSequence() {
                @Override
                public int length() {
                    return 0;
                }

                @Override
                public char charAt(int index) {
                    return 0;
                }

                @Override
                public CharSequence subSequence(int start, int end) {
                    return null;
                }
            }, 1);
        }
    }

    // constructor for creating time entries through the timesheet generator
    public TimeEntry(String employeeID, String employeeName, String checkinLocation, Double payRate, LocalDateTime entryDate, boolean morning) {

        try {
            this.employeeID = employeeID;
            this.employeeName = employeeName;
            this.checkinLocation = checkinLocation;
            this.payRate = payRate;

            // declare variables for generating random times
            LocalDateTime startingRange, endingRange, endOfRange;

            // condition to create time in / time out stamps for morning or afternoon
            if (morning) {

                // Generate boundaries for start time (am)
                startingRange = entryDate.plusHours(6); // e.g. 6am
                endingRange = entryDate.plusHours(10); // e.g. 10am

                // Generate boundary for end time (am)
                endOfRange = entryDate.plusHours(12); // e.g. 12pm

            } else {

                // Generate boundaries for start time (pm)
                startingRange = entryDate.plusHours(12); // e.g. 12pm
                endingRange = entryDate.plusHours(15); // e.g. 3pm

                // Generate boundary for end time (pm)
                endOfRange = entryDate.plusHours(18); // e.g. 6pm
            }

            // set start time
            this.startTime = getRandomTime(startingRange, endingRange);

            // set end time (could be as soon as start time)
            this.endTime = getRandomTime(this.startTime, endOfRange);

            // set time worked as a total
            this.timeWorked = this.calculateTimeWorked();

        } catch (Exception e) {
            System.out.println("There must be a code problem to fix");;
        }
    }

    // generates a random timestamp within a given time range
    public static LocalDateTime getRandomTime(LocalDateTime startTime, LocalDateTime endTime) {

        Random random = new Random();

        // Get the duration between start time and end time
        Duration duration =  Duration.between(startTime, endTime);

        int seconds = (int) (Math.random() * (duration.getSeconds()));

        // return the  LocalDateTime object
        return startTime.plusSeconds(seconds);
    }

    private Double calculateTimeWorked() {

        Duration duration = Duration.between(this.startTime, this.endTime);

        return (double) (duration.getSeconds() / 60) * 100 / 100D;
    }

    // this is used to save entries back to csv
    public String toCommaDelimited(){
        return (this.employeeID + "," +
                this.employeeName + "," +
                this.checkinLocation + "," +
                this.payRate + "," +
                this.startTime + "," +
                this.endTime + "," +
                this.timeWorked + "\n");
    }

    // to be used for debugging only
    public String toString(){
        return ("EmployeeID: " + this.employeeID + ", CheckIn Location: " + this.checkinLocation + ", Start Time: " + this.startTime + ", End Time: " + this.endTime + ", Time Worked: " + this.timeWorked);
    }
}
