package com.interviews.kvstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ConcurrentKeyValMap that allows the concurrent reads and writes on HashMap
 * for Key String and Binary values.
 * TODO: Need to be extended for storing the binary data on external filesystem and store location in map
 */
public class ConcurrentKeyValMap {
    public Map<String, byte[]> valuesMap = new HashMap<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    private Stats stats = new Stats();
    private long valuesSize;

    public void putAll(List<String> keys, List<byte[]> values) {
        stats.setOpsCount++;
        writeLock.lock();
        try {

            for (int ii=0; ii < keys.size(); ii++) {
                byte[] val = valuesMap.put(keys.get(ii), values.get(ii));
                if (val != null)
                    valuesSize-=values.get(ii).length;
                valuesSize+= values.get(ii).length;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void put(String key, byte[] value) {
        // TODO: If the value is larger is size, store it in file system and store the location in hashmap
        writeLock.lock();
        byte[] val = null;
        try {
            val = valuesMap.put(key, value);
        } finally {
            writeLock.unlock();
            stats.setOpsCount++;
            if (val != null)
                valuesSize-= value.length;
            valuesSize+= value.length;
        }
    }
    public void removeAll(List<String> keys) {
        writeLock.lock();
        try {
            stats.deleteOpsCount++;
            for (int ii=0; ii < keys.size(); ii++) {
                byte[] val = valuesMap.remove(keys.get(ii));
                if (val != null)
                    valuesSize-= val.length;
            }
        } finally {
            writeLock.unlock();
        }
    }
    public void remove(String key) {
        // TODO: If the value is larger is size, store it in file system and store the location in hashmap
        writeLock.lock();
        try {
            stats.deleteOpsCount++;
            byte[] val = valuesMap.remove(key);
            if (val != null)
                valuesSize-= val.length;
        } finally {
            writeLock.unlock();
        }
    }

    public byte[] get(String key) {
        readLock.lock();
        try {
            stats.getOpsCount++;
            return valuesMap.get(key);
        } finally {
            readLock.unlock();
        }
    }
    public List<KeyValData> getAll(List<String> keys) {
        readLock.lock();
        try {
            stats.getOpsCount++;
            List<KeyValData> kvList= new ArrayList<>();
            for (int ii=0; ii < keys.size(); ii++) {
                kvList.add(new KeyValData(keys.get(ii), valuesMap.get(keys.get(ii))));
            }
            return kvList;
        } finally {
            readLock.unlock();
        }
    }

    // Stats API
    // ===================================
    public long getKeys() {
        return valuesMap.keySet().size();
    }
    // Get Size in bytes
    public long getSizeInBytes() {
        return valuesSize;
    }
    public long getGetOpsCount() {
        return stats.getOpsCount;
    }
    public long getSetOpsCount() {
        return stats.setOpsCount;
    }
    public long getDeleteOpsCount() {
        return stats.deleteOpsCount;
    }
    // ======================================

    class Stats {
        public long getOpsCount;  // Maintains the number of get and getAll ops
        public long setOpsCount;  // Maintains the number of put and putAll ops
        public long deleteOpsCount; // Maintains the number of remove and removeAll ops
    }

    class KeyValData {
        public String key;
        public byte[] val;
        KeyValData(String key, byte[] val) {
            this.key = key;
            this.val = val;
        }
    }
}

