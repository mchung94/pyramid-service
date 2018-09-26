package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.BoardChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardChallengeRepository extends JpaRepository<BoardChallenge, Long> {
    BoardChallenge findByDeckString(String deckString);
}
