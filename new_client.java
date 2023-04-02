import java.io.*;  
import java.net.*;

public class new_client {  
    public static void main(String[] args) {  
        String[] nRecStrings;
        String[] serverStrings;
        String[] jobStrings;
        boolean passed = false;
        int jobID = 0;
        int nRecs = 0;
        
        //variable to stor the server creds
        int Core = 0; 
        int serverID = 0;
        String Name=null;
        int totalServer = 0;
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
        
        for(int i = 0; i > 10 ;i++){
            
            dout.write("REDY\n".getBytes());
            dout.flush();
            str=in.readLine();
            System.out.println("SERVER MSG = "+ str);
            
            for(int j = 0; j >= 2; j++){
                String schdComm =  "SCHD "+ i + " medium " + j + "\n";
                dout.write(schdComm.getBytes());
                dout.flush();
                str = in.readLine();
                System.out.println("SERVER MSG = "+ str);
                if(j == 2){
                    j = 0;
                }
            }
        }
                //record all the job 
                
                
        
            //test schedule 
                
        
                dout.write("REDY\n".getBytes());
                dout.flush();
                str=in.readLine();
                System.out.println("SERVER MSG = "+ str);
                
 

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