package com.deluan.jenkins.plugins.changelog;

import hudson.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * User: deluan
 * Date: 21/10/11
 */
public class JazzChangeLogFormatter {

    public void format(List<JazzChangeSet> changeSetList, File changelogFile) throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(changelogFile));
        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<changelog>");
        for (JazzChangeSet changeSet : changeSetList) {
            writer.println(String.format("\t<changeset rev=\"%s\">", changeSet.getRev()));
//            writer.println(String.format("\t\t<date>%s</date>", Util.XS_DATETIME_FORMATTER.format(changeSet.getDate())));
            writer.println(String.format("\t\t<date>%s</date>", changeSet.getDate()));
            writer.println(String.format("\t\t<author>%s</author>", changeSet.getAuthor()));
            writer.println(String.format("\t\t<email>%s</email>", changeSet.getEmail()));
            writer.println(String.format("\t\t<msg>%s</msg>", Util.escape(changeSet.getMsg())));
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
}
