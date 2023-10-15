package com.littleyellow.utils.cipher;

import android.util.Base64;
import android.util.Log;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64Encoder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * 验签就必须要引入以下的包才可以进行，但是java是不需要的。
 * implementation 'org.bouncycastle:bcprov-jdk16:1.46'
 */
public class ECUtil {

    private static final String SIGNALGORITHMS = "SHA256withECDSA";
    private static final String ALGORITHM = "EC";
    private static final String PROVIDER = "BC";

    private static final String SECP256K1 = "secp256r1";

    /**
     * BC和java以及android使用的ECDSA签名格式为DER编码的ASN.1格式，其中包含两个整数值r和s,并且应该为70字节，但是一般情况下ECDSA加签方法不是由java生成的话，它的格式不会是适合我们直接可以使用的r|s格式，因此可以使用一下方式来进行格式转换，使得我们可以直接使用，完成验签操作。
     * ————————————————
     * 版权声明：本文为CSDN博主「baijianglei」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/baijianglei/article/details/125555609
     * @param signature
     * @return
     * @throws IOException
     */
    private static byte[] DEREncodeSignature(byte[] signature) throws IOException {
        BigInteger r = new BigInteger(1, Arrays.copyOfRange(signature, 0, 32));
        BigInteger s = new BigInteger(1, Arrays.copyOfRange(signature, 32, 64));
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(r));
        v.add(new ASN1Integer(s));
        return new DERSequence(v).getEncoded(ASN1Encodable.DER);
    }

    /**
     * 加签
     * @param privateKey 私钥
     * @param data 数据
     * @return
     */
    public static String signECDSA(PrivateKey privateKey, String data) {
        String result = "";
        try {
            //执行签名
            Signature signature = Signature.getInstance(SIGNALGORITHMS);
            signature.initSign(privateKey);
            signature.update(data.getBytes());
            byte[] sign = signature.sign();
            return Base64.encodeToString(sign,Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验签
     * @param publicKey 公钥
     * @param signed 签名
     * @param data 数据
     * @return
     */
    public static boolean verifyECDSA(String publicKey, String signed, String data) {
        try {
            //验证签名
            Security.removeProvider("BC");
            Security.addProvider(new BouncyCastleProvider());
            Signature signature = Signature.getInstance(SIGNALGORITHMS,PROVIDER);
            signature.initVerify(getPublicKey(publicKey));
            signature.update(Base64.decode(data,Base64.NO_WRAP));
            byte[] hex = DEREncodeSignature(Base64.decode(signed,Base64.NO_WRAP));
            boolean bool = signature.verify(hex);
            // System.out.println("验证：" + bool);
            return bool;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 从string转publicKey
     * @param key 公钥的字符串
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        Security.removeProvider("BC");
        Security.addProvider(new BouncyCastleProvider());
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decode(key,Base64.NO_WRAP));
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM,PROVIDER);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        return publicKey;
    }

    /**
     * 生成密钥对
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SECP256K1);
        KeyPairGenerator kf = KeyPairGenerator.getInstance(ALGORITHM);
        kf.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = kf.generateKeyPair();
        return keyPair;
    }

    /**
     * 从string转private key
     * @param key 私钥的字符串
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] bytes = Base64.decode(key,Base64.NO_WRAP);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }


    public static void main(String[] args) throws Exception {
//        生成公钥私钥
        KeyPair keyPair1 = getKeyPair();
        PublicKey publicKey1 = keyPair1.getPublic();
        PrivateKey privateKey1 = keyPair1.getPrivate();
        //密钥转16进制字符串
        String publicKey = Base64.encodeToString(publicKey1.getEncoded(),Base64.NO_WRAP);
        String privateKey = Base64.encodeToString(privateKey1.getEncoded(),Base64.NO_WRAP);
        System.out.println("生成公钥："+publicKey);
        System.out.println("生成私钥："+privateKey);


//        Log.e("ECUtil","生成公钥："+publicKey);
//        Log.e("ECUtil","生成私钥："+privateKey);
        String pPublicKey = printKey("PUBLIC KEY",publicKey1);
        String pPrivateKey = printKey("PRIVATE KEY",privateKey1);
        Log.e("ECUtil","生成公钥："+pPublicKey);
        Log.e("ECUtil","生成私钥："+pPrivateKey);
        //16进制字符串转密钥对象
        PrivateKey privateKey2 = getPrivateKey(privateKey);
        PublicKey publicKey2 = getPublicKey(publicKey);
        //加签验签
        String data="需要签名的数据";
        String signECDSA = signECDSA(privateKey2, data);
        boolean verifyECDSA = verifyECDSA(publicKey, signECDSA, data);
        System.out.println("验签结果："+verifyECDSA);
        Log.e("ECUtil","验签结果："+verifyECDSA);
    }

    public static String printKey(String type, Key data) throws IOException {
        PemObject pem = new PemObject(type,data.getEncoded());
        StringWriter str = new StringWriter();

        PemWriter pemWriter = new PemWriter(str);
        pemWriter.writeObject(pem);
        pemWriter.close();
        str.close();
        return str.toString();
    }


    public RSAPublicKey readPublicKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return (RSAPublicKey) factory.generatePublic(pubKeySpec);
        }
    }

    public RSAPrivateKey readPrivateKey(File file) throws Exception {
        KeyFactory factory = KeyFactory.getInstance("RSA");

        try (FileReader keyReader = new FileReader(file);
             PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
            return (RSAPrivateKey) factory.generatePrivate(privKeySpec);
        }
    }


}
