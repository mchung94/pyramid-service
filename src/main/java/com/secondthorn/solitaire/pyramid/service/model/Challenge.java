package com.secondthorn.solitaire.pyramid.service.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

/**
 * Base class for Board/Score/Card challenges.  The main idea is that the ID
 * must be unique across all types of challenges.  The subclasses all have the
 * card deck in common, however, it's not stored here since the uniqueness
 * constraint is different (the deck must be unique for all board challenges
 * but duplicate decks must be allowed for score/card challenges).
 */
@Entity
@Table(name = "challenges")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Challenge {
    // the deck is common to all subclasses, but it's with the subclasses
    // because some require it to be unique and others require allowing
    // duplicate decks.
    public abstract String getDeckString();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "challenge_id", nullable = false, updatable = false)
    protected Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "challenge")
    @OrderBy("solution_id")
    protected List<Solution> solutions;

    public Long getId() {
        return id;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }
}
