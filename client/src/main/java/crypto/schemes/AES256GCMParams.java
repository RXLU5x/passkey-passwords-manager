package crypto.schemes;

import javafx.util.Pair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class AES256GCMParams extends VaultCryptoParams
{
    public AES256GCMParams(byte[] iv, byte[] enc_key, byte[] mac_key) throws Exception {
        super();
        setParams();
        this.iv = iv;
        this.rawKey = enc_key;
        this.rawMac = mac_key;
        build();
    }

    public AES256GCMParams() throws Exception {
        super();
        setParams();
        buildNew();
    }

    private void setParams() {
        this.algo = "AES-256-GCM";
        this.mac = null;
        this.cipherSign = "AES";
        this.jcaSign = "AES/GCM/NoPadding";
        this.tagLength = 128;
    }

    @Override
    public Pair<byte[], String> encrypt(byte[] plaintext, String headerForm) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        return this.encryptAEAD(plaintext, headerForm.getBytes());
    }

    @Override
    public String decrypt(String ciphertext, byte[] mac, String headerForm) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidAlgorithmParameterException {
        return this.decryptAEAD(ciphertext, mac, headerForm.getBytes());
    }

    @Override
    public boolean isAEAD() {
        return true;
    }
}
