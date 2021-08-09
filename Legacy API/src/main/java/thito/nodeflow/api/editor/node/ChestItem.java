package thito.nodeflow.api.editor.node;

import java.lang.reflect.*;
import java.util.*;

public class ChestItem {
    private Field type;
    private int amount = 1;
    private List<String> itemFlags = new ArrayList<>();
    private Map<String, Integer> enchantments = new HashMap<>();
    private double durabilityPercentage;
    private String displayName;
    private List<String> lore = new ArrayList<>();

    public ChestItem(Field type) {
        this.type = type;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setDurabilityPercentage(double durabilityPercentage) {
        this.durabilityPercentage = durabilityPercentage;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public double getDurabilityPercentage() {
        return durabilityPercentage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public List<String> getItemFlags() {
        return itemFlags;
    }

    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    public Field getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
}
