package com.interviews.kvstore;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import com.interviews.cli.*;
import io.grpc.*;
import picocli.CommandLine;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
        // Channel is the abstraction to connect to a service endpoint
        // Let's use plaintext communication because we don't have certs
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
                .usePlaintext(true)
                .build();
        KeyValServiceGrpc.KeyValServiceBlockingStub stub = KeyValServiceGrpc.newBlockingStub(channel);

        CommandLine commandLine = new CommandLine(new KVStoreCommand());
        commandLine.addSubcommand("set", new KVStoreSetCommand(stub));
        commandLine.addSubcommand("get", new KVStoreGetCommand(stub));
        commandLine.addSubcommand("delete", new KVStoreDeleteCommand(stub));
        commandLine.addSubcommand("metrics", new KVStoreStatCommand(stub));

        commandLine.parseWithHandler(new CommandLine.RunLast(), args);

        channel.shutdownNow();
    }





}
