import java.io.*;  
import java.net.*;
import java.util.ArrayList;

public class new_client {  
public static void main(String[] args) {  

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
        ArrayList<String> server = new ArrayList<String>();
        
        while(true){
            dout.write("OK\n".getBytes());
            dout.flush();
            str=in.readLine();
            server.add(str);
            System.out.println("SERVER MSG = " + str);
        
            if(str.isEmpty()){
                break;
            }
        };  
        //SORTING needed
        
        //send jobs 
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
        

    }
    catch(Exception e){System.out.println(e);}  
}  
}  