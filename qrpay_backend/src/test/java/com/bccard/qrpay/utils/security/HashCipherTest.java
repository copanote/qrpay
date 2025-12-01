package com.bccard.qrpay.utils.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HashCipherTest {

    @Test
    void sha256EncodedBase64() {
        //given
        String target = "112233";

        //when
        String hashedTarget = HashCipher.sha256EncodedBase64(target);

        //then
        String expected = "4LxgyCcT9k74pXwMQNAs4k/QFB1cwwhiWcGbHmKmK+o=";
        Assertions.assertThat(hashedTarget).isEqualTo(expected);
    }

    @Test
    void sha512EncodedBase64() {

        //given
        String target = "112233";

        //when
        String hashedTarget = HashCipher.sha512EncodedBase64(target);

        //then
        String expected = "6qYlphaeQmNDs3ApJMeTnqUIsXuRiOJx0tHMK1T8ARCfUOFKlCUIj7eilL9UyeMzF+YP3Jy1g7md7BJLpgoatQ==";
        Assertions.assertThat(hashedTarget).isEqualTo(expected);
    }
}