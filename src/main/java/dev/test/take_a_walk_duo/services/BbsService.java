package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.mappers.IBbsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dev.test.take_a_walk_duo.services.BbsService")
public class BbsService {

    private final IBbsMapper BbsMapper;

    @Autowired
    public BbsService(IBbsMapper bbsMapper) {
        BbsMapper = bbsMapper;
    }
}
