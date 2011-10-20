package com.deluan.jenkins.plugins;

import hudson.AbortException;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import hudson.util.ForkOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates the invocation of RTC's SCM Command Line Interface, "scm".
 *
 * @author Deluan Quintao
 */
public class JazzCmd {
    protected static final Logger logger = Logger.getLogger(JazzSCM.class.getName());

    private final ArgumentListBuilder base;
    private final Launcher launcher;
    private final TaskListener listener;
    private String repositoryLocation;
    private String workspaceName;
    private String streamName;
    private String username;
    private String password;


    public JazzCmd(Launcher launcher, TaskListener listener, String rtcExecutable, String user, String password,
                   String repositoryLocation, String streamName, String workspaceName) {
        base = new ArgumentListBuilder(rtcExecutable);
        this.launcher = launcher;
        this.listener = listener;
        this.username = user;
        this.password = password;
        this.repositoryLocation = repositoryLocation;
        this.streamName = streamName;
        this.workspaceName = workspaceName;
    }

    public List<JazzChangeSet> getChanges() throws IOException, InterruptedException {
        List<JazzChangeSet> result = new ArrayList<JazzChangeSet>();

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("compare");
        args.add("ws", workspaceName);
        args.add("stream", streamName);
//        args.add("ws", "Negociacao Principal Workspace - Deluan");
//        args.add("ws", workspaceName);
        args.add("-u", username);
        args.add("-P", password);
        args.add("-r", repositoryLocation);
        args.add("-I", "s");
        args.add("-C", JazzChangeSet.CONTRIBUTOR_FORMAT);
        args.add("-D", JazzChangeSet.DATE_FORMAT);

        logger.log(Level.FINER, args.toStringWithQuote());

        BufferedReader in = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(popen(args).toByteArray())));
        String line;
        while ((line = in.readLine()) != null) {
            result.add(new JazzChangeSet(line));
        }

        return result;
    }

    private ArgumentListBuilder seed() {
        return base.clone();
    }

    private ProcStarter l(ArgumentListBuilder args) {
        // set the default stdout
        return launcher.launch().cmds(args).stdout(listener);
    }

    private ProcStarter run(String... args) {
        return l(seed().add(args));
    }

    private ProcStarter run(ArgumentListBuilder args) {
        return l(seed().add(args.toCommandArray()));
    }

    private int joinWithPossibleTimeout(ProcStarter proc, boolean useTimeout, final TaskListener listener) throws IOException, InterruptedException {
        return useTimeout ? proc.start().joinWithTimeout(60 * 60, TimeUnit.SECONDS, listener) : proc.join();
    }

    /**
     * Runs the command and captures the output.
     */
    private ByteArrayOutputStream popen(ArgumentListBuilder args)
            throws IOException, InterruptedException {

        PrintStream output = listener.getLogger();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ForkOutputStream fos = new ForkOutputStream(baos, output);
        if (joinWithPossibleTimeout(run(args).stdout(fos), true, listener) == 0) {
            return baos;
        } else {
            listener.error("Failed to run " + args.toStringWithQuote());
            throw new AbortException();
        }
    }


}
