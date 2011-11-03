package com.deluan.jenkins.plugins.rtc.commands;

import hudson.util.ArgumentListBuilder;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractCommand implements Command {

    private final JazzConfigurationProvider config;

    public AbstractCommand(JazzConfigurationProvider configurationProvider) {
        this.config = configurationProvider;
    }

    protected ArgumentListBuilder addLoginArgument(ArgumentListBuilder arguments) {
        if (StringUtils.isNotBlank(config.getUsername())) {
            arguments.add("-u", config.getUsername());
        }
        if (StringUtils.isNotBlank(config.getPassword())) {
            arguments.add("-P", config.getPassword());
        }

        return arguments;
    }

    protected ArgumentListBuilder addRepositoryArgument(ArgumentListBuilder args) {
        return args.add("-r", getConfig().getRepositoryLocation());
    }

    protected ArgumentListBuilder addLocalWorkspaceArgument(ArgumentListBuilder args) {
        args.add("-d");
        return args.add(getConfig().getJobWorkspace());
    }

    public JazzConfigurationProvider getConfig() {
        return config;
    }
}
