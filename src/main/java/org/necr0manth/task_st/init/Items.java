package org.necr0manth.task_st.init;

import net.minecraft.world.item.Item;
import org.necr0manth.task_st.items.LightningWand;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;

@SimplyRegister
public interface Items
{
	@RegistryName("lightning_wand")
	Item LIGHTNING_WAND = new LightningWand();
}