package com.jia.ezcamera.alarm;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

public class DelPicUtils {
	String dirPath;
	File mFile;
	File [] mFileList;
	int maxFileCount;

    public DelPicUtils(String dirpath , int max_file) {
    	dirPath = dirpath;
    	maxFileCount = max_file;
	}
    
    public void DelUnnecessaryPic(){
    	mFile = new File(dirPath);  
    	mFileList = mFile.listFiles(jpgFilter);  
    	if(mFileList.length>maxFileCount){
    		Arrays.sort(mFileList, new FileComparator());
    		for(int i=0;i<mFileList.length-maxFileCount;i++){
    			mFileList[i].delete();
    		}
    	}
    	
    }
    
    
    public class FileComparator implements Comparator<File> {  
        public int compare(File file1, File file2) {  
            if(file1.lastModified() < file2.lastModified())  
            {  
                return -1;  
            }else  
            {  
                return 1;  
            }  
        }
    }  
    
    private FileFilter jpgFilter = new FileFilter() {  
        public boolean accept(File file) {  
            String tmp = file.getName().toLowerCase();  
            if (tmp.endsWith(".jpg")||tmp.endsWith(".jpeg")) {  
                return true;  
            }  
            return false;  
        }  
    };
    
}
