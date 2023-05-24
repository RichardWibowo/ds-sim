import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyClient {

    private static Socket socket;
    private static DataOutputStream output;
    private static BufferedReader input;

    public static void main(String[] args) {
        try {
            connectToServer(); // Connect to the server
            performAuthentication(); // Perform authentication with the server
            processJobs(); // Process the jobs received from the server
            closeConnection(); // Close the connection
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void connectToServer() throws Exception {
        socket = new Socket("127.0.0.1", 50000); // Connect to the server
        output = new DataOutputStream(socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sendCommand("HELO"); // Send a HELO command to the server
        readResponse(); // Read and print the response from the server
    }

    private static void performAuthentication() throws Exception {
        String username = System.getProperty("user.name"); // Get the username
        sendCommand("AUTH " + username); // Send an AUTH command with the username to the server
        readResponse(); // Read and print the response from the server
    }

    private static void processJobs() throws Exception {
        sendCommand("REDY"); // Send a REDY command to request a job from the server
        String response = readResponse(); // Read the response from the server

        while (!response.equals("NONE")) { // if there is job that needs work
            if (response.startsWith("JOBN")) {  // and it starts with jobn
                processJob(response);   //process it
            } else if (response.equals("JCPL")) { // else if completed 
                // Job complete, do nothing
            }
            sendCommand("REDY"); // Send another REDY command to request the next job
            response = readResponse(); // Read the response from the server
        }
    }

    private static void processJob(String jobInfo) throws Exception {
        //take the job and parse it
        String[] jobData = jobInfo.split(" ");
        int jobID = Integer.parseInt(jobData[2]);
        int jobCore = Integer.parseInt(jobData[4]);
        int jobMemory = Integer.parseInt(jobData[5]);
        int jobDisk = Integer.parseInt(jobData[6]);

        //request server that can handle the job 
        sendCommand("GETS Available " + jobCore + " " + jobMemory + " " + jobDisk);
        String serverData = readResponse(); // read how many can handle it
        int numberOfServers = extractNumberOfServers(serverData); // total of server that can handle it

        if (numberOfServers == 0) { // if no server is capable of doing it
            sendCommand("OK"); // reset
            readResponse();
            //then get the server that is CAPABLE OF DOING IT 
            sendCommand("GETS Capable " + jobCore + " " + jobMemory + " " + jobDisk);
            serverData = readResponse(); // see how many can do it
            numberOfServers = extractNumberOfServers(serverData); // total server
        }

        sendCommand("OK"); //get server info
        String[] serverInfo = new String[7]; //prepare store all server info
        for (int i = 0; i < numberOfServers; i++) { // loop through all the server sent from ds.server
            String received = readResponse(); 
            if (i == 0) { // only pick one on the top since it is first fit
                serverInfo = received.split(" "); // parse the server 
            }
        }

        sendCommand("OK"); //reset
        readResponse();

        scheduleJob(jobID, serverInfo); // schedule it
        readResponse();
    }

    private static int extractNumberOfServers(String serverData) {
        String[] serverInfo = serverData.split(" ");
        return Integer.parseInt(serverInfo[1]);
    }

    private static void scheduleJob(int jobID, String[] serverInfo) throws Exception {
        String serverSize = serverInfo[0];
        int serverID = Integer.parseInt(serverInfo[1]);
        sendCommand("SCHD " + jobID + " " + serverSize + " " + serverID);
    }

    private static void sendCommand(String command) throws Exception {
        output.write((command + "\n").getBytes());
        output.flush();
    }

    private static String readResponse() throws Exception {
        String response = input.readLine();
        // Uncomment the line below to display the received response
        // System.out.println("RCVD: " + response);
        return response;
    }

    private static void closeConnection() throws Exception {
        sendCommand("QUIT");
        readResponse();

        input.close();
        output.close();
        socket.close();
    }
}
