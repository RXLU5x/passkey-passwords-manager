package crypto.schemes;

import crypto.MacErrorException;
import javafx.util.Pair;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

public class Serpent256CBCParams extends VaultCryptoParams
{
    public Serpent256CBCParams(byte[] iv, byte[] enc_key, byte[] mac_key) throws Exception {
        super();
        setParams();
        this.iv = iv;
        this.rawKey = enc_key;
        this.rawMac = mac_key;
        build();
    }

    public Serpent256CBCParams() throws Exception {
        super();
        setParams();
        buildNew();
    }

    private void setParams(){
        this.algo = "Serpent-256-CBC";
        this.cipherSign = "Serpent";
        this.jcaSign = "Serpent/CBC/PKCS5Padding";
    }

    @Override
    public Pair<byte[], String> encrypt(byte[] plaintext, String headerForm) throws BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        String ciphertext = this.encryptRegular(plaintext, headerForm.toCharArray());

        byte[] target = ("{" + headerForm + ",\"vault_body:\"" + ciphertext + "}").getBytes();
        byte[] mac = this.computeMAC(target);

        return new Pair<>(mac, ciphertext);
    }

    @Override
    public String decrypt(String ciphertext, byte[] mac, String headerForm) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, MacErrorException, InvalidAlgorithmParameterException {
        byte[] target = ("{" + headerForm + ",\"vault_body:\"" + ciphertext + "}").getBytes();
        byte[] macActual = this.computeMAC(target);

        if(!Arrays.equals(mac, macActual)){
            throw new MacErrorException();
        }

        return this.decryptRegular(ciphertext);
    }

    @Override
    public boolean isAEAD() {
        return false;
    }
}
