package de.aventax.utils;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class MongoDB {

    public static MongoClient client;
    public static String database;

    private static final Map<String, Structure> tabellen = new HashMap<>();

    public static void connect(String host, int port, String database, String user, String password) {

        try {
            client = MongoClients.create(new ConnectionString("mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database));

        } catch (Exception ex) {
            System.out.println("[MongoDB] Error: " + ex.getMessage());
            ex.printStackTrace();

        }
    }

    public static Structure getCollection(String name) {

        if (tabellen.containsKey(name)) {
            return tabellen.get(name);

        } else {
            System.out.println("[MongoDB] Create: Collection " + name);
            Structure structure = new Structure();
            structure.tablename = name;
            tabellen.put(name, structure);
            return structure;

        }
    }

    public static class Structure {

        public String tablename = null;
        private MongoCollection<Document> collection = null;

        public MongoCollection<Document> getStructure() {
            if (collection == null) {
                collection = client.getDatabase(database).getCollection(tablename);
            }
            return collection;
        }
    }

}
