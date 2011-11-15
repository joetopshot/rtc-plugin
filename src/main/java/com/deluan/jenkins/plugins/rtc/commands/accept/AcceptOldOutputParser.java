package com.deluan.jenkins.plugins.rtc.commands.accept;

import hudson.scm.EditType;

/**
 * @author deluan
 */
public class AcceptOldOutputParser extends BaseAcceptOutputParser {

    public AcceptOldOutputParser() {
        super("^\\s{8}\\((\\d+)\\)\\s(.*)$",
                "^\\s{12}(.{3})\\s+(.*)$",
                "^\\s{12}\\((\\d+)\\)\\s+(.*)$");
    }

    protected String parseWorkItem(String string) {
        return string.substring(1, string.length() - 1);
    }

    protected String parseAction(String string) {
        String flag = string.substring(1, 2);
        String action = EditType.EDIT.getName();
        if ("a".equals(flag)) {
            action = EditType.ADD.getName();
        } else if ("d".equals(flag)) {
            action = EditType.DELETE.getName();
        }
        return action;
    }
}
