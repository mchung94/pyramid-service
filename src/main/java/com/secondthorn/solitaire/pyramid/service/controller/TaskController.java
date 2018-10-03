package com.secondthorn.solitaire.pyramid.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.secondthorn.solitaire.pyramid.service.exception.TaskNotFoundException;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.Solution;
import com.secondthorn.solitaire.pyramid.service.repository.ChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * When a user posts a challenge to be solved, it isn't solved immediately.
 * The user gets redirected to check on the challenge's solving status here
 * to see if it has been solved yet, and if so they get the solution back.
 */
@RestController
public class TaskController {
    ChallengeRepository repository;

    public TaskController(ChallengeRepository repository) {
        this.repository = repository;
    }

    /**
     * Check on a challenge to see if it's been solved yet.  Either the
     * challenge doesn't exist (404), not solved yet (200 with status pending
     * JSON, or 500 if there's a problem), or solved (303 with a link to where
     * to get the solution).
     */
    @GetMapping("/pyramid-solitaire/solver/tasks/{id}")
    public ResponseEntity<JsonNode> getTaskStatus(@PathVariable long id, UriComponentsBuilder ucb) {
        Optional<Challenge> ch = repository.findById(id);
        if (!ch.isPresent()) {
            throw new TaskNotFoundException("Task ID " + id + " doesn't exist.");
        }
        Challenge challenge = ch.get();
        List<Solution> solutions = challenge.getSolutions();
        if (solutions.size() > 0) {
            String path = challenge.getUriPath();
            String query = challenge.getUriQuery();
            URI uri = ucb.path(path).replaceQuery(query).build().toUri();
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(uri).build();
        }
        Map<String, String> task = new HashMap<>();
        task.put("task_id", challenge.getId().toString());
        task.put("status", "pending");
        JsonNode node = new ObjectMapper().valueToTree(task);
        return ResponseEntity.ok(node);
    }
}
