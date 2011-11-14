package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.FilePath;
import org.junit.Before;

import java.io.*;
import java.text.ParseException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author deluan
 */
abstract public class BaseCommandTest {

    protected static final String[] TEST_REVISIONS = new String[]{"1714", "1657", "1652", "1651", "1650", "1648", "1645", "1640", "1625"};
    protected JazzConfigurationProvider config;

    @Before
    public void setUp() {
        config = mock(JazzConfigurationProvider.class);
        when(config.getRepositoryLocation()).thenReturn("https://jazz/jazz");
        when(config.getWorkspaceName()).thenReturn("My Workspace");
        when(config.getStreamName()).thenReturn("My Stream");
        when(config.getUsername()).thenReturn("user");
        when(config.getPassword()).thenReturn("password");
        when(config.getJobWorkspace()).thenReturn(new FilePath(new File("c:\\test")));
    }

    protected Map<String, JazzChangeSet> callParser(ParseableCommand<Map<String, JazzChangeSet>> cmd, String fileName, int sizeExpected, String[] revisionsExpected) throws ParseException, IOException {
        BufferedReader reader = getReader(fileName);

        Map<String, JazzChangeSet> result = cmd.parse(reader);

        assertEquals("The number of change sets in the list was incorrect", sizeExpected, result.size());

        for (String rev : revisionsExpected) {
            assertNotNull("Change set (" + rev + ") not in result", result.get(rev));
        }
        return result;
    }

    protected BufferedReader getReader(String fileName) {
        InputStream in = getClass().getResourceAsStream(fileName);
        return new BufferedReader(new InputStreamReader(in));
    }
}
