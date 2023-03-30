import java.io.*;  
import java.net.*;
import java.util.*;

public class new_client {  
    public static void main(String[] args) {  
        String Name; 
        int Core;
        //
        String NameTemp; 
        int CoreTemp;
    try{      

        Socket s=new Socket("localhost",50000);  

        DataOutputStream dout=new DataOutputStream(s.getOutputStream()); 
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //helo initial handshake
        dout.write(("HELO\n").getBytes());  
        dout.flush();  
        String  str=in.readLine();  
        System.out.println("Server message= "+str); 

        //auth phase
        String name  = System.getProperty("user.name");
        dout.write(("AUTH " + name + "\n").getBytes());  
        dout.flush(); 
        str=in.readLine();  
        System.out.println("Server message= "+str); 

        //ready phase
        dout.write(("REDY\n").getBytes());  
        dout.flush(); 
        str=in.readLine();  
        System.out.println("Server message= "+str); 

        //SCHEDULING GOES HERE
        dout.write("GETS All\n".getBytes());
        dout.flush();
        str=in.readLine();
        System.out.println("SERVER MSG = " + str);
        
        //initial data 
        dout.write("OK\n".getBytes());
            dout.flush();
            str=in.readLine();
            String serverStrings[] = str.split(" ");
            System.out.println("original Server = " + str);
            System.out.println("serverStrings = " + serverStrings[0] + " || " + serverStrings[4]);
                Name = serverStrings[0];
                Core = Integer.parseInt(serverStrings[4]);
        
        while(true){
            dout.write("OK\n".getBytes());
            dout.flush();
            str=in.readLine();
            if(str.length() > 1){
            serverStrings = str.split(" ");
            System.out.println("original Server = " + str);
            System.out.println("serverStrings = " + serverStrings[0] + " || " + serverStrings[4]);
                if(Integer.parseInt(serverStrings[4]) >= Core){
                    Name = serverStrings[0];
                }
            } 
            if(str.isEmpty()){
                break;
            }
        };  
        System.out.println("biggest Server = " + Name );
        //SORTING needed
        String schdCommand = "SCHD 0 " + Name + " 0\n";
        System.out.println(schdCommand);
        
        //send jobs 
        dout.write(schdCommand.getBytes());
        dout.write("SCHD 0 medium 0\n".getBytes());
        dout.flush();
        str=in.readLine();
        System.out.println("SERVER MSG = " + str);

        //QUIT SCHEDULING
        dout.write(("QUIT\n").getBytes());  
        dout.flush(); 
        str=in.readLine();  
        System.out.println("Server message = "+str);      

        dout.close();
        s.close();
        
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}