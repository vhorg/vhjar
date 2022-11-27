package iskallia.vault.client.gui.helper;

import java.util.LinkedList;
import java.util.List;

public class AnimationManyPhased {
   protected List<AnimationManyPhased.PhaseRange> ranges = new LinkedList<>();
   protected float value;
   protected int elapsedTime;
   protected int animationTime;
   public static AnimationManyPhased.PhaseRange DEFAULT_RANGE = new AnimationManyPhased.PhaseRange();

   public AnimationManyPhased(int animationTime) {
      this.animationTime = animationTime;
   }

   public AnimationManyPhased withRange(float start, float end, Easing easing) {
      return this.withRange(start, end, easing, false);
   }

   public AnimationManyPhased withRange(float start, float end, Easing easing, boolean reverse) {
      AnimationManyPhased.PhaseRange phaseRange = new AnimationManyPhased.PhaseRange();
      phaseRange.start = start;
      phaseRange.end = end;
      phaseRange.easing = easing;
      phaseRange.reverse = reverse;
      this.ranges.add(phaseRange);
      return this;
   }

   public void tick(int deltaTime) {
      this.elapsedTime = Math.min(this.elapsedTime + deltaTime, this.animationTime);
      float elapsedPercentage = this.getElapsedPercentage();
      AnimationManyPhased.PhaseRange phaseRange = this.overlappingRange(elapsedPercentage);
      float elapsedRange = elapsedPercentage - phaseRange.start;
      float rangeLength = phaseRange.end - phaseRange.start;
      float mappedTime = mapRange(elapsedRange, 0.0F, rangeLength, 0.0F, 1.0F);
      this.value = phaseRange.calc(mappedTime);
   }

   public float getValue() {
      return this.value;
   }

   public void reset() {
      this.elapsedTime = 0;
   }

   public boolean isFinished() {
      return this.elapsedTime >= this.animationTime;
   }

   public float getElapsedPercentage() {
      return (float)this.elapsedTime / this.animationTime;
   }

   public AnimationManyPhased.PhaseRange overlappingRange(float percentage) {
      for (AnimationManyPhased.PhaseRange range : this.ranges) {
         if (range.intersects(percentage)) {
            return range;
         }
      }

      return DEFAULT_RANGE;
   }

   public static float mapRange(float value, float start1, float stop1, float start2, float stop2) {
      return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
   }

   public static class PhaseRange {
      protected float start = 0.0F;
      protected float end = 1.0F;
      protected Easing easing = Easing.CONSTANT_ONE;
      protected boolean reverse;

      public boolean intersects(float point) {
         return this.start <= point && point <= this.end;
      }

      public float calc(float time) {
         return this.reverse ? 1.0F - this.easing.calc(time) : this.easing.calc(time);
      }
   }
}
