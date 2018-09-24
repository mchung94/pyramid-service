package com.secondthorn.solitaire.pyramid.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "steps",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "solution_id",
                "step_number",
                "action"
        })
)
public class Step {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "step_id", nullable = false, updatable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "solution_id")
    private Solution solution;

    @Column(name = "step_number", nullable = false, updatable = false, columnDefinition = "int")
    private Integer stepNumber;

    @Column(name = "action", nullable = false, updatable = false, columnDefinition = "varchar(16)")
    private String action;

    protected Step() {
    }

    public Step(Integer stepNumber, String action) {
        this.stepNumber = stepNumber;
        this.action = action;
    }

    public Long getId() {
        return id;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public String getAction() {
        return action;
    }
}
