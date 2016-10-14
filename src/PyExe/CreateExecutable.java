package PyExe;

import java.io.*;

/**
 * Creates the executable file from the python script.
 * Windows machines: py2exe
 * Other OS': pyinstaller 
 * @author Graham Keenan (GAK)
 *
 */

public class CreateExecutable 
{
	  private String filename;
	  private String absolutePath;

	  /**
	   * Create Executable constructor
	   * @param fn Name of the script
	   * @param path The location of the script
	   */
	  public CreateExecutable(String fn, String path)
	  {
	    filename = fn;
	    absolutePath = path;
	  }

	  /**
	   * Creates the setup.py file for py2exe (Windows)
	   */
	  private void createSetupFile()
	  {
	    CreateSetup setup = new CreateSetup(filename);
	    setup.createSetupFile();
	  }

	  /**
	   * Creates the executable using py2exe (Windows machines)
	   */
	  public void createExecutableOnWindows()
	  {
	    try
	    {
	      //Creates the setup file and  launches the py2exe converion from command line
	      createSetupFile();
	      Runtime.getRuntime().exec("python setup.py py2exe");
	    }
	    catch(IOException e){}
	  }

	  /**
	   * Creates the executable using pyinstaller in a user designated location (Other OS')
	   * @param destPath The user designated save location
	   */
	  public void createExecutableOtherOS(String destPath)
	  {
		  Process p = null;
	      try
	      {	
	    	//Creates the command to start pyinstaller
	        String cmd = String.format("pyinstaller \"%s\" --clean --onefile --distpath %s", absolutePath, destPath);
	        //System.out.println("Command format: " + cmd);
	        
	        //Executes command
	        p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd});
	        
	        if(p != null)
	        	p.waitFor();
	      }
	      catch (Exception e){}
	  }
	  
	  /**
	   * Creates the executable using pyinstaller in the same directory as the python script (Other OS')
	   */
	  public void createExecutableOtherOS()
	  {
		  //Strips the filename from the scripts location to obtain the directory.
		  String[] stripFilename = absolutePath.split("/");
		  String sameDir = "";
		  for(int i = 0; i < stripFilename.length - 1; i++)
			  sameDir += stripFilename[i] + "/";

		  Process p = null;
		  
		  try
		  {
			  //Creates the command to start pyinstaller
			  String cmd = String.format("pyinstaller \"%s\" --clean --onefile --distpath %s", absolutePath, sameDir);
			  
			  //Executes the command
			  p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd});
			  
			  if(p != null)
				  p.waitFor();
		  }
		  catch(Exception e){}
	  }
}
