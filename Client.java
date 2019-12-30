
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.event.EventHandler;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.shape.Rectangle;
import static javafx.scene.paint.Color.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.shape.*;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client extends Application 
{ 
	final static int ServerPort = 1234; 
	private TextArea tfMessages = new TextArea();
	private TextField tfMessage = new TextField();
	Stage chatStage;
	Thread readMessage;
	public static String name;
	public void start(Stage primaryStage) throws UnknownHostException, IOException 
	{ 
		TextField tfName = new TextField();
		Label lbHandle = new Label("Enter a handle: ");
		HBox pane = new HBox(5);
		pane.getChildren().add(lbHandle);
		pane.getChildren().add(tfName);
		pane.setAlignment(Pos.CENTER);
		
		tfMessages.setEditable(false);
		tfMessages.setPrefHeight(500);
		tfMessages.setPrefWidth(500);
		tfMessage.setPrefWidth(500);
		tfMessage.setPrefHeight(100);
		VBox box = new VBox(tfMessages, tfMessage);
		primaryStage.setScene(new Scene(pane, 300, 100));
		primaryStage.setTitle("Start Chat");
		primaryStage.show();
		Scanner scn = new Scanner(System.in); 
		
		InetAddress ip = InetAddress.getByName("localhost"); 
		
		
		Socket s = new Socket(ip, ServerPort); 
		
		DataInputStream dis = new DataInputStream(s.getInputStream()); 
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		tfName.setOnKeyReleased(e -> {
			   if (e.getCode() == KeyCode.ENTER) {
				   try { 
					name = tfName.getText();
						dos.writeUTF(name); 
					} catch (IOException ev) { 
						ev.printStackTrace(); 
					} 
					try{
						dos.writeUTF(name + " has entered the chat#"+name);
					} catch (IOException ev) {
						ev.printStackTrace();
					}
					Stage chatStage = new Stage();
					primaryStage.close();
					chatStage.setScene(new Scene(box, 600, 500));
					chatStage.setTitle(name);
					chatStage.show();
					chatStage.setOnCloseRequest(ev->{
						try { 
							dos.writeUTF("logout#"+name); 
						} catch (IOException ex) { 
							ex.printStackTrace(); 
						} 
					});
					tfMessage.setOnKeyReleased(ev -> 
					{ 
						if (ev.getCode() == KeyCode.ENTER) { 
								if (tfMessage.getText().toLowerCase().equals("logout")) chatStage.close();
								String msg = tfMessage.getText() + "#" + name; 
								tfMessages.appendText(name + "> " + msg.replaceAll("#" + name, "")+"\n");
								try { 
									dos.writeUTF(msg); 
								} catch (IOException ex) { 
									ex.printStackTrace(); 
								} 
								tfMessage.clear();
								
							}  
					}); 
					
					readMessage = new Thread(new Runnable() 
					{ 
						@Override
						public void run() { 

							while (true) { 
								try { 
									String msg = dis.readUTF(); 
									tfMessages.appendText(msg+"\n"); 
								} catch (IOException e) { 

									e.printStackTrace();
								} 
							} 
						} 
					}); 
					readMessage.start(); 
			   }					
		});
		
	} 
} 
