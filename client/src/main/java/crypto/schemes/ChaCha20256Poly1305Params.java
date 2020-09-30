package crypto.schemes;

import crypto.Crypto;
import javafx.util.Pair;
import org.bouncycastle.util.encoders.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;

public class ChaCha20256Poly1305Params extends VaultCryptoParams
{
    Crypto crypto;

    public ChaCha20256Poly1305Params(byte[] iv, byte[] enc_key) throws Exception {
        super();
        crypto = Crypto.getInstance();
        setParams();
        this.iv = iv;
        this.rawKey = enc_key;
        this.encKey = new SecretKeySpec(crypto.unWrapKey(rawKey, this.iv), jcaSign);
    }

    public ChaCha20256Poly1305Params() throws Exception {
        super();
        crypto = Crypto.getInstance();
        setParams();

        // Generate needed keys
        KeyGenerator kgen = KeyGenerator.getInstance(cipherSign);

        this.encKey = kgen.generateKey();

        // Generate iv
        Cipher cipher = Cipher.getInstance(jcaSign);
        cipher.init(Cipher.ENCRYPT_MODE, encKey);

        IvParameterSpec spec = cipher
            .getParameters()
            .getParameterSpec(IvParameterSpec.class);

        this.iv = spec.getIV();
        this.rawKey = crypto.wrapKey(this.encKey.getEncoded(), this.iv);
    }

    private void setParams() {
        this.algo = "ChaCha20-256-Poly1305";
        this.mac = null;
        this.cipherSign = "ChaCha20";
        this.jcaSign = "ChaCha20-Poly1305/None/NoPadding";
        this.tagLength = 128;
    }

    @Override
    public Pair<byte[], String> encrypt(byte[] plaintext, String headerForm) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        byte[] associatedData = headerForm.getBytes();

        Cipher cipher = Cipher.getInstance(jcaSign);
        AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(this.iv);
        cipher.init(Cipher.ENCRYPT_MODE, encKey, ivParameterSpec);

        cipher.updateAAD(associatedData);

        byte[] result = cipher.doFinal(plaintext);
        byte[] ciphertext = new byte[result.length-tagLength/8];
        byte[] tag = new byte[tagLength/8];

        System.arraycopy(result, 0, tag, 0, tagLength/8);
        System.arraycopy(result, tagLength/8, ciphertext, 0, result.length-tagLength/8);

        return new Pair<>(Hex.encode(tag), Hex.toHexString(ciphertext));
    }

    @Override
    public String decrypt(String ciphertext, byte[] tag, String headerForm) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        byte[] associatedData = headerForm.getBytes();

        Cipher cipher = Cipher.getInstance(jcaSign);
        AlgorithmParameterSpec ivParameterSpec = new IvParameterSpec(this.iv);
        cipher.init(Cipher.DECRYPT_MODE, encKey, ivParameterSpec);

        // Decode Hex
        byte[] decodedCyphertext = Hex.decode(ciphertext);
        byte[] decodedTag = Hex.decode(tag);

        //Reconstruct [Tag + Ciphertext]
        int tLength = decodedTag.length, // Size bytes AEAD tag
                cLength = decodedCyphertext.length, // Size bytes ciphertext
                tarLength = tLength + cLength; // Size bytes sum
        byte[] target = new byte[tarLength];

        System.arraycopy(decodedTag, 0, target, 0, tLength);
        System.arraycopy(decodedCyphertext, 0, target, tLength, cLength);

        cipher.updateAAD(associatedData);
        byte[] result = cipher.doFinal(target);

        return new String(result, StandardCharsets.UTF_8);
    }

    @Override
    public boolean isAEAD() {
        return true;
    }
}
