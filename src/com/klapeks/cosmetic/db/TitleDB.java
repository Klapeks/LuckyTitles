package com.klapeks.cosmetic.db;

import java.util.ArrayList;
import java.util.List;

import com.klapeks.db.SQL;
import com.klapeks.sql.Where;

public class TitleDB {
	
	public static void init() {
		SQL.sql().createTableIfNotExists(TitlePlayer.class);
	}

	public static List<String> getTitles(String playername, String category) {
		Where where = SQL.where("`player` = ? AND `category` = ?", playername.toLowerCase(), category);
		TitlePlayer tp = SQL.sql().selectOne(TitlePlayer.class, where);
		if (tp==null || tp.titles.isEmpty()) return null;
		return tp.titles;
	}

	public static void addTitle(String playername, String category, String title) {
		Where where = SQL.where("`player` = ? AND `category` = ?", playername.toLowerCase(), category);
		TitlePlayer tp = SQL.sql().selectOne(TitlePlayer.class, where);
		if (tp==null) {
			tp = new TitlePlayer();
			tp.player = playername.toLowerCase();
			tp.category = category;
			tp.titles = new ArrayList<>();
			SQL.sql().insert(tp);
		}
		else if (tp.titles.contains(title)) return;
		tp.titles.add(title);
		SQL.sql().update(tp, where);
	}

	public static void removeTitle(String playername, String category, String title) {
		Where where = SQL.where("`player` = ? AND `category` = ?", playername.toLowerCase(), category);
		TitlePlayer tp = SQL.sql().selectOne(TitlePlayer.class, where);
		if (tp==null) return;
		if (tp.titles.contains(title)) tp.titles.remove(title);
		SQL.sql().update(tp, where);
	
	}
	
}
