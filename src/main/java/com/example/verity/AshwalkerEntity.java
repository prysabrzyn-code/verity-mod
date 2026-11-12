package com.example.verity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/** A hostile ash-covered walker. Uses zombie behaviour with its own look. */
public class AshwalkerEntity extends Zombie {

    public AshwalkerEntity(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
    }
}
