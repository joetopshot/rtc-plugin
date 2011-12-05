package com.deluan.jenkins.plugins.rtc;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JazzSCMTest {

    @Test
    public void getNullPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", null);

        assertThat(scm.getPassword(), is(""));
    }

    @Test
    public void getEmptyPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", "");

        assertThat(scm.getPassword(), is(""));
    }

    @Test
    @Ignore // only works as an integration test (needs a Hudson instance)
    public void getNotNullPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", "secret");

        assertThat(scm.getPassword(), is("secret"));
    }
}
