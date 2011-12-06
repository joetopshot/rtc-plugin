package com.deluan.jenkins.plugins.rtc.commands;

import com.deluan.jenkins.plugins.rtc.JazzConfiguration;
import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;
import hudson.scm.EditType;
import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author deluan
 */
public class ListCommand extends AbstractCommand implements ParseableCommand<Map<String, JazzChangeSet>> {
    private Collection<String> changeSets;

    public ListCommand(JazzConfiguration configurationProvider, Collection<String> changeSets) {
        super(configurationProvider);
        this.changeSets = new LinkedHashSet<String>(changeSets);
    }

    public ArgumentListBuilder getArguments() {
        ArgumentListBuilder args = new ArgumentListBuilder();

        args.add("list");
        args.add("changesets");
        addLoginArgument(args);
        addLocalWorkspaceArgument(args);
        for (String changeSet : this.changeSets) {
            args.add(changeSet);
        }

        return args;
    }

    public Map<String, JazzChangeSet> parse(BufferedReader reader) throws ParseException, IOException {
        Map<String, JazzChangeSet> result = new HashMap<String, JazzChangeSet>();

        String line;
        JazzChangeSet changeSet = null;
        Pattern startChangesetPattern = Pattern.compile("^\\s{2}\\((\\d+)\\)\\s*---[$]\\s*(\\D*)\\s+(.*)$");
        Pattern filePattern = Pattern.compile("^\\s{6}(.{5})\\s(\\S*)\\s+(.*)$");
        Pattern workItemPattern = Pattern.compile("^\\s{6}\\((\\d+)\\)\\s+(.*)$");
        Matcher matcher;

        while ((line = reader.readLine()) != null) {

            if ((matcher = startChangesetPattern.matcher(line)).matches()) {
                if (changeSet != null) {
                    result.put(changeSet.getRev(), changeSet);
                }
                changeSet = new JazzChangeSet();
                changeSet.setRev(matcher.group(1));
            } else if ((matcher = filePattern.matcher(line)).matches()) {
                assert changeSet != null;
                String path = matcher.group(3).replaceAll("\\\\", "/").trim();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                String action = EditType.EDIT.getName();
                String flag = matcher.group(1).substring(2, 3);
                if ("a".equals(flag)) {
                    action = EditType.ADD.getName();
                } else if ("d".equals(flag)) {
                    action = EditType.DELETE.getName();
                }
                changeSet.addItem(path, action);
            } else if ((matcher = workItemPattern.matcher(line)).matches()) {
                assert changeSet != null;
                changeSet.addWorkItem(matcher.group(2));
            }
        }

        if (changeSet != null) {
            result.put(changeSet.getRev(), changeSet);
        }

        return result;
    }

}
