package com.tyrfing.games.id3;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import tyrfing.common.files.FileReader;

public class SaveGameProvider extends ContentProvider {

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
        String[] columns = new String[]{ "character.bs", "floors.bs", "powers.bs", "rituals.bs", 
        								 "buildings.bs", "Blessings.bs", "prayers.bs", "tutorial.bs"}; 
        MatrixCursor mc = new MatrixCursor(columns);
        
        Object[] row = new Object[] {	FileReader.readFile(getContext(), "character.bs"),
        								FileReader.readFile(getContext(), "floors.bs"),
        								FileReader.readFile(getContext(), "powers.bs"),
        								FileReader.readFile(getContext(), "rituals.bs"),
        								FileReader.readFile(getContext(), "buildings.bs"),
        								FileReader.readFile(getContext(), "Blessings.bs"),
        								FileReader.readFile(getContext(), "prayers.bs"),
        								FileReader.readFile(getContext(), "tutorial.bs")
        							}; 
        mc.addRow(row);
        
        return mc;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
