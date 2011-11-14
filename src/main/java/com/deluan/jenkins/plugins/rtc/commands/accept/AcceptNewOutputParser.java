package com.deluan.jenkins.plugins.rtc.commands.accept;

import hudson.scm.EditType;

/**
 * @author deluan
 */
public class AcceptNewOutputParser extends BaseAcceptOutputParser {

    public AcceptNewOutputParser() {
        super("^\\s{8}\\((\\d+)\\)\\s*---[$]\\s*(\\D*)\\s+(.*)$",
                "^\\s{12}(.{5})\\s+(.*)$",
                "^\\s{12}\\((\\d+)\\)\\s+(.*)$");
    }

    protected String parseWorkItem(String string) {
        return string;
    }

    protected String parseAction(String string) {
        String flag = string.substring(2, 3);
        String action = EditType.EDIT.getName();
        if ("a".equals(flag)) {
            action = EditType.ADD.getName();
        } else if ("d".equals(flag)) {
            action = EditType.DELETE.getName();
        }
        return action;
    }
}
