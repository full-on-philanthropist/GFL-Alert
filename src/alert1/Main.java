package alert1;

import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;

public class Main {
	private static String pL4Cropped;
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		URL gfl = new URL("http://gflclan.com/GFL/serverlist.php");
		BufferedReader in = new BufferedReader(new InputStreamReader(gfl.openStream()));

		if(!SystemTray.isSupported()){
            System.out.println("SystemTray is not supported.");
        }
		
		if(!Desktop.isDesktopSupported()){
		    System.out.println("Desktop is not supported.");
		}
		
	    SystemTray tray = SystemTray.getSystemTray(); //creates system tray notification instance
	    Image img = ImageIO.read(new File("gflicon.jpg"));
	    TrayIcon tI = new TrayIcon(img, "Server Scanner");
	    tI.setImageAutoSize(true); 
	    try{
	        tray.add(tI);
	    } catch (AWTException e){
	        System.err.println(e);
	    }
	    
	    tI.addMouseListener(new MouseAdapter(){
        	public void mouseClicked(MouseEvent e){
        		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
	        		try {
						Desktop.getDesktop().browse(new URI("steam://connect/" + pL4Cropped));
					} catch (IOException | URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
        		}
        	}
       	});
	    
	    PopupMenu popup = new PopupMenu();
	    MenuItem exitItem = new MenuItem("Exit");
	    popup.add(exitItem);
	    tI.setPopupMenu(popup);
	    exitItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tray.remove(tI);
                System.out.println("Scan Stopped.");
                System.exit(0);
            }
        });
		
		ArrayList<String> list = new ArrayList<String>();
		Scanner scan = new Scanner(System.in); //retrieves name of map from IO
		
		String map = "";
		while (!map.equals("n")){
			System.out.println("Add map (\"n\" to start scan): ");
			map = scan.nextLine();
			
			if(!map.equals("n"))
				list.add(map);
		}
		scan.close();
		
		System.out.println("Scan started...");
		
		while(true){
                                        //pL = previousLine, keeps a history of the previous 5 lines in the stream
			String pL1 = null;          //line which contains the server name
			String pL2 = null;
			String pL3 = null;
			String pL4 = null;          //line which contains the server IP
			String pL5 = null;
			String currentLine = null;  //line which contains the map name
			int x = 0;                  //variable to check if "if" statement has run
			
	        while ((currentLine = in.readLine()) != null){
	        	x = 0;
		        for(int i = 0; i < list.size(); i++){
		        	if(currentLine.contains(list.get(i))){
		        		String pL1Cropped = pL1.replaceAll("\\<.*?\\> ?", "").trim(); //removes HTML/CSS formatting
		        		pL4Cropped = pL4.replaceAll("\\<.*?\\> ?", "").trim();
		        		String curCropped = currentLine.replaceAll("\\<.*?\\> ?", "").trim();
		        		tI.displayMessage("Server Found for \"" + curCropped + "\"",
		        				              "Server Name: \"" + pL1Cropped + "\"" +
		        				              "\nServer IP: " + pL4Cropped, TrayIcon.MessageType.INFO);
		        		
		        		x = 1;
		        		Thread.sleep(10000);
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
	        Thread.sleep(60000);
	        in = new BufferedReader(new InputStreamReader(gfl.openStream()));
		}
	}
}