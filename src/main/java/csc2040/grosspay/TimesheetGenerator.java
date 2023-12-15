package csc2040.grosspay;

import javax.swing.*;
import java.io.*;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Matthew Kruse
 * @version CSC 2040 C40 Michael Seely, Instructor
 * Purpose: This is a program that generates random timesheet csv files to be used by the Gross Pay Application
 * There is a random value to mimic the likelyhood people are not clocking in twice a day, every day
 * Date: Nov 27, 2023
 */
public class TimesheetGenerator {

    public static final String resourcePath = "./src/main/resources/csc2040/grosspay";

    public static void main(String[] args) {

        JOptionPane.showMessageDialog(null,"Welcome to the Timesheet Generator\n" +
                "This program generates a number of timesheets to be used for the Payroll Application." );

        String numberOfTimesheets = JOptionPane.showInputDialog("How many weeks of timesheet data do you want to generate?");

        int sheetCount = Integer.valueOf(numberOfTimesheets);

        // start with most recent sunday as payroll date, and work backwards
        int currentDateIndex = DayOfWeek.from(LocalDate.now()).getValue();
        LocalDate localDate = LocalDate.now();

        // the cutoff is Sunday with timesheets reflecting periods Sunday through Saturday
        LocalDate initialDate = localDate.minusDays(currentDateIndex);

        // generate a timesheet for every 7 days given number of sheets requested
        for (int i=0; i<sheetCount*7;i=i+7) {
            createTimeSheet(initialDate.minusDays(i));
        }

        // Provide program exit message
        JOptionPane.showMessageDialog(null, "Congratulations. Your time sheets are available to use \nin the Payroll application. Goodbye.");
    }

    // method to create a timesheet with up to 14 entries per employee for a specified week
    public static void createTimeSheet(LocalDate endDate) {

        // establish array list to store all the time entries for a week
        ArrayList<TimeEntry> timeEntries = new ArrayList<>();

        // randomizer to account for employee day(s) off
        Random random = new Random();

        // Set date variables, start of week, end of week
        LocalDateTime startDate = endDate.atStartOfDay().minusDays(7);

        // read the HourlyEmployees.csv for list of employees
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader( resourcePath +"/HourlyEmployees.csv"))) {

            // open employee file, read it to array and close
            String rowData = "";

            // read header row so it is not processed
            bufferedReader.readLine();

            // collect employees until end of file reached,
            while ((rowData = bufferedReader.readLine()) != null) {

                // set targetDate as start date to begin
                LocalDateTime targetDate = startDate;

                // loop to create a time entry record for each day of the week
                while (!endDate.atStartOfDay().equals(targetDate)) {

                    // set 80% chance hourly employee works on target date ( 5 days a week max)
                    if(random.nextDouble() < 0.714) {

                        //populate timesheet array with new record
                        appendTimesheet(timeEntries, rowData, targetDate);
                    }

                    // move targetDate to next day of the week
                    targetDate = targetDate.plusDays(1);
                }
            }

        } catch (IOException error) {
            System.out.println(error);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // format the ending date for the filename
        Formatter format = new Formatter();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String timePeriodEnding = formatter.format(endDate);

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(resourcePath + "/timesheets/" + timePeriodEnding + ".csv"))) {

            // write each timeEntry to csv
            for (TimeEntry timeEntry : timeEntries) bw.write(timeEntry.toCommaDelimited());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // this method adds up to 2 timesheet entries to the main array for the specified date for morning and afternoon
    public static void appendTimesheet(ArrayList<TimeEntry> timeEntries, String rowData, LocalDateTime entryDate) throws ParseException {
        Random random = new Random();

        // Split the timesheet record into array elements
        String[] employeeInfo = rowData.split(",");

            // simulate 85% of employees clock in the morning
            if (random.nextDouble() < 0.85) {

                TimeEntry timeEntry = new TimeEntry(employeeInfo[0],
                                                    employeeInfo[2] + " " + employeeInfo[1],
                                                    employeeInfo[3],
                                                    Double.valueOf(employeeInfo[4]),
                                                    entryDate, true);

                // add the new payroll entry to the array list
                timeEntries.add(timeEntry);
            }

        //  simulate 90% of employees clock in the afternoon
        if (random.nextDouble() < 0.90) {

            TimeEntry timeEntry = new TimeEntry(employeeInfo[0],
                                  employeeInfo[2] + " " + employeeInfo[1],
                                               employeeInfo[3],
                                               Double.valueOf(employeeInfo[4]),
                                               entryDate, false);

            // add the new payroll entry to the array list
            timeEntries.add(timeEntry);
        }
    }
}
