package PyExe;

import java.io.*;

/**
 * Checks for required python libraries PyInstaller & py2exe at startup.
 * If not installed, the program will attempt to install them via Pip.
 * 
 * @author Graham Keenan (GAK)
 *
 */
public class Requirements 
{
	//Commands to check for install.
	private final String pyInstallerCheckCommand = "pip show pyinstaller";
	private final String py2exeCheckCommand = "pip3 show py2exe";
	
	//Commands to install the libraries
	private final String installPyInstaller = "pip install pyinstaller";
	private final String installPy2exe = "pip3 install py2exe";
	
	//Holds the commands for command line execution
	private String[] commands;
	
	//Flag for all libraries installed.
	private boolean allInstalled;
	
	/**
	 * Constructor
	 */
	public Requirements(){allInstalled = false;}
	
	/**
	 * Checks for installed libraries. If not installed, will attempt to install them.
	 */
	public void checkRequirements()
	{
		boolean pyInstallerInstalled = installCheck(pyInstallerCheckCommand);
		boolean py2exeInstalled = installCheck(py2exeCheckCommand);
		
		if(pyInstallerInstalled && py2exeInstalled)
			setAllInstalled(true);
		else if((!pyInstallerInstalled) && (!py2exeInstalled))
		{
			installPyInstaller();
			installPy2exe();
		}
		else if(!pyInstallerInstalled)
			installPyInstaller();
		else if(!py2exeInstalled)
			installPy2exe();
	}
	
	/**
	 * Checks for the installed libraries via Pip 
	 * @param cmd the command to execute
	 * @return the install status of the library
	 */
	private boolean installCheck(String cmd)
	{
		Process p = null;
		boolean installed = false;
		
		setCommands(cmd);
		
		try
		{
			p = Runtime.getRuntime().exec(commands);
			
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			
			String line;
			
			while((line = r.readLine()) != null)
			{
				if (line.startsWith("Name: "))
				{
					installed = true;
					break;
				}
			}
			
			while((line = error.readLine()) != null)
				System.out.println("Error stream: " + line);
			
		}
		catch(Exception e){}
		
		return installed;
	}
	
	/**
	 * Installs PyInstaller via Pip
	 */
	private void installPyInstaller()
	{
		Process p = null;
		setCommands(installPyInstaller);
		
		try
		{
			p = Runtime.getRuntime().exec(commands);
			if(p != null)
				p.waitFor();
		}
		catch(Exception e){}
	}
	
	/**
	 * Installs py2exe via Pip3 (Requires Python3)
	 */
	private void installPy2exe()
	{
		Process p = null;
		setCommands(installPy2exe);
		
		try
		{
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
	
	//Accessors/Setters
	public boolean getAllInstalled(){return allInstalled;}
	private void setAllInstalled(boolean status){allInstalled = status;}
	private String[] setCommands(String cmd)
	{
		if(isWindows())
			commands = new String[] {"cmd", cmd};
		else
			commands = new String[] {"/bin/sh", "-c", cmd};
		
		return commands;
	}
}
