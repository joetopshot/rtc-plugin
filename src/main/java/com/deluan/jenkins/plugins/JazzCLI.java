package com.deluan.jenkins.plugins;

import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import hudson.util.ForkOutputStream;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates the invocation of RTC's SCM Command Line Interface, "scm".
 *
 * @author Deluan Quintao
 */
public class JazzCLI {
    protected static final Logger logger = Logger.getLogger(JazzSCM.class.getName());

    private final ArgumentListBuilder base;
    private final Launcher launcher;
    private final TaskListener listener;
    private String repositoryLocation;
    private String workspaceName;
    private String streamName;
    private String username;
    private String password;
    private FilePath jobWorkspace;
    private String jazzSandbox;


    public JazzCLI(Launcher launcher, TaskListener listener, FilePath jobWorkspace, String jazzExecutable,
                   String jazzSandbox, String user, String password,
                   String repositoryLocation, String streamName, String workspaceName) {
        base = new ArgumentListBuilder(jazzExecutable);
        this.jazzSandbox = jazzSandbox;
        this.launcher = launcher;
        this.listener = listener;
        this.username = user;
        this.password = password;
        this.repositoryLocation = repositoryLocation;
        this.streamName = streamName;
        this.workspaceName = workspaceName;
        this.jobWorkspace = jobWorkspace;
    }

    private ArgumentListBuilder addAuthInfo(ArgumentListBuilder args) {
        if (StringUtils.isNotBlank(username)) {
            args.add("-u", username);
        }
        if (StringUtils.isNotBlank(password)) {
            args.add("-P", password);
        }

        return args;
    }

    public boolean hasChanges() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("status");
        addAuthInfo(args);
        args.add("-C", "-w", "-n");
        args.add("-d");
        args.add(jobWorkspace);

        logger.log(Level.FINER, args.toStringWithQuote());

        ByteArrayOutputStream output = popen(args);
        try {
            String outputString = new String(output.toByteArray());
            return outputString.contains("    Entrada:"); //FIXME How to force english?!
        } finally {
            output.close();
        }
    }

    public boolean load() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("load", workspaceName);
        addAuthInfo(args);
        args.add("-r", repositoryLocation);
        args.add("-d");
        args.add(jobWorkspace);
        args.add("-f");

        logger.log(Level.FINER, args.toStringWithQuote());

        return (joinWithPossibleTimeout(run(args), true, listener) == 0);
    }

    public boolean accept() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("accept");
        addAuthInfo(args);
        args.add("-d");
        args.add(jobWorkspace);
        args.add("--flow-components", "-o", "-v");

        logger.log(Level.FINER, args.toStringWithQuote());

        return (joinWithPossibleTimeout(run(args), true, listener) == 0);
    }

    public boolean isLoaded() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("status");
        addAuthInfo(args);
        args.add("-C", "-w", "-n");
        args.add("-d");
        args.add(jobWorkspace);

        logger.log(Level.FINER, args.toStringWithQuote());

        return (joinWithPossibleTimeout(run(args), true, listener) == 0);
    }

    public boolean getChanges(File changeLog) throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("status");
        addAuthInfo(args);
        args.add("-C", "-w", "-n");
        args.add("-d");
        args.add(jobWorkspace);

        logger.log(Level.FINER, args.toStringWithQuote());

        FileOutputStream fos = new FileOutputStream(changeLog);
        try {
            return (joinWithPossibleTimeout(run(args).stdout(fos), true, listener) == 0);
        } finally {
            fos.close();
        }
    }

    private ArgumentListBuilder seed() {
        return base.clone();
    }

    private ProcStarter l(ArgumentListBuilder args) {
        // set the default stdout
        return launcher.launch().cmds(args).stdout(listener).pwd(jazzSandbox);
    }

    private ProcStarter run(String... args) {
        return l(seed().add(args));
    }

    private ProcStarter run(ArgumentListBuilder args) {
        return l(seed().add(args.toCommandArray()));
    }

    private int joinWithPossibleTimeout(ProcStarter proc, boolean useTimeout, final TaskListener listener) throws IOException, InterruptedException {
        return useTimeout ? proc.start().joinWithTimeout(60 * 5, TimeUnit.SECONDS, listener) : proc.join();
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
