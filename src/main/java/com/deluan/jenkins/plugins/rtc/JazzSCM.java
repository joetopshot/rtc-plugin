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
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: deluan
 * Date: 19/10/11
 */
@SuppressWarnings("UnusedDeclaration")
public class JazzSCM extends SCM {

    private String repositoryLocation;
    private String workspaceName;
    private String streamName;
    private String username;
    private String password;

    @DataBoundConstructor
    public JazzSCM(String repositoryLocation, String workspaceName, String streamName,
                   String username, String password) {

        this.repositoryLocation = repositoryLocation;
        this.workspaceName = workspaceName;
        this.streamName = streamName;
        this.username = username;
        this.password = password;
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
        return password;
    }

    private JazzClient getClientInstance(Launcher launcher, TaskListener listener, FilePath jobWorkspace) {
        return new JazzClient(launcher, listener, jobWorkspace, getDescriptor().getJazzExecutable(),
                username, password, repositoryLocation, streamName, workspaceName);
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

        // Forces a load of the workspace. If it's already loaded, scm do nothing.
        client.load();

        List<JazzChangeSet> changes = client.getChanges();
        if (!changes.isEmpty()) {
            JazzChangeLogWriter writer = new JazzChangeLogWriter();

            boolean success = client.accept(changes);
            if (success) {
                writer.write(changes, changelogFile);
            } else {
                createEmptyChangeLog(changelogFile, listener, "changelog");
            }
            return success;
        } else {
            createEmptyChangeLog(changelogFile, listener, "changelog");
            return true;
        }
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return new JazzChangeLogReader();
    }

    @Override
    public boolean processWorkspaceBeforeDeletion(AbstractProject<?, ?> project, FilePath workspace, Node node) throws IOException, InterruptedException {
        // TODO How to obtain a Laucher, so I can call JazzClient.stopDaemon()?
        return super.processWorkspaceBeforeDeletion(project, workspace, node);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends SCMDescriptor<JazzSCM> {
        private String jazzExecutable;

        public DescriptorImpl() {
            super(JazzSCM.class, null);
            load();
        }

        @Override
        public String getDisplayName() {
            return "RTC";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            jazzExecutable = Util.fixEmpty(req.getParameter("rtc.jazzExecutable").trim());
            save();
            return true;
        }

        public String getJazzExecutable() {
            if (jazzExecutable == null) {
                return "scm";
            } else {
                return jazzExecutable;
            }
        }

        public FormValidation doExecutableCheck(@QueryParameter String value) {
            return FormValidation.validateExecutable(value);
        }
    }
}
