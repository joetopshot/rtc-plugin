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

    public JazzConfigurationProvider getConfig() {
        return config;
    }
}
