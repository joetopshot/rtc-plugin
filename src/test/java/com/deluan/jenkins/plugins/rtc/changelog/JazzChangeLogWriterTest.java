package com.deluan.jenkins.plugins.rtc.changelog;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author deluan
 */
public class JazzChangeLogWriterTest {

    JazzChangeLogWriter changeLogWriter;

    @Before
    public void setUp() {
        changeLogWriter = new JazzChangeLogWriter();
    }

    @Test
    public void testEscapeNullString() {
        String escaped = changeLogWriter.escapeForXml(null);

        assertThat(escaped, is(nullValue()));
    }

    @Test
    public void testEscapeEmptyString() {
        String original = "";
        String escaped = changeLogWriter.escapeForXml(original);

        assertThat(escaped, is(equalTo(original)));
    }

    @Test
    public void testEscapeAllSpecialChars() {
        String original = "AB&<>\'\"CD";
        String escaped = changeLogWriter.escapeForXml(original);
        String expected = "AB&amp;&lt;&gt;&apos;&quot;CD";

        assertThat(escaped, is(equalTo(expected)));
    }

    @Test
    public void testRepeatedSpecialChar() {
        String original = "&&";
        String escaped = changeLogWriter.escapeForXml(original);
        String expected = "&amp;&amp;";

        assertThat(escaped, is(equalTo(expected)));
    }
}
