package de.aventax.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;
import de.aventax.utils.MongoDB;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class UUIDFetcher {

    /**
     * Date when name changes were introduceasdad
     *
     * @see UUIDFetcher#getUUIDAt(String, long)
     */
    public static final long FEBRUARY_2015 = 1422748800000L;
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private static final Map<UUID, String> nameCache = new HashMap<UUID, String>();
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private String name;
    private UUID id;

    /**
     * Fetches the uuid asynchronously and passes it to the consumer
     *
     * @param name The name
     * @param action Do what you want to do with the uuid her
     */

    /**
     * Fetches the uuid synchronously and returns it
     *
     * @param name The name
     * @return The uuid
     */
    public static UUID getUUID(String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    /**
     * Fetches the uuid synchronously for a specified name and time and passes the result to the consumer
     *
     * @param name The name
     * @param timestamp Time when the player had this name in milliseconds
     * @param action Do what you want to do with the uuid her
     */

    /**
     * Fetches the uuid synchronously for a specified name and time
     *
     * @param name      The name
     * @param timestamp Time when the player had this name in milliseconds
     * @see UUIDFetcher#FEBRUARY_2015
     */
    public static UUID getUUIDAt(String name, long timestamp) {

        name = name.toLowerCase();
        if (uuidCache.containsKey(name)) {
            return uuidCache.get(name);
        }

        UUID result = null;

        String finalName = name;
        Document document = MongoDB.getCollection("Mojang").getStructure().find(new Document("Name", name.toLowerCase())).first();
        if (document != null) {
            if (System.currentTimeMillis() - document.getLong("timestep") >= TimeUnit.DAYS.toMillis(30)) {
                MongoDB.getCollection("Mojang").getStructure().findOneAndDelete(new Document("Name", finalName.toLowerCase()));
            } else {
                result = UUID.fromString(document.getString("uuid"));
            }
        }

        if (result != null) {
            uuidCache.put(name, result);
            nameCache.put(result, name);
            return result;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
            connection.setReadTimeout(5000);
            UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            uuidCache.put(name, data.id);
            nameCache.put(data.id, data.name);
            MongoDB.getCollection("Mojang").getStructure().insertOne(new Document("Name", name.toLowerCase()).append("CaseName", data.name).append("uuid", data.id.toString()).append("timestep", System.currentTimeMillis()));
            return data.id;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * Fetches the name asynchronously and passes it to the consumer
     *
     * @param uuid The uuid
     * @param action Do what you want to do with the name her
     */
    /**
     * Fetches the name synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getName(UUID uuid) {
        if (nameCache.containsKey(uuid)) {
            return nameCache.get(uuid);
        }

        String result = null;

        Document document = MongoDB.getCollection("Mojang").getStructure().find(new Document("uuid", uuid.toString())).first();
        if (document != null) {
            if (System.currentTimeMillis() - document.getLong("timestamp") >= TimeUnit.DAYS.toMillis(30)) {
                MongoDB.getCollection("Mojang").getStructure().findOneAndDelete(new Document("uuid", uuid.toString()));
            } else {
                result = document.getString("CaseName");
            }
        }

        if (result != null) {
            uuidCache.put(result, uuid);
            nameCache.put(uuid, result);
            return result;
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);
            UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
            UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];
            uuidCache.put(currentNameData.name.toLowerCase(), uuid);
            nameCache.put(uuid, currentNameData.name);
            MongoDB.getCollection("Mojang").getStructure().insertOne(new Document("Name", currentNameData.name.toLowerCase()).append("CaseName", currentNameData.name).append("uuid", uuid.toString()).append("timestep", System.currentTimeMillis()));
            return currentNameData.name;
        } catch (Exception e) {
        }
        return null;
    }
}
