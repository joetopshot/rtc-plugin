package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.scm.EditType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandTest extends BaseCommandTest {

    public static final String[] TEST_REVISIONS = new String[]{"1714", "1657", "1652", "1651", "1650", "1648", "1645", "1640", "1625"};

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
}
