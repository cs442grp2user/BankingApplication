package cs442.group2.BankingApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cart {
	private int customerID;
	HashMap<Item, Integer> items;

	public Cart(int customerID) {
		// Populate data into items variable from Cart table
		items = new HashMap<Item, Integer>();
		this.customerID = customerID;

	}

	public List<Item> getAllItems() {
		// Get all unique items from cart
		ArrayList<Item> items = new ArrayList<Item>();

		return items;
	}

	public int getQuantity(Item i) {
		Integer itemQuantity = items.get(i);
		if (itemQuantity == null)
			return 0;
		else
			return itemQuantity.intValue();
	}

	public void addItem(Item item, int quantity) {
		// Save in Cart table too
		items.put(item, quantity);
	}

	public void removeItem(Item item) {
		// Remove from Cart table too
		items.remove(item);
	}

	public void changeQuantity(Item item, int quantity) {
		// Update in Cart table too
		items.put(item, quantity);
	}

	public int getCustomerID() {
		return customerID;
	}

}
