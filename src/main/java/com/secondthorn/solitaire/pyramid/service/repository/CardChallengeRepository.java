package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.CardChallenge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardChallengeRepository extends CrudRepository<CardChallenge, Long> {
    CardChallenge findByDeckStringAndGoalRankAndNumToRemove(String deckString, Character goalRank, Integer numToRemove);
}
