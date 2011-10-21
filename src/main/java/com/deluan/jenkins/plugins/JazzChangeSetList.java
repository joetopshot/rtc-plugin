package com.deluan.jenkins.plugins;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * User: deluan
 * Date: 21/10/11
 */
public class JazzChangeSetList extends ChangeLogSet<JazzChangeSet> {
    private final List<JazzChangeSet> changeSets;

    JazzChangeSetList(AbstractBuild build, List<JazzChangeSet> logs) {
        super(build);
        Collections.reverse(logs);  // put new things first
        this.changeSets = Collections.unmodifiableList(logs);
        for (JazzChangeSet log : logs)
            log.setParent(this);
    }

    public boolean isEmptySet() {
        return changeSets.isEmpty();
    }

    public Iterator<JazzChangeSet> iterator() {
        return changeSets.iterator();
    }

    public List<JazzChangeSet> getLogs() {
        return changeSets;
    }

    public
    @Override
    String getKind() {
        return "rtc";
    }

}
