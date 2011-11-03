package com.deluan.jenkins.plugins.rtc.commands;

import hudson.util.ArgumentListBuilder;

/**
 * Command that issues a tf command line client command.
 *
 * @author Erik Ramfelt
 */
public interface Command {

    /**
     * Returns the arguments to be sent to the TF command line client
     *
     * @return arguments for the TF tool
     */
    ArgumentListBuilder getArguments();
}
