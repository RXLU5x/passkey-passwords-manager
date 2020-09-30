package crypto;

import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class VaultCryptoTest
{
    private static final Crypto crypto = Crypto.getInstance();

    @Test
    public void testAEAD() throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] plaintext = "test".getBytes();
        byte[] associatedData = "data".getBytes();

        // Encrypt
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");

        KeyGenerator kgen = KeyGenerator.getInstance("AES", "BC");
        SecretKey enckey =  kgen.generateKey();

        GCMParameterSpec spec = new GCMParameterSpec(128, "0".getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, enckey, spec);
        cipher.updateAAD(associatedData);

        byte[] result = cipher.doFinal(plaintext);
        byte[] ciphertext = new byte[result.length-128/8];
        byte[] tag = new byte[128/8];

        System.arraycopy(result, 0, tag, 0, 128/8);
        System.arraycopy(result, 128/8, ciphertext, 0, result.length-128/8);

        // Decrypt

        /*
            cipher.init(Cipher.DECRYPT_MODE, enckey, spec);
            cipher.updateAAD(associatedData);
            byte[] recoveredText = cipher.doFinal(result);
        */

        byte[] recoveredText = decryptAEAD(Hex.toHexString(ciphertext),Hex.encode(tag), associatedData ,enckey, 128, "0".getBytes());

        assertArrayEquals(plaintext, recoveredText);
    }


    public byte[] decryptAEAD(String ciphertext, byte[] tag, byte[] associatedData, SecretKey encKey, int tagLength, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC");
        GCMParameterSpec spec = new GCMParameterSpec(tagLength, iv);
        cipher.init(Cipher.DECRYPT_MODE, encKey, spec);

        // Decode Hex
        byte[] decodedCyphertext = Hex.decode(ciphertext);
        byte[] decodedTag = Hex.decode(tag);

        //Reconstruct [Tag + Ciphertext]
        int tLength = decodedTag.length; // Size bytes AEAD tag
        int cLength = decodedCyphertext.length; // Size bytes ciphertext
        int tarLength = tLength + cLength; // Size bytes sum

        byte[] target = new byte[tarLength];

        System.arraycopy(decodedTag, 0, target, 0, tLength);
        System.arraycopy(decodedCyphertext, 0, target, tLength, cLength);

        cipher.updateAAD(associatedData);

        return cipher.doFinal(target);
        //return new String(result, StandardCharsets.UTF_8);
    }
}