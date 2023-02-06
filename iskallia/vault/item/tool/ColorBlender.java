package iskallia.vault.item.tool;

import iskallia.vault.client.util.color.ColorUtil;
import java.util.ArrayList;
import java.util.List;
import oshi.util.tuples.Pair;

public class ColorBlender {
   private final float speed;
   private final List<Pair<Integer, Float>> sequence = new ArrayList<>();
   private float totalTime;

   public ColorBlender(float speed) {
      this.speed = speed;
   }

   public ColorBlender add(int color, float interval) {
      this.sequence.add(new Pair(color, interval));
      this.totalTime += interval;
      return this;
   }

   public int getColor(float time) {
      if (this.totalTime == 0.0F) {
         return 16777215;
      } else {
         float value = time * this.speed % this.totalTime;
         float ratio = 0.0F;
         int color1 = 0;
         int color2 = 0;

         for (int j = 0; j < this.sequence.size(); j++) {
            Pair<Integer, Float> pair = this.sequence.get(j);
            int color = (Integer)pair.getA();
            float interval = (Float)pair.getB();
            if (value < interval) {
               color1 = color;
               color2 = (Integer)this.sequence.get((j + 1) % this.sequence.size()).getA();
               ratio = value / interval;
               break;
            }

            value -= interval;
         }

         return ColorUtil.blendColors(color2, color1, ratio);
      }
   }
}
