package com.deluan.jenkins.plugins.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.util.Digester2;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: deluan
 * Date: 21/10/11
 */
public class JazzChangeLogParser extends ChangeLogParser {

    @Override
    public ChangeLogSet<? extends ChangeLogSet.Entry> parse(AbstractBuild build, File changelogFile) throws IOException, SAXException {
        List<JazzChangeSet> changesetList = new ArrayList<JazzChangeSet>();
        Digester digester = new Digester2();
        digester.push(changesetList);

        digester.addObjectCreate("*/changeset", JazzChangeSet.class);
        digester.addSetProperties("*/changeset");
        digester.addBeanPropertySetter("*/changeset/msg", "msgEscaped");
        digester.addBeanPropertySetter("*/changeset/user");
        digester.addBeanPropertySetter("*/changeset/email");
        digester.addBeanPropertySetter("*/changeset/date");
        digester.addSetNext("*/changeset", "add");


        // When digester reads a {{<items>}} child node of {{<changeset}} it will create a {{TeamFoundationChangeSet.Item}} object
//        digester.addObjectCreate("*/changeset/items/item", JazzChangeSet.Item.class);
//        digester.addSetProperties("*/changeset/items/item");
//        digester.addBeanPropertySetter("*/changeset/items/item", "path");
//        // The digested node/item is added to the change set through {{TeamFoundationChangeSet.add()}}
//        digester.addSetNext("*/changeset/items/item", "add");

        // Do the actual parsing
        FileReader reader = new FileReader(changelogFile);
        digester.parse(reader);
        reader.close();

        return new JazzChangeSetList(build, changesetList);
    }

}
