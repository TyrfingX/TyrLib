package com.tyrlib2.util;

import java.io.IOException;
import java.io.InputStream;

import com.tyrlib2.materials.DefaultMaterial3;
import com.tyrlib2.renderables.Entity;

import android.content.Context;

public class IQMEntityFactory implements IEntityFactory {

	
	
	public IQMEntityFactory(Context context, String fileName, DefaultMaterial3 baseMaterial) {
		try {
			InputStream inputStream = context.getResources().getAssets().open(fileName);
		} catch (IOException e) {
			throw new RuntimeException("Failed to load entity " + fileName + ".");
		}
	}
	
	@Override
	public Entity create() {
		return null;
	}

}
