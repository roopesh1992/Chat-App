
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer
{  public static void main(String[] args ) 
{  int tid=0;
ArrayList<ChatHandler> AllHandlers = new ArrayList<ChatHandler>();
ArrayList<ChatMessageExt1> History = new ArrayList<ChatMessageExt1>();
ArrayList<ChatMessageExt1> DrawHistory = new ArrayList<ChatMessageExt1>();

try 
{  ServerSocket s = new ServerSocket(4000);

for (;;)
{  Socket incoming = s.accept( );
tid++;
new ChatHandler(incoming, AllHandlers,History,DrawHistory,tid).start();
}   
}
catch (Exception e) 
{  System.out.println("Expception is FLAG 1"+e);          //FLAG 1
} 
} 
}

class ChatHandler extends Thread
{  public ChatHandler(Socket i, ArrayList<ChatHandler> h,ArrayList<ChatMessageExt1> m,ArrayList<ChatMessageExt1> d,int tid) 
{      
	ThreadId = tid;
	incoming = i;
	handlers = h;
	history = m;
	drawhistory = d;
	handlers.add(this);
	try{
		in = new ObjectInputStream(incoming.getInputStream());
		out = new ObjectOutputStream(incoming.getOutputStream());
	}catch(IOException ioe){
		System.out.println("Expception is FLAG 2:Could not create streams.");  //FLAG 2
	}
}
public synchronized void broadcast(){
	if(myObject.getFlag().equals("message")){
		history.add(myObject);
	}
	else if(myObject.getFlag().equals("DRAW")){
		drawhistory.add(myObject);
	}
	else{
		drawhistory.clear();
	}
	ChatHandler left = null;
	for(ChatHandler handler : handlers){
		ChatMessageExt1 cm = new ChatMessageExt1();
		cm.setMessage(myObject.getMessage());
		cm.setName(myObject.getName());
		cm.setFlag(myObject.getFlag());
		cm.setlastX(myObject.getlastX());
		cm.setlastY(myObject.getlastY());
		cm.setx(myObject.getx());
		cm.sety(myObject.gety());
		cm.setId(this.ThreadId);
		try{
			handler.out.writeObject(cm);
			//System.out.println("Writing to handler outputstream: " + cm.getMessage());
		}catch(IOException ioe){
			System.out.println("Expception is FLAG 3"+ioe);          //FLAG 3
			//one of the other handlers hung up
			left = handler; // remove that handler from the arraylist
		}
	}
	handlers.remove(left);
	System.out.println("Number of handlers: " + handlers.size());
}

public synchronized void giveHist(){

	for(ChatMessageExt1 historys:history){

		try{
			historys.setFlag("HIST");
			this.out.writeObject(historys);
			//System.out.println("Writing to handler outputstream: " + myObject.getMessage());
		}catch(IOException ioe){
			System.out.println("Expception is FLAG 3"+ioe);          //FLAG 3
			//one of the other handlers hung up

		}
	}

}

public synchronized void giveStat(){
	//System.out.println("The name arrived = "+myObject.getName());
	//System.out.println("Give Status");             //Status
	if(myObject.getFlag().equals("Connect")){
		//System.out.println("The name arrived inside = "+myObject.getName());
		this.Cname=myObject.getName();
		ChatMessageExt1 temp = new ChatMessageExt1();
		temp.setFlag("ThreadId");
		temp.setId(this.ThreadId);
		myObject.setMessage(" joined the room.");
		broadcast();
		try{
			this.out.writeObject(temp);
		}
		catch(Exception e){
			System.out.println("Exception in flag threadid");     //ThreadId Flag
		}
	}
	else if(myObject.getFlag().equals("Disconnect")){

		myObject.setMessage(" left the room.");
		broadcast();
		handlers.remove(this);
	}
	ChatHandler left = null;
	for(ChatMessageExt1 dhiterator: drawhistory){

		try{
			dhiterator.setFlag("DRAW");
			this.out.writeObject(dhiterator);
		}catch(IOException ioe){
			System.out.println("Expception is FLAG 3"+ioe);          //FLAG 3
			//one of the other handlers hung up

		}
	}
	for(ChatHandler handler : handlers){
		int i=0;
		for(ChatHandler handler1 : handlers){
			ChatMessageExt1 cm = new ChatMessageExt1();
			if(handler1==this){
				if(myObject.getFlag().equals("Disconnect")){
					continue;
				}
			}
			if(i==0){
				cm.setFlag("STAT1");
				++i;
			}
			else{
				cm.setFlag("STAT");
			}
			cm.setMessage("");
			cm.setName(handler1.Cname);
			try{
				handler.out.writeObject(cm);
			}catch(IOException ioe){
				System.out.println("Expception is FLAG 3"+ioe);          //FLAG 3
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
	}
	handlers.remove(left);
	System.out.println("Number of handlers: " + handlers.size());
}


public void run()
{ 
	try{ 	
		while(!done){
			myObject = (ChatMessageExt1)in.readObject();
			if(myObject.getFlag().equals("HIST")){
				giveHist();
			}
			else if((myObject.getFlag().equals("Connect"))||(myObject.getFlag().equals("LOGOUT"))){
				giveStat();
			}
			else{
				broadcast();
			}
		}			    
	} catch (IOException e){  
		if(e.getMessage().equals("Connection reset")){
			System.out.println("Expception is FLAG 4:A client terminated its connection.");  //FLAG 4
		}else{
			System.out.println("Expception is FLAG 5:Problem receiving: " + e.getMessage());  //FLAG 5
		}
	}catch(ClassNotFoundException cnfe){
		System.out.println("Expception is FLAG 6"+cnfe.getMessage());   //FLAG 6
	}catch(Exception e){
		System.out.println("Exception is FLAG 7"+e);           //FLAG 7 
	}finally{
		System.out.println("Reached finally");     //FINALLY
		handlers.remove(this);
	}
}

ChatMessageExt1 myObject = null;
private Socket incoming;
String Cname;

boolean done = false;
ArrayList<ChatHandler> handlers;
ArrayList<ChatMessageExt1> history;
ArrayList<ChatMessageExt1> drawhistory;
int ThreadId;

ObjectOutputStream out;
ObjectInputStream in;
}

