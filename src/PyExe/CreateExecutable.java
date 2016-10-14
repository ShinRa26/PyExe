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
	  private String filename; //Name of the script
	  private String absolutePath; //Path to the script
	  private String[] commands; //Commands to be executed by command line

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
	      String cmd = "python3 setup.py py2exe";
	      setCommands(cmd);
	      Runtime.getRuntime().exec(commands);
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
	        setCommands(cmd);
	        
	        //Executes command
	        p = Runtime.getRuntime().exec(commands);
	        
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
			  setCommands(cmd);
			  
			  //Executes the command
			  p = Runtime.getRuntime().exec(commands);
			  
			  if(p != null)
				  p.waitFor();
		  }
		  catch(Exception e){}
	  }
	  
	  /**
		 * Detects if the OS running is Windows
		 * @return If the running OS is Windows or not
		 */
		private boolean isWindows()
		{
			String OS = System.getProperty("os.name");
			if(OS.startsWith("Windows"))
				return true;
			else
				return false;
		}
		
		/**
		 * Sets the command arguments
		 * @param cmd The command to set
		 */
		private String[] setCommands(String cmd)
		{
			if(isWindows())
			{
				commands = new String[] {"cmd", cmd};
			}
			else
			{
				commands = new String[] {"/bin/sh", "-c", cmd};
			}
			
			return commands;
		}
}
