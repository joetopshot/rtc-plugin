package com.deluan.jenkins.plugins.rtc;

import hudson.FilePath;
import net.vidageek.mirror.dsl.Mirror;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @author deluan
 */
public class JazzConfigurationTest {

    JazzConfiguration configuration;

    @Before
    public void setUp() {
        configuration = createConfiguration();
    }

    @Test
    public void testAllSettersCalled() throws Exception {
        assertNoNullFields(configuration);
    }

    @Test
    public void testClone() throws Exception {
        JazzConfiguration clone = configuration.clone();

        assertNoNullFields(clone);
    }

    private void assertNoNullFields(JazzConfiguration clone) {
        List<Field> fields = new Mirror().on(JazzConfiguration.class).reflectAll().fields();

        for (Field field : fields) {
            Object value = new Mirror().on(clone).get().field(field);
            assertNotNull("Field " + field.getName() + " is null", value);
        }
    }

    private JazzConfiguration createConfiguration() {
        JazzConfiguration configuration = new JazzConfiguration();

        configuration.setRepositoryLocation("repo");
        configuration.setWorkspaceName("workspace");
        configuration.setStreamName("stream");
        configuration.setUsername("user");
        configuration.setPassword("password");
        configuration.setJobWorkspace(new FilePath(new File("job")));
        configuration.setUseTimeout(true);
        configuration.setTimeoutValue(1L);
        return configuration;
    }
}
