package PyExe;

import java.io.*;
import java.util.*;

public class CreateSetup 
{
	  private String filename;
	  private PrintWriter pw;

	  public CreateSetup(String fn)
	  {
	    filename = fn;
	  }

	  //Writes the setup info to a new file ("setup.py")
	  public void createSetupFile()
	  {
	    try
	    {
	      pw = new PrintWriter("setup.py");
	      String setupInfo = setupLayout();
	      pw.write(setupInfo);
	      pw.close();
	    }
	    catch (FileNotFoundException e)
	    {}
	  }

	  //Text for the setup file.
	  private String setupLayout()
	  {
	    String imports = "from distutils.core import setup\nimport py2exe, sys, os\n\n";
	    String sysAppends = "sys.argv.append(\"py2exe\")\n";
	    String setup1 = String.format("setup = (windows = [{'script': \"%s\"}], ", filename);
	    String setup2 = String.format("options = {'py2exe': {'bundle_files': 1}}, ");
	    String setup3 = "zipfile=None,)";

	    String fullSetup = imports + sysAppends + setup1 + setup2 + setup3;
	//options = {'py2exe': {'bundle_files': 1}},\n\t");
	    return fullSetup;
	  }
}
