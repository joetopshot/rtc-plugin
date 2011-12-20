package com.deluan.jenkins.plugins.rtc;

import hudson.FilePath;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author deluan
 */
public final class JazzConfiguration {
    public static final Long DEFAULT_TIMEOUT = 60L * 60; // in seconds

    private String repositoryLocation;
    private String workspaceName;
    private String streamName;
    private String username;
    private String password;
    private FilePath jobWorkspace;
    private Boolean useTimeout;
    private Long timeoutValue;

    public JazzConfiguration() {
    }

    public JazzConfiguration(JazzConfiguration aJazzConfiguration) {
        this.repositoryLocation = aJazzConfiguration.repositoryLocation;
        this.workspaceName = aJazzConfiguration.workspaceName;
        this.streamName = aJazzConfiguration.streamName;
        this.username = aJazzConfiguration.username;
        this.password = aJazzConfiguration.password;
        this.jobWorkspace = aJazzConfiguration.jobWorkspace;
        this.useTimeout = aJazzConfiguration.useTimeout;
        this.timeoutValue = aJazzConfiguration.timeoutValue;
    }

    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public FilePath getJobWorkspace() {
        return jobWorkspace;
    }

    public Boolean isUseTimeout() {
        return useTimeout;
    }

    public Long getTimeoutValue() {
        return timeoutValue;
    }

    public void setRepositoryLocation(String repositoryLocation) {
        this.repositoryLocation = repositoryLocation;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setJobWorkspace(FilePath jobWorkspace) {
        this.jobWorkspace = jobWorkspace;
    }

    public void setUseTimeout(Boolean useTimeout) {
        this.useTimeout = useTimeout;
    }

    public void setTimeoutValue(Long timeoutValue) {
        this.timeoutValue = timeoutValue;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
