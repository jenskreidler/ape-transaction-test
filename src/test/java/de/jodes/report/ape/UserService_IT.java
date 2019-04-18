package de.jodes.report.ape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.persistence.ApplyScriptBefore;
import org.jboss.arquillian.persistence.ShouldMatchDataSet;
import org.jboss.arquillian.persistence.UsingDataSet;
import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.api.web.WebAppDescriptor;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Transactional // may be void - transaction support is also declared in arquillian.xml
public class UserService_IT {

    @Deployment
    public static WebArchive createDeployment() {
        PomEquippedResolveStage stage = Maven.resolver().loadPomFromFile("pom.xml");
        File[] libs = stage.importCompileAndRuntimeDependencies().resolve().withTransitivity().asFile();

        WebArchive archive = ShrinkWrap.create(WebArchive.class, UserService_IT.class.getSimpleName() + ".war")
                .addAsLibraries(libs)
                // Java11 manifest entry (must be resolved in future)
                .addAsManifestResource(new StringAsset("Dependencies: jdk.unsupported\n"), "MANIFEST.MF")
                .addPackage("de.jodes.report.ape")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .setWebXML(new StringAsset(Descriptors.create(WebAppDescriptor.class).version("4.0")
                        .exportAsString()))
                .addAsResource("persistence-dev.xml", "META-INF/persistence.xml");
        return archive;
    }

    @Inject
    private UserService userService;

    @Test
    @InSequence(value = 1)
    @ApplyScriptBefore({ "SET REFERENTIAL_INTEGRITY false" })
    @UsingDataSet // seeds one user "99/foo99"
    public void testPersistUsers() {
        User u = new User("foo");
        User saved = userService.saveUser(u);
        assertEquals("foo", saved.getLogin());
        assertNotNull(saved.getId());
    }

    @Test
    @InSequence(value = 2)
    @ApplyScriptBefore({ "SET REFERENTIAL_INTEGRITY false", "ALTER SEQUENCE user_seq RESTART WITH 102" })
    @UsingDataSet // seeds two users "100/alpha100" and "101/alpha101"
    @ShouldMatchDataSet(orderBy = { "id" }) // asserts that tree users, including new "102/alpha102" are present
    public void testPersistAdditionalUsers() {
        assertEquals("alpha100", userService.getUser(100L).getLogin());
        assertEquals("alpha101", userService.getUser(101L).getLogin());

        User u = new User("alpha102"); // should get id 102
        User saved = userService.saveUser(u);
        assertEquals("alpha102", saved.getLogin());
        assertEquals(Long.valueOf(102), saved.getId());

        //
        // You should see, that no commit nor a cleanup of the previous test method has been performed :-(
        // Using arquillian-persistence 1.1.13.0, it succeeds, with recent 1.2.0.2 it fails.
        //
    }
}
