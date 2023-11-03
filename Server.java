import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Iterative Server
 */
public class Server {

    public static void main(String[] args) {
        //Setup and prep
        System.out.println("Booting Server...");
        Scanner userInput = new Scanner(System.in);
        Process process = null;
        BufferedReader processOut = null;
        String currentStreamline = "";

        //Get port to observe
        System.out.println("Enter port to observe. Range 1025-4998: ");
        int observedPort = userInput.nextInt();

        try {
            //Create socket server
            System.out.println("Creating socket for port " + observedPort + "...");
            ServerSocket serverSocket = new ServerSocket(observedPort);
            System.out.println("Socket created.");

            while (true) {
                //Take in input from observed port
                System.out.println("Waiting for client requests...");
                Socket currentRequest = serverSocket.accept();
                long requestStartTime = System.currentTimeMillis();
                String requestClientInfo = currentRequest.getInetAddress().getHostAddress() + ":" + currentRequest.getPort();
                
                //Determine data request type
                BufferedReader reader = new BufferedReader(new InputStreamReader(currentRequest.getInputStream()));
                int dataRequest = Integer.parseInt(reader.readLine());

                String requestResponse = "";
                switch (dataRequest) {
                    case 0:
                        //DateTime request
                        System.out.println("Date and time request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"date"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Date/Time: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    case 1:
                        //Uptime request
                        System.out.println("Uptime request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"uptime"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Uptime: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    case 2:
                        //Memory request
                        System.out.println("Memory usage request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"free", "-m"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Memory Usage: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    case 3:
                        //Netstat request
                        System.out.println("Netstat request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"netstat"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Network Connections: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    case 4:
                        //User list request
                        System.out.println("User list request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"who"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Current Users: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    case 5:
                        //Process list request
                        System.out.println("Process list request received from: " + requestClientInfo);
                        process = Runtime.getRuntime().exec(args = new String[]{"ps"});
                        processOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        requestResponse = "Current Processes: \n";
                        currentStreamline = "";
                        while ((currentStreamline = processOut.readLine()) != null) {
                            requestResponse += currentStreamline + "\n";
                        }
                        processOut.close();
                        break;
                    default:
                        //Invalid request
                        System.out.println("Invalid request received from: " + requestClientInfo);
                        requestResponse = "Invalid request sent.";
                        break;
                }

                //Send response
                PrintWriter writer = new PrintWriter(currentRequest.getOutputStream(), true);
                writer.println(requestResponse);

                //Close request
                currentRequest.close();

                //Report response time
                long requestEndTime = System.currentTimeMillis() - requestStartTime;
                System.out.println("Response time: " + requestEndTime + " ms");
            }

        } catch (IOException e) {
            System.out.println("Error occurred. Could not create socket.");
            e.printStackTrace();
        }

        //Shutdown server
        System.out.println("Shutting down server...");
        userInput.close();
    }
}