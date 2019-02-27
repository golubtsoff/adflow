package util;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashTest {

    @Test
    public void getHash() {
        String source = "123";
        String hash = Hash.getHash(source);
        Assert.assertEquals(hash, "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3");
    }

    @Test(expected = NullPointerException.class)
    public void getHashNull() {
        String source = null;
        String hash = Hash.getHash(source);
    }

    @Test
    public void isEqualTrue() {
        Assert.assertTrue(Hash.isEqual(
                "123",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3"
        ));
    }

    @Test
    public void isEqualFalse() {
        Assert.assertFalse(Hash.isEqual(
                "123",
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27aeq"
        ));
    }
}