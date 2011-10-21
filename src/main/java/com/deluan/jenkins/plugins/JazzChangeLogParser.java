package com.deluan.jenkins.plugins;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: deluan
 * Date: 21/10/11
 */
public class JazzChangeLogParser extends ChangeLogParser {
    JazzChangeLogFormatter formatter = new JazzChangeLogFormatter();

    @Override
    public ChangeLogSet<? extends ChangeLogSet.Entry> parse(AbstractBuild build, File changelogFile) throws IOException, SAXException {
        List<JazzChangeSet> r = formatter.parse(changelogFile);

        return new JazzChangeSetList(build, r);
    }
}
