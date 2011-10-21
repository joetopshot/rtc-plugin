package com.deluan.jenkins.plugins;

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

    private String author;
    private String date;
    private String rev;
    private String msg;

    JazzChangeSet(String line) {
        System.out.println(line);
        String[] parts = line.split("\\|");
        this.rev = parts[0].trim().substring(1);
        this.rev = rev.substring(0, rev.length() - 1);
        this.author = parts[1].trim();
        this.author = author + " <" + parts[1].trim() + ">";
        this.msg = parts[3].trim();
        this.date = parts[4].trim();
    }

    @Exported
    public String getMsg() {
        return msg;
    }

    @Exported
    public User getAuthor() {
        return User.get(author);
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

}
