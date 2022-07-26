package com.klapeks.cosmetic;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.klapeks.cosmetic.Titles.TitleCategory;
import com.klapeks.libs.commands.Messaging;
import com.klapeks.libs.inv.CustomInventory;
import com.klapeks.libs.nms.NMS;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;

public class TitleMenu extends CustomInventory {
	
	private TitleCategory c;
	TitleMenu(TitleCategory category) {
		super("Титулы: " + category.getName(), 5);
		this.c = category;
		setCenterTitle(true);
	}
	
	@Override
	public Inventory onOpen(Player p, Inventory inv) {
		return inv;
	}
	
	@Override
	public void onClick(InventoryClickEvent e) {
		e.setCancelled(true);
		int slot = e.getRawSlot();
		if (slot==e.getView().getTopInventory().getSize()-1) {
			setSuffix((Player) e.getWhoClicked(), null);
			return;
		}
		String id = NMS.item.getNBT(e.getCurrentItem(), "title_id", String.class);
		if (id==null) return;
		setSuffix((Player) e.getWhoClicked(), Titles.get(c.getId(), id));
	}
	
	public static void setSuffix(Player p, String suffix) {
		User user = LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId());
		user.data().clear(NodeType.SUFFIX.predicate());
		if (suffix!=null) user.data().add(SuffixNode.builder(Messaging.msg(suffix), 111).build());
		p.sendMessage(Messaging.msg(suffix+""));
		LuckPermsProvider.get().getUserManager().saveUser(user);
	}
}
