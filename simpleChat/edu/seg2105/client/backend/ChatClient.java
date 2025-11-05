// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String id, String host, int port, ChatIF clientUI) throws IOException 
  {
  super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = id;
    openConnection();
  }
  
  // METHODS DESIGNED TO BE OVERRIDEN BY ABSTRACTCLIENT
  
  
  /**
   * Hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
      clientUI.display("Connection closed");
  }
  
  /**
   * Hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   * 
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down.");
    System.exit(0);
  }
  
  /**
   * Implemented hook method called after a connection has been established. The default
   * implementation does nothing. It may be overridden by subclasses to do
   * anything they wish.
   */
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginID);
    } catch (IOException e) {
      clientUI.display("Error connecting to the server.");
      quit();
    }
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString()); 
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.startsWith("#")) {
        handleCommand(message);
      }
      else {
          sendToServer(message);
      }
      //check if starts with hash, then dont send to the server, then 
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  
  private void handleCommand(String command) throws IOException {
    String[] cmdParts = command.split(" ", 2);
    String cmd = cmdParts[0];

    if(cmd.equals("#quit")) {
      quit();
    } else if(cmd.equals("#logoff")) {
      if(isConnected()){
        closeConnection();
      } else {
        clientUI.display("ERROR - Not connected.");
      }
    } else if(cmd.equals("#sethost")) {
      if(!isConnected()){
        if(cmdParts.length > 1){
          setHost(cmdParts[1]);
          clientUI.display("Host set to: " + cmdParts[1]);
        }
      } else {
        clientUI.display("ERROR - log off.");
      }
    } else if(cmd.equals("#setport")) {
      if(!isConnected()){
        if(cmdParts.length > 1){
          setPort(Integer.parseInt(cmdParts[1]));
          clientUI.display("Port set to: " + cmdParts[1]);
        } else {
          clientUI.display("ERROR - log off.");
        }
      }
    } else if(cmd.equals("#login")) {
      if(!isConnected()) {
        openConnection();
      } else {
        clientUI.display("ERROR - Already connected");
      }
    } else if(cmd.equals("#gethost")) {
      clientUI.display("Host - " + getHost());
    } else if(cmd.equals("#getport")) {
      clientUI.display("Port - " + getPort());
    }
  } 
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

}

//End of ChatClient class
