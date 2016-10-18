package PyExe;

import java.io.*;

import javax.swing.JOptionPane;

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
	
	//Commands to install the libraries
	private final String installPyInstaller = "pip install pyinstaller";
	
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
		
		if(pyInstallerInstalled)
			setAllInstalled(true);
		else if(!pyInstallerInstalled)
		{
			installPyInstaller();
			setAllInstalled(true);
		}
	}
	
	/**
	 * Checks for the installed libraries via Pip.
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
				else
				{
					continue;
				}
			}
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
			JOptionPane.showMessageDialog(null, "Installing PyInstaller...", "Installing PyInstaller", JOptionPane.INFORMATION_MESSAGE);
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
			commands = new String[] {"cmd", "/C", cmd};
		else
			commands = new String[] {"/bin/sh", "-c", cmd};
		
		return commands;
	}
}
