package com.klapeks.cosmetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.klapeks.libs.xItem;
import com.klapeks.libs.nms.NMS;

public class Titles {
	
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
		TitleCategory c = getCategory(id);
		if (c != null) return c;
		c = new TitleCategory(id, name);
		categories.add(c);
		return c;
	}
	public static TitleCategory getCategory(String id) {
		for (TitleCategory tc : categories) {
			if (tc.getId().equalsIgnoreCase(id)) return tc;
		}
		return null;
	}
	public static List<String> getCategoriesIds() {
		return Titles.categories.stream().map(t->t.id).collect(Collectors.toList());
	}
	public static Set<String> getCategoryTitlesIds(String category_id) {
		TitleCategory tc = Titles.getCategory(category_id);
		if (tc==null) return null;
		return tc.titles.keySet();
	}
//	public static void openMenu(Player p, String id) {
//		getCategory(id).getMenu().openInventory(p);
//	}
	
	
	
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
