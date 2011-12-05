package com.deluan.jenkins.plugins.rtc;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeLogReader;
import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeLogWriter;
import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.*;
import hudson.scm.*;
import hudson.util.FormValidation;
import hudson.util.LogTaskListener;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author deluan
 */
@SuppressWarnings("UnusedDeclaration")
public class JazzSCM extends SCM {

    private static final Logger logger = Logger.getLogger(JazzClient.class.getName());

    private String repositoryLocation;
    private String workspaceName;
    private String streamName;
    private String username;
    private Secret password;

    private JazzRepositoryBrowser repositoryBrowser;

    @DataBoundConstructor
    public JazzSCM(String repositoryLocation, String workspaceName, String streamName,
                   String username, String password) {

        this.repositoryLocation = repositoryLocation;
        this.workspaceName = workspaceName;
        this.streamName = streamName;
        this.username = username;
        this.password = StringUtils.isEmpty(password) ? null : Secret.fromString(password);
    }

    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getStreamName() {
        return streamName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return Secret.toString(password);
    }

    private JazzClient getClientInstance(Launcher launcher, TaskListener listener, FilePath jobWorkspace) {
        return new JazzClient(launcher, listener, jobWorkspace, getDescriptor().getJazzExecutable(),
                username, getPassword(), repositoryLocation, streamName, workspaceName);
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        return null; // This implementation is not necessary, as this information is obtained from the remote RTC's repository
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
        JazzClient client = getClientInstance(launcher, listener, workspace);
        try {
            return (client.hasChanges()) ? PollingResult.SIGNIFICANT : PollingResult.NO_CHANGES;
        } catch (Exception e) {
            return PollingResult.NO_CHANGES;
        }
    }

    @Override
    public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher, FilePath workspace, BuildListener listener, File changelogFile) throws IOException, InterruptedException {
        JazzClient client = getClientInstance(launcher, listener, workspace);

        // Forces a load of the workspace. If it's already loaded, the scm command will do nothing.
        client.load();

        // Accepts all incoming changes
        List<JazzChangeSet> changes;
        try {
            changes = client.accept();
        } catch (IOException e) {
            return false;
        }

        if (!changes.isEmpty()) {
            JazzChangeLogWriter writer = new JazzChangeLogWriter();
            writer.write(changes, changelogFile);
        } else {
            createEmptyChangeLog(changelogFile, listener, "changelog");
        }

        return true;
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return new JazzChangeLogReader();
    }

    @Override
    public JazzRepositoryBrowser getBrowser() {
        return repositoryBrowser;
    }

    @Override
    public boolean processWorkspaceBeforeDeletion(AbstractProject<?, ?> project, FilePath workspace, Node node) throws IOException, InterruptedException {
        LogTaskListener listener = new LogTaskListener(logger, Level.INFO);
        Launcher launcher = node.createLauncher(listener);

        // Stop any daemon started for the workspace
        JazzClient client = getClientInstance(launcher, listener, workspace);
        client.stopDaemon();

        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends SCMDescriptor<JazzSCM> {
        private String jazzExecutable;

        public DescriptorImpl() {
            super(JazzSCM.class, JazzRepositoryBrowser.class);
            load();
        }

        @Override
        public String getDisplayName() {
            return "RTC";
        }

        @Override
        public SCM newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            JazzSCM scm = (JazzSCM) super.newInstance(req, formData);
            scm.repositoryBrowser = RepositoryBrowsers.createInstance(
                    JazzRepositoryBrowser.class,
                    req,
                    formData,
                    "browser");
            return scm;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            jazzExecutable = Util.fixEmpty(req.getParameter("rtc.jazzExecutable").trim());
            save();
            return true;
        }

        public String getJazzExecutable() {
            if (jazzExecutable == null) {
                return JazzClient.SCM_CMD;
            } else {
                return jazzExecutable;
            }
        }

        public FormValidation doExecutableCheck(@QueryParameter String value) {
            return FormValidation.validateExecutable(value);
        }
    }
}
