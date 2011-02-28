package com.tinkerpop.frames.example;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.FramesManager;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ExampleTest extends TestCase {

    public void testPlay() {
        Graph graph = TinkerGraphFactory.createTinkerGraph();
        FramesManager manager = new FramesManager(graph);
        Person person = manager.frame(graph.getVertex(1), Person.class);

        System.out.println(person.getName());

        person.setName("jen");
        System.out.println(person.getName());

        for (Knows know : person.getKnows()) {
            System.out.println(know.getWeight() + "---" + know.getRange().getName());
        }

        System.out.println("---");
        for (Person know : person.getKnowsPerson()) {
            System.out.println(know.getName());
            for (Project project : know.getCreatedProject()) {
                System.out.println("\t" + project.getName());
                for (Person p : project.getCreatedByPerson()) {
                    System.out.println("\t\t" + p.getName());
                }
            }
        }

        Project project = manager.frame(graph.getVertex(3), Project.class);
        for(CreatedBy createdBy : project.getCreatedBy()){
            System.out.println(createdBy.getDomain().getName() + "-->"  + createdBy.getRange().getAge());
        }

    }
}
