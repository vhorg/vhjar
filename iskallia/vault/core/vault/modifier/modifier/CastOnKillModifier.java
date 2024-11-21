package iskallia.vault.core.vault.modifier.modifier;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.modifier.spi.predicate.IModifierImmunity;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.mana.FullManaPlayer;
import iskallia.vault.skill.ability.effect.spi.core.InstantAbility;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.skill.base.SkillContext;
import iskallia.vault.skill.base.SpecializedSkill;
import iskallia.vault.skill.base.TieredSkill;
import iskallia.vault.skill.source.SkillSource;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.world.data.PlayerAbilitiesData;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.EventPriority;

public class CastOnKillModifier extends VaultModifier<CastOnKillModifier.Properties> {
   public CastOnKillModifier(ResourceLocation id, CastOnKillModifier.Properties properties, VaultModifier.Display display) {
      super(id, properties, display);
      this.setDescriptionFormatter((t, p, s) -> t.formatted((int)Math.abs(p.getProbability() * s * 100.0F)));
   }

   @Override
   public void initServer(VirtualWorld world, Vault vault, ModifierContext context) {
      CommonEvents.ENTITY_DROPS
         .register(
            context.getUUID(),
            EventPriority.HIGHEST,
            event -> {
               if (event.getSource().getEntity() instanceof ServerPlayer attacker && !attacker.getLevel().isClientSide()) {
                  if (attacker.level == world) {
                     if (!IModifierImmunity.of(event.getEntity()).test(this)) {
                        if (this.properties.filter.test(event.getEntity())) {
                           if (!(attacker.getLevel().getRandom().nextFloat() >= this.properties.probability)) {
                              AbilityTree tree = PlayerAbilitiesData.get((ServerLevel)attacker.level).getAbilities(attacker);
                              this.resolve(tree)
                                 .ifPresent(
                                    ability -> ability.onAction(
                                       SkillContext.of(attacker, SkillSource.of(attacker).setPos(event.getEntity().position()).setMana(FullManaPlayer.INSTANCE))
                                    )
                                 );
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   public Optional<InstantAbility> resolve(AbilityTree tree) {
      return tree.getForId(this.properties.ability).map(skill -> {
         Skill var2 = skill instanceof SpecializedSkill specialized ? specialized.getSpecialization() : skill;
         var2 = var2 instanceof TieredSkill tiered ? tiered.getChild() : var2;
         return (InstantAbility)(var2 instanceof InstantAbility ? var2 : null);
      });
   }

   @Override
   public void releaseServer(ModifierContext context) {
      CommonEvents.ENTITY_DROPS.release(context.getUUID());
   }

   public static class Properties {
      @Expose
      private EntityPredicate filter;
      @Expose
      private String ability;
      @Expose
      private float probability;

      public Properties(EntityPredicate filter, String ability, float probability) {
         this.filter = filter;
         this.ability = ability;
         this.probability = probability;
      }

      public EntityPredicate getFilter() {
         return this.filter;
      }

      public String getAbility() {
         return this.ability;
      }

      public float getProbability() {
         return this.probability;
      }
   }
}
