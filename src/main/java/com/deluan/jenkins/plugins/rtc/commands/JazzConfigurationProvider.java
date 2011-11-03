package com.deluan.jenkins.plugins.rtc.commands;

public interface JazzConfigurationProvider {

    public String getRepositoryLocation();

    public String getWorkspaceName();

    public String getStreamName();

    public String getUsername();

    public String getPassword();

    public String getJobWorkspace();


}
