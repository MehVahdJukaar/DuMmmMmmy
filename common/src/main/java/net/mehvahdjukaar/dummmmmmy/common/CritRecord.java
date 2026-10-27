package net.mehvahdjukaar.dummmmmmy.common;

import net.minecraft.world.entity.Entity;

//victim field is for stuff that reuse the source on other entities mid hurt
public record CritRecord(Entity victim, float multiplier) {
}
