package com.deluan.jenkins.plugins.rtc;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.junit.Before;
import org.junit.Test;
import org.jvnet.hudson.test.Bug;

import java.io.File;
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


}
