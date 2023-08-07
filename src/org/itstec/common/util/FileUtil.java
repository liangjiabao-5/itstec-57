
package org.itstec.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileUtil {

	private static FileUtil fileunit = null;

    private FileUtil() {}

    public static FileUtil getInstance() {
        if (null == fileunit) {
        	fileunit = new FileUtil();
        }
        return fileunit;
    }

    public boolean isFile(String fp){
    	File file = new File(fp);
    	return file.isFile();
    }

    public boolean isFolder(String fop){
    	File file = new File(fop);
        return file.isDirectory();
    }

    public String[][] showFolder(String fop){
    	File file = new File(fop);
    	File[] subs = file.listFiles();
    	int m=0,n=0;
    	String [][] folder = new String [2][subs.length];
    	for(int i=0;i<subs.length;i++){
    		if(subs[i].isDirectory()){
    			folder[0][m]=subs[i].getName();
    			m++;
    		}else{
    			folder[1][n]=subs[i].getName();
    			n++;
    		}
    	}
    	return folder;
    }

    public boolean makeFile(String fp) {
        try {
        	File file = new File(fp);
            return file.createNewFile();
        } catch (IOException e) {
            return false;
        }
    }

    public boolean createFolder(String fop) {
        try {
            File f = new File(fop);
            return f.mkdirs();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean copyFile(String copyFile , String copyToFile) throws IOException {
    	FileOutputStream fileOUT = null;
    	FileInputStream fileIO = null;
        try {
            File file = new File(copyFile);
            fileIO = new FileInputStream(file);
            fileOUT = new FileOutputStream(copyToFile);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fileIO.read(buffer, 0, 8192)) != -1) {
                fileOUT.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (FileNotFoundException fe) {
            throw fe;
        }finally{
        	if(fileOUT!=null){fileOUT.close();}
        	if(fileIO!=null){fileIO.close();}
        }
    }

}
