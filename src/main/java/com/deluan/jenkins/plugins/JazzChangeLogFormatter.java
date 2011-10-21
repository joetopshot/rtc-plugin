package com.deluan.jenkins.plugins;

import hudson.Util;
import hudson.util.Digester2;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: deluan
 * Date: 21/10/11
 */
class JazzChangeLogFormatter {

    void format(List<JazzChangeSet> changeSetList, File changelogFile) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(changelogFile));
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<changelog>");
        for (JazzChangeSet changeSet : changeSetList) {
            writer.println(String.format("\t<changeset rev=\"%s\">", changeSet.getRev()));
            writer.println(String.format("\t\t<date>%s</date>", Util.XS_DATETIME_FORMATTER.format(changeSet.getDate())));
            writer.println(String.format("\t\t<author>%s</author>", changeSet.getAuthor()));
            writer.println(String.format("\t\t<message>%s</message>", changeSet.getMsg()));
//            writer.println("\t\t<items>");
//            for (TeamFoundationChangeSet.Item item : changeSet.getItems()) {
//                writer.println(String.format("\t\t\t<item action=\"%s\">%s</item>", item.getAction(), item.getPath()));
//            }
//            writer.println("\t\t</items>");
            writer.println("\t</changeset>");
        }
        writer.println("</changelog>");
        writer.close();
    }

    List<JazzChangeSet> parse(File changelogFile) throws IOException, SAXException {
        List<JazzChangeSet> changesetList = new ArrayList<JazzChangeSet>();
        Digester digester = new Digester2();
        digester.push(changesetList);

        // When digester reads a {{<changeset>}} node it will create a {{JazzChangeSet}} object
        digester.addObjectCreate("*/changeset", JazzChangeSet.class);
        // Reads all attributes in the {{<changeset>}} node and uses setter method in class to set the values
        digester.addSetProperties("*/changeset");
        // Reads the child node {{<comment>}} and uses {{JazzChangeSet.setComment()}} to set the value
        digester.addBeanPropertySetter("*/changeset/message");
        digester.addBeanPropertySetter("*/changeset/author");
        // Reading the {{<date<}} child node will use the {{JazzChangeSet.setDateStr()}} method
        // instead of the default {{JazzChangeSet.setDate()}}
        digester.addBeanPropertySetter("*/changeset/date");
        // The digested node/change set is added to the list through {{List.add()}}
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

        return changesetList;
    }

}
