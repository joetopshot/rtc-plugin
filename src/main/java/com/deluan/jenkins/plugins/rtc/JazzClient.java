package com.deluan.jenkins.plugins.rtc;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import com.deluan.jenkins.plugins.rtc.commands.*;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.Computer;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import hudson.util.ForkOutputStream;
import org.kohsuke.stapler.framework.io.WriterOutputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Encapsulates the invocation of RTC's SCM Command Line Interface, "scm".
 *
 * @author deluan
 */
@SuppressWarnings("JavaDoc")
public class JazzClient {
    public static final String SCM_CMD = "scm";

    private static final int TIMEOUT = 60 * 60; // in seconds

    private JazzConfiguration configuration = new JazzConfiguration();
    private final Launcher launcher;
    private final TaskListener listener;
    private String jazzExecutable;

    public JazzClient(Launcher launcher, TaskListener listener, FilePath jobWorkspace, String jazzExecutable,
                      JazzConfiguration configuration) {
        this.jazzExecutable = jazzExecutable;
        this.launcher = launcher;
        this.listener = listener;
        this.configuration = configuration.clone();
        this.configuration.setJobWorkspace(jobWorkspace);
    }

    /**
     * Returns true if there is any incoming changes to be accepted.
     *
     * @return <tt>true</tt> if any changes are found
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean hasChanges() throws IOException, InterruptedException {
        Map<String, JazzChangeSet> changes = compare();

        return !changes.isEmpty();
    }

    /**
     * Call <tt>scm load</tt> command. <p/>
     * <p/>
     * Will load the workspace using the parameters defined.
     *
     * @return <tt>true</tt> on success
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean load() throws IOException, InterruptedException {
        Command cmd = new LoadCommand(configuration);

        return (joinWithPossibleTimeout(run(cmd.getArguments()), true, listener) == 0);
    }

    /**
     * Call <tt>scm daemon stop</tt> command. <p/>
     * <p/>
     * Will try to stop any daemon associated with the workspace.
     * <p/>
     * This will be executed with the <tt>scm</tt> command, as the <tt>lscm</tt> command
     * does not support this operation.
     *
     * @return <tt>true</tt> on success
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean stopDaemon() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder(findSCMExecutable());

        args.add(new StopDaemonCommand(configuration).getArguments().toCommandArray());

        return (joinWithPossibleTimeout(l(args), true, listener) == 0);
    }

    protected String findSCMExecutable() {
        String[] cmds = {"scm", "scm.exe", "scm.sh"};
        File file = new File(jazzExecutable);
        String installDir = file.getParent();
        for (String cmd : cmds) {
            String fullCmdPath = installDir + '/' + cmd;
            if (canExecute(fullCmdPath)) {
                return fullCmdPath;
            }
        }
        return SCM_CMD;
    }

    protected boolean canExecute(String fullCmdPath) {
        return new File(fullCmdPath).canExecute();
    }

    /**
     * Call <tt>scm accept</tt> command.<p/>
     *
     * @return all changeSets accepted, complete with affected paths and related work itens
     * @throws IOException
     * @throws InterruptedException
     */
    public List<JazzChangeSet> accept() throws IOException, InterruptedException {
        Map<String, JazzChangeSet> compareCmdResults = compare();

        if (!compareCmdResults.isEmpty()) {
            Map<String, JazzChangeSet> acceptCmdResult = accept(compareCmdResults.keySet());

            for (Map.Entry<String, JazzChangeSet> entry : compareCmdResults.entrySet()) {
                JazzChangeSet changeSet1 = entry.getValue();
                JazzChangeSet changeSet2 = acceptCmdResult.get(entry.getKey());
                changeSet1.copyItemsFrom(changeSet2);
            }
        }

        return new ArrayList<JazzChangeSet>(compareCmdResults.values());
    }

    private String getVersion() throws IOException, InterruptedException {
        VersionCommand cmd = new VersionCommand(configuration);
        return execute(cmd);
    }

    private Map<String, JazzChangeSet> accept(Collection<String> changeSets) throws IOException, InterruptedException {
        String version = getVersion(); // TODO The version should be checked when configuring the Jazz Executable
        AcceptCommand cmd = new AcceptCommand(configuration, changeSets, version);
        return execute(cmd);
    }

    private Map<String, JazzChangeSet> compare() throws IOException, InterruptedException {
        CompareCommand cmd = new CompareCommand(configuration);
        return execute(cmd);
    }

    private <T> T execute(ParseableCommand<T> cmd) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(popen(cmd.getArguments()).toByteArray())));
        T result;

        try {
            result = cmd.parse(in);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            in.close();
        }

        return result;
    }

    private ProcStarter l(ArgumentListBuilder args) {
        // set the default stdout
        return launcher.launch().cmds(args).stdout(listener);
    }

    private ProcStarter run(ArgumentListBuilder args) {
        ArgumentListBuilder cmd = args.clone().prepend(jazzExecutable);
        return l(cmd);
    }

    private int joinWithPossibleTimeout(ProcStarter proc, boolean useTimeout, final TaskListener listener) throws IOException, InterruptedException {
        return useTimeout ? proc.start().joinWithTimeout(TIMEOUT, TimeUnit.SECONDS, listener) : proc.join();
    }

    /**
     * Runs the command and captures the output.
     */
    private ByteArrayOutputStream popen(ArgumentListBuilder args)
            throws IOException, InterruptedException {

        // scm produces text in the platform default encoding, so we need to convert it back to UTF-8
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WriterOutputStream o = new WriterOutputStream(new OutputStreamWriter(baos, "UTF-8"),
                getDefaultCharset());

        PrintStream output = listener.getLogger();
        ForkOutputStream fos = new ForkOutputStream(o, output);
        if (joinWithPossibleTimeout(run(args).stdout(fos), true, listener) == 0) {
            o.flush();
            return baos;
        } else {
            listener.error("Failed to run " + args.toStringWithQuote());
            throw new AbortException();
        }
    }

    private Charset getDefaultCharset() {
        // First check if we can get currentComputer. See issue JENKINS-11874
        if (Computer.currentComputer() != null) {
            return Computer.currentComputer().getDefaultCharset();
        } else {
            return Charset.forName("UTF-8");
        }
    }
}
