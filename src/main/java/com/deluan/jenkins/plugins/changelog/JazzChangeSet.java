package com.deluan.jenkins.plugins.changelog;

import hudson.model.User;
import hudson.scm.ChangeLogSet;
import org.kohsuke.stapler.export.Exported;

import java.util.Collection;
import java.util.Collections;

/**
 * {@link hudson.scm.ChangeLogSet} for IBM Rational Team Concert Source Code Management
 *
 * @author Deluan Cotts Quintao
 */
public final class JazzChangeSet extends ChangeLogSet.Entry {
    public static final String DATE_FORMAT = "|yyyy-MM-dd-HH:mm:ss|";
    public static final String CONTRIBUTOR_FORMAT = "|{name}|{email}|";

    private String user;
    private String date;
    private String rev;
    private String msg;

    /**
     * Default constructor needed for the Commons Digester library, used in JazzChangeLogParser
     */
    public JazzChangeSet() {
    }

    @Exported
    public String getMsg() {
        return msg;
    }

    @Exported
    public User getAuthor() {
        return User.get(user);
    }

    @Exported
    public String getUser() {
        return user;
    }

    @Exported
    public String getDate() {
        return date;
    }

    @Exported
    public String getRev() {
        return rev;
    }

    @Override
    public Collection<String> getAffectedPaths() {
        // TODO How to get this information?
        return Collections.emptyList();
    }

    @Override
    protected void setParent(ChangeLogSet parent) {
        super.setParent(parent);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMsgEscaped(String msgEscaped) {
        this.msg = msgEscaped.replaceAll("&lt;", "<").replaceAll("&amp;", "&");
    }
}
