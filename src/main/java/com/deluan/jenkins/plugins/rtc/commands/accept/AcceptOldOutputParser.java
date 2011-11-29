package com.deluan.jenkins.plugins.rtc.commands.accept;

/**
 * @author deluan
 */
public class AcceptOldOutputParser extends BaseAcceptOutputParser {

    public AcceptOldOutputParser() {
        super("^\\s{8}[^\\d\\s]+(\\d+)[^\\d]+\\s(.*)$",
                "^\\s{12}(.{3})\\s+(.*)$",
                "^\\s{12}[^\\d\\s]+(\\d+)[^\\d]+(.*)$");
    }

    @Override
    protected String parseWorkItem(String string) {
        return string.substring(0, string.length() - 1);
    }

    @Override
    protected String parseEditFlag(String string) {
        return string.substring(1, 2);
    }
}
