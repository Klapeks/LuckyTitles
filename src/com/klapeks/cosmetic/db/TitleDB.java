package com.klapeks.cosmetic.db;

import java.util.List;

import org.bukkit.entity.Player;

public interface TitleDB {

	public List<String> getTitles(String playername, String category);
	public void addTitle(String playername, String category, String title);
	public void init();
	
}
