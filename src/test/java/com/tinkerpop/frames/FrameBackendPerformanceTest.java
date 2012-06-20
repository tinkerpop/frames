package com.tinkerpop.frames;

import java.util.Random;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexTestSuite;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.proxy.AsmProxyGenerator;
import com.tinkerpop.frames.proxy.CGLibProxyGenerator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

/**
 * Test that attempts to benchmark different frame implementations
 *
 * @author Greg Bowyer
 */
@BenchmarkOptions(benchmarkRounds = 50, warmupRounds = 5)
public class FrameBackendPerformanceTest {

    private final Graph graph = TinkerGraphFactory.createTinkerGraph();

    @Rule public MethodRule benchmarkRun = new BenchmarkRule();

    @Before
    public void createGraph() {
        Random random = new Random();
        for (int i=0; i<50000; i++) {
            int id = random.nextInt();
            Vertex relation = null;

            if (graph.getVertex(id) != null) {
                relation = graph.getVertex(id);
            }
            while(graph.getVertex(id) != null) {
                id = random.nextInt();
            }

            Vertex vertex = graph.addVertex(id);
            vertex.setProperty("age", random.nextInt());
            if (relation != null) {
                try {
                    Edge edge = graph.addEdge(i+1, relation, vertex, "knows");
                    edge.setProperty("weight", i);
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }

    @Test
    public void testJavaProxyFrames() throws Exception {
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph);
        Iterable<Person> person = framedGraph.frameVertices(framedGraph.getVertices(), Person.class);
        for (Person p : person) {
            p.getAge();
        }
    }

    @Test
    public void testJavaCgLibFrames() throws Exception {
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new CGLibProxyGenerator());
        Iterable<Person> person = framedGraph.frameVertices(framedGraph.getVertices(), Person.class);
        for (Person p : person) {
            p.getAge();
        }
    }

    @Test
    public void testJavaAsmFrames() throws Exception {
        FramedGraph<Graph> framedGraph = new FramedGraph<Graph>(graph, new AsmProxyGenerator());
        Iterable<Person> person = framedGraph.frameVertices(framedGraph.getVertices(), Person.class);
        for (Person p : person) {
            p.getAge();
        }
    }

}
