package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.ScoreChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreChallengeRepository extends JpaRepository<ScoreChallenge, Long> {
    ScoreChallenge findByDeckStringAndNumPoints(String deckString, Integer numPoints);
}
