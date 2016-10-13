package PyExe;

import java.io.*;

public class CreateExecutable 
{
	  private String filename;
	  private String absolutePath;

	  public CreateExecutable(String fn, String path)
	  {
	    filename = fn;
	    absolutePath = path;
	  }

	  //Creates the setup.py file
	  private void createSetupFile()
	  {
	    CreateSetup setup = new CreateSetup(filename);
	    setup.createSetupFile();
	  }

	  //Creates the executable using py2exe (Windows machines)
	  public void createExecutableOnWindows()
	  {
	    try
	    {
	      createSetupFile();
	      Runtime.getRuntime().exec("python setup.py py2exe");
	    }
	    catch(IOException e){}
	  }

	  //Creates the executable using pyinstaller
	  public void createExecutableOtherOS(String destPath)
	  {
		  Process p = null;
	      try
	      {	
	        String cmd = String.format("pyinstaller \"%s\" --clean --onefile --distpath %s", absolutePath, destPath);
	        //System.out.println("Command format: " + cmd);
	      
	        p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd});
	        
	        if(p != null)
	        	p.waitFor();
	      }
	      catch (Exception e){}
	  }
}
