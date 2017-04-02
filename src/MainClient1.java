import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class MainClient1 implements ActionListener{
	String Name;
	ChatMessageExt1 myObject;
	boolean sendingdone = false, recievingdone = false;
	Scanner scan;
	Socket socketToServer;
	ObjectOutputStream myOutputStream;
	ObjectInputStream myInputStream;
	JFrame f;
	JTextField tf;
	JTextArea ta;
	JTextArea hta;
	JButton hbutton;
	JPanel hpan;
	JTextPane Shead;
	JTextPane Sactive;
	JPanel span;
	JList<String> StatList;
	JButton logout;
	Container Content;
	JScrollPane sp;
	JScrollPane hsp;
	JScrollPane ListScroller;
	Vector<String> v;
	JPanel CenterPanel;
	JPanel Draw;
	ArrayList<cord> cord1;
	JPanel MCenterPanel;
	JPanel ButtonPanel;
	JButton Clear;
	JButton Red;
	JButton Green;
	JButton Blue;
	JButton Cyan;
	JButton Yellow;

	protected int lastx=0, lasty=0;  ////Change made

	private class PositionRecorder extends MouseAdapter {
		public void mouseEntered(MouseEvent event) {
			record(event.getX(), event.getY());
		}  

		public void mousePressed(MouseEvent event) {
			record(event.getX(), event.getY());
		}
	}

	private class LineDrawer extends MouseMotionAdapter {
		public void mouseDragged(MouseEvent event) {
			int x = event.getX();
			int y = event.getY();
			myObject = new ChatMessageExt1();
			myObject.setMessage("");
			myObject.setlastX(lastx);
			myObject.setlastY(lasty);
			myObject.setx(x);
			myObject.sety(y);
			record(x, y);
			myObject.setFlag("DRAW");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}

		}
	}

	protected void record(int x, int y) {
		lastx = x;
		lasty = y;
	}

	public MainClient1(){	
		cord1 = new ArrayList<cord>();
		System.out.println("reached client");
		f = new JFrame();
		Content = f.getContentPane();
		f.setSize(300,400);
		f.setTitle("Chat Client");
		f.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				myObject = new ChatMessageExt1();
				myObject.setMessage("Logged Out");
				myObject.setName(Name);
				myObject.setFlag("Disconnect");
				try{
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);
				}catch(IOException ioe){
					System.out.println(ioe.getMessage());
				}
				System.exit(0);
			}
		});
		tf = new JTextField();
		tf.addActionListener(this);
		Content.add(tf, BorderLayout.NORTH);
		ta = new JTextArea();
		ta.setEditable(false);
		sp = new JScrollPane(ta);
		CenterPanel = new JPanel();
		CenterPanel.setLayout(new GridLayout(2,1));
		Draw = new JPanel(){
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				for(cord cord2: cord1){ 
					g.drawLine(cord2.getlastX(), cord2.getlastY(), cord2.getx(), cord2.gety());

				}
			}
		};
		Draw.setBackground(Color.white);
		Draw.addMouseListener(new PositionRecorder());
		Draw.addMouseMotionListener(new LineDrawer());
		CenterPanel.add(sp);
		CenterPanel.add(Draw);
		MCenterPanel = new JPanel();
		Clear = new JButton("CLEAR BOARD");
		Red = new JButton("RED");
		Green = new JButton("GREEN");
		Blue = new JButton("BLUE");
		Cyan = new JButton("CYAN");
		Yellow = new JButton("YELLOW");
		Clear.addActionListener(this);
		Clear.setActionCommand("clear");
		Red.addActionListener(this);
		Red.setActionCommand("red");
		Green.addActionListener(this);
		Green.setActionCommand("green");
		Blue.addActionListener(this);
		Blue.setActionCommand("blue");
		Cyan.addActionListener(this);
		Cyan.setActionCommand("cyan");
		Yellow.addActionListener(this);
		Yellow.setActionCommand("yellow");
		MCenterPanel.setLayout(new BorderLayout());
		MCenterPanel.add(CenterPanel, BorderLayout.CENTER);
		ButtonPanel = new JPanel();
		ButtonPanel.setLayout(new GridLayout());
		ButtonPanel.add(Red);
		ButtonPanel.add(Green);
		ButtonPanel.add(Blue);
		ButtonPanel.add(Cyan);
		ButtonPanel.add(Yellow);
		ButtonPanel.add(Clear);
		MCenterPanel.add(ButtonPanel, BorderLayout.SOUTH);
		Content.add(MCenterPanel, BorderLayout.CENTER);
		hta = new JTextArea();
		hta.setEditable(false);
		hsp = new JScrollPane(hta);
		hta.setBackground(Color.LIGHT_GRAY);
		hbutton = new JButton("HISTORY");
		hbutton.setActionCommand("history");
		hbutton.addActionListener(this);
		hpan = new JPanel();
		hpan.setLayout(new BorderLayout());
		hpan.add(hbutton, BorderLayout.NORTH);
		hpan.add(hsp, BorderLayout.CENTER);
		v=new Vector<String>();
		StatList = new JList<String>(v);
		StatList.setLayoutOrientation(JList.VERTICAL_WRAP);
		ListScroller = new JScrollPane(StatList);
		Shead = new JTextPane();
		Shead.setText("  ACTIVE  ");
		span = new JPanel();
		span.setLayout(new BorderLayout());
		span.add(Shead, BorderLayout.NORTH);
		span.add(ListScroller, BorderLayout.CENTER);
		Content.add(span,BorderLayout.WEST);  
		Content.add(hpan,BorderLayout.EAST); 
		logout = new JButton("Connect");
		logout.setActionCommand("log");
		logout.addActionListener(this);
		Content.add(logout, BorderLayout.SOUTH);
		f.setVisible(true);
	}
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equals("log")){
			if(recievingdone){
				hta.setText("");
				v.removeAllElements();
				StatList.setListData(v);
				f.invalidate();
				f.validate();
				cord1.clear();
				ta.setText("");
				tf.setText("");
				logout.setText("Connect");
				System.out.println("logging out");
				myObject = new ChatMessageExt1();
				myObject.setMessage("Logged Out");
				myObject.setName(Name);
				myObject.setFlag("Disconnect");
				try{
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);

				}catch(IOException ioe){
					System.out.println(ioe.getMessage());
				}
				finally{
					recievingdone=false;
				}
			}
			else{
				recievingdone=true;
				Name = tf.getText();
				tf.setText("");
				logout.setText("Disconnect");
				Client5 MThread = new Client5(this);
			}
		}
		else if(ae.getActionCommand().equals("history")){
			hta.setText("");
			myObject = new ChatMessageExt1();
			myObject.setMessage("");
			myObject.setFlag("HIST");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("clear")){
			myObject = new ChatMessageExt1();
			myObject.setMessage("");
			myObject.setFlag("CLEAR");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);       
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("red")){
			myObject = new ChatMessageExt1();
			myObject.setFlag("red");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("green")){
			myObject = new ChatMessageExt1();
			myObject.setFlag("green");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);       
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("blue")){
			myObject = new ChatMessageExt1();
			myObject.setFlag("blue");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);       
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("cyan")){
			myObject = new ChatMessageExt1();
			myObject.setFlag("cyan");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);       
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else if(ae.getActionCommand().equals("yellow")){
			myObject = new ChatMessageExt1();
			myObject.setFlag("yellow");
			myObject.setName(Name);
			try{
				myOutputStream.reset();
				myOutputStream.writeObject(myObject);       
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		else{
			if(recievingdone){
				myObject = new ChatMessageExt1();
				myObject.setMessage(tf.getText());
				myObject.setName(Name);
				myObject.setFlag("message");
				tf.setText("");
				try{
					myOutputStream.reset();
					myOutputStream.writeObject(myObject);
				}catch(IOException ioe){
					System.out.println(ioe.getMessage());
				}
			}
		}
	}
	public static void main(String[] arg){
		MainClient1 c = new MainClient1();
	}
} 

