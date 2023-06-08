package org.zeith.modid.mixins;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
	@Shadow public abstract int getCount();
	
	@Shadow public abstract Item getItem();
	
	@Inject(
			method = "toString",
			at = @At("HEAD"),
			cancellable = true
	)
	public void toString_MI(CallbackInfoReturnable<String> cir)
	{
		cir.setReturnValue(this.getCount() + " " + this.getItem() + " (modified by Modid's mixins)");
		cir.cancel();
	}
}