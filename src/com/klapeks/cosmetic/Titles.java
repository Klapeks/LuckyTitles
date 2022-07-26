package com.klapeks.cosmetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.klapeks.cosmetic.db.TitleDB;
import com.klapeks.cosmetic.db.TitleSQL;
import com.klapeks.db.SQL;
import com.klapeks.libs.xItem;
import com.klapeks.libs.nms.NMS;

public class Titles {
	
	private static TitleDB _db;
	public static TitleDB db() {
		if (!SQL.isSQLType()) {}
		else _db = new TitleSQL();
		return _db;
	}
	static void disconnect() {
		if (_db==null) return;
		_db.disconnect();
		_db = null;
	}
	
	static List<TitleCategory> categories = new ArrayList<>();
	
	static void resetTitles() {
		categories.forEach(TitleCategory::clear);
		categories.clear();
	}

	public static void add(String categoryID, String titleID, String titleName) {
		getCategory(categoryID).addTitle(titleID, titleName);
	}
	public static String get(String categoryID, String titleID) {
		return getCategory(categoryID).getTitle(titleID);
	}
	
	static TitleCategory createCategory(String id, String name) {
		return category(id, name);
	}
	public static TitleCategory getCategory(String id) {
		return category(id, id);
	}
	private static TitleCategory category(String id, String name) {
		for (TitleCategory tc : categories) {
			if (tc.getId().equals(id)) return tc;
		}
		TitleCategory c = new TitleCategory(id, name);
		categories.add(c);
		return c;
	}
	public static void openMenu(Player p, String id) {
		getCategory(id).getMenu().openInventory(p);
	}
	
	
	
	public static class TitleCategory {
		String id, name;
		Map<String, String> titles = new HashMap<>();
		TitleMenu menu;
		private TitleCategory(String id) {
			this(id, null);
		}
		private TitleCategory(String id, String name) {
			this.id = id;
			this.name = name;
			menu = new TitleMenu(this);
		}
		
		public String getName() {
			return name == null ? id : name;
		}
		public String getId() {
			return id;
		}
		public void addTitle(String id, String name) {
			ItemStack item = xItem.of(Material.LIGHT_GRAY_DYE, "ßf“ËÚÛÎ: "+name);
			item = NMS.item.setNBT(item, "title_id", id);
			menu.setItem(titles.size(), item);
			titles.put(id, name);
		}
		void clear() {
			titles.clear();
		}
		public String getTitle(String id) {
			return titles.get(id);
		}
		public TitleMenu getMenu() {
			return menu;
		}
	}
}
