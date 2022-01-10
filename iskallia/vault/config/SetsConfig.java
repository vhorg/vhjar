package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.skill.set.AssassinSet;
import iskallia.vault.skill.set.BloodSet;
import iskallia.vault.skill.set.CarapaceSet;
import iskallia.vault.skill.set.DragonSet;
import iskallia.vault.skill.set.DreamSet;
import iskallia.vault.skill.set.DryadSet;
import iskallia.vault.skill.set.GolemSet;
import iskallia.vault.skill.set.NinjaSet;
import iskallia.vault.skill.set.PlayerSet;
import iskallia.vault.skill.set.PorcupineSet;
import iskallia.vault.skill.set.SetGroup;
import iskallia.vault.skill.set.TreasureSet;
import iskallia.vault.skill.set.VampirismSet;
import java.util.Arrays;
import java.util.List;

public class SetsConfig extends Config {
   @Expose
   public SetGroup<DragonSet> DRAGON;
   @Expose
   public SetGroup<PlayerSet> ZOD;
   @Expose
   public SetGroup<NinjaSet> NINJA;
   @Expose
   public SetGroup<GolemSet> GOLEM;
   @Expose
   public SetGroup<PlayerSet> RIFT;
   @Expose
   public SetGroup<CarapaceSet> CARAPACE;
   @Expose
   public SetGroup<PlayerSet> DIVINITY;
   @Expose
   public SetGroup<DryadSet> DRYAD;
   @Expose
   public SetGroup<BloodSet> BLOOD;
   @Expose
   public SetGroup<VampirismSet> VAMPIRE;
   @Expose
   public SetGroup<TreasureSet> TREASURE;
   @Expose
   public SetGroup<PlayerSet> PHOENIX;
   @Expose
   public SetGroup<AssassinSet> ASSASSIN;
   @Expose
   public SetGroup<DreamSet> DREAM;
   @Expose
   public SetGroup<PorcupineSet> PORCUPINE;

   @Override
   public String getName() {
      return "sets";
   }

   public List<SetGroup<?>> getAll() {
      return Arrays.asList(
         this.DRAGON,
         this.RIFT,
         this.GOLEM,
         this.ASSASSIN,
         this.VAMPIRE,
         this.DRYAD,
         this.NINJA,
         this.TREASURE,
         this.ZOD,
         this.DIVINITY,
         this.CARAPACE,
         this.BLOOD,
         this.DREAM,
         this.PORCUPINE
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
      this.DRAGON = SetGroup.of("Dragon", 1, i -> new DragonSet(0.5F));
      this.ZOD = SetGroup.of("Zod", 1, i -> new PlayerSet(VaultGear.Set.ZOD));
      this.NINJA = SetGroup.of("Ninja", 1, i -> new NinjaSet(0.3F, 0.1F));
      this.GOLEM = SetGroup.of("Golem", 1, i -> new GolemSet(0.0F, 20.0F));
      this.RIFT = SetGroup.of("Rift", 1, i -> new PlayerSet(VaultGear.Set.RIFT));
      this.CARAPACE = SetGroup.of("Carapace", 1, i -> new CarapaceSet(0.5F));
      this.DIVINITY = SetGroup.of("Divinity", 1, i -> new PlayerSet(VaultGear.Set.DIVINITY));
      this.DRYAD = SetGroup.of("Dryad", 1, i -> new DryadSet(20.0F));
      this.BLOOD = SetGroup.of("Blood", 1, i -> new BloodSet(2.0F));
      this.VAMPIRE = SetGroup.of("Vampire", 1, i -> new VampirismSet(0.05F));
      this.TREASURE = SetGroup.of("Treasure", 1, i -> new TreasureSet(2.0F));
      this.PHOENIX = SetGroup.of("Phoenix", 1, i -> new PlayerSet(VaultGear.Set.PHOENIX));
      this.ASSASSIN = SetGroup.of("Assassin", 1, i -> new AssassinSet(0.4F));
      this.DREAM = SetGroup.of("Dream", 1, i -> new DreamSet(0.5F, 2, 0.1F, 0.1F, 0.25F));
      this.PORCUPINE = SetGroup.of("Porcupine", 1, i -> new PorcupineSet(0.4F, 1.0F));
   }
}
