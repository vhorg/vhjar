package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.BloodSet;
import iskallia.vault.skill.set.CarapaceSet;
import iskallia.vault.skill.set.DragonSet;
import iskallia.vault.skill.set.DryadSet;
import iskallia.vault.skill.set.EffectSet;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.skill.set.SetGroup;
import iskallia.vault.skill.set.VampirismSet;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.Arrays;
import java.util.List;
import net.minecraft.potion.Effects;

public class SetsConfig extends Config {
   @Expose
   public SetGroup<DragonSet> DRAGON;
   @Expose
   public SetGroup<PlayerSet> RIFT;
   @Expose
   public SetGroup<GolemSet> GOLEM;
   @Expose
   public SetGroup<EffectSet> GOBLIN;
   @Expose
   public SetGroup<AssassinSet> ASSASSIN;
   @Expose
   public SetGroup<EffectSet> SLAYER;
   @Expose
   public SetGroup<VampirismSet> VAMPIRE;
   @Expose
   public SetGroup<EffectSet> BRUTE;
   @Expose
   public SetGroup<DryadSet> DRYAD;
   @Expose
   public SetGroup<EffectSet> TITAN;
   @Expose
   public SetGroup<NinjaSet> NINJA;
   @Expose
   public SetGroup<EffectSet> TREASURE_HUNTER;
   @Expose
   public SetGroup<PlayerSet> ZOD;
   @Expose
   public SetGroup<PlayerSet> DIVINITY;
   @Expose
   public SetGroup<CarapaceSet> CARAPACE;
   @Expose
   public SetGroup<BloodSet> BLOOD;

   @Override
   public String getName() {
      return "sets";
   }

   public List<SetGroup<?>> getAll() {
      return Arrays.asList(
         this.DRAGON,
         this.RIFT,
         this.GOLEM,
         this.GOBLIN,
         this.ASSASSIN,
         this.SLAYER,
         this.VAMPIRE,
         this.BRUTE,
         this.DRYAD,
         this.TITAN,
         this.NINJA,
         this.TREASURE_HUNTER,
         this.ZOD,
         this.DIVINITY,
         this.CARAPACE,
         this.BLOOD
      );
   }

   public SetGroup<?> getByName(String name) {
      return this.getAll()
         .stream()
         .filter(group -> group.getParentName().equals(name))
         .findFirst()
         .orElseThrow(() -> new IllegalStateException("Unknown set with name " + name));
   }

   @Override
   protected void reset() {
      this.DRAGON = SetGroup.of("Dragon", 1, i -> new DragonSet(1.5F));
      this.RIFT = SetGroup.of("Rift", 1, i -> new PlayerSet(VaultGear.Set.RIFT));
      this.GOLEM = SetGroup.of("Golem", 1, i -> new GolemSet(0.08F));
      this.GOBLIN = SetGroup.of(
         "Goblin", 1, i -> new EffectSet(VaultGear.Set.GOBLIN, Effects.field_188425_z, 0, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)
      );
      this.ASSASSIN = SetGroup.of("Assassin", 1, i -> new AssassinSet(1, 0.1F));
      this.SLAYER = SetGroup.of(
         "Slayer", 1, i -> new EffectSet(VaultGear.Set.SLAYER, Effects.field_76420_g, 2, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)
      );
      this.VAMPIRE = SetGroup.of("Vampire", 1, i -> new VampirismSet(0.05F));
      this.BRUTE = SetGroup.of(
         "Brute", 1, i -> new EffectSet(VaultGear.Set.BRUTE, Effects.field_76420_g, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)
      );
      this.DRYAD = SetGroup.of("Dryad", 1, i -> new DryadSet(20.0F));
      this.TITAN = SetGroup.of(
         "Titan", 1, i -> new EffectSet(VaultGear.Set.TITAN, Effects.field_76429_m, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)
      );
      this.NINJA = SetGroup.of("Ninja", 1, i -> new NinjaSet(0.3F, 0.1F));
      this.TREASURE_HUNTER = SetGroup.of(
         "Treasure Hunter",
         1,
         i -> new EffectSet(VaultGear.Set.TREASURE_HUNTER, Effects.field_188425_z, 1, EffectTalent.Type.ICON_ONLY, EffectTalent.Operator.ADD)
      );
      this.ZOD = SetGroup.of("Zod", 1, i -> new PlayerSet(VaultGear.Set.ZOD));
      this.DIVINITY = SetGroup.of("Divinity", 1, i -> new PlayerSet(VaultGear.Set.DIVINITY));
      this.CARAPACE = SetGroup.of("Carapace", 1, i -> new CarapaceSet(0.5F));
      this.BLOOD = SetGroup.of("Blood", 1, i -> new BloodSet(3.0F));
   }
}
