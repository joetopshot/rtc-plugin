package com.deluan.jenkins.plugins.rtc.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

/**
 * Command that issues a scm command line client command and parses its output.
 *
 * @param <T> the return type when parsing the output from the command line client.
 * @author Erik Ramfelt
 */
public interface ParseableCommand<T> extends Command {

    /**
     * Returns data from parsing the command line client output in reader
     *
     * @param reader reader containing the output from the command line client
     * @return parsed data
     * @throws java.text.ParseException thrown if there was a problem parsing the data
     * @throws java.io.IOException      thrown if there was a problem reading the data from the reader
     */
    T parse(BufferedReader reader) throws ParseException, IOException;
}
