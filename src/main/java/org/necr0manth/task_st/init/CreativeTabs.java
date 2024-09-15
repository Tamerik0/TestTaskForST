package org.necr0manth.task_st.init;

import org.necr0manth.task_st.TaskStMod;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.init.ItemsHL;
import org.zeith.hammerlib.proxy.HLConstants;

public class CreativeTabs {
    @CreativeTab.RegisterTab
    public static final CreativeTab MOD_TAB = new CreativeTab(TaskStMod.id("root"),
            builder -> builder
                    .icon(ItemsHL.COPPER_GEAR::getDefaultInstance)
                    .withTabsBefore(HLConstants.HL_TAB.id())
    );
}
