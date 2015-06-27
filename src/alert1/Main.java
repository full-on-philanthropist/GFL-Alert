package alert1;

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException {
		URL gfl = new URL("http://gflclan.com/GFL/serverlist.php");
		BufferedReader in = new BufferedReader(new InputStreamReader(gfl.openStream()));

		if(!SystemTray.isSupported()){
            System.out.println("SystemTray is not supported.");
        }
		
	    SystemTray tray = SystemTray.getSystemTray(); //creates system tray notification instance
	    Image img = ImageIO.read(new File("gflicon.jpg"));
	    TrayIcon trayIc = new TrayIcon(img, "Server Scanner");
	    trayIc.setImageAutoSize(true); 
	    try{
	        tray.add(trayIc);
	    } catch (AWTException e){
	        System.err.println(e);
	    }
	    
	    PopupMenu popup = new PopupMenu();
	    MenuItem exitItem = new MenuItem("Exit");
	    popup.add(exitItem);
	    trayIc.setPopupMenu(popup);
	    exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tray.remove(trayIc);
                System.exit(0);
            }
        });
		
		String ans = "Y";
		String map = "";
		ArrayList<String> list = new ArrayList<String>();
		Scanner scan = new Scanner(System.in); //retrieves name of map from IO
		System.out.println("Refresh time? (in seconds)");
		int refresh = scan.nextInt() * 1000;
		while (!ans.equals("n")){
			System.out.println("Name of map: ");
			map = scan.nextLine();
			
			list.add(map);
			
			System.out.println("Add another? (Y/n)");
			ans = scan.nextLine();
		}
		scan.close();
		
		while(true){
							    		//pL = previousLine, keeps a history of the previous 5 lines in the stream
			String pL1 = null;  		//line which contains the server name
			String pL2 = null;
			String pL3 = null;
			String pL4 = null;  		//line which contains the server IP
			String pL5 = null;
			String currentLine = null;  //line which contains the map name
			int x = 0; 					//variable to check if "if" statement has run
			
	        while ((currentLine = in.readLine()) != null){
	        	x = 0;
		        for(int i = 0; i < list.size(); i++){
		        	if(currentLine.contains(list.get(i))){
		        		String pL1fixed = pL1.replaceAll("\\<.*?\\> ?", "").trim(); //removes HTML/CSS formatting
		        		String pL4fixed = pL4.replaceAll("\\<.*?\\> ?", "").trim();
		        		String curfixed = currentLine.replaceAll("\\<.*?\\> ?", "").trim();
		        		trayIc.displayMessage("Server Found for \"" + curfixed + "\"",
		        				              "Server Name: \"" + pL1fixed + "\"" +
		        				              "\nServer IP: " + pL4fixed, TrayIcon.MessageType.INFO);
		        		x = 1;
		        	}
		        }
			    if(x == 0){
		    		pL1 = pL2; 			//updates stream's line history
		    		pL2 = pL3;
		    		pL3 = pL4;
		    		pL4 = pL5;
		    		pL5 = currentLine;
			    }
	        }
	        in.close();
	        Thread.sleep(refresh);
	        in = new BufferedReader(new InputStreamReader(gfl.openStream()));
		}
	}
}