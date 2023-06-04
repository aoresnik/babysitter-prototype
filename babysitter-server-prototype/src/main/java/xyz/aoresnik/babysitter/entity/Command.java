package xyz.aoresnik.babysitter.entity;

import javax.persistence.*;

@Entity
@Table(name = "COMMAND")
public class Command {
    // TODO: migrate to GUID for this and all IDs that are sent via REST (requires Quarkus 3 with JEE 10 and JPA 3.1)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "COMMAND_SCRIPT")
    private String commandScript;

    @ManyToOne
    @JoinColumn(name = "COMMAND_SOURCE_ID")
    private CommandSource commandSource;
}
