package PyExe;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This class holds the GUI information and controls the logic.
 * @author Graham Keenan (GAK)
 *
 */

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener 
{
	  private JPanel pan;
	  private JFileChooser fc;
	  private JButton createButton, exitButton;

	  private String filename;
	  private String filePath;
	  private String[] commands;
	  
	  private CreateExecutable ce;
	  private Requirements req;

	  /**
	   * GUI Constructor
	   */
	  public GUI()
	  {
		  req = new Requirements();
		  
		  /*
		   * Source for this following piece found here: http://stackoverflow.com/questions/14126975/joptionpane-without-button
		   * START
		   */
		  JOptionPane optionPane = new JOptionPane("Checking package requirements...", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
		  final JDialog dialog = new JDialog();
		  dialog.setTitle("Checking Packages");
		  dialog.setModal(true);
		  dialog.setContentPane(optionPane);
		  dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		  dialog.pack();
		  dialog.setLocation(700, 400);

		  //create timer to dispose of dialog after 5 seconds
		  Timer timer = new Timer(2500, new AbstractAction() {
		      @Override
		      public void actionPerformed(ActionEvent ae) {
		          dialog.dispose();
		      }
		  });
		  timer.setRepeats(false);//the timer should only go off once

		  //start timer to close JDialog as dialog modal we must start the timer before its visible
		  timer.start();
		  dialog.setVisible(true);
		  /*
		   * END
		   */
		  
		  req.checkRequirements();
		  
		  if(req.getAllInstalled() == true)
		  {
		      setDefaultCloseOperation(EXIT_ON_CLOSE);
		      setSize(550,400);
		      setLocation(500,100);
		      setTitle("PyExe: Python scripts to Windows executables");
		      guiLayout();
		      setVisible(true);
		  }
		  else
		  {
			  JOptionPane.showMessageDialog(null, "Error when checking/installing required python libraries via Pip.", "Error: Pip Error", JOptionPane.ERROR_MESSAGE);
			  System.exit(1);
		  }
	  }
	  
	  /**
	   * Layout for the GUI
	   */
	  private void guiLayout()
	  {
		  	pan = new JPanel();
		    fc = new JFileChooser();
		    fc.setControlButtonsAreShown(false);

		    createButton = new JButton("Create Executable");
		    createButton.addActionListener(this);

		    exitButton = new JButton("Exit");
		    exitButton.addActionListener(this);

		    add(pan);
		    pan.add(fc);
		    pan.add(createButton);
		    pan.add(exitButton);
	  }
	  
	  /**
	   * Controls the action events (button clicks, etc)
	   */
	public void actionPerformed(ActionEvent ae)
	{
		//Clicking the Exit button
	    if(ae.getSource() == exitButton)
	    {
	      int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit Program", JOptionPane.YES_NO_OPTION);

	      if(reply == JOptionPane.YES_OPTION)
	        System.exit(0);
	    }
	    //Creates the executable file.
	    else if(ae.getSource() == createButton)
	    {
	      try
	      {
	    	  //Gets the name and path of the selected file and creates a new CreateExectuable object.
	          File setupFile = fc.getSelectedFile();
	          filename = setupFile.getName();
	          filePath = setupFile.getAbsolutePath();
	          //System.out.println("Source file path: " + filePath);
	          ce = new CreateExecutable(filename, filePath);
	        
              //Asks the user if they wish to create the executable in the same directory as the script.
              int result = JOptionPane.showConfirmDialog(null, "Create executable in the same directory?\n(If your script relies on external libraries and files, click Yes)", "Confirm", JOptionPane.YES_NO_OPTION);
          
              if(result == JOptionPane.YES_OPTION)
              {
        	      ce.createExecutable();
	          
        	      //Asks the remove unnecessary files created with the executable
	              int dialogOption = JOptionPane.showConfirmDialog(null, "Executable created. Remove unnecessary files?\n(These are files created with the exectuable that will not affect the program if removed)", "Remove Unnecessary Files?", JOptionPane.YES_NO_OPTION);

	              if(dialogOption == JOptionPane.YES_OPTION)
	              {
	        	      removeUnnecessaryFiles(filePath);
	              }
              }
              else
              {
        	      //Creates the executable in a user designated location
	              String path = getDestinationPath();
	          
	              if(path != "")
	              {
		              ce.createExecutable(path);
		          
		              //ASks to remove unnecessary files created with the executable
		              int dialogOption = JOptionPane.showConfirmDialog(null, "Executable created. Remove unnecessary files?", "Remove Unnecessary Files?", JOptionPane.YES_NO_OPTION);
	
		              if(dialogOption == JOptionPane.YES_OPTION)
		              {
		        	      removeUnnecessaryFiles(path);
		              }
	              }
              }
	      }
		  //Thrown when attempting to create an executable without selecting a file.
	      catch(NullPointerException e)
	      {
	          JOptionPane.showMessageDialog(null, "Please select a file!", "Select a file", JOptionPane.WARNING_MESSAGE);
		      e.printStackTrace();
		  }
	   }
    }

	  /**
	   * Gets the user designated location to save the executable to.
	   * @return the path to save the executable to.
	   */
	  private String getDestinationPath()
	  {
		  //Lets the user choose a directory to save the executable to.
	    JFileChooser destLoc = new JFileChooser();
	    destLoc.setDialogTitle("Choose save location");
	    destLoc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    destLoc.setAcceptAllFileFilterUsed(false);
	    int returnVal = destLoc.showSaveDialog(null);
	    
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	    {
	    	String absPath = destLoc.getSelectedFile().toString();
	    	//System.out.println("Destination path: " + absPath);
	    
	    	return absPath;
	    }
	    else
	    	return "";
	    
	  }
	  
	  /**
	   * Removes the build/ folder and .spec files, leaving just the executable (PyInstaller)
	   * @param path The location path of the build/ folder and .spec file
	   * TODO Fix for windows
	   */
	  private void removeUnnecessaryFiles(String path)
	  {
		  File temp = new File("");
		  String location = temp.getAbsolutePath().toString();
		  Process p = null;
		  try
          {
            //Strips the filename
            String[] stripFilename = filename.split("\\.");
            String stripped = stripFilename[0];
            
            //If Unix-based OS
            if(!isWindows())
            {
	            String moveToSaveDir = "cd " + path;
	            setCommands(moveToSaveDir);
	            p = Runtime.getRuntime().exec(commands);
	            if(p != null)
	            	p.waitFor();
	      
	            //removes the /build folder
	            String cmd1 = "rm -rf build/";
	            setCommands(cmd1);
	            p = Runtime.getRuntime().exec(commands);
	            if(p != null)
	            	p.waitFor();
	            
	            //Removes the shitty .spec file
	            String cmd2 = String.format("rm %s.spec", stripped);
	            setCommands(cmd2);
	            p = Runtime.getRuntime().exec(commands);
	            if(p != null)
	            	p.waitFor();
            }
            //If on Windows
            else
            {
            	String moveToSaveDir = "cd " + path;
            	setCommands(moveToSaveDir);
            	p = Runtime.getRuntime().exec(commands);
            	if(p != null)
            		p.waitFor();
            	
            	//Removes the /build folder
            	String remove = "rmdir build/";
            	setCommands(remove);
            	p = Runtime.getRuntime().exec(commands);
            	if(p != null)
            		p.waitFor();
            	
            	//Removes the shitty .spec file
            	String removeSpec = String.format("del %s.spec", stripped);
            	setCommands(removeSpec);
            	p = Runtime.getRuntime().exec(commands);
            	if(p != null)
            		p.waitFor();
            }

          }
          catch(Exception e){}
		  
		  //Asks user if they wish to quit upon file removal.
          int result = JOptionPane.showConfirmDialog(null, "Files removed! Quit the program?", "Files Removed", JOptionPane.YES_NO_OPTION);
          if(result == 0)
            System.exit(0);
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
	   * Sets the commands for command line execution
	   * @param cmd the command to set
	   * @return the array of commands
	   */
	  private String[] setCommands(String cmd)
		{
			if(isWindows())
				commands = new String[] {"cmd", "/C", cmd};
			else
				commands = new String[] {"/bin/sh", "-c", cmd};
			
			return commands;
		}
}
