package com.secondthorn.solitaire.pyramid.service.repository;

import com.secondthorn.solitaire.pyramid.service.model.Challenge;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


/**
 * The Challenge Repository covers all challenge types.  There's no new methods
 * here, it's meant to be used with CrudRepository and other methods such as
 * findById() for interacting with challenges regardless of subclass.
 */
@Repository
public interface ChallengeRepository extends CrudRepository<Challenge, Long> {
}
