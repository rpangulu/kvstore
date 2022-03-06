package com.interviews.cli;

import com.interviews.kvstore.KeyValServiceGrpc;
import com.interviews.kvstore.Kv;
import picocli.CommandLine;

public class KVStoreGetCommand implements Runnable {
    @CommandLine.Option(names = "-k")
    String[] keys;

    KeyValServiceGrpc.KeyValServiceBlockingStub stub = null;

    public KVStoreGetCommand(KeyValServiceGrpc.KeyValServiceBlockingStub stub) {
        this.stub = stub;
    }
    @Override
    public void run() {
        if (keys == null || keys.length == 0) {
            System.out.println("Error: Missing required argument(s): -k=<key>");
            return;
        }
        if (keys.length == 1) {
            Kv.Key keyRequest = Kv.Key.newBuilder().setKey(keys[0]).build();
            Kv.Item response =
                    stub.get(keyRequest);
            System.out.println("Response=>");
            System.out.println(response);
        } else if (keys.length > 1) {
            Kv.AllKeys.Builder builder = Kv.AllKeys.newBuilder();
            for (String key: keys) {
                builder.addKeys(Kv.Key.newBuilder().setKey(key).build());
            }
            Kv.AllItems response =
                    stub.getAll(builder.build());
            System.out.println("Response=>");
            System.out.println(response);
        }
    }
}
