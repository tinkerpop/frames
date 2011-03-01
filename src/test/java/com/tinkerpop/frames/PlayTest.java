package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraphFactory;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;
import com.tinkerpop.frames.domain.relations.CreatedBy;
import com.tinkerpop.frames.domain.relations.Knows;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class PlayTest extends TestCase {

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
        for (Person know : person.getKnowsPeople()) {
            System.out.println(know.getName());
            for (Project project : know.getCreatedProjects()) {
                System.out.println("\t" + project.getName());
                for (Person p : project.getCreatedByPerson()) {
                    System.out.println("\t\t" + p.getName());
                }
            }
        }

        Project project = manager.frame(graph.getVertex(3), Project.class);
        for (CreatedBy createdBy : project.getCreatedBy()) {
            System.out.println(createdBy.getDomain().getName() + "-->" + createdBy.getRange().getAge());
        }

        Project ripple = manager.frame(graph.getVertex(5), Project.class);
        person.addCreatedProject(ripple);
        for (Project p : person.getCreatedProjects()) {
            System.out.println(person.getName() + " created " + p.getName());
        }
        Person peter = manager.frame(graph.getVertex(6), Person.class);
        peter.addCreated(ripple);
        for (Project p : person.getCreatedProjects()) {
            System.out.println(peter.getName() + " created " + p.getName());
        }

    }
}
