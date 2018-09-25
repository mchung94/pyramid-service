package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import com.secondthorn.solitaire.pyramid.service.model.ScoreChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for querying challenges with their solutions from the database.
 */
@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    BoardChallenge findByDeckString(String deckString);

    ScoreChallenge findByDeckStringAndGoalScore(String deckString, Integer goalScore);

    CardChallenge findByDeckStringAndGoalRankAndGoalNum(String deckString, Character goalRank, Integer goalNum);
}