class cord{
	int lastX,lastY,x,y;
	public void setval(int lastX,int lastY,int x,int y){
		this.lastX = lastX;
		this.lastY = lastY;
		this.x = x;
		this.y = y;
	}

	public int getlastX(){
		return lastX;
	}
	public int getlastY(){
		return lastY;
	}
	public int getx(){
		return x;
	}
	public int gety(){
		return y;
	}
}

class Client5 extends Thread{
	int tid;
	MainClient1 m;
	ObjectOutputStream myOutputStream1;
	ObjectInputStream myInputStream1;
	Socket SocketToServer1;
	ChatMessageExt1 myObject1;
	public Client5(MainClient1 obj){
		m=obj;
		try{					
			m.socketToServer = new Socket("127.0.0.1", 4000);        //THIS IS WHERE THE CONNECTION IS HANDLED
			m.myOutputStream = new ObjectOutputStream(m.socketToServer.getOutputStream());
			m.myInputStream = new ObjectInputStream(m.socketToServer.getInputStream());
			m.myObject = new ChatMessageExt1();
			m.myObject.setMessage("");
			m.myObject.setFlag("Connect");
			m.myObject.setName(m.Name);

			try{
				m.myOutputStream.reset();
				m.myOutputStream.writeObject(m.myObject);
			}catch(IOException ioe){
				System.out.println(ioe.getMessage());
			}
		}
		catch(Exception e){
			System.out.println(e.getMessage());	
		}

		start();
	}    
	public void run(){
		System.out.println("Listening for messages from server . . . ");
		try{ 
			while(m.recievingdone){
				m.myObject = (ChatMessageExt1)m.myInputStream.readObject();
				if(m.myObject.getFlag().equals("HIST")){
					m.hta.append(m.myObject.getName() + ": "+m.myObject.getMessage() + "\n");
				}
				else if(m.myObject.getFlag().equals("STAT1")){
					m.v.removeAllElements();
					//if(m.myObject.getName().equals(m.Name)){
					//m.StatList.setListData(m.v);
					//m.f.invalidate();
					//m.f.validate();
					//continue;
					//}
					m.v.add(m.myObject.getName());
					m.StatList.setListData(m.v);
					m.f.invalidate();
					m.f.validate();
				} 
				else if(m.myObject.getFlag().equals("STAT")){
					//if(m.myObject.getName().equals(m.Name)){
					//continue;
					//}
					if(m.myObject!=null){
						m.v.add(m.myObject.getName());
					}
					m.StatList.setListData(m.v);
					m.f.invalidate();
					m.f.validate();
				} 
				else if(m.myObject.getFlag().equals("CLEAR")){
					m.cord1.clear();
					m.Draw.repaint();
				} 
			 
				else if(m.myObject.getFlag().equals("DRAW")){
					cord temp = new cord();
					temp.setval(m.myObject.getlastX(),m.myObject.getlastY(),m.myObject.getx(),m.myObject.gety());
					m.cord1.add(temp);
					m.Draw.repaint();
				}   
				else if(m.myObject.getFlag().equals("ThreadId")){
					tid = m.myObject.getId();
				}
				else{
					if(m.myObject.getId()==tid){
						m.ta.append("You: "+m.myObject.getMessage() + "\n");
					}
					else{
						m.ta.append(m.myObject.getName() + ": "+m.myObject.getMessage() + "\n");
					}
				}
			}
		}catch(IOException ioe){
			System.out.println("IOE: " + ioe.getMessage());
		}catch(ClassNotFoundException cnf){
			System.out.println(cnf.getMessage());
		}
	}
}
