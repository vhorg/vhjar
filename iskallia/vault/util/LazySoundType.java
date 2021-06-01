package iskallia.vault.util;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundEvent;

public class LazySoundType extends SoundType {
   private boolean initialized;
   protected float lazyVolume;
   protected float lazyPitch;
   protected SoundEvent lazyBreakSound;
   protected SoundEvent lazyStepSound;
   protected SoundEvent lazyPlaceSound;
   protected SoundEvent lazyHitSound;
   protected SoundEvent lazyFallSound;

   public LazySoundType(SoundType vanillaType) {
      super(
         1.0F,
         1.0F,
         vanillaType.func_185845_c(),
         vanillaType.func_185844_d(),
         vanillaType.func_185841_e(),
         vanillaType.func_185846_f(),
         vanillaType.func_185842_g()
      );
   }

   public LazySoundType() {
      this(SoundType.field_185851_d);
   }

   public void initialize(
      float volumeIn, float pitchIn, SoundEvent breakSoundIn, SoundEvent stepSoundIn, SoundEvent placeSoundIn, SoundEvent hitSoundIn, SoundEvent fallSoundIn
   ) {
      if (this.initialized) {
         throw new InternalError("LazySoundTypes should be initialized only once!");
      } else {
         this.lazyVolume = volumeIn;
         this.lazyPitch = pitchIn;
         this.lazyBreakSound = breakSoundIn;
         this.lazyStepSound = stepSoundIn;
         this.lazyPlaceSound = placeSoundIn;
         this.lazyHitSound = hitSoundIn;
         this.lazyFallSound = fallSoundIn;
         this.initialized = true;
      }
   }

   public float func_185843_a() {
      return this.lazyVolume;
   }

   public float func_185847_b() {
      return this.lazyPitch;
   }

   public SoundEvent func_185845_c() {
      return this.lazyBreakSound == null ? super.func_185845_c() : this.lazyBreakSound;
   }

   public SoundEvent func_185844_d() {
      return this.lazyStepSound == null ? super.func_185844_d() : this.lazyStepSound;
   }

   public SoundEvent func_185841_e() {
      return this.lazyPlaceSound == null ? super.func_185841_e() : this.lazyPlaceSound;
   }

   public SoundEvent func_185846_f() {
      return this.lazyHitSound == null ? super.func_185846_f() : this.lazyHitSound;
   }

   public SoundEvent func_185842_g() {
      return this.lazyFallSound == null ? super.func_185842_g() : this.lazyFallSound;
   }
}
