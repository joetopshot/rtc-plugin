package com.deluan.jenkins.plugins.rtc;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSetList;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: deluan
 * Date: 04/11/11
 */
public class JazzRepositoryBrowserTest {

    private static final String SERVER_URL = "http://jazzserver:9443/jazz";

    @Test
    public void testGetUrl() throws Exception {
        JazzRepositoryBrowser browser = new JazzRepositoryBrowser(SERVER_URL);
        assertEquals(SERVER_URL, browser.getUrl());
    }

    @Test
    public void testGetWorkItemLink() throws Exception {
        JazzRepositoryBrowser browser = new JazzRepositoryBrowser(SERVER_URL);
        JazzChangeSet changeSet = new JazzChangeSet();
        String workItem = "503 \"This is a test\"";
        changeSet.addWorkItem(workItem);
        assertEquals(new URL(SERVER_URL + "/resource/itemName/com.ibm.team.workitem.WorkItem/503"),
                browser.getWorkItemLink(changeSet, workItem));
    }

    @Test
    public void testGetWorkItemLinkUsingScmConfiguration() throws Exception {
        AbstractBuild build = mock(AbstractBuild.class);
        AbstractProject<?, ?> project = mock(AbstractProject.class);
        when(build.getProject()).thenReturn(project);
        when(project.getScm()).thenReturn(new JazzSCM(SERVER_URL, null, null, null, null));

        JazzRepositoryBrowser browser = new JazzRepositoryBrowser(null);
        JazzChangeSet changeSet = new JazzChangeSet();
        new JazzChangeSetList(build, Arrays.asList(changeSet));
        String workItem = "503 \"This is a test\"";
        changeSet.addWorkItem(workItem);
        assertEquals(new URL(SERVER_URL + "/resource/itemName/com.ibm.team.workitem.WorkItem/503"),
                browser.getWorkItemLink(changeSet, workItem));
    }
}
