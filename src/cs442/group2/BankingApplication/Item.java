package cs442.group2.BankingApplication;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Item {
	private int itemID;
	private String itemName;
	private double itemCost;
	private int itemQuantity;
	private String itemCategory;
	public static void setQuantity(int itemID, int quantity) {
		// Add DB Code, to update quantity in Item table
		// This code is only called after an order is succesfull
	}
	public static List<Item> searchItem(String itemName) { 
		ArrayList<Item> result = new ArrayList<Item>();
		// Add DB code.
		return result;
	}
	private Item(int itemID, String itemName, double itemCost, int itemQuantity,
			String itemCategory) {
		this.itemID = itemID;
		this.itemName = itemName;
		this.itemCost = itemCost;
		this.itemQuantity = itemQuantity;
		this.itemCategory = itemCategory;
	}
}
