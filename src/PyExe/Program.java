package PyExe;
import java.io.*;
/**
 * Launches the applicaiton
 * @author Graham Keenan (GAK)
 *
 */
public class Program 
{
	public static void main(String[] args)
	{
		/*
		GUI g = new GUI();
		*/
		try
		{
			Process p = null;
			p = Runtime.getRuntime().exec("cmd /C pip3 install py2exe");
			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = "";
			
			while((line = r.readLine()) != null)
				System.out.println(line);
			
			while((line = err.readLine()) != null)
				System.out.println(line);
			
			
			if( p!= null)
				p.waitFor();
		}
		catch(Exception e){}
	}
}
