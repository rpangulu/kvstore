package com.interviews.kvstore;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * KeyValueStore Server
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        // Create a new server to listen on port 8080
        Server server = ServerBuilder.forPort(8080)
                .addService(new KeyValServiceImpl())
                .build();

        // Start the server
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Server threads are running in the background.
        System.out.println("Server started");
        // Don't exit the main thread. Wait until server is terminated.
        try {
            server.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
