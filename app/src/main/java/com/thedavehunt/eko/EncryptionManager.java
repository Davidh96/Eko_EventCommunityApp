package com.thedavehunt.eko;

import android.util.Base64;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EncryptionManager {

    public KeyPair keys;
    public String algo="RSA";
    public int keySize=1024;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void generateKeys(){
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(algo);
            kpg.initialize(keySize);

            //generate keys
            keys = kpg.generateKeyPair();

            //save keys to file
            //files stored in files folder
            File root = new File("/data/data/com.thedavehunt.eko/files");

            //write private key to file
            File keyFile = new File(root, "privateKey.txt");
            FileWriter writer = new FileWriter(keyFile);
            writer.write(this.convertKeyToString(keys.getPrivate()));
            writer.flush();

            //write public key to file
            keyFile = new File(root, "publicKey.txt");
            writer = new FileWriter(keyFile);
            writer.write(this.convertKeyToString(keys.getPublic()));
            writer.flush();

            writer.close();

            //update key on Firebase
            databaseManager dbm = new databaseManager();
            dbm.updatePublicKey(user.getUid(),this.convertKeyToString(keys.getPublic()));

        }catch(Exception e){
            Log.d("Key Gen error",e.toString());
        }
    }

    public String getKeyFromFile(String Filename){

       File root = new File("/data/data/com.thedavehunt.eko/files");
       File keyFile = new File(root, Filename);

        StringBuilder text = new StringBuilder();


        //read in Key from file
        try {
            BufferedReader br = new BufferedReader(new FileReader(keyFile));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (Exception e) {
        }

        return new String(text);
    }

//    public String getPublicKey(){
//
//        File pubKeyFile = new File("/data/data/com.thedavehunt.eko/files/publicKey.txt");
//        //File pubKeyFile = new File(root, "");
//
//        StringBuilder text = new StringBuilder();
//
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(pubKeyFile));
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                text.append(line);
//                text.append('\n');
//            }
//            br.close();
//        }
//        catch (Exception e) {
//        }
//
//        return new String(text);
//    }

    public byte[] encrypt(String text, PublicKey key){
        byte[] cipherText = null;
        try {
            //set encryption algorithm
            Cipher cipher = Cipher.getInstance(algo);

            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);
            cipherText = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }

    public String decrypt(byte[] text, PrivateKey key) {
        byte[] dectyptedText = null;
        try {
            //set encryption algorithm
            final Cipher cipher = Cipher.getInstance(algo);

            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, key);
            dectyptedText = cipher.doFinal(text);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    public String convertKeyToString(Key key){

        byte[] keyByte = key.getEncoded();

        String keyString = Base64.encodeToString(keyByte, Base64.DEFAULT);

        return keyString;

    }

//    public String convertPubToString(PublicKey publicKey){
//
//        byte[] pubByte = publicKey.getEncoded();
//
//        String pubString = Base64.encodeToString(pubByte, Base64.DEFAULT);
//
//        return pubString;
//
//    }

    public PrivateKey convertStringToPriv(String privString){
        PrivateKey privateKey=null;

        byte[] privByte = Base64.decode(privString, Base64.DEFAULT);

        PKCS8EncodedKeySpec PKx509KeySpec = new PKCS8EncodedKeySpec(privByte);

        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFact.generatePrivate(PKx509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return privateKey;
    }

    public PublicKey convertStringToPub(String pubString){

        PublicKey publicKey=null;

        byte[] pubByte = Base64.decode(pubString, Base64.DEFAULT);


        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubByte);
        KeyFactory keyFact = null;
        try {
            keyFact = KeyFactory.getInstance(algo);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            publicKey = keyFact.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return publicKey;

    }

}
