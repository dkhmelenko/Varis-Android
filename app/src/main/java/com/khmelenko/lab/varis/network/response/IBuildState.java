package com.khmelenko.lab.varis.network.response;

import java.util.List;

/**
 * Defines common data for the build state
 *
 * @author Dmytro Khmelenko
 */
public interface IBuildState {

    long getId();

    void setId(long id);

    long getRepositoryId();

    void setRepositoryId(long repositoryId);

    long getCommitId();

    void setCommitId(long commitId);

    String getNumber();

    void setNumber(String number);

    String getState();

    void setState(String state);

    String getStartedAt();

    void setStartedAt(String startedAt);

    String getFinishedAt();

    void setFinishedAt(String finishedAt);

    long getDuration();

    void setDuration(long duration);

    boolean isPullRequest();

    void setPullRequest(boolean pullRequest);

    List<Long> getJobIds();

    void setJobIds(List<Long> jobIds);
}
