package com.deluan.jenkins.plugins.rtc.commands;

import hudson.util.ArgumentListBuilder;

/**
 * @author deluan
 */
public class LoadCommand extends AbstractCommand {

    public LoadCommand(JazzConfigurationProvider configurationProvider) {
        super(configurationProvider);
    }

    public ArgumentListBuilder getArguments() {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("load", getConfig().getWorkspaceName());
        addLoginArgument(args);
        addRepositoryArgument(args);
        addLocalWorkspaceArgument(args);
        args.add("-f");
        return args;
    }

}
