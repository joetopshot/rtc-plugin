package com.deluan.jenkins.plugins.rtc.changelog;

import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;
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
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setNormalizeWhitespace(true);
        XMLUnit.setIgnoreComments(true);
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

    @Test
    public void testWriteEmptyChangeSet() throws Exception {
        StringWriter output = generateChangeSetXml(null);

        assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<changelog></changelog>",
                output.getBuffer().toString());
    }

    @Test
    public void testWriteCompleteChangeSet() throws Exception {
        JazzChangeSet changeSet = createBasicChangeSet();
        changeSet.addItem("test/Class1.java", "delete");
        changeSet.addItem("test/Class2.java", "add");
        changeSet.addWorkItem("501 \"Just a test\"");

        StringWriter output = generateChangeSetXml(changeSet);

        assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<changelog>" +
                "<changeset rev=\"" + changeSet.getRev() + "\">" +
                "<date>" + changeSet.getDateStr() + "</date>" +
                "<user>" + changeSet.getUser() + "</user>" +
                "<email>" + changeSet.getEmail() + "</email>" +
                "<comment>" + changeSet.getMsg() + "</comment>" +
                "<files>" +
                "<file action=\"delete\">test/Class1.java</file>" +
                "<file action=\"add\">test/Class2.java</file>" +
                "</files>" +
                "<workitems>" +
                "<workitem>501 \"Just a test\"</workitem>" +
                "</workitems>" +
                "</changeset>" +
                "</changelog>",
                output.getBuffer().toString());
    }

    @Test
    public void testWriteChangeSetWithoutItems() throws Exception {
        JazzChangeSet changeSet = createBasicChangeSet();
        changeSet.addWorkItem("501 \"Just a test\"");

        StringWriter output = generateChangeSetXml(changeSet);

        assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<changelog>" +
                "<changeset rev=\"" + changeSet.getRev() + "\">" +
                "<date>" + changeSet.getDateStr() + "</date>" +
                "<user>" + changeSet.getUser() + "</user>" +
                "<email>" + changeSet.getEmail() + "</email>" +
                "<comment>" + changeSet.getMsg() + "</comment>" +
                "<workitems>" +
                "<workitem>501 \"Just a test\"</workitem>" +
                "</workitems>" +
                "</changeset>" +
                "</changelog>",
                output.getBuffer().toString());
    }

    @Test
    public void testWriteChangeSetWithoutWorkItems() throws Exception {
        JazzChangeSet changeSet = createBasicChangeSet();
        changeSet.addItem("test/Class1.java", "delete");
        changeSet.addItem("test/Class2.java", "add");

        StringWriter output = generateChangeSetXml(changeSet);

        assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<changelog>" +
                "<changeset rev=\"" + changeSet.getRev() + "\">" +
                "<date>" + changeSet.getDateStr() + "</date>" +
                "<user>" + changeSet.getUser() + "</user>" +
                "<email>" + changeSet.getEmail() + "</email>" +
                "<comment>" + changeSet.getMsg() + "</comment>" +
                "<files>" +
                "<file action=\"delete\">test/Class1.java</file>" +
                "<file action=\"add\">test/Class2.java</file>" +
                "</files>" +
                "</changeset>" +
                "</changelog>",
                output.getBuffer().toString());
    }

    @Test
    public void testWriteChangeSetWithoutAnyChild() throws Exception {
        JazzChangeSet changeSet = createBasicChangeSet();

        StringWriter output = generateChangeSetXml(changeSet);

        assertXMLEqual("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<changelog>" +
                "<changeset rev=\"" + changeSet.getRev() + "\">" +
                "<date>" + changeSet.getDateStr() + "</date>" +
                "<user>" + changeSet.getUser() + "</user>" +
                "<email>" + changeSet.getEmail() + "</email>" +
                "<comment>" + changeSet.getMsg() + "</comment>" +
                "</changeset>" +
                "</changelog>",
                output.getBuffer().toString());
    }

    private StringWriter generateChangeSetXml(JazzChangeSet changeSet) throws Exception {
        Collection<JazzChangeSet> changeSetList = new HashSet<JazzChangeSet>();
        if (changeSet != null) {
            changeSetList.add(changeSet);
        }
        StringWriter output = new StringWriter();
        changeLogWriter.write(changeSetList, output);
        return output;
    }

    private JazzChangeSet createBasicChangeSet() throws Exception {
        Long revNumber = Math.round(Math.random() * 10000);
        JazzChangeSet changeSet = new JazzChangeSet();
        changeSet.setRev(revNumber.toString());
        changeSet.setDate(new Date());
        changeSet.setUser("deluan");
        changeSet.setMsg("comment");
        changeSet.setEmail("deluan@email.com.br");

        return changeSet;
    }
}
