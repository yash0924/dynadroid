package org.dynadroid.utils;

import android.content.Context;
import org.dynadroid.Application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ObjectStore {

    public static Object load(String fileName){
        //System.out.println("**loading file: "+fileName);
		Object data = null;
		FileInputStream fis = null;
		ObjectInputStream inStream = null;
		try{
			fis = Application.activity.openFileInput(fileName);
		    inStream = new ObjectInputStream( fis );

		    data = inStream.readObject();
		}catch(Exception e){
		}finally{
			try{
				if(inStream != null)
					inStream.close();
			}catch(Exception e){}
			try{
				if(fis != null)
					fis.close();
			}catch(Exception e){}
		}
        //System.out.println("**load completed");
		return data;
	}

	public static void save(Object o, String fileName){
        //System.out.println("**saving file: "+fileName);
		FileOutputStream fos = null;
	    ObjectOutputStream outStream = null;
		try{
			fos = Application.activity.openFileOutput(fileName, Context.MODE_PRIVATE);
			outStream = new ObjectOutputStream( fos );
			outStream.writeObject(o);
			outStream.flush();
		}catch(Exception e){
			e.printStackTrace(System.out);
		}finally{
			try{
				if(outStream != null)
					outStream.close();
			}catch(Exception e){}
			try{
				if(fos != null)
					fos.close();
			}catch(Exception e){}
		}
        //System.out.println("**save completed");
	}
}
