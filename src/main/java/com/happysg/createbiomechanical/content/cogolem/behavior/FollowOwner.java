package com.happysg.createbiomechanical.content.cogolem.behavior;

import com.happysg.createbiomechanical.content.cogolem.CogolemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FollowEntity;

/**
 * @param <E> The owner of the brain
 */
public class FollowOwner<E extends CogolemEntity> extends FollowEntity<E, LivingEntity> {
	protected LivingEntity owner = null;

	public FollowOwner() {
		following(this::getOwner);
	}

	protected LivingEntity getOwner(E entity) {
		if (this.owner == null)
			this.owner = entity.getOwner();

		if (this.owner != null && this.owner.isRemoved())
			this.owner = null;

		return this.owner;
	}
}
