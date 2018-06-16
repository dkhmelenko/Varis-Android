package com.khmelenko.lab.varis.storage

import com.google.gson.Gson
import com.khmelenko.lab.varis.network.response.Repo
import com.khmelenko.lab.varis.network.response.User
import com.khmelenko.lab.varis.util.FileUtils

private const val USER_FILE = "UserData"
private const val REPOS_FILE = "ReposFile"

/**
 * Stores data to the cache
 *
 * @author Dmytro Khmelenko
 */
class CacheStorage private constructor() {

    /**
     * Saves user to the cache
     *
     * @param user User
     */
    fun saveUser(user: User) {
        val gson = Gson()
        val dataToStore = gson.toJson(user)
        FileUtils.writeInternalFile(USER_FILE, dataToStore)
    }

    /**
     * Restores user from the cache
     *
     * @return User
     */
    fun restoreUser(): User? {
        val fileData = FileUtils.readInternalFile(USER_FILE)
        val gson = Gson()
        return gson.fromJson(fileData, User::class.java)
    }

    /**
     * Deletes the user from the cache
     */
    fun deleteUser() {
        FileUtils.deleteInternalFile(USER_FILE)
    }

    /**
     * Caches repositories data
     *
     * @param repos Repositories to cache
     */
    fun saveRepos(repos: List<Repo>) {
        val builder = StringBuilder()
        for (repo in repos) {
            builder.append(repo.slug)
            builder.append(",")
        }
        FileUtils.writeInternalFile(REPOS_FILE, builder.toString())
    }

    /**
     * Restores the list of repositories
     *
     * @return Collection of repositories slugs
     */
    fun restoreRepos(): Array<String> {
        val fileData = FileUtils.readInternalFile(REPOS_FILE)
        return fileData.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * Deletes cached repositories
     */
    fun deleteRepos() {
        FileUtils.deleteInternalFile(REPOS_FILE)
    }

    companion object {

        @JvmStatic
        fun newInstance(): CacheStorage {
            return CacheStorage()
        }
    }
}
