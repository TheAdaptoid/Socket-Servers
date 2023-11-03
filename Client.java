import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Multi-Threaded Client
 */
public class Client {
    
    public static void main(String[] args) throws InterruptedException {
        //Setup and prep
        System.out.print("Booting Client...");
        Scanner userInput = new Scanner(System.in);
        int userCommand = 0;

        //Get server IP
        System.out.print("\nEnter server IP or Network address: ");
        String serverIP = userInput.nextLine();

        //Get server port
        System.out.print("Enter server port. Range 1025-4998: ");
        int serverPort = userInput.nextInt();

        while (true) {
            //Get user command
            System.out.print(
            "\nSelect a command:" + 
            "\n0 - Date/Time" +
            "\n1 - Uptime" +
            "\n2 - Memory Usage" +
            "\n3 - Network Connections" +
            "\n4 - User List" +
            "\n5 - Process List" +
            "\n6 - exit" +
            "\nEnter command (int): ");
            userCommand = userInput.nextInt();
            
            //Exit case
            if (userCommand == 6) {
                break;
            }

            //Get number of requests to generate
            System.out.print(
            "\nSelect the number of requests to generate:" +
            "\n 1, 5, 10, 15, 20, 25" +
            "\nEnter number of requests (int): ");
            int requestCount = userInput.nextInt();

            //Generate threaded requests
            ArrayList<Thread> threadsArray = new ArrayList<>();
            ArrayList<Long> requestTimes = new ArrayList<>();
            for (int i = 0; i < requestCount; i++) {
                final int localUserCommand = userCommand;
                Thread thread = new Thread(() -> CreateRequest(serverIP, serverPort, localUserCommand));
                threadsArray.add(thread);
                thread.start();
                requestTimes.add(System.currentTimeMillis());
            }

            //Collect all threads
            for (Thread thread : threadsArray) {
                thread.join();
                long calculatedTime = System.currentTimeMillis() - requestTimes.get(threadsArray.indexOf(thread));
                requestTimes.set(threadsArray.indexOf(thread), calculatedTime);
            }

            //Calculate thread times
            long totalThreadTime = 0;
            for (long time : requestTimes) {
                totalThreadTime += time;
            }
            float averageThreadTime = totalThreadTime / requestCount;
            System.out.println("Average thread time: " + averageThreadTime + " ms");
            System.out.println("Total thread time: " + totalThreadTime + " ms");

        }

        //Close client socket
        System.out.println("Closing client socket...");
        userInput.close();
    }

    /**
     * Creates a request to the server with the given server IP, server port, and data request.
     *
     * @param  serverIP    the IP address of the server
     * @param  serverPort  the port number of the server
     * @param  dataRequest the data request to send to the server
     */
    private static void CreateRequest(String serverIP, int serverPort, int dataRequest) {        
        try {
            //Create client socket
            Socket clientSocket = new Socket(serverIP, serverPort);

            //Send data request to server
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println(dataRequest);

            //Get response from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String serverResponse = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                serverResponse += line + "\n";
            }

            //Report response
            System.out.println(serverResponse);

            //Close client socket
            clientSocket.close();

        } catch (UnknownHostException e) {
            System.out.println("Error occurred. Could not create client socket.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error occurred. Could not create client socket.");
            e.printStackTrace();
        }
    }
}
