package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CompareCommandTest extends BaseCommandTest {

    public static final String[] TEST_REVISIONS = new String[]{"1714", "1657", "1652", "1651", "1650", "1648", "1645", "1640", "1625"};

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

}
