package com.deluan.jenkins.plugins.rtc;

import org.jvnet.hudson.test.HudsonTestCase;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JazzSCMTest extends HudsonTestCase {

    public void testGetNullPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", null, false, 1L);

        assertThat(scm.getPassword(), is(""));
    }

    public void testGetEmptyPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", "", false, 1L);

        assertThat(scm.getPassword(), is(""));
    }

    public void testGetNotNullPassword() throws Exception {
        JazzSCM scm = new JazzSCM("http://xxx", "workspace", "stream", "user", "secret", false, 1L);

        assertThat(scm.getPassword(), is("secret"));
    }
}
