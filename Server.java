import java.io.*; 
import java.util.*; 
import java.net.*; 

public class Server 
{ 

	static Vector<ClientHandler> ar = new Vector<>(); 
	
	static int i = 0; 

	public static void main(String[] args) throws IOException 
	{ 
		ServerSocket ss = new ServerSocket(1234); 
		
		Socket s; 
		while (true) 
		{ 
			s = ss.accept(); 
			System.out.println("New client request received : " + s);
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
			System.out.println("Creating a new handler for this client...");
			String name = "";
			try { 
				name = dis.readUTF(); 
			} catch (IOException e) { 
				e.printStackTrace(); 
			}
			System.out.println("Client's name: " + name);
			ClientHandler mtch = new ClientHandler(s, name, dis, dos); 

			Thread t = new Thread(mtch); 
			
			System.out.println("Thread for user " + name + " is created on " + (new Date()).toString()); 

			ar.add(mtch); 

			t.start(); 
			i++; 

		} 
	} 
} 

class ClientHandler implements Runnable 
{ 
	Scanner scn = new Scanner(System.in); 
	private String name; 
	final DataInputStream dis; 
	final DataOutputStream dos; 
	Socket s; 
	boolean isloggedin; 
	
	public ClientHandler(Socket s, String name, 
							DataInputStream dis, DataOutputStream dos) { 
		this.dis = dis; 
		this.dos = dos; 
		this.name = name; 
		this.s = s; 
		this.isloggedin=true; 
	} 

	@Override
	public void run() { 

		String received; 
		while (true) 
		{ 
			try
			{  
				received = dis.readUTF(); 
				
				System.out.println(received); 
				
				StringTokenizer st = new StringTokenizer(received, "#"); 
				String MsgToSend = st.nextToken(); 
				
				String recipient = st.nextToken();
				if(MsgToSend.contains(" has entered the chat")){
					for (ClientHandler mc: Server.ar)
					{
						mc.dos.writeUTF(this.name + " has entered the chat");
					}
				}
				
				else if(MsgToSend.equals("logout")){ 
					this.isloggedin=false; 
					for (ClientHandler mc : Server.ar) 
					{ 
						if ((!mc.name.equals(recipient))) 
					{ 
						mc.dos.writeUTF(this.name+" has left the conversation" + "\n");
					} 
				} 
					this.s.close(); 
					break; 
				} 
				else{
					for (ClientHandler mc : Server.ar) 
					{ 
						if ((!mc.name.equals(recipient)) && mc.isloggedin==true) 
						{ 
							mc.dos.writeUTF(this.name+"> "+MsgToSend);
						} 
					} 
				}
			} catch (IOException e) { 
				
				e.printStackTrace(); 
			} 
			
		} 
		try
		{ 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 
