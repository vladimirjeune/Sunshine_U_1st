/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vrj.udacity.sunshine.app.data;

import java.util.Date;
import java.util.HashSet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.vrj.udacity.sunshine.app.data.WeatherContract.LocationEntry;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    
	String testLocationSetting;
	String testCityName;
	double testLatitude;
	double testLongitude;

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
    	testLocationSetting = "99705";
    	testCityName = "North Pole";
    	testLatitude = 64.7488;
    	testLongitude = -147.353;
    	
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.
    */
    public void testLocationTable() {

    	insertLocation();

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.

        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.
    	long locationRowId = insertLocation();
    	
    	// Make sure we have a valid row ID.
    	assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);

        // First step: Get reference to writable database
    	WeatherDbHelper wdbh = new WeatherDbHelper(getContext());  // Used to open DB
    	SQLiteDatabase db = wdbh.getWritableDatabase();  // Should be a cached DB
    	
        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
    	ContentValues cv = TestUtilities.createWeatherValues(locationRowId);

        // Insert ContentValues into database and get a row ID back
    	// Like: INSERT INTO TABLE_NAME VALUES(value1, value2,...);
    	long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, cv);
    	
    	// Tests that we got a row created
    	assertTrue(weatherRowId != -1L);

        // Query the database and receive a Cursor back
    	// Like: SELECT * FROM TABLE_NAME
    	Cursor cursor = db.query(
    			WeatherContract.WeatherEntry.TABLE_NAME, 
    			null,   // Leaving columns null will be like *
    			null,   // cols for where clause
    			null,   // value for where clause
    			null,   // Cols to group by
    			null,   // Cols to filter by row group
    			null);  // Sort order

    	// Move the cursor to the first valid database row and check to see if we have any rows
    	assertTrue("Error: No Records returned from location query", cursor.moveToFirst() == true);

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
    	TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate", cursor, cv);

    	
    	// Move the cursor to demonstrate that there is only one record in the database
    	assertFalse("Error: More than one record returned from weather query", cursor.moveToNext());
    	
        // Finally, close the cursor and database
    	db.close();
    	cursor.close();
    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
    	
    	// STEP 1: Get reference to writable database
    	// If there's an error in those massive SQL table creation Strings,
    	// errors will be thrown here when you try to get a writable database.
    	WeatherDbHelper wdbh = new WeatherDbHelper(getContext());
    	SQLiteDatabase db = wdbh.getWritableDatabase();

    	// STEP 2: Create ContentValues of what you want to insert
    	ContentValues cv = new ContentValues();

    	// (you can use the TestUtilities.createNorthPoleLocationValues if you wish)
    	cv.put(LocationEntry.COLUMN_CITY_NAME, testCityName);
    	cv.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
    	cv.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
    	cv.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);

    	// STEP 3: Insert ContentValues into database and get a row ID back
    	long locationRowId = db.insert(LocationEntry.TABLE_NAME, null, cv);

    	// Verify we get a row back
    	assertTrue(locationRowId != -1);

    	// Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
    	// the round trip.

    	// STEP 4: Query the database and receive a Cursor back
    	// A cursor is your primary interface to the query results.
    	// Just giving the table name should be similar to 
    	// SELECT * FROM TABLENAME;
    	Cursor locationCursor = db.query(
    			LocationEntry.TABLE_NAME, // Table we are querying
    			null,  // all columns
    			null,  // Columns of WHERE 
    			null,  // Values for the WHERE
    			null,  // Columns to FILTER by Row Groups
    			null,  // Sort order
    			null);

    	// Move the cursor to a valid database row and check to see if we got any records back
    	// from the query
    	assertTrue("Error: No Records returned from location query", locationCursor.moveToFirst());

    	// STEP 5: Validate data in resulting Cursor with the original ContentValues
    	// (you can use the validateCurrentRecord function in TestUtilities to validate the
    	// query if you like)
    	TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", locationCursor, cv);


    	// Finally, close the cursor and database
    	// Move the cursor to demonstrate that there is only one record in the database
    	assertFalse("Error: More than one record returned from location query", 
    			locationCursor.moveToNext());

    	// STEP 6: Finally, close the cursor and database
    	locationCursor.close();
    	db.close();

    	return locationRowId;
    }
}
