package com.khmelenko.lab.travisclient.storage;

import com.google.gson.Gson;
import com.khmelenko.lab.travisclient.network.response.User;
import com.khmelenko.lab.travisclient.util.FileUtils;

/**
 * Stores data to the cache
 *
 * @author Dmytro Khmelenko
 */
public final class CacheStorage {

    private static final String USER_FILE = "UserData";

    private CacheStorage() {

    }

    public static CacheStorage newInstance() {
        return new CacheStorage();
    }

    /**
     * Saves user to the cache
     *
     * @param user User
     */
    public void saveUser(User user) {
        Gson gson = new Gson();
        String dataToStore = gson.toJson(user);
        FileUtils.writeInternalFile(USER_FILE, dataToStore);
    }

    /**
     * Restores user from the cache
     *
     * @return User
     */
    public User restoreUser() {
        String fileData = FileUtils.readInternalFile(USER_FILE);
        Gson gson = new Gson();
        User user = gson.fromJson(fileData, User.class);
        return user;
    }

    /**
     * Deletes the user from the cache
     */
    public void deleteUser() {
        FileUtils.deleteInternalFile(USER_FILE);
    }
}
