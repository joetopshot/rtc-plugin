package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.scm.EditType;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AcceptCommandTest extends BaseCommandTest {

    public static final String[] TEST_REVISIONS = new String[]{"1714", "1657", "1652", "1651", "1650", "1648", "1645", "1640", "1625"};

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

    @Test
    @Ignore
    public void acceptCommandParseUnix() throws Exception {
        AcceptCommand cmd = new AcceptCommand(config, Arrays.asList(TEST_REVISIONS));
        Map<String, JazzChangeSet> result = callParser(cmd, "scm-accept-unix.txt", 4, new String[]{"1002", "1001", "1008", "1009"});

        JazzChangeSet changeSet = result.get("1002");
        assertEquals("The number of files in the changesets was incorrect", 8, changeSet.getAffectedPaths().size());
        assertEquals("The number of work itens in the changesets was incorrect", 1, changeSet.getWorkItems().size());

        JazzChangeSet.Item item = changeSet.getItems().get(3);
        assertTrue("The file is not the expected one", item.getPath().endsWith("readme_ja.html"));
        assertEquals("The edit type is not the expected one", EditType.EDIT, item.getEditType());

        item = changeSet.getItems().get(4);
        assertTrue("The file is not the expected one", item.getPath().endsWith("readme.html"));
        assertEquals("The edit type is not the expected one", EditType.ADD, item.getEditType());

        String workItem = changeSet.getWorkItems().get(0);
        assertTrue("The work item is not the expected one", workItem.startsWith("3076"));
    }

}
