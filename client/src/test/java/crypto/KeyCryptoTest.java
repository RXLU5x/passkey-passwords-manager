package crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KeyCryptoTest
{
    private static final Crypto crypto = Crypto.getInstance();

    /**
     * RFC 5869
     * Apendix A    Test Vectors
     * A.1  Test case 1.
     */
    @Test
    public void hkdfTestCase1() {
        byte[] IKM  = Hex.decode("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b");
        byte[] salt = Hex.decode("000102030405060708090a0b0c");
        byte[] info = Hex.decode("f0f1f2f3f4f5f6f7f8f9");

        int L = 42;

        byte[] PRK  = Hex.decode("077709362c2e32df0ddc3f0dc47bba6390b6c73bb50f9c3122ec844ad7c2b3e5");
        byte[] OKM  = Hex.decode("3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865");

        Assertions.assertArrayEquals(OKM, KeyCrypto.hkdf(salt, IKM, L, info));
    }

    /**
     * RFC 5869
     * Apendix A    Test Vectors
     * A.2  Test case 2.
     */
    @Test
    public void hkdfTestCase2() {
        byte[] IKM  = Hex.decode("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b") ;
        byte[] salt = Hex.decode("000102030405060708090a0b0c");
        byte[] info = Hex.decode("f0f1f2f3f4f5f6f7f8f9");

        int L = 42;

        byte[] PRK  = Hex.decode("077709362c2e32df0ddc3f0dc47bba6390b6c73bb50f9c3122ec844ad7c2b3e5");
        byte[] OKM  = Hex.decode("3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865");

        Assertions.assertArrayEquals(OKM, KeyCrypto.hkdf(salt, IKM, L, info));
    }

    /**
     * RFC 5869
     * Apendix A    Test Vectors
     * A.3  Test case 3.
     */
    @Test
    public void hkdfTestCase3() {
        byte[] IKM  = Hex.decode("0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b");
        byte[] salt = new byte[] {0x00};
        byte[] info = new byte[] {};

        int L = 42;

        byte[] PRK  = Hex.decode("19ef24a32c717b167f33a91d6f648bdf96596776afdb6377ac434c1c293ccb04");
        byte[] OKM  = Hex.decode("8da4e775a563c18f715f802a063c5a31b8a11f5c5ee1879ec3454e5f3c738d2d9d201395faa4b61a96c8");

        Assertions.assertArrayEquals(OKM, KeyCrypto.hkdf(salt, IKM, L, info));
    }

}