package PyExe;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener 
{
	private JPanel pan;
	  private JFileChooser fc;
	  private JButton createButton, exitButton;
	  private JCheckBox windows;

	  private String filename;
	  private String filePath;
	  private CreateExecutable ce;

	  public GUI()
	  {
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    setSize(550,400);
	    setLocation(500,100);
	    setTitle("PyExe: Python scripts to Windows executables");
	    guiLayout();
	    setVisible(true);
	  }
	  
	  //Layout for the GUI
	  private void guiLayout()
	  {
		  	pan = new JPanel();
		    fc = new JFileChooser();
		    fc.setControlButtonsAreShown(false);

		    createButton = new JButton("Create Executable");
		    createButton.addActionListener(this);

		    exitButton = new JButton("Exit");
		    exitButton.addActionListener(this);

		    windows = new JCheckBox("On a windows machine?", false);
		    windows.addActionListener(this);

		    add(pan);
		    pan.add(fc);
		    pan.add(windows);
		    pan.add(createButton);
		    pan.add(exitButton);
	  }
	  
	  //Action events
	  public void actionPerformed(ActionEvent ae)
	  {
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
	        File setupFile = fc.getSelectedFile();
	        filename = setupFile.getName();
	        filePath = setupFile.getAbsolutePath();
	        //System.out.println("Source file path: " + filePath);
	        ce = new CreateExecutable(filename, filePath);

	        boolean checked = windows.isSelected();

	        //On a Windows machine, use py2exe for conversion
	        if(checked)
	        {
	          JOptionPane.showMessageDialog(null, "This feature is still in development and is not available yet!", "Feature Not Available", JOptionPane.INFORMATION_MESSAGE);
	          //ce.createExecutableOnWindows();
	        }
	        //On another OS, use pyinstaller for conversion.
	        else
	        {
	          int result = JOptionPane.showConfirmDialog(null, "Create executable in the same directory?\n(If your script relies on external libraries and files, click Yes)", "Confirm", JOptionPane.YES_NO_OPTION);
	          
	          if(result == JOptionPane.YES_OPTION)
	          {
	        	  ce.createExecutableOtherOS();
		          
		          int dialogOption = JOptionPane.showConfirmDialog(null, "Executable created. Remove unnecessary files?", "Remove Unnecessary Files?", JOptionPane.YES_NO_OPTION);
	
		          if(dialogOption == JOptionPane.YES_OPTION)
		          {
		        	  removeUnnecessaryFiles(filePath);
		          }
	          }
	          else
	          {
		          String path = getDestinationPath();
		          
		          if(path != "")
		          {
			          ce.createExecutableOtherOS(path);
			          
			          int dialogOption = JOptionPane.showConfirmDialog(null, "Executable created. Remove unnecessary files?", "Remove Unnecessary Files?", JOptionPane.YES_NO_OPTION);
		
			          if(dialogOption == JOptionPane.YES_OPTION)
			          {
			        	  removeUnnecessaryFiles(path);
			          }
		          }
	          }
	        }
	      }
	      catch(NullPointerException e)
	      {
	        JOptionPane.showMessageDialog(null, "Please select a file!", "Select a file", JOptionPane.WARNING_MESSAGE);
	        e.printStackTrace();
	      }
	    }
	  }

	  //Gets the destination path for the build
	  private String getDestinationPath()
	  {
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
	  
	  //Removes the build/ folder and .spec files, leaving just the executable
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
            
            String moveToSaveDir = "cd " + path;
            p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", moveToSaveDir});
            if(p != null)
            	p.waitFor();
      
            //removes the /build folder
            String cmd1 = "rm -rf build/";
            p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd1});
            if(p != null)
            	p.waitFor();
            
            //Removes the shitty .spec file
            String cmd2 = String.format("rm %s.spec", stripped);
            p = Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", cmd2});
            if(p != null)
            	p.waitFor();

          }
          catch(Exception e){}
		  
          int result = JOptionPane.showConfirmDialog(null, "Files removed! Quit the program?", "Files Removed", JOptionPane.YES_NO_OPTION);
          if(result == 0)
            System.exit(0);
	  }
}
