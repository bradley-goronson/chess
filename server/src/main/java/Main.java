import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }
            Server myServer = new Server();
            myServer.run(port);
            System.out.println("""
                Chess Server:
                java Main
                port:""" + port
                );
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}