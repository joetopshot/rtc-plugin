package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.JazzConfiguration;
import hudson.util.ArgumentListBuilder;
import org.apache.commons.lang.StringUtils;

/**
 * @author deluan
 */
public abstract class AbstractCommand implements Command {

    private final JazzConfiguration config;

    public AbstractCommand(JazzConfiguration configurationProvider) {
        this.config = configurationProvider;
    }

    protected ArgumentListBuilder addLoginArgument(ArgumentListBuilder arguments) {
        if (StringUtils.isNotBlank(config.getUsername())) {
            arguments.add("-u");
            arguments.addMasked(config.getUsername());
        }
        if (StringUtils.isNotBlank(config.getPassword())) {
            arguments.add("-P");
            arguments.addMasked(config.getPassword());
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

    protected JazzConfiguration getConfig() {
        return config;
    }
}
