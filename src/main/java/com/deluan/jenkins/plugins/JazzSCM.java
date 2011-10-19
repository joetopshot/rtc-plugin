package com.deluan.jenkins.plugins;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.scm.*;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: deluan
 * Date: 19/10/11
 */
public class JazzSCM extends SCM {

    protected static final Logger log = Logger.getLogger(JazzSCM.class.getName());

    private String repositoryLocation;
    private String workspaceName;
    private String buildEngineId;
    private String buildDefinitionId;
    private String username;
    private String password;

    @DataBoundConstructor
    public JazzSCM(String repositoryLocation, String workspaceName,
                   String buildEngineId, String buildDefinitionId, String username,
                   String password) {

        log.log(Level.FINER, "In JazzSCM constructor");

        this.repositoryLocation = repositoryLocation;
        this.workspaceName = workspaceName;
        this.buildEngineId = buildEngineId;
        this.buildDefinitionId = buildDefinitionId;
        this.username = username;
        this.password = password;
    }

    public String getRepositoryLocation() {
        return repositoryLocation;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public String getBuildEngineId() {
        return buildEngineId;
    }

    public String getBuildDefinitionId() {
        return buildDefinitionId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState baseline) throws IOException, InterruptedException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher, FilePath workspace, BuildListener listener, File changelogFile) throws IOException, InterruptedException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Extension
    public static class DescriptorImpl extends SCMDescriptor {
        private String jazzExecutable;

        public DescriptorImpl() {
            super(JazzSCM.class, null);
            load();
        }

        @Override
        public String getDisplayName() {
            return "Rational Team Concert";
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
