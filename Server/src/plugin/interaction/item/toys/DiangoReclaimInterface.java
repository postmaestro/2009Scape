package plugin.interaction.item.toys;

import org.crandor.cache.def.impl.ItemDefinition;
import org.crandor.game.component.Component;
import org.crandor.game.component.ComponentDefinition;
import org.crandor.game.component.ComponentPlugin;
import org.crandor.game.container.access.InterfaceContainer;
import org.crandor.game.node.entity.player.Player;
import org.crandor.game.node.item.Item;
import org.crandor.plugin.InitializablePlugin;
import org.crandor.plugin.Plugin;
import plugin.holiday.easter.BasketofEggsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Handles diango's item reclaiming interface
 * @author ceik, for the interface, and for most of the holiday items/toys
 */
@InitializablePlugin
public class DiangoReclaimInterface extends ComponentPlugin {
    private static final int COMPONENT_ID = 468;
    public static final List<Item> ITEMS = new ArrayList<>();
    public static final Item[] HOLIDAY_ITEMS = {YoyoPlugin.YOYO, ReindeerHatPlugin.ReindeerHat, BasketofEggsEvent.RUBBER_CHICKEN,ZombieHeadPlugin.ZOMBIE_HEAD, new Item(6857), new Item(6856), new Item(6858), new Item(6859), new Item(6860), new Item(6861), new Item(6862), new Item(6863), new Item(9920), new Item(9921),new Item(9922), new Item(9923), new Item(9924), new Item(9925), new Item(11019), new Item(11020), new Item(11021), new Item(11022), new Item(11789), new Item(11949), new Item(12634), new Item(14076), new Item(14077), new Item(14081),new Item(14595), new Item(14602), new Item(14603), new Item(14605)};

    //initialize the plugin, add lists of items to the ITEMS list...
    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.put(COMPONENT_ID,this);
        ITEMS.addAll(Arrays.asList(HOLIDAY_ITEMS));
        ITEMS.addAll(MarionettePlugin.MARIONETTES);
        return this;
    }

    public static void open(Player player){
        //close any currently open interfaces
        Component curOpen = player.getInterfaceManager().getOpened();
        if(curOpen != null){
            curOpen.close(player);
        }

        //filter out items the player already has in their bank, inventory, or equipped
        Item[] reclaimables = ITEMS.stream().filter(Objects::nonNull).filter(item -> !player.getEquipment().containsItem(item) && !player.getInventory().containsItem(item) && !player.getBank().containsItem(item)).toArray(Item[]::new);

        //only send items if there are some to send
        if(reclaimables.length > 0) {
            InterfaceContainer.generateItems(player, reclaimables, new String[]{"Examine", "Take"}, 468, 2, 8, 8);
        }
        //open the interface
        player.getInterfaceManager().open(new Component(468));
    }

    //refresh the interface
    public static void refresh(Player player){
        player.getInterfaceManager().close();
        open(player);
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
        Item[] reclaimables = ITEMS.stream().filter(Objects::nonNull).filter(item -> !player.getEquipment().containsItem(item) && !player.getInventory().containsItem(item) && !player.getBank().containsItem(item)).toArray(Item[]::new);
        switch(opcode){
            case 155: //interface item option 1 == take
                //add the clicked item to the player's inventory and refresh the interface
                player.getInventory().add(reclaimables[slot]);
                refresh(player);
                break;
            case 196: //interface item option 2 == examine
                //send the examine text for the item to the player
                player.getPacketDispatch().sendMessage(reclaimables[slot].getDefinition().getExamine());
                break;
        }
        return false;
    }
}
