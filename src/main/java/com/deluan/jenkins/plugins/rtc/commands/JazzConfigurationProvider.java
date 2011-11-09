package com.deluan.jenkins.plugins.rtc.commands;

import hudson.FilePath;

/**
 * @author deluan
 */
public interface JazzConfigurationProvider {

    public String getRepositoryLocation();

    public String getWorkspaceName();

    public String getStreamName();

    public String getUsername();

    public String getPassword();

    public FilePath getJobWorkspace();


}
