package thito.nodeflow.internal.node;

import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.editor.node.ChestSlot;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.list.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.list.*;
import thito.nodeflow.minecraft.*;

import java.util.*;

public class ChestSlotImpl implements ChestSlot {
    private thito.nodeflow.minecraft.ChestSlot impl;
    private ChestItem item;

    public ChestSlotImpl(thito.nodeflow.minecraft.ChestSlot impl) {
        this.impl = impl;
    }

    @Override
    public ChestItem getItem() {
        return item;
    }

    private static String toRoman(int x) {
        switch (x) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            case 6: return "VI";
            case 7: return "VII";
            case 8: return "VIII";
            case 9: return "IX";
            case 10: return "X";
        }
        return "enchantment.level."+x;
    }
    @Override
    public void setItem(ChestItem item) {
        this.item = item;
        IconedListHandler handler = ((IconedListImpl) UIManagerImpl.getInstance().getIconedList()).get(item.getType().getType());
        ArrayList<String> lore = new ArrayList<>();
        impl.durabilityPercentageProperty().set(item.getDurabilityPercentage());
        impl.amountProperty().set(item.getAmount());
        lore.addAll(item.getLore());
        if (item.getItemFlags().contains("HIDE_ENCHANTS")) {
            item.getEnchantments().forEach((ench, level) -> {
                lore.add("§7" + ench+" "+toRoman(level));
            });
        }
        MinecraftHover hover = impl.getHover();
        MinecraftHover.ItemHover itemHover = new MinecraftHover.ItemHover();
        itemHover.getLore().addAll(lore);
        hover.getPane().setCenter(itemHover);
        ImageView view = new ImageView();
        impl_getPeer().setCenter(view);
        if (handler != null) {
            IconedContent content = handler.getContent(item.getType());
            if (content != null) {
                Icon icon = content.getIcon();
                if (icon != null) {
                    view.imageProperty().bind(icon.impl_propertyPeer());
                    return;
                }
            }
        }
        view.imageProperty().bind(Icon.icon("missing-object").impl_propertyPeer());
    }

    @Override
    public BorderPane impl_getPeer() {
        return impl.getItemContainer();
    }
}
