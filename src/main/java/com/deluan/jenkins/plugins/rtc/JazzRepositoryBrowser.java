package com.deluan.jenkins.plugins.rtc;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.Extension;
import hudson.Util;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.scm.RepositoryBrowser;
import hudson.scm.SCM;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: deluan
 * Date: 03/11/11
 * Time: 15:43
 */
public class JazzRepositoryBrowser extends RepositoryBrowser<JazzChangeSet> {

    private final String repositoryUrl;

    @DataBoundConstructor
    public JazzRepositoryBrowser(String repositoryUrl) {
        this.repositoryUrl = Util.fixEmpty(repositoryUrl);
    }

    public String getUrl() {
        return repositoryUrl;
    }

    private String getServerConfiguration(JazzChangeSet changeset) {
        AbstractProject<?, ?> project = changeset.getParent().build.getProject();
        SCM scm = project.getScm();
        if (scm instanceof JazzSCM) {
            return ((JazzSCM) scm).getRepositoryLocation();
        } else {
            throw new IllegalStateException("Jazz repository browser used on a non Jazz SCM");
        }
    }

    private String getBaseUrlString(JazzChangeSet changeSet) throws MalformedURLException {
        if (repositoryUrl == null) {
            return String.format("%s/", getServerConfiguration(changeSet));
        } else {
            return repositoryUrl;
        }
    }

    @Override
    public URL getChangeSetLink(JazzChangeSet changeSet) throws IOException {
        return new URL(getBaseUrlString(changeSet)); // TODO
    }

    // ${repositoryUrl}/resource/itemName/com.ibm.team.workitem.WorkItem/${alias}
    public URL getWorkItemLink(JazzChangeSet changeSet, String workItem) throws IOException {
        String[] parts = workItem.split(" ");
        String url = getBaseUrlString(changeSet) + "/resource/itemName/com.ibm.team.workitem.WorkItem/" + parts[0];
        return new URL(url);
    }

    @Extension
    public static final class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {

        private String baseUrl;

        public DescriptorImpl() {
            super(JazzRepositoryBrowser.class);
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            baseUrl = Util.fixEmpty(req.getParameter("rtc.browser.url").trim());
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Jazz Web Access";
        }

        public String getBaseUrl() {
            return baseUrl;
        }
    }


}
