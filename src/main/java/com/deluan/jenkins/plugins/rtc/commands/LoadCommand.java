package com.deluan.jenkins.plugins.rtc.commands;

import hudson.util.ArgumentListBuilder;

/**
 * User: deluan
 * Date: 01/11/11
 * Time: 16:00
 */
public class LoadCommand extends AbstractCommand {

    public LoadCommand(JazzConfigurationProvider jazzClient) {
        super(jazzClient);
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
