package com.interviews.cli;

import com.google.protobuf.ByteString;
import com.interviews.kvstore.KeyValServiceGrpc;
import com.interviews.kvstore.Kv;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class KVStoreSetCommand implements Runnable {
    @CommandLine.ArgGroup(exclusive = false, multiplicity = "1..*")
    private List<KeyVal> composites;

    KeyValServiceGrpc.KeyValServiceBlockingStub stub = null;

    static class KeyVal {
        @CommandLine.Option(names = "-k", required = true) String key;
        @CommandLine.Option(names = "-v", required = true) String val;
    }

    public KVStoreSetCommand(KeyValServiceGrpc.KeyValServiceBlockingStub stub) {
        this.stub = stub;
    }

    @Override
    public void run() {
        if (composites == null || composites.isEmpty()) {
            System.out.println("-k and -v must be specified");
            return;
        }
        int keyCnt = composites.size();
        if (keyCnt == 1) {
            Kv.Item request = Kv.Item.newBuilder().setKey(composites.get(0).key).setValue(ByteString.copyFromUtf8(composites.get(0).val)).build();
            Kv.Item response =
                    stub.set(request);
            System.out.println("Response=>");
            System.out.println(response);
        } else {

            Kv.AllItems.Builder request = Kv.AllItems.newBuilder();
            for(KeyVal kv: composites) {
                request.addItems(Kv.Item.newBuilder().setKey(kv.key).setValue(ByteString.copyFromUtf8(kv.val)).build());
            }
            Kv.AllItems response =
                    stub.setAll(request.build());
            System.out.println("Response=>");
            System.out.println(response);
        }
    }
}
