package dev.twd.take_a_walk_duo;

import dev.twd.take_a_walk_duo.utils.CryptoUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PetWalkApplicationTests {

    @Test
    void contextLoads() {
        System.out.println(CryptoUtils.hashSha512("1234"));
        System.out.println("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4");
    }

}
