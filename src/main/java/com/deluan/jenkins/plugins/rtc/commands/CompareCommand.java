package com.deluan.jenkins.plugins.rtc.commands;

import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.deluan.jenkins.plugins.rtc.JazzConfiguration;
import com.deluan.jenkins.plugins.rtc.changelog.JazzChangeSet;

public class CompareCommand extends AbstractCommand implements ParseableCommand<Map<String, JazzChangeSet>> {
    private static final Logger logger = Logger.getLogger(CompareCommand.class.getName());

    private static final String DATE_FORMAT = "yyyy-MM-dd-HH:mm:ss";
    private static final String CONTRIBUTOR_FORMAT = "|{name}|{email}|";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		
    public CompareCommand(JazzConfiguration configurationProvider) {
        super(configurationProvider);
    }

    public ArgumentListBuilder getArguments() {
        ArgumentListBuilder args = new ArgumentListBuilder();

        args.add("compare");
        args.add("ws", getConfig().getWorkspaceName());
        args.add("stream", getConfig().getStreamName());
        addLoginArgument(args);
        addRepositoryArgument(args);
        args.add("-I", "s");
        args.add("-C", '"' + CONTRIBUTOR_FORMAT + '"');
        args.add("-D", "\"|" + DATE_FORMAT + "|\"");

        return args;
    }
    
    public Map<String, JazzChangeSet> parse(BufferedReader reader) throws ParseException, IOException {
        Map<String, JazzChangeSet> result = new LinkedHashMap<String, JazzChangeSet>();

        String line;
        while ((line = reader.readLine()) != null) {
            try {
                JazzChangeSet changeSet = new JazzChangeSet();
                String[] parts = line.split("\\|");
                String rev = parseRevisionNumber(parts[0]);
                changeSet.setRev(rev);
                changeSet.setUser(parts[1].trim());
                changeSet.setEmail(parts[2].trim());
                changeSet.setMsg(parseMessage(parts[3]));
                try {
                    changeSet.setDate(sdf.parse(parts[4].trim()));
                } catch (ParseException e) {
                    logger.log(Level.WARNING, "Error parsing date '" + parts[4].trim() + "' for revision (" + rev + ")");
                }
                result.put(rev, changeSet);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error parsing compare output:\n\n" + line + "\n\n", e);
            }
        }

        return result;
    }

    private String parseRevisionNumber(String part) {
        return part.replaceAll("[^0-9]", "");
    }

    private String parseMessage(String string) {
        String msg = string.trim();
        if (msg.startsWith("\"")) {
            int closingQuotes = msg.lastIndexOf('\"');
            msg = msg.substring(1, closingQuotes).trim();
        }
        return msg;
    }
}
