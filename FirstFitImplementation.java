import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MyClient {

    public static void main(String[] args) {
        String jobStatus = "";
        int jobID = -1;
        int jobCore = -1;
        int jobMemory = -1;
        int jobDisk = -1;

        int numberOfServers = -1;
        String serverSize = "";
        int serverID = -1;
        String serverStatus = "";
        int serverCore = -1;
        int serverMemory = -1;
        int serverDisk = -1;
        String[] serverInfo;

        try {
            Socket socket = new Socket("127.0.0.1", 50000);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            output.write(("HELO\n").getBytes());
            output.flush();

            String received = input.readLine(); // Read the server's response

            String username = System.getProperty("user.name");
            output.write(("AUTH " + username + "\n").getBytes());
            output.flush();

            received = input.readLine(); // Read the server's response

            output.write(("REDY\n").getBytes());
            output.flush();

            received = input.readLine(); // Read the server's response

            String[] jobInfo = received.split(" ");
            jobStatus = jobInfo[0]; // Set the job status

            while (!jobStatus.equals("NONE")) {
                if (jobInfo[0].equals("JOBN")) { // Check if the server has sent a job
                    jobID = Integer.parseInt(jobInfo[2]);
                    jobCore = Integer.parseInt(jobInfo[4]);
                    jobMemory = Integer.parseInt(jobInfo[5]);
                    jobDisk = Integer.parseInt(jobInfo[6]);

                    output.write(("GETS Available " + jobCore + " " + jobMemory + " " + jobDisk + "\n").getBytes());
                    output.flush();

                    received = input.readLine(); // Read the server's response

                    String[] serverData = received.split(" ");
                    numberOfServers = Integer.parseInt(serverData[1]); // Record the number of available servers

                    if (numberOfServers == 0) { // If no available servers, check which servers are capable
                        output.write(("OK\n").getBytes());
                        output.flush();

                        received = input.readLine(); // Read the server's response

                        output.write(("GETS Capable " + jobCore + " " + jobMemory + " " + jobDisk + "\n").getBytes());
                        output.flush();

                        received = input.readLine(); // Read the server's response
                        serverData = received.split(" ");
                        numberOfServers = Integer.parseInt(serverData[1]); // Record the number of capable servers
                    }

                    output.write(("OK\n").getBytes());
                    output.flush();

                    for (int i = 0; i < numberOfServers; i++) {
                        received = input.readLine(); // Read the server's response
                        if (i == 0) {
                            serverInfo = received.split(" ");

                            serverSize = serverInfo[0];
                            serverID = Integer.parseInt(serverInfo[1]);
                            serverStatus = serverInfo[2];
                            serverCore = Integer.parseInt(serverInfo[4]);
                            serverMemory = Integer.parseInt(serverInfo[5]);
                            serverDisk = Integer.parseInt(serverInfo[6]);
                        }
                    }

                    output.write(("OK\n").getBytes());
                    output.flush();

                    received = input.readLine(); // Read the server's response

                    output.write(("SCHD " + jobID + " " + serverSize + " " + serverID + "\n").getBytes());
                    output.flush();

                    received = input.readLine(); // Read the server's response

                }else if (jobStatus.equals("NONE")) { // Check if there are no more jobs
                    output.write(("QUIT\n").getBytes());
                    output.flush();

                    received = input.readLine(); // Read the server's response
                    break; // Exit the while loop
                }

                output.write(("REDY\n").getBytes());
                output.flush();

                received = input.readLine(); // Read the server's response

                if (received != null && !received.isEmpty()) {
                    jobInfo = received.split(" ");
                    jobStatus = jobInfo[0];
                }
            }

            output.write(("QUIT\n").getBytes());
            output.flush();

            received = input.readLine();

            input.close();
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
