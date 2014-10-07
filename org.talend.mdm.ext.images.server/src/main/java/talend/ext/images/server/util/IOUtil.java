package talend.ext.images.server.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class IOUtil {
	
	public static boolean byteToImage(byte[] b, String filePath) {
		boolean bl = false;
		File binaryFile = new File(filePath);
		FileOutputStream fileOutStream = null;
		
		try {
		    if(!binaryFile.exists()) {
		        int index = filePath.lastIndexOf("\\");
		        File dateFolder = new File(filePath.substring(0, index));
		        
		        if(!dateFolder.exists()) {
		            dateFolder.mkdir();
		        }
		    
		        binaryFile.createNewFile();
		    }
		    
			fileOutStream = new FileOutputStream(binaryFile);
			
			for (int i = 0; i < b.length; i++) {
				fileOutStream.write(b[i]);
			}
			
			fileOutStream.flush();
			bl = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bl;
	}

}
