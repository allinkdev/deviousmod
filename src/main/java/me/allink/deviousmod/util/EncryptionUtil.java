package me.allink.deviousmod.util;

import me.allink.deviousmod.client.DeviousModClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.List;
import java.util.Objects;

public class EncryptionUtil {
    public static String decryptMessage(Text input) {
        StringBuilder hex = new StringBuilder();
        if (input.getString().contains(DeviousModClient.encryptionChar)) {
            if (input.getString().contains("§")) {
                String[] chars = input.getString().split("");
                for (String aChar : chars) {
                    if (!Objects.equals(aChar, DeviousModClient.encryptionChar) && !Objects.equals(aChar, "§")) {
                        hex.append(aChar);
                    }
                }
            } else {
                try {
                    List<Text> list = input.getWithStyle(Style.EMPTY);
                    for (Text text : list) {
                        String[] chars = text.getString().split("");
                        for (String aChar : chars) {
                            if (Objects.equals(aChar, DeviousModClient.encryptionChar)) {
                                String name = text.getStyle().getColor().getName();
                                String hexValue = DeviousModClient.colorToHex.get((name == null) ? "white" : name);
                                hex.append(hexValue);
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(DeviousModClient.CONFIG.key.toCharArray(), DeviousModClient.salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            final String result = new String(cipher.doFinal(Hex.decodeHex(hex.toString())));

            DeviousModClient.alreadyDecrypted.put(input.getString(), result);
            return result;
        } catch (Exception ignored) {
            DeviousModClient.alreadyDecrypted.put(input.getString(), "Unable to decrypt");
            return "Unable to decrypt";
        }
    }

    public static String encrypt(String input) {

        try {
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(DeviousModClient.CONFIG.key.toCharArray(), DeviousModClient.salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            char[] hex = Hex.encodeHex(cipher.doFinal(input.getBytes(StandardCharsets.UTF_8)));

            StringBuilder toSay = new StringBuilder();
            for (char s : hex) {
                toSay.append(String.format("&%s%s", s, DeviousModClient.encryptionChar));
            }
            return toSay.toString();
        } catch (Exception e) {
            return "Unable to encrypt";
        }
    }
}
