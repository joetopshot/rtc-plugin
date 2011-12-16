package com.deluan.jenkins.plugins.rtc;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

/**
 * @author deluan
 */
public class JazzClientTest {

    protected JazzConfiguration config;

    @Before
    public void setUp() {
        config = new JazzConfiguration();
        config.setRepositoryLocation("https://jazz/jazz");
        config.setWorkspaceName("My Workspace");
        config.setStreamName("My Stream");
        config.setUsername("user");
        config.setPassword("password");
        config.setJobWorkspace(new FilePath(new File("c:\\test")));
    }

    private JazzClient createTestableJazzClient(Launcher launcher, TaskListener listener, FilePath jobWorkspace, String jazzExecutable) {
        JazzClient client = new JazzClient(jazzExecutable, jobWorkspace, config, launcher, listener);
        return spy(client);
    }

    private String useSystemSeparator(String originalPath) {
        String separator = Matcher.quoteReplacement(File.separator);
        return originalPath.replaceAll("/", separator);
    }

    @Test
    @Bug(12059)
    public void foundSCMExecutable() {
        String installDir = useSystemSeparator("/a/bb/ccc");
        JazzClient testClient = createTestableJazzClient(null, null, null, installDir + "/lscm.bat");

        doReturn(true).when(testClient).canExecute(installDir + "/scm.exe");

        String scmExecutable = testClient.findSCMExecutable();

        assertThat(scmExecutable, is(installDir + "/scm.exe"));
    }

    @Test
    public void jazzExecutableWithoutPath() {
        JazzClient testClient = createTestableJazzClient(null, null, null, "lscm.bat");

        String scmExecutable = testClient.findSCMExecutable();

        assertThat(scmExecutable, is(JazzClient.SCM_CMD));
    }

    @Test
    public void useDefaultSCMExecutable() {
        String installDir = useSystemSeparator("/a/bb/ccc");
        JazzClient testClient = createTestableJazzClient(null, null, null, installDir + "/lscm.bat");

        String scmExecutable = testClient.findSCMExecutable();

        assertThat(scmExecutable, is(JazzClient.SCM_CMD));
    }

    @Test
    public void useConfiguredSCMExecutable() {
        String installDir = useSystemSeparator("/a/bb/ccc");
        JazzClient testClient = createTestableJazzClient(null, null, null, installDir + "/scm.sh");

        String scmExecutable = testClient.findSCMExecutable();

        assertThat(scmExecutable, is(installDir + "/scm.sh"));
    }

    @Test
    public void useConfiguredSCMExecutableWithoutPath() {
        JazzClient testClient = createTestableJazzClient(null, null, null, "scm.sh");

        String scmExecutable = testClient.findSCMExecutable();

        assertThat(scmExecutable, is("scm.sh"));
    }

    private JazzChangeSet createChangeSet(String rev, Date date, String user, String email, String msg) {
        JazzChangeSet changeSet = new JazzChangeSet();
        changeSet.setRev(rev);
        changeSet.setDate(date);
        changeSet.setUser(user);
        changeSet.setMsg(msg);
        changeSet.setEmail(email);

        return changeSet;
    }

    @Test
    public void testAcceptCommand() throws IOException, InterruptedException {
        JazzClient testClient = createTestableJazzClient(null, null, null, "scm.sh");
        JazzChangeSet changeSet = createChangeSet("1", new Date(), "deluan", "email@a.com", "msg"); // original JazzChangeSet

        Map<String, JazzChangeSet> compareCmdResults = new HashMap<String, JazzChangeSet>();
        compareCmdResults.put("1", changeSet);
        doReturn(compareCmdResults).when(testClient).compare();

        Map<String, JazzChangeSet> acceptCmdResults = new HashMap<String, JazzChangeSet>();
        JazzChangeSet changeSet2 = createChangeSet(null, new Date(), null, null, null); // original JazzChangeSet
        changeSet2.addWorkItem("123 A Work Item");
        acceptCmdResults.put("1", changeSet2);
        doReturn(acceptCmdResults).when(testClient).accept(compareCmdResults.keySet());

        List<JazzChangeSet> result = testClient.accept();
        assertThat(result.size(), is(1));

        JazzChangeSet returnedChangeSet = result.get(0);
        assertThat(returnedChangeSet.getUser(), is("deluan"));
        assertThat(returnedChangeSet.getWorkItems().size(), is(1));

        String returnedWorkItem = returnedChangeSet.getWorkItems().get(0);
        assertThat(returnedWorkItem, is(changeSet2.getWorkItems().get(0)));
    }

    @Test(expected = IOException.class)
    public void errorParsingAcceptOutput() throws IOException, InterruptedException {
        JazzClient testClient = createTestableJazzClient(null, null, null, "scm.sh");
        JazzChangeSet changeSet = createChangeSet("1", new Date(), "deluan", "email@a.com", "msg"); // original JazzChangeSet

        Map<String, JazzChangeSet> compareCmdResults = new HashMap<String, JazzChangeSet>();
        compareCmdResults.put("1", changeSet);
        doReturn(compareCmdResults).when(testClient).compare();

        Map<String, JazzChangeSet> acceptCmdResults = new HashMap<String, JazzChangeSet>();
        doReturn(acceptCmdResults).when(testClient).accept(compareCmdResults.keySet());

        testClient.accept();
    }


}
