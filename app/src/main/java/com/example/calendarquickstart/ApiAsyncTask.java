package com.example.calendarquickstart;

/**
 * Created by MJ on 9/17/15.
 */

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private com.example.calendarquickstart.MainActivity mActivity;

    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(com.example.calendarquickstart.MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    com.example.calendarquickstart.MainActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
        }
        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {

        /**
        String [] tableColumns = new String[2];
        tableColumns[0] = bandProvider.Band_Data.TIMESTAMP;
        tableColumns[1] = bandProvider.Band_Data._Value;


        Cursor band_data = mActivity.band_data;
        band_data.moveToFirst();

        java.sql.Timestamp timestamp = new java.sql.Timestamp((long)band_data.getDouble(0));

        int year = timestamp.getYear();
        int month = timestamp.getMonth();
        int day = timestamp.getDay();
        int hour = timestamp.getHours();

        DateTime startDate = getStartDate(timestamp);
        DateTime endDate = getEndDate(timestamp);

        float sum = band_data.getFloat(1);
        int count = 1;
        while(band_data.moveToNext()) {

            java.sql.Timestamp timestamp1 = new java.sql.Timestamp((long)band_data.getDouble(0));

            int year1 = timestamp1.getYear();
            int month1 = timestamp1.getMonth();
            int day1 = timestamp1.getDay();
            int hour1 = timestamp1.getHours();

            if(year == year1 && month == month1 && day == day1 && hour == hour1){
                sum += band_data.getFloat(1);
                count ++;
            }
            else {
                Event event = new Event()

                        .setSummary(String.valueOf(sum / count))
                        .setDescription("skin temperature");

                EventDateTime start = new EventDateTime()
                        .setDateTime(startDate)
                        .setTimeZone("America/New_York");
                event.setStart(start);

                EventDateTime end = new EventDateTime()
                        .setDateTime(endDate)
                        .setTimeZone("America/New_York");
                event.setEnd(end);


                String calendarId = "primary";

                event = mActivity.mService.events().insert(calendarId, event).execute();

                timestamp = new java.sql.Timestamp((long) band_data.getDouble(0));

                year = timestamp.getYear();
                month = timestamp.getMonth();
                day = timestamp.getDay();
                hour = timestamp.getHours();

                startDate = getStartDate(timestamp);
                endDate = getEndDate(timestamp);
                sum = band_data.getFloat(1);
                count = 1;
            }
        }

**/



            DateTime startDateTime = new DateTime("2015-11-13T09:00:00-07:00");
            DateTime endDateTime = new DateTime("2015-11-13T09:00:00-07:00");

            Event event = new Event()
                    .setSummary("testing")
                    .setDescription("skin Temperature");

            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("America/New_York");
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("America/New_York");
            event.setEnd(end);
            CalendarList calendarList = null;
            List<CalendarListEntry> list = null;
            String calendarId = null;
            try {
                calendarList = mActivity.mService.calendarList().list().execute();
            } catch (IOException e) {
            }
            list = calendarList.getItems();
            for (CalendarListEntry item : list) {
                if (item.getSummary().equals("band")) {
                    calendarId = item.getId();
                }
            }
            if (calendarId == null) {
                com.google.api.services.calendar.model.Calendar newCal = new com.google.api.services.calendar.model.Calendar();
                newCal.setSummary("band");
                try {
                    com.google.api.services.calendar.model.Calendar createEntry = mActivity.mService.calendars().insert(newCal).execute();
                } catch (IOException e) {
                }
            }
            try {
                calendarList = mActivity.mService.calendarList().list().execute();
            } catch (IOException e) {
            }
            list = calendarList.getItems();
            for (CalendarListEntry item : list) {
                if (item.getSummary().equals("band")) {
                    calendarId = item.getId();
                }
            }
            try {
                event = mActivity.mService.events().insert(calendarId, event).execute();
            } catch (IOException e) {
            }


            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            List<String> eventStrings = new ArrayList<String>();
            Events events = mActivity.mService.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            for (Event event1 : items) {
                DateTime start1 = event1.getStart().getDateTime();
                if (start1 == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start1 = event1.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event1.getSummary(), start1));
            }

        return eventStrings;
    }
    private DateTime getStartDate(java.sql.Timestamp timestamp){
        int year = timestamp.getYear();
        int month = timestamp.getMonth();
        int day = timestamp.getDay();
        int hour = timestamp.getHours();

        Date date= new Date(year, month + 1, day, hour, 0);

        return new DateTime(date);
    }
    private DateTime getEndDate(java.sql.Timestamp timestamp){

        int year = timestamp.getYear();
        int month = timestamp.getMonth();
        int day = timestamp.getDay();
        int hour = timestamp.getHours();

        Date date= new Date(year, month + 1, day, hour, 59);

        return new DateTime(date);
    }

}