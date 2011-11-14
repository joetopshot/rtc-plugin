package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.FilePath;
import hudson.scm.EditType;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CommandTest {

    public static final String[] TEST_REVISIONS = new String[]{"1714", "1657", "1652", "1651", "1650", "1648", "1645", "1640", "1625"};
    JazzConfigurationProvider config;

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

    @Test
    public void loadCommandArguments() throws Exception {
        LoadCommand cmd = new LoadCommand(config);

        assertEquals("load \"My Workspace\" -u user -P password -r https://jazz/jazz -d c:\\test -f", cmd.getArguments().toStringWithQuote());
    }

    @Test
    public void stopDaemonCommandArguments() throws Exception {
        StopDaemonCommand cmd = new StopDaemonCommand(config);

        assertEquals("daemon stop c:\\test", cmd.getArguments().toStringWithQuote());
    }

    @Test
    public void compareCommandArguments() throws Exception {
        CompareCommand cmd = new CompareCommand(config);

        assertEquals("compare ws \"My Workspace\" stream \"My Stream\" -u user -P password -r https://jazz/jazz -I s -C \"|{name}|{email}|\" -D \"|yyyy-MM-dd-HH:mm:ss|\"", cmd.getArguments().toStringWithQuote());
    }

    @Test
    public void compareCommandParse() throws Exception {
        Map<String, JazzChangeSet> result = callParser(new CompareCommand(config), "scm-compare.txt", 9, TEST_REVISIONS);

        JazzChangeSet changeSet = result.get("1657");
        assertEquals("Roberto", changeSet.getUser());
        assertEquals("roberto.rodriguez@email.com.br", changeSet.getEmail());
        assertEquals("Faltou compartilhar o novo projeto da bridge", changeSet.getMsg());
        assertEquals("2011-11-01-12:16:00", changeSet.getDateStr());
    }

    @Test
    public void compareCommandParseUnix() throws Exception {
        Map<String, JazzChangeSet> result = callParser(new CompareCommand(config), "scm-compare-unix.txt", 2, new String[]{"1625", "1640"});

        JazzChangeSet changeSet = result.get("1640");
        assertEquals("Pedro", changeSet.getUser());
        assertEquals("pedro.modrach@email.com.br", changeSet.getEmail());
        assertEquals("Criacao da tela de cadastro de oferta", changeSet.getMsg());
        assertEquals("2011-10-31-16:56:23", changeSet.getDateStr());
    }

    private Map<String, JazzChangeSet> callParser(ParseableCommand<Map<String, JazzChangeSet>> cmd, String fileName, int sizeExpected, String[] revisionsExpected) throws ParseException, IOException {
        BufferedReader reader = getReader(fileName);

        Map<String, JazzChangeSet> result = cmd.parse(reader);

        assertEquals("The number of change sets in the list was incorrect", sizeExpected, result.size());

        for (String rev : revisionsExpected) {
            assertNotNull("Change set (" + rev + ") not in result", result.get(rev));
        }
        return result;
    }

    @Test
    public void listCommandArguments() throws Exception {
        ListCommand cmd = new ListCommand(config, Arrays.asList(TEST_REVISIONS));

        assertEquals("list changesets -u user -P password -d c:\\test 1652 1625 1650 1651 1648 1657 1714 1645 1640", cmd.getArguments().toStringWithQuote());
    }

    @Test
    public void listCommandParse() throws Exception {
        ListCommand cmd = new ListCommand(config, Arrays.asList(TEST_REVISIONS));
        Map<String, JazzChangeSet> result = callParser(cmd, "scm-list.txt", 9, TEST_REVISIONS);

        JazzChangeSet changeSet = result.get("1714");
        assertEquals("The number of files in the changesets was incorrect", 8, changeSet.getAffectedPaths().size());
        assertEquals("The number of work itens in the changesets was incorrect", 2, changeSet.getWorkItems().size());

        JazzChangeSet.Item item = changeSet.getItems().get(0);
        assertTrue("The file is not the expected one", item.getPath().endsWith("GerenteOferta.java"));
        assertEquals("The edit type is not the expected one", EditType.EDIT, item.getEditType());

        item = changeSet.getItems().get(4);
        assertTrue("The file is not the expected one", item.getPath().endsWith("ISERetirarOfertas.java"));
        assertEquals("The edit type is not the expected one", EditType.ADD, item.getEditType());

        String workItem = changeSet.getWorkItems().get(0);
        assertTrue("The work item is not the expected one", workItem.startsWith("516"));

    }

    @Test
    public void acceptCommandArguments() throws Exception {
        AcceptCommand cmd = new AcceptCommand(config, Arrays.asList(TEST_REVISIONS));

        assertEquals("accept -u user -P password -d c:\\test --flow-components -o -v -c 1652 1625 1650 1651 1648 1657 1714 1645 1640", cmd.getArguments().toStringWithQuote());
    }

    @Test
    public void acceptCommandParse() throws Exception {
        AcceptCommand cmd = new AcceptCommand(config, Arrays.asList(TEST_REVISIONS));
        Map<String, JazzChangeSet> result = callParser(cmd, "scm-accept.txt", 9, TEST_REVISIONS);

        JazzChangeSet changeSet = result.get("1714");
        assertEquals("The number of files in the changesets was incorrect", 8, changeSet.getAffectedPaths().size());
        assertEquals("The number of work itens in the changesets was incorrect", 2, changeSet.getWorkItems().size());

        JazzChangeSet.Item item = changeSet.getItems().get(0);
        assertTrue("The file is not the expected one", item.getPath().endsWith("GerenteOferta.java"));
        assertEquals("The edit type is not the expected one", EditType.EDIT, item.getEditType());

        item = changeSet.getItems().get(4);
        assertTrue("The file is not the expected one", item.getPath().endsWith("ISERetirarOfertas.java"));
        assertEquals("The edit type is not the expected one", EditType.ADD, item.getEditType());

        String workItem = changeSet.getWorkItems().get(0);
        assertTrue("The work item is not the expected one", workItem.startsWith("516"));

    }

    private BufferedReader getReader(String fileName) {
        InputStream in = getClass().getResourceAsStream(fileName);
        return new BufferedReader(new InputStreamReader(in));
    }

}
