package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.sail.SailGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.memory.MemoryStore;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class SailFramesTest {
    private Sail sail;
    private SailGraph sailGraph;

    @Before
    public void setUp() throws Exception {
        sail = new MemoryStore();
        sailGraph = new SailGraph(sail);
    }

    @After
    public void tearDown() throws Exception {
        sailGraph.shutdown();
    }

    @Test
    public void testAll() throws Exception {
        URI planet = new URIImpl("http://example.org/terms/planet");
        URI gasGiant = new URIImpl("http://example.org/terms/gasGiant");
        URI narrower = new URIImpl("http://www.w3.org/2004/02/skos/core#narrower");

        SailConnection sc = sail.getConnection();
        try {
            sc.addStatement(planet, RDFS.LABEL, new LiteralImpl("planet", "en"));
            sc.addStatement(gasGiant, RDFS.LABEL, new LiteralImpl("gas giant", "en"));
            sc.addStatement(planet, narrower, gasGiant);
            sc.commit();
        } finally {
            sc.close();
        }

        Vertex p = sailGraph.getVertex(planet.stringValue());

        FramesManager manager = new FramesManager(sailGraph);
        Concept planetFrame = manager.frame(p, Concept.class);
        assertNotNull(planetFrame);

        assertEquals("uri", planetFrame.getKind());
        //assertEquals("...", planetFrame.getValue());
        RDFFrame label = planetFrame.getLabel();
        assertNotNull(label);
        assertEquals("literal", label.getKind());
        assertEquals("en", label.getLang());
        assertEquals("planet", label.getValue());

        Collection<Concept> narrowerConcepts = planetFrame.getNarrower();
        assertEquals(1, narrowerConcepts.size());
        Concept gasGiantFrame = narrowerConcepts.iterator().next();
        label = gasGiantFrame.getLabel();
        assertEquals("literal", label.getKind());
        assertEquals("en", label.getLang());
        assertEquals("gas giant", label.getValue());
    }

    private interface RDFFrame {
        @Property("value")
        public String getValue();

        @Property("kind")
        public String getKind();

        @Property("type")
        public String getType();

        @Property("lang")
        public String getLang();
    }

    private interface Concept extends RDFFrame {
        @Relation(label = "http://www.w3.org/2004/02/skos/core#narrower")
        public Collection<Concept> getNarrower();

        @Relation(label = "http://www.w3.org/2000/01/rdf-schema#label")
        public RDFFrame getLabel();
    }
}
