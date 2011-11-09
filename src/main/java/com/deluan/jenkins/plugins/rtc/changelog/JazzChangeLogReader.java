package com.deluan.jenkins.plugins.rtc.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.util.Digester2;
import hudson.util.IOException2;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author deluan
 */
public class JazzChangeLogReader extends ChangeLogParser {


    @Override
    public ChangeLogSet<? extends ChangeLogSet.Entry> parse(AbstractBuild build, File changelogFile) throws IOException, SAXException {
        Digester digester = new Digester2();
        List<JazzChangeSet> result = new ArrayList<JazzChangeSet>();
        digester.push(result);

        digester.addObjectCreate("*/changeset", JazzChangeSet.class);
        digester.addSetProperties("*/changeset");
        digester.addBeanPropertySetter("*/changeset/date", "dateStr");
        digester.addBeanPropertySetter("*/changeset/user");
        digester.addBeanPropertySetter("*/changeset/email");
        digester.addBeanPropertySetter("*/changeset/comment", "msg");
        digester.addSetNext("*/changeset", "add");

        digester.addObjectCreate("*/changeset/files/file", JazzChangeSet.Item.class);
        digester.addSetProperties("*/changeset/files/file");
        digester.addBeanPropertySetter("*/changeset/files/file", "path");
        digester.addSetNext("*/changeset/files/file", "addItem");

        digester.addCallMethod("*/changeset/workitems/workitem", "addWorkItem", 1);
        digester.addCallParam("*/changeset/workitems/workitem", 0);

        try {
            digester.parse(changelogFile);
        } catch (IOException e) {
            throw new IOException2("Failed to parse " + changelogFile, e);
        } catch (SAXException e) {
            throw new IOException2("Failed to parse " + changelogFile, e);
        }

        return new JazzChangeSetList(build, result);

    }
}
