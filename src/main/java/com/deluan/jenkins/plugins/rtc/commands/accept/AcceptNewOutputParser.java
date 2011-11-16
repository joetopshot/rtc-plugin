package com.deluan.jenkins.plugins.rtc.commands.accept;

/**
 * @author deluan
 */
public class AcceptNewOutputParser extends BaseAcceptOutputParser {

    public AcceptNewOutputParser() {
        super("^\\s{8}\\((\\d+)\\)\\s*---[$]\\s*(\\D*)\\s+(.*)$",
                "^\\s{12}(.{5})\\s+(.*)$",
                "^\\s{12}\\((\\d+)\\)\\s+(.*)$");
    }

    @Override
    protected String parseWorkItem(String string) {
        return string;
    }

    @Override
    protected String parseEditFlag(String string) {
        return string.substring(2, 3);
    }
}
