package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardChallengeRepository extends CrudRepository<BoardChallenge, Long> {
    BoardChallenge findByDeckString(String deckString);
}
