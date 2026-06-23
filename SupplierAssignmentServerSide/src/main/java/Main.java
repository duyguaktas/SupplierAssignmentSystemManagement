import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        int port = 8080;

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter port number: ");

            if (sc.hasNextInt()) {
                port = sc.nextInt();
            }
        } catch (Exception e) {
            System.out.println("Console input timed out or not supported by IDE runner. Defaulting to port: " + port);
        }

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);

            Socket socket = server.accept();
            System.out.println("Client connected");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = in.readLine();
            System.out.println("Received: " + message);

            String jsonOut = getString(message);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Sending: " + jsonOut);
            out.println(jsonOut);
            System.out.println("Assignments sent back to client.");

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private static String getString(String message) {
        Gson gson = new Gson();
        new GsonBuilder().setPrettyPrinting().create();

        Type type = new TypeToken<Map<String, List<Supplier>>>(){}.getType();
        Map<String, List<Supplier>> data = gson.fromJson(message, type);
        List<Supplier> suppliers = data.get("suppliers");

        AssignmentGenerator generator = new AssignmentGenerator();
        List<Assignment> assignmentsOfTheMonth = generator.generateAssignments(suppliers);
        Map<String, List<Assignment>> response = new HashMap<>();
        response.put("assignments", assignmentsOfTheMonth);
        String jsonOut = gson.toJson(response);
        return jsonOut;
    }

}
