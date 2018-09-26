package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardChallengeRepository extends JpaRepository<CardChallenge, Long> {
    CardChallenge findByDeckStringAndGoalRankAndNumToRemove(String deckString, Character goalRank, Integer numToRemove);
}
