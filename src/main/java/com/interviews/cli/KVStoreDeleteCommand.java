package com.interviews.cli;

import com.interviews.kvstore.KeyValServiceGrpc;
import com.interviews.kvstore.Kv;
import picocli.CommandLine;

public class KVStoreDeleteCommand implements Runnable {
    @CommandLine.Option(names = "-k")
    String[] keys;

    KeyValServiceGrpc.KeyValServiceBlockingStub stub = null;

    public KVStoreDeleteCommand(KeyValServiceGrpc.KeyValServiceBlockingStub stub) {
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
            Kv.Success response =
                    stub.delete(keyRequest);
            System.out.println("Response=>" + response);
        } else if (keys.length > 1) {
            Kv.AllKeys.Builder builder = Kv.AllKeys.newBuilder();
            for (String key: keys) {
                builder.addKeys(Kv.Key.newBuilder().setKey(key).build());
            }
            Kv.Success response =
                    stub.deleteAll(builder.build());
            System.out.println("Response=>" + response);
        }
    }
}
