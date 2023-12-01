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
public class TServer {

    public static void main(String[] args) throws InterruptedException {
        //Setup and prep
        System.out.println("Booting Server...");
        Scanner userInput = new Scanner(System.in);
        Boolean freeThreads = true;

        //Get port to observe
        System.out.println("Enter port to observe. Range 1025-4998: ");
        int observedPort = userInput.nextInt();

        //Get Thread limit
        System.out.println("Enter thread limit. Range 2 to 2^32: ");
        long threadLimit = userInput.nextInt();
        if (threadLimit > (long)(Math.pow(2.0, 32.0))) {
            threadLimit = (long)(Math.pow(2.0, 32.0));
        } else if (threadLimit < 2) {
            threadLimit = 2;
        }
        double threadWaitTime = ((1.0)/threadLimit) * 1000;

        try {
            //Create socket server
            System.out.println("Creating socket for port " + observedPort + "...");
            ServerSocket serverSocket = new ServerSocket(observedPort);
            System.out.println("Socket created.");

            while (true) {

                //Check if thread limit has been reached
                if (Thread.activeCount() >= threadLimit) {
                    if (freeThreads == true) {
                        System.out.println("|| Thread limit reached. Waiting " + threadWaitTime + "ms for threads to finish. ||");
                        freeThreads = false;
                    }
                    
                    //Wait for a thread to finish
                    Thread.sleep(((long)threadWaitTime));
                } else {
                    //Take in input from observed port
                    freeThreads = true;
                    System.out.println("[AT: " + Thread.activeCount() + "/" + threadLimit + "] Waiting for client requests...");
                    Socket currentRequest = serverSocket.accept();

                    //Create new Handling Thread
                    Thread handlingThread = new Thread(() -> {
                        try {
                            HandleRequest(currentRequest);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    handlingThread.start();  
                }          
            }

        } catch (IOException e) {
            System.out.println("Error occurred. Could not create socket.");
            e.printStackTrace();
        }

        //Shutdown server
        System.out.println("Shutting down server...");
        userInput.close();
    }

    private static void HandleRequest(Socket currentRequest) throws IOException {
        //Set up and prep
        Process process = null;
        BufferedReader processOut = null;
        String currentStreamline = "";
        String[] internalArgs = null;
        int dataRequest = -1;

        //Track Handling time
        long requestStartTime = System.currentTimeMillis();

        //Get client information
        String requestClientInfo = currentRequest.getInetAddress().getHostAddress() + ":" + currentRequest.getPort();

        //Determine data request type
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(currentRequest.getInputStream()));
            dataRequest = Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error occurred. Could not read request data.");
        }

        String requestResponse = "";
        switch (dataRequest) {
            case 0:
                //DateTime request
                System.out.println("Date and time request received from: " + requestClientInfo);
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"date"});
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
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"uptime"});
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
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"free", "-m"});
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
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"netstat"});
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
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"who"});
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
                process = Runtime.getRuntime().exec(internalArgs = new String[]{"ps"});
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
        System.out.println("Response time of " + requestEndTime + " ms for request from " + requestClientInfo);
    }
}