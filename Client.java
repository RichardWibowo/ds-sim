import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        String serverType = null;
        int maxCore = 0;
        int serverID = 0;
        int counter = 0;

        try {
            Socket s = new Socket("localhost", 50000);

            DataOutputStream dout = new DataOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

            // HELO initial handshake
            dout.write(("HELO\n").getBytes());
            dout.flush();
            String str = in.readLine();
            // System.out.println("Server message= "+str);

            // AUTH phase
            String name = System.getProperty("user.name");
            dout.write(("AUTH " + name + "\n").getBytes());
            dout.flush();
            str = in.readLine();
            // System.out.println("Server message= "+str);

            // READY phase
            dout.write(("REDY\n").getBytes());
            dout.flush();
            str = in.readLine();
            // parse job 
            String[] jobInfo = str.split(" ");
            int jobID = Integer.parseInt(jobInfo[2]);
            dout.flush();
            //System.out.println("Server message= "+str);

            // GETS phase to identify largest server type
            dout.write(("GETS All\n").getBytes());
            dout.flush();
            str = in.readLine();
            //parse Gets ALL
            String[] Data  = str.split(" ");
            int nRecs =  Integer.parseInt(Data[1]);

            //System.out.println("Server message = " + str);
            String previousServerType = "";

            // parse server information to find largest server type
            dout.write(("OK\n").getBytes());
            
            for(int i = 0; i!=nRecs; i++) {
                str = in.readLine();

                String[] serverInfo = str.split(" ");
                String type = serverInfo[0];
                int ID = Integer.parseInt(serverInfo[1]);
                int core = Integer.parseInt(serverInfo[4]);

                if (core > maxCore) {
                    serverType = type;
                    maxCore = core;
                    serverID = ID;
                    //System.out.println(serverType);
                    //System.out.println(maxCore);
                    //System.out.println(serverID);
                }
                if (core == maxCore && serverType.equals(type)){
                    if(serverType.equals(previousServerType)){
                        serverID = ID;
                    }else{
                        serverID = 0;
                        previousServerType = type;
                    }
                }

                dout.flush();
            }
            //finish gets all 
            dout.write(("OK\n").getBytes());
            str = in.readLine();
            dout.flush();

            //schedule the first job to avoid overlap
            String schdCommand = "SCHD " + jobID + " " + serverType + " " + counter + "\n";
            dout.write(schdCommand.getBytes());
            dout.flush();
            str = in.readLine();
            counter++;

            // SCHD phase to schedule jobs
            while (true) {
                dout.write(("REDY\n").getBytes());
                dout.flush();
                str = in.readLine();
                
                if (str.startsWith("NONE")) {
                    break;
                }

                if (str.startsWith("JOBN")) {
                    jobInfo = str.split(" ");
                    jobID = Integer.parseInt(jobInfo[2]);

                    if(counter == serverID+1){
                        counter = 0;
                    }
                    schdCommand = "SCHD " + jobID + " " + serverType + " " + counter + "\n";
                    //System.out.println(schdCommand);
                    dout.write(schdCommand.getBytes());
                    dout.flush();
                    str = in.readLine();
                    counter++;
                    

                    if (str.startsWith("OK")) {
                        continue;
                    }
                }
            }

            // QUIT phase
            dout.write(("QUIT\n").getBytes());
            dout.flush();
            str = in.readLine();
            // System.out.println("Server message = "+str);

            dout.close();
            s.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
