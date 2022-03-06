package com.interviews.kvstore;

import com.google.protobuf.ByteString;
import com.interviews.kvstore.ConcurrentKeyValMap.KeyValData;

import java.util.ArrayList;
import java.util.List;

/**
 * Service to interface with the InMemory Keyvalue store
 */
public class KeyValServiceImpl extends KeyValServiceGrpc.KeyValServiceImplBase {
    private static ConcurrentKeyValMap store = new ConcurrentKeyValMap();
    /** sets a key value pair
     */
    public void set(com.interviews.kvstore.Kv.Item request,
                    io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.Item> responseObserver) {
        System.out.println("set command request => " + request);

        store.put(request.getKey(), request.getValue().toByteArray());
        Kv.Item response = Kv.Item.newBuilder().setKey(request.getKey()).setValue(request.getValue()).build();

        System.out.println("set command response => " + response);
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    /**
     * sets one or more keys with the values
     */
    public void setAll(com.interviews.kvstore.Kv.AllItems request,
                       io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.AllItems> responseObserver) {
        System.out.println("set all command request => " + request);
        List<String> keyList = new ArrayList<>();
        List<byte[]> valList = new ArrayList<>();
        for(Kv.Item kv: request.getItemsList()) {
            keyList.add(kv.getKey());
            valList.add(kv.getValue().toByteArray());
        }
        store.putAll(keyList, valList);
        System.out.println("set all command response => " + request);
        responseObserver.onNext(request);

        responseObserver.onCompleted();
    }

    /**
     * gets data if the key is present else empty response
     */
    public void get(com.interviews.kvstore.Kv.Key request,
                    io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.Item> responseObserver) {
        System.out.println("get command request => " + request);
        byte[] val = store.get(request.getKey());
        Kv.Item.Builder responseBuilder = Kv.Item.newBuilder();
        if (val != null) {
            responseBuilder.setKey(request.getKey()).setValue(ByteString.copyFrom(val));
        }
        Kv.Item response = responseBuilder.build();
                System.out.println("get command response => " + response);
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    /**
     * gets all the existing keys present and ignores the keys that are missing
     */
    public void getAll(com.interviews.kvstore.Kv.AllKeys request,
                       io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.AllItems> responseObserver) {
        System.out.println("get all command request => " + request);
        List<String> keys = new ArrayList<>();
        for (Kv.Key k: request.getKeysList()) {
            keys.add(k.getKey());
        }
        List<KeyValData> keyValData = store.getAll(keys);
        Kv.AllItems.Builder builder = Kv.AllItems.newBuilder();
        if (keyValData != null) {
            for (KeyValData data : keyValData) {
                if (data != null && data.val != null) {
                    builder.addItems(
                            Kv.Item.newBuilder().setKey(data.key).setValue(ByteString.copyFrom(data.val)).build()
                    );
                }
            }
        }
        Kv.AllItems response = builder.build();
        System.out.println("get all ommand response => " + response);
        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    /**
     * Delete returns true if the key does not exist or successfully deleted
     *
     */
    public void delete(com.interviews.kvstore.Kv.Key request,
                       io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.Success> responseObserver) {
        System.out.println("delete command request => " + request);
        store.remove(request.getKey());
        Kv.Success response = Kv.Success.newBuilder().setSuccess(true).build();
        System.out.println("delete command response => " + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * Delete returns true if any of the keys does not exist( deletes existing keys) or successfully deleted
     */
    public void deleteAll(com.interviews.kvstore.Kv.AllKeys request,
                          io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.Success> responseObserver) {
        System.out.println("delete all command request => " + request);
        List<String> keys = new ArrayList<>();
        for (Kv.Key k: request.getKeysList()) {
            keys.add(k.getKey());
        }
        store.removeAll(keys);
        Kv.Success response = Kv.Success.newBuilder().setSuccess(true).build();
        System.out.println("delete all command response => " + response);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     */
    public void getMetrics(com.google.protobuf.Empty request,
                           io.grpc.stub.StreamObserver<com.interviews.kvstore.Kv.Stat> responseObserver) {
        System.out.println("metrics command request => " + request);
        Kv.Stat response = Kv.Stat.newBuilder()
                .setKcnt(store.getKeys())
                .setVsize(store.getSizeInBytes())
                .setGetcnt(store.getGetOpsCount())
                .setSetcnt(store.getSetOpsCount())
                .setDelcnt(store.getDeleteOpsCount())
                .build();
        responseObserver.onNext(response);
        System.out.println("Current Store=>");
        store.valuesMap.entrySet().forEach(entry -> {
            System.out.println("Key: " + entry.getKey() + " Val:" + new String(entry.getValue()));
        });
        System.out.println("Metrics=>");
        System.out.println("Keys Count: " + response.getKcnt());
        System.out.println("Values Size(In Bytes): " + response.getVsize());
        System.out.println("Get Ops Count: " + response.getGetcnt());
        System.out.println("Set Ops Count: " + response.getSetcnt());
        System.out.println("Delete Ops Count: " + response.getDelcnt());
        responseObserver.onCompleted();
    }

}
