package iskallia.vault.init;

import iskallia.vault.VaultMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModSounds {
   public static SoundEvent GRASSHOPPER_BRRR;
   public static SoundEvent RAFFLE_SFX;
   public static SoundEvent VAULT_AMBIENT_LOOP;
   public static SoundEvent VAULT_AMBIENT;
   public static SoundEvent VAULT_BOSS_LOOP;
   public static SoundEvent TIMER_KILL_SFX;
   public static SoundEvent TIMER_PANIC_TICK_SFX;
   public static SoundEvent CONFETTI_SFX;
   public static SoundEvent IDENTIFICATION_SFX;
   public static SoundEvent MEGA_JUMP_SFX;
   public static SoundEvent DASH_SFX;
   public static SoundEvent VAULT_EXP_SFX;
   public static SoundEvent VAULT_LEVEL_UP_SFX;
   public static SoundEvent SKILL_TREE_LEARN_SFX;
   public static SoundEvent SKILL_TREE_UPGRADE_SFX;
   public static SoundEvent VENDING_MACHINE_SFX;
   public static SoundEvent ARENA_HORNS_SFX;
   public static SoundEvent BOOSTER_PACK_SUCCESS_SFX;
   public static SoundEvent BOOSTER_PACK_FAIL_SFX;
   public static SoundEvent GIFT_BOMB_SFX;
   public static SoundEvent GIFT_BOMB_GAIN_SFX;
   public static SoundEvent MEGA_GIFT_BOMB_GAIN_SFX;
   public static SoundEvent BOSS_TP_SFX;
   public static SoundEvent VAULT_GEM_HIT;
   public static SoundEvent VAULT_GEM_BREAK;
   public static SoundEvent ROBOT_HURT;
   public static SoundEvent ROBOT_DEATH;
   public static SoundEvent BOOGIE_AMBIENT;
   public static SoundEvent BOOGIE_HURT;
   public static SoundEvent BOOGIE_DEATH;
   public static SoundEvent VAULT_PORTAL_OPEN;
   public static SoundEvent VAULT_PORTAL_LEAVE;
   public static SoundEvent CLEANSE_SFX;
   public static SoundEvent GHOST_WALK_SFX;
   public static SoundEvent INVISIBILITY_SFX;
   public static SoundEvent NIGHT_VISION_SFX;
   public static SoundEvent RAMPAGE_SFX;
   public static SoundEvent EMPOWER;
   public static SoundEvent EMPOWER_ICE_ARMOUR;
   public static SoundEvent SHELL;
   public static SoundEvent SHELL_PORCUPINE;
   public static SoundEvent SHELL_QUILL;
   public static SoundEvent VAMPIRE_HISSING_SFX;
   public static SoundEvent CAULDRON_BUBBLES_SFX;
   public static SoundEvent EXECUTION_SFX;
   public static SoundEvent GOBLIN_BAIL;
   public static SoundEvent GOBLIN_DEATH;
   public static SoundEvent GOBLIN_HURT;
   public static SoundEvent GOBLIN_IDLE;
   public static SoundEvent VAULT_DOOD_IDLE;
   public static SoundEvent SKELETON_PIRATE_IDLE;
   public static SoundEvent WINTERWALKER_IDLE;
   public static SoundEvent WINTERWALKER_STEP;
   public static SoundEvent WINTERWALKER_HURT;
   public static SoundEvent WINTERWALKER_DEATH;
   public static SoundEvent OVERGROWN_ZOMBIE_IDLE;
   public static SoundEvent OVERGROWN_ZOMBIE_STEP;
   public static SoundEvent OVERGROWN_ZOMBIE_HURT;
   public static SoundEvent OVERGROWN_ZOMBIE_DEATH;
   public static SoundEvent SHIVER_IDLE;
   public static SoundEvent SHIVER_HURT;
   public static SoundEvent SHIVER_DEATH;
   public static SoundEvent BLOODHORDE_IDLE;
   public static SoundEvent BLOODHORDE_TANK_IDLE;
   public static SoundEvent BLOODHORDE_TANK_ROAR;
   public static SoundEvent BLOODHORDE_STEP;
   public static SoundEvent BLOODHORDE_HURT;
   public static SoundEvent BLOODHORDE_DEATH;
   public static SoundEvent BURP;
   public static SoundEvent PUZZLE_COMPLETION_MAJOR;
   public static SoundEvent PUZZLE_COMPLETION_MINOR;
   public static SoundEvent PUZZLE_COMPLETION_FAIL;
   public static SoundEvent VAULT_CHEST_EPIC_OPEN;
   public static SoundEvent VAULT_CHEST_OMEGA_OPEN;
   public static SoundEvent VAULT_CHEST_RARE_OPEN;
   public static SoundEvent WITCHSKALL_IDLE;
   public static SoundEvent FAVOUR_UP;
   public static SoundEvent COIN_PILE_PLACE;
   public static SoundEvent COIN_SINGLE_PLACE;
   public static SoundEvent COIN_PILE_BREAK;
   public static SoundEvent CRATE_OPEN;
   public static SoundEvent MAGNET_TABLE;
   public static SoundEvent NOVA_SPEED;
   public static SoundEvent HEAL;
   public static SoundEvent MANA_SHIELD;
   public static SoundEvent MANA_SHIELD_HIT;
   public static SoundEvent TAUNT;
   public static SoundEvent TAUNT_REPEL;
   public static SoundEvent TAUNT_CHARM;
   public static SoundEvent ARTISAN_SMITHING;
   public static SoundEvent HUNTER_SFX;
   public static SoundEvent MOB_TRAP;
   public static SoundEvent DISARM_TRAP;
   public static SoundEvent TOTEM;
   public static SoundEvent SMITE_BOLT;
   public static SoundEvent SMITE;
   public static SoundEvent MOB_CRIT;
   public static SoundEvent ABILITY_OUT_OF_MANA;
   public static SoundEvent ABILITY_ON_COOLDOWN;
   public static SoundEvent BONK_CHARGE;
   public static SoundEvent BONK;
   public static SoundEvent JEWEL_CUT;
   public static SoundEvent JEWEL_CUT_SUCCESS;
   public static SoundEvent ARTIFACT_COMPLETE;
   public static SoundEvent GATE_OPEN;
   public static SoundEvent GATE_CLOSE;
   public static SoundEvent BOSS_FIGHT_1;
   public static SoundEvent BOSS_FIGHT_2;
   public static SoundEvent BOSS_FIGHT_3;
   public static SoundEvent BOSS_FIGHT_4;
   public static SoundEvent ARTIFACT_BOSS_AMBIENT;
   public static SoundEvent ARTIFACT_BOSS_HURT;
   public static SoundEvent ARTIFACT_BOSS_DEATH;
   public static SoundEvent ARTIFACT_BOSS_ATTACK;
   public static SoundEvent ARTIFACT_BOSS_MAGIC_ATTACK;
   public static SoundEvent ARTIFACT_BOSS_MAGIC_ATTACK_HIT;
   public static SoundEvent ARTIFACT_BOSS_CATALYST_HIT;
   public static SoundEvent ARTIFACT_BOSS_CATALYST_HIT_WRONG;
   public static SoundEvent SPARK_EXPUNGE;
   public static SoundEvent DESTROY_MONOLITH;
   public static SoundEvent ICE_BOLT_ARROW;
   public static SoundEvent ICE_BOLT_CHUNK;
   public static SoundEvent BOOSTER_PACK_OPEN;
   public static SoundEvent RAID_GATE_OPEN;
   public static SoundEvent RAID_CHAIN_LOCK;
   public static SoundEvent RAID_HATCH_OPEN;
   public static SoundEvent RAID_HATCH_LOCK;
   public static SoundEvent RAID_IMPACT;
   public static ForgeSoundType VAULT_GET_SOUND_TYPE = new ForgeSoundType(
      0.25F, 1.0F, () -> VAULT_GEM_BREAK, SoundType.STONE::getStepSound, SoundType.STONE::getPlaceSound, () -> VAULT_GEM_HIT, SoundType.STONE::getFallSound
   );
   public static ForgeSoundType COIN_PILE_SOUND_TYPE = new ForgeSoundType(
      1.0F, 1.0F, () -> COIN_PILE_BREAK, SoundType.CHAIN::getStepSound, () -> COIN_PILE_PLACE, SoundType.CHAIN::getHitSound, SoundType.CHAIN::getFallSound
   );
   public static ForgeSoundType COIN_PILE_DECO_SOUND_TYPE = new ForgeSoundType(
      1.0F, 1.0F, () -> COIN_PILE_BREAK, SoundType.CHAIN::getStepSound, () -> COIN_SINGLE_PLACE, SoundType.CHAIN::getHitSound, SoundType.CHAIN::getFallSound
   );

   public static void registerSounds(Register<SoundEvent> event) {
      GRASSHOPPER_BRRR = registerSound(event, "grasshopper_brrr");
      RAFFLE_SFX = registerSound(event, "raffle");
      VAULT_AMBIENT_LOOP = registerSound(event, "vault_ambient_loop");
      VAULT_AMBIENT = registerSound(event, "vault_ambient");
      VAULT_BOSS_LOOP = registerSound(event, "boss_loop");
      TIMER_KILL_SFX = registerSound(event, "timer_kill");
      TIMER_PANIC_TICK_SFX = registerSound(event, "timer_panic_tick");
      CONFETTI_SFX = registerSound(event, "confetti");
      IDENTIFICATION_SFX = registerSound(event, "identification");
      MEGA_JUMP_SFX = registerSound(event, "mega_jump");
      DASH_SFX = registerSound(event, "dash");
      VAULT_EXP_SFX = registerSound(event, "vault_exp");
      VAULT_LEVEL_UP_SFX = registerSound(event, "vault_level_up");
      SKILL_TREE_LEARN_SFX = registerSound(event, "skill_tree_learn");
      SKILL_TREE_UPGRADE_SFX = registerSound(event, "skill_tree_upgrade");
      VENDING_MACHINE_SFX = registerSound(event, "vending_machine");
      ARENA_HORNS_SFX = registerSound(event, "arena_horns");
      BOOSTER_PACK_SUCCESS_SFX = registerSound(event, "booster_pack");
      BOOSTER_PACK_FAIL_SFX = registerSound(event, "booster_pack_fail");
      GIFT_BOMB_SFX = registerSound(event, "gift_bomb");
      GIFT_BOMB_GAIN_SFX = registerSound(event, "sub_bomb_gain");
      MEGA_GIFT_BOMB_GAIN_SFX = registerSound(event, "sub_bomb_gain_mega");
      BOSS_TP_SFX = registerSound(event, "boss_tp");
      VAULT_GEM_HIT = registerSound(event, "vault_gem_hit");
      VAULT_GEM_BREAK = registerSound(event, "vault_gem_break");
      ROBOT_HURT = registerSound(event, "robot_hurt");
      ROBOT_DEATH = registerSound(event, "robot_death");
      BOOGIE_AMBIENT = registerSound(event, "boogie_ambient");
      BOOGIE_HURT = registerSound(event, "boogie_hurt");
      BOOGIE_DEATH = registerSound(event, "boogie_death");
      VAULT_PORTAL_OPEN = registerSound(event, "vault_portal_open");
      VAULT_PORTAL_LEAVE = registerSound(event, "vault_portal_leave");
      CLEANSE_SFX = registerSound(event, "cleanse");
      GHOST_WALK_SFX = registerSound(event, "ghost_walk");
      INVISIBILITY_SFX = registerSound(event, "invisibility");
      NIGHT_VISION_SFX = registerSound(event, "night_vision");
      RAMPAGE_SFX = registerSound(event, "rampage");
      EMPOWER = registerSound(event, "empower");
      EMPOWER_ICE_ARMOUR = registerSound(event, "empower_ice_armour");
      SHELL = registerSound(event, "shell");
      SHELL_PORCUPINE = registerSound(event, "shell_porcupine");
      SHELL_QUILL = registerSound(event, "shell_quill");
      VAMPIRE_HISSING_SFX = registerSound(event, "vampire_hissing");
      CAULDRON_BUBBLES_SFX = registerSound(event, "cauldron_bubbles");
      EXECUTION_SFX = registerSound(event, "execution");
      GOBLIN_BAIL = registerSound(event, "goblin_bail");
      GOBLIN_DEATH = registerSound(event, "goblin_death");
      GOBLIN_HURT = registerSound(event, "goblin_hurt");
      GOBLIN_IDLE = registerSound(event, "goblin_idle");
      VAULT_DOOD_IDLE = registerSound(event, "vault_dood.idle");
      SKELETON_PIRATE_IDLE = registerSound(event, "skeleton_pirate.idle");
      WINTERWALKER_IDLE = registerSound(event, "winterwalker.idle");
      WINTERWALKER_STEP = registerSound(event, "winterwalker.step");
      WINTERWALKER_HURT = registerSound(event, "winterwalker.hurt");
      WINTERWALKER_DEATH = registerSound(event, "winterwalker.death");
      OVERGROWN_ZOMBIE_IDLE = registerSound(event, "overgrown_zombie.idle");
      OVERGROWN_ZOMBIE_STEP = registerSound(event, "overgrown_zombie.step");
      OVERGROWN_ZOMBIE_HURT = registerSound(event, "overgrown_zombie.hurt");
      OVERGROWN_ZOMBIE_DEATH = registerSound(event, "overgrown_zombie.death");
      SHIVER_IDLE = registerSound(event, "shiver.idle");
      SHIVER_HURT = registerSound(event, "shiver.hurt");
      SHIVER_DEATH = registerSound(event, "shiver.death");
      BLOODHORDE_IDLE = registerSound(event, "bloodhorde.idle");
      BLOODHORDE_TANK_IDLE = registerSound(event, "bloodhorde.tank_idle");
      BLOODHORDE_TANK_ROAR = registerSound(event, "bloodhorde.tank_roar");
      BLOODHORDE_STEP = registerSound(event, "bloodhorde.step");
      BLOODHORDE_HURT = registerSound(event, "bloodhorde.hurt");
      BLOODHORDE_DEATH = registerSound(event, "bloodhorde.death");
      BURP = registerSound(event, "burp");
      PUZZLE_COMPLETION_MAJOR = registerSound(event, "puzzle_completion_major");
      PUZZLE_COMPLETION_MINOR = registerSound(event, "puzzle_completion_minor");
      PUZZLE_COMPLETION_FAIL = registerSound(event, "puzzle_completion_fail");
      VAULT_CHEST_EPIC_OPEN = registerSound(event, "vault_chest_epic_open");
      VAULT_CHEST_OMEGA_OPEN = registerSound(event, "vault_chest_omega_open");
      VAULT_CHEST_RARE_OPEN = registerSound(event, "vault_chest_rare_open");
      WITCHSKALL_IDLE = registerSound(event, "witchskall_idle");
      FAVOUR_UP = registerSound(event, "favour_up");
      COIN_PILE_BREAK = registerSound(event, "coin_pile_break");
      COIN_PILE_PLACE = registerSound(event, "coin_pile_place");
      COIN_SINGLE_PLACE = registerSound(event, "coin_single_place");
      CRATE_OPEN = registerSound(event, "crate_open");
      MAGNET_TABLE = registerSound(event, "magnet_table");
      NOVA_SPEED = registerSound(event, "nova_speed");
      HEAL = registerSound(event, "heal");
      MANA_SHIELD = registerSound(event, "mana_shield");
      MANA_SHIELD_HIT = registerSound(event, "mana_shield_hit");
      TAUNT = registerSound(event, "taunt");
      TAUNT_REPEL = registerSound(event, "taunt_repel");
      TAUNT_CHARM = registerSound(event, "taunt_charm");
      ARTISAN_SMITHING = registerSound(event, "artisan_smithing");
      HUNTER_SFX = registerSound(event, "hunter");
      MOB_TRAP = registerSound(event, "mob_trap");
      DISARM_TRAP = registerSound(event, "disarm_trap");
      TOTEM = registerSound(event, "totem");
      SMITE_BOLT = registerSound(event, "smite_bolt");
      SMITE = registerSound(event, "smite");
      MOB_CRIT = registerSound(event, "mob_crit");
      ABILITY_OUT_OF_MANA = registerSound(event, "ability_out_of_mana");
      ABILITY_ON_COOLDOWN = registerSound(event, "ability_on_cooldown");
      BONK_CHARGE = registerSound(event, "bonk_charge");
      BONK = registerSound(event, "bonk");
      JEWEL_CUT = registerSound(event, "jewel_cut");
      JEWEL_CUT_SUCCESS = registerSound(event, "jewel_cut_success");
      ARTIFACT_COMPLETE = registerSound(event, "artifact_complete");
      GATE_OPEN = registerSound(event, "gate_open");
      GATE_CLOSE = registerSound(event, "gate_close");
      ARTIFACT_BOSS_AMBIENT = registerSound(event, "artifact_boss_ambient");
      ARTIFACT_BOSS_HURT = registerSound(event, "artifact_boss_hurt");
      ARTIFACT_BOSS_DEATH = registerSound(event, "artifact_boss_death");
      ARTIFACT_BOSS_ATTACK = registerSound(event, "artifact_boss_attack");
      ARTIFACT_BOSS_MAGIC_ATTACK = registerSound(event, "artifact_boss_magic_attack");
      ARTIFACT_BOSS_MAGIC_ATTACK_HIT = registerSound(event, "artifact_boss_magic_attack_hit");
      SPARK_EXPUNGE = registerSound(event, "spark_expunge");
      BOSS_FIGHT_1 = registerSound(event, "boss_fight_1");
      BOSS_FIGHT_2 = registerSound(event, "boss_fight_2");
      BOSS_FIGHT_3 = registerSound(event, "boss_fight_3");
      BOSS_FIGHT_4 = registerSound(event, "boss_fight_4");
      ARTIFACT_BOSS_CATALYST_HIT = registerSound(event, "artifact_boss_catalyst_hit");
      ARTIFACT_BOSS_CATALYST_HIT_WRONG = registerSound(event, "artifact_boss_catalyst_hit_wrong");
      DESTROY_MONOLITH = registerSound(event, "destroy_monolith");
      ICE_BOLT_ARROW = registerSound(event, "ice_bolt_arrow");
      ICE_BOLT_CHUNK = registerSound(event, "ice_bolt_chunk");
      BOOSTER_PACK_OPEN = registerSound(event, "booster_pack_open");
      RAID_GATE_OPEN = registerSound(event, "raid_gate_open");
      RAID_CHAIN_LOCK = registerSound(event, "raid_chain_lock");
      RAID_HATCH_OPEN = registerSound(event, "raid_hatch_open");
      RAID_HATCH_LOCK = registerSound(event, "raid_hatch_lock");
      RAID_IMPACT = registerSound(event, "raid_impact");
   }

   private static SoundEvent registerSound(Register<SoundEvent> event, String soundName) {
      ResourceLocation location = VaultMod.id(soundName);
      SoundEvent soundEvent = new SoundEvent(location);
      soundEvent.setRegistryName(location);
      event.getRegistry().register(soundEvent);
      return soundEvent;
   }
}
