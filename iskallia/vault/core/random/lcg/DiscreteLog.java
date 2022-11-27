package iskallia.vault.core.random.lcg;

import iskallia.vault.core.util.MathUtils;
import java.math.BigInteger;

public class DiscreteLog {
   public static boolean supports(LCG lcg) {
      return lcg.isModPowerOf2() && lcg.getModTrailingZeroes() <= 61 ? lcg.multiplier % 2L != 0L && lcg.addend % 2L != 0L : false;
   }

   public static long distanceFromZero(LCG lcg, long seed) {
      int exp = lcg.getModTrailingZeroes();
      long a = lcg.multiplier;
      long b = MathUtils.mask(seed * (lcg.multiplier - 1L) * MathUtils.modInverse(lcg.addend, exp) + 1L, exp + 2);
      long aBar = theta(a, exp);
      long bBar = theta(b, exp);
      return bBar * MathUtils.mask(MathUtils.modInverse(aBar, exp), exp);
   }

   private static long theta(long number, int exp) {
      if (number % 4L == 3L) {
         number = MathUtils.getPow2(exp + 2) - number;
      }

      BigInteger xHat = BigInteger.valueOf(number);
      xHat = xHat.modPow(BigInteger.ONE.shiftLeft(exp + 1), BigInteger.ONE.shiftLeft(2 * exp + 3));
      xHat = xHat.subtract(BigInteger.ONE);
      xHat = xHat.divide(BigInteger.ONE.shiftLeft(exp + 3));
      xHat = xHat.mod(BigInteger.ONE.shiftLeft(exp));
      return xHat.longValue();
   }
}
