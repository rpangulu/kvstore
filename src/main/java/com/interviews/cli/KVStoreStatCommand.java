package com.interviews.cli;

import com.google.protobuf.Empty;
import com.interviews.kvstore.KeyValServiceGrpc;
import com.interviews.kvstore.Kv;
import picocli.CommandLine;

@CommandLine.Command(
        name = "metrics"
)
public class KVStoreStatCommand implements Runnable {

    KeyValServiceGrpc.KeyValServiceBlockingStub stub = null;

    public KVStoreStatCommand(KeyValServiceGrpc.KeyValServiceBlockingStub stub) {
        this.stub = stub;
    }
    @Override
    public void run() {
        Empty request = Empty.newBuilder().build();
        Kv.Stat response =
                stub.getMetrics(request);
        System.out.println("Metrics=>");
        System.out.println("Keys Count: " + response.getKcnt());
        System.out.println("Values Size(In Bytes): " + response.getVsize());
        System.out.println("Get Ops Count: " + response.getGetcnt());
        System.out.println("Set Ops Count: " + response.getSetcnt());
        System.out.println("Delete Ops Count: " + response.getDelcnt());
    }
}
