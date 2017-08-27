package com.TyrLib2.PC.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class MainStarter {
	
	static final String MAIN_METHOD_NAME                     = "main";  //$NON-NLS-1$
	static final String PATH_SEPARATOR                       = "/";  //$NON-NLS-1$
	static final String CURRENT_DIR                          = "./";  //$NON-NLS-1$
	static final String UTF8_ENCODING                        = "UTF-8";  //$NON-NLS-1$
	
	private static class ManifestInfo {
		String[] rsrcClassPath;
	}
	
	public void main(String className, String[] args) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
		
		ManifestInfo mi = getManifestInfo();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		
		URL url = cl.getClass().getResource("/");
		
		URL[] rsrcUrls = new URL[mi.rsrcClassPath.length];
		for (int i = 0; i < mi.rsrcClassPath.length; i++) {
			String rsrcPath = url.getPath() + mi.rsrcClassPath[i];
			if (rsrcPath.endsWith(PATH_SEPARATOR)) 
				rsrcUrls[i] = new File(rsrcPath).toURI().toURL(); 
			else
				rsrcUrls[i] = new File(rsrcPath).toURI().toURL();  
		}
		
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		Class<?> c = Class.forName(className, true, jceClassLoader);
		Method main = c.getMethod(MAIN_METHOD_NAME, new Class[]{args.getClass()}); 
		main.invoke((Object)null, new Object[]{args});
	}
	
	private static ManifestInfo getManifestInfo() throws IOException {
		
		String OS = System.getProperty("os.name").toLowerCase();
		String bit = System.getProperty("sun.arch.data.model") ;
		
		String target = "Class-Path";
		if (OS.indexOf("win") >= 0) {
			target += "Win" + bit;
		} else if (OS.indexOf("mac") >= 0) {
			target += "MacOSX";
		} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
			target += "Lin" + bit;
		} else {
			System.out.println("Your OS is not support!!");
		}
		
		Enumeration<?> resEnum;
		resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME); 
		while (resEnum.hasMoreElements()) {
			try {
				URL url = (URL)resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					String mainClass = mainAttribs.getValue("Main-Class");
					
					if (mainClass != null && mainClass.equals("com.tyrfing.games.pc.main.MainStarter")){		
						ManifestInfo result = new ManifestInfo();
						
						String rsrcCP = mainAttribs.getValue(target); 
						result.rsrcClassPath = splitSpaces(rsrcCP);
						return result;
					}
				}
			}
			catch (Exception e) {
				// Silently ignore wrong manifests on classpath?
			}
		}

		return null;
	}
	
	private static String[] splitSpaces(String line) {
		if (line == null) 
			return null;
		List<String> result = new ArrayList<String>();
		int firstPos = 0;
		while (firstPos < line.length()) {
			int lastPos = line.indexOf(' ', firstPos);
			if (lastPos == -1)
				lastPos = line.length();
			if (lastPos > firstPos) {
				result.add(line.substring(firstPos, lastPos));
			}
			firstPos = lastPos+1; 
		}
		return result.toArray(new String[result.size()]);
	}
	
}

