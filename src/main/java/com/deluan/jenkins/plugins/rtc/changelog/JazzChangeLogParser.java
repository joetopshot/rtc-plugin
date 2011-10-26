package com.deluan.jenkins.plugins.rtc.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: deluan
 * Date: 21/10/11
 */
public class JazzChangeLogParser extends ChangeLogParser {


    @Override
    public ChangeLogSet<? extends ChangeLogSet.Entry> parse(AbstractBuild build, File changelogFile) throws IOException, SAXException {
        List<JazzChangeSet> changesetList = new ArrayList<JazzChangeSet>();

        BufferedReader in = new BufferedReader(new FileReader(changelogFile));

        try {
            String lineRaw;
            Boolean incoming = false;
            JazzChangeSet changeSet = null;
            Pattern startIncomingPattern = Pattern.compile("^\\s{4}Entrada:$"); // Incoming //FIXME How to force english?!
            Pattern endIncomingPattern = Pattern.compile("^\\s{4}\\w*$");
            Pattern startChangesetPattern = Pattern.compile("^\\s{8}\\((\\d+)\\)\\s*---[$]\\s*(\\D*)\\s+(.*)$");
            Pattern filePattern = Pattern.compile("^\\s{12}(.{5})\\s(\\S*)\\s+(.*)$");
            Pattern workItemPattern = Pattern.compile("^\\s{12}\\((\\d+)\\)\\s+(.*)$");
            Matcher matcher = null;

            while ((lineRaw = in.readLine()) != null) {
                String line = new String(lineRaw.getBytes(), "ISO-8859-1"); //TODO Fix char encoding

                if (startIncomingPattern.matcher(line).matches()) {
                    incoming = true;
                } else if (endIncomingPattern.matcher(line).matches()) {
                    incoming = false;
                } else if (incoming) {
                    if ((matcher = startChangesetPattern.matcher(line)).matches()) {
                        if (changeSet != null) {
                            changesetList.add(changeSet);
                        }
                        changeSet = new JazzChangeSet();
                        changeSet.setRev(matcher.group(1));
                        changeSet.setUser(matcher.group(2));
                        changeSet.setMsg(matcher.group(3));
                    } else if ((matcher = filePattern.matcher(line)).matches()) {
                        // TODO
                        // flags = matcher.group(1)
                        // path = matcher.group(2)
                        // movedFrom = matcher.group(3)
                    } else if ((matcher = workItemPattern.matcher(line)).matches()) {
                        // TODO
                        // workItem = matcher.group(2)
                    }
                }
            }

            if (changeSet != null) {
                changesetList.add(changeSet);
            }
        } finally {
            in.close();
        }

        return new JazzChangeSetList(build, changesetList);
    }
}
