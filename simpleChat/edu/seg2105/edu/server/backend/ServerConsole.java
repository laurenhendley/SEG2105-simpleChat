
package edu.seg2105.edu.server.backend;

import ocsf.server.*;
import java.io.*;
import java.util.Scanner;
import edu.seg2105.client.common.*;



public class ServerConsole implements ChatIF {

	Scanner fromConsole;
	EchoServer server;
	
	
	public ServerConsole(EchoServer server) {
		this.server = server;
		fromConsole = new Scanner(System.in);
	}
	
	
	/**
	 * This method overrides the method in the ChatIF interface.  It
	 * displays a message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("Server > " + message);
	}
	  
	  
	/**
	 * This method waits for input from the console.  Once it is 
	 * received, it sends it to the client's message handler.
	 */
	public void accept() {
		try{

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        if(message.startsWith("#")) {
	        	handleCommand(message);
	        } else {
	        	display(message);
	        	server.sendToAllClients("Server > " +message);
	        }
	        }
	      } 
	    catch (Exception ex) {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }
	
	private void handleCommand(String msg) throws IOException {
		String[] messageSplit = msg.split(" ");
		String command = messageSplit[0];
		int tmpPort = 5555;
		
		if(messageSplit.length > 1) {
			tmpPort = Integer.parseInt(messageSplit[1]);
		}
		
		if(command.equals("#quit")) {
			System.exit(0);
		} else if(command.equals("#stop")) {
			  server.stopListening();
		} else if(command.equals("#close")) {
			server.close();
		} else if(command.equals("#setport")) {
			if(server.isListening() || server.getNumberOfClients() > 0) {
				display("Server must be shut down first.");
			} else {
				server.setPort(tmpPort);
				display("Set port to: " + tmpPort);
			}
		} else if(command.equals("#start")) {
			if(!server.isListening()) {
				server.listen();
			} else {
				display("Server already listening.");
			}
		} else if(command.equals("#getport")) {
			display("Port: " + server.getPort());
		}
	} 
}
