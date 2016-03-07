package common


import javax.crypto.{KeyGenerator, Mac}
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

import org.apache.commons.codec.binary.Base64

import scala.util.Random

object SecretGenerator {
  //사용자별 고유한 secret값 생성
  def getSecret(): String ={
    val MAC_NAME = "HmacSHA1"
    val keygen = KeyGenerator.getInstance(MAC_NAME);
    val macKey = keygen.generateKey();
    val mac = Mac.getInstance(MAC_NAME);
    val secret = new SecretKeySpec(macKey.getEncoded(), mac.getAlgorithm());
    mac.init(secret);
    val random = new SecureRandom();
    val bytes = new Array[Byte](20)
    random.nextBytes(bytes)
    val digest = mac.doFinal(bytes);
    new Base64().encodeToString(digest);
  }

  //임시 비밀번호 생성
  def getTempPassword(length: Int): String = {
    val random: Random = new scala.util.Random(System.nanoTime)
    val tempPassword = new StringBuilder(length)
    val ab = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    for (i <- 0 until length) {
      tempPassword.append(ab(random.nextInt(ab.length)))
    }
    tempPassword.toString
  }
}
