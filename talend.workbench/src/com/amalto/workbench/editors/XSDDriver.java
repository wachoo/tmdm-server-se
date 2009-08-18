package com.amalto.workbench.editors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.xml.sax.SAXException;

import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.input.xml.XmlInputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.rnc.RncOutputFormat;
import com.thaiopensource.relaxng.output.rng.RngOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;
import com.thaiopensource.util.Localizer;
import com.thaiopensource.util.UriOrFile;
import com.thaiopensource.util.Version;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;

public class XSDDriver {
  static private final Localizer localizer = new Localizer(XSDDriver.class);
  private String inputType;
  private String outputType;
  private File outPut;
  private final ErrorHandlerImpl eh = new ErrorHandlerImpl();
  private static final String DEFAULT_OUTPUT_ENCODING = "UTF-8";
  private static final int DEFAULT_LINE_LENGTH = 72;
  private static final int DEFAULT_INDENT = 2;

 
  public int doMain(String[] args) {
    List<String> outputParams = new Vector<String>();

      if (args.length < 2) {
        error(localizer.message("too_few_arguments"));
        eh.print(localizer.message("usage", Version.getVersion(XSDDriver.class)));
        return 2;
      }
      
      outPut = new File(args[1]);
      
      if (inputType == null) {
        inputType = extension(args[0]);
        if (inputType.length() > 0)
          inputType = inputType.substring(1);
      }
      InputFormat inFormat;
      if (inputType.equalsIgnoreCase("rng"))
        inFormat = new SAXParseInputFormat();
      else if (inputType.equalsIgnoreCase("rnc"))
        inFormat = new CompactParseInputFormat();
      else if (inputType.equalsIgnoreCase("dtd"))
        inFormat = new DtdInputFormat();
      else if (inputType.equalsIgnoreCase("xml"))
        inFormat = new XmlInputFormat();
      else {
        error(localizer.message("unrecognized_input_type", inputType));
        return 2;
      }
      OutputFormat of;
      String ext = extension(args[args.length - 1]);
      if (outputType == null) {
        outputType = ext;
        if (outputType.length() > 0)
          outputType = outputType.substring(1);
      }
      if (outputType.equalsIgnoreCase("dtd"))
        of = new DtdOutputFormat();
      else if (outputType.equalsIgnoreCase("rng"))
        of = new RngOutputFormat();
      else if (outputType.equalsIgnoreCase("xsd"))
        of = new XsdOutputFormat();
      else if (outputType.equalsIgnoreCase("rnc"))
        of = new RncOutputFormat();
      else {
        error(localizer.message("unrecognized_output_type", outputType));
        return 2;
      }
      outputType = outputType.toLowerCase();
      SchemaCollection sc;

    try {
			sc = inFormat.load(UriOrFile.toUri(args[0]), new String[] {},
					outputType, eh);

			if (ext.length() == 0)
				ext = outputType;
			OutputDirectory od = new LocalOutputDirectory(sc.getMainUri(),
					new File(args[args.length - 1]), ext,
					DEFAULT_OUTPUT_ENCODING, DEFAULT_LINE_LENGTH,
					DEFAULT_INDENT);
			of.output(sc, od, outputParams.toArray(new String[0]), inputType
					.toLowerCase(), eh);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return 1;
		}

      return 0;

  }

  private void error(String message) {
    eh.printException(new SAXException(message));
  }

  static private String extension(String s) {
    int dot = s.lastIndexOf(".");
    if (dot < 0)
      return "";
    return s.substring(dot);
  }
  
  public String outputXSD()
  {
	  StringBuffer buffer = new StringBuffer();
		FileReader reader;
		try {
			reader = new FileReader(outPut);
			BufferedReader bufferedReader = new BufferedReader(reader);

			String nextLine = bufferedReader.readLine();
			while (nextLine != null) {
				buffer.append(nextLine);
				buffer.append("\r\n");
				nextLine = bufferedReader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			boolean result = false;
			int tryCount = 0;
			while (!result && tryCount++ < 10) {
				System.gc();
				result = delete(outPut);
			}
		}
		
		return buffer.toString();
  }
  
  public File outputXSD(String src, String fileName)
  {
      File file = new File(fileName);
	  try {
			FileWriter writer = new FileWriter(file);
			writer.write(src);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return file;
  }
  // Deletes all files and subdirectories under path
  public static boolean delete(File file)
  {
      if (!file.exists())
      {
          return true;
      }

      if (file.isDirectory())
      {
          XSDDriver.deleteContent(file);
      }

      // now delete the root
      boolean val = file.delete();

      return val;
  }

  public static boolean deleteContent(File file)
  {
      assert (file != null);

      String[] children = file.list();
      if (children == null)
      {
          return true;
      }

      for (int i = 0; i < children.length; i++)
      {
          if (!XSDDriver.delete(new File(file, children[i])))
          {
              return false;
          }
      }

      return true;
  }

  public static boolean deleteDir(File dir)
  {
      if (dir.isDirectory())
      {
          String[] children = dir.list();
          for (int i = 0; i < children.length; i++)
          {
              boolean success = deleteDir(new File(dir, children[i]));
              if (!success)
              {
                  return false;
              }
          }
      }

      return dir.delete();
  }
}
