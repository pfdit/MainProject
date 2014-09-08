package com.dit.group2.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.dit.group2.order.Order;
import com.dit.group2.person.Customer;
import com.dit.group2.person.Person;
import com.dit.group2.person.Staff;
import com.dit.group2.retailSystem.RetailSystemDriver;
import com.dit.group2.stock.Product;
import com.dit.group2.stock.StockItem;

@SuppressWarnings("serial")
public class CustomerOrderTab extends GuiLayout implements ActionListener, ItemListener,
		ListSelectionListener {

	private RetailSystemDriver driver;
	private ArrayList<StockItem> orderStockItemList;
	private Staff currentlyLoggedInStaff;
	private NumberFormat formatter = new DecimalFormat("#0.00");

	protected Vector<String> comboBoxItems = new Vector<String>();
	protected ArrayList<Integer> itemsQuantity = new ArrayList<Integer>();
	protected ArrayList<Double> itemsPrice = new ArrayList<Double>();
	protected DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<String>(
			comboBoxItems);
	protected JComboBox<String> productComboBox = new JComboBox<String>(comboBoxModel);

	protected Vector<String> customerComboBoxItems = new Vector<String>();
	protected DefaultComboBoxModel<String> customerComboBoxModel = new DefaultComboBoxModel<String>(
			customerComboBoxItems);
	protected JComboBox<String> customerComboBox = new JComboBox<String>(customerComboBoxModel);

	protected DefaultListModel<String> listModel = new DefaultListModel<String>();
	protected JList<String> orderingList = new JList<String>(listModel);
	protected JScrollPane orderingListSroll = new JScrollPane(orderingList);
	private JScrollBar scrollBar = orderingListSroll.getVerticalScrollBar();

	protected ArrayList<Product> currentOrderList = new ArrayList<Product>();
	protected ArrayList<Integer> currentOrderListQuantity = new ArrayList<Integer>();
	protected ArrayList<Integer> currentOrderComboIndex = new ArrayList<Integer>();

	protected JLabel productComboBoxLabel, quantityLabel, availableQuantityLabel,
			availableQuantityField, priceLabel, priceField, grandTotalLabel, grandTotalField,
			orderListProductLabel, orderListQtyLabel, orderListPriceLabel,
			orderListTotalPriceLabel, customerComboBoxLabel;
	protected JTextField quantityTextField;
	protected JButton addButton, removeButton, processButton, cancelButton;
	protected double grandTotal = 0;

	private TabListCellRenderer renderer = new TabListCellRenderer(false);

	// source for this class:
	// http://www.grandt.com/sbe/files/uts2/Chapter10html/Chapter10.htm

	public CustomerOrderTab(Staff currentlyLoggedInStaff, RetailSystemDriver driver) {
		super();

		this.driver = driver;
		this.currentlyLoggedInStaff = currentlyLoggedInStaff;

		titleLabel.setText("Customer Order Form");
		setUpCustomerComboBox();
		setUpProductComboBox();
		setUpQuantityTextField();
		setUpAvailableQuantity();
		setUpPriceFields();
		setAddButton();
		setRemoveButton();
		setProcessButton();
		setCancelButton();
		setUpSubtotal();
		orderingList();
		refreshTab(0);

		setLayout(null);
		setVisible(true);

		// Set tool tips for clickable buttons
		addButton.setToolTipText("Click to add an order for this customer.");
		removeButton.setToolTipText("Click to remove an order.");
		processButton.setToolTipText("Click to process this customer's order.");
		cancelButton.setToolTipText("Click to remove all orders for this customer.");

		// Set tool tips for combobox items
		customerComboBox.setToolTipText("Click to select which customer to order for.");
		productComboBox.setToolTipText("Click to select which product to order.");

		// Other tool tips...

		// automaticallyCreateOrders();
	}

	/** add customer combo box */
	private void setUpCustomerComboBox() {
		customerComboBoxLabel = new JLabel("Customers");
		customerComboBoxLabel.setBounds(59, 7, 93, 20);
		mainPanel.add(customerComboBoxLabel);
		customerComboBox.setBounds(200, 7, 265, 23);
		mainPanel.add(customerComboBox);
		fillUpCustomerComboBox();
		customerComboBox.addItemListener(this);
		customerComboBox.addMouseListener(this);
	}

	/** add product combo box */
	private void setUpProductComboBox() {
		productComboBoxLabel = new JLabel("Products");
		productComboBoxLabel.setBounds(59, 32, 265, 20);
		productComboBox.setBounds(200, 32, 265, 23);
		mainPanel.add(productComboBoxLabel);
		mainPanel.add(productComboBox);
		fillUpProductComboBox();
		productComboBox.addItemListener(this);
	}

	/** add product quantity field */
	private void setUpQuantityTextField() {
		quantityLabel = new JLabel("Order Quantity");
		quantityLabel.setBounds(59, 107, 110, 20);
		quantityTextField = new JTextField("1");
		quantityTextField.setBounds(200, 109, 50, 23);
		mainPanel.add(quantityLabel);
		mainPanel.add(quantityTextField);
	}

	/** add product available quantity */
	private void setUpAvailableQuantity() {
		availableQuantityLabel = new JLabel("Available");
		availableQuantityLabel.setBounds(59, 60, 93, 14);
		availableQuantityField = new JLabel(Integer.toString(itemsQuantity.get(0)));
		availableQuantityField.setBounds(200, 60, 93, 14);
		mainPanel.add(availableQuantityLabel);
		mainPanel.add(availableQuantityField);
	}

	/** add product price fields */
	private void setUpPriceFields() {
		priceLabel = new JLabel("Unit Price");
		priceLabel.setBounds(59, 85, 93, 14);
		priceField = new JLabel(formatter.format(itemsPrice.get(0)));
		priceField.setBounds(200, 85, 93, 14);
		mainPanel.add(priceLabel);
		mainPanel.add(priceField);
	}

	/** add Grand Total price fields */
	private void setUpSubtotal() {
		grandTotalLabel = new JLabel("Grand Total:");
		grandTotalLabel.setBounds(335, 320, 130, 23);
		grandTotalField = new JLabel("0.00");
		grandTotalField.setBounds(350, 320, 130, 23);

		grandTotalField.setHorizontalAlignment(JLabel.RIGHT);
		mainPanel.add(grandTotalLabel);
		mainPanel.add(grandTotalField);
	}

	/** add add button */
	private void setAddButton() {
		addButton = new JButton("+");
		addButton.setFont(new Font("Arial", Font.BOLD, 22));
		addButton.setBounds(270, 110, 93, 20);
		addButton.addActionListener(this);
		mainPanel.add(addButton);
	}

	/** add remove button */
	private void setRemoveButton() {
		removeButton = new JButton("-");
		removeButton.setFont(new Font("Arial", Font.BOLD, 35));

		removeButton.setBounds(380, 110, 93, 20);
		removeButton.addActionListener(this);
		mainPanel.add(removeButton);
		removeButton.setEnabled(false);
	}

	/** add process button */
	private void setProcessButton() {
		processButton = new JButton("Create Order");
		processButton.setBounds(199, 320, 130, 23);
		processButton.addActionListener(this);
		mainPanel.add(processButton);
		processButton.setEnabled(false);
	}

	/** set up cancel button */
	private void setCancelButton() {
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(64, 320, 130, 23);
		cancelButton.addActionListener(this);
		mainPanel.add(cancelButton);
		cancelButton.setEnabled(false);
	}

	/** add buying list */
	private void orderingList() {
		orderListProductLabel = new JLabel("Product name");
		orderListProductLabel.setBounds(59, 140, 110, 20);
		orderListQtyLabel = new JLabel("Qty");
		orderListQtyLabel.setBounds(275, 140, 30, 20);
		orderListPriceLabel = new JLabel("Price");
		orderListPriceLabel.setBounds(340, 140, 50, 20);
		orderListTotalPriceLabel = new JLabel("Total");
		orderListTotalPriceLabel.setBounds(420, 140, 50, 20);
		mainPanel.add(orderListProductLabel);
		mainPanel.add(orderListQtyLabel);
		mainPanel.add(orderListPriceLabel);
		mainPanel.add(orderListTotalPriceLabel);

		orderingList.addListSelectionListener(this);
		orderingListSroll.setBounds(59, 157, 420, 157);

		mainPanel.add(orderingListSroll);

		renderer.setTabs(new int[] { 230, 315, 395 });
		orderingList.setCellRenderer(renderer);
	}

	public JComboBox<String> getCustomerComboBox() {
		return customerComboBox;
	}

	/** fill up customer combo Box */
	public void fillUpCustomerComboBox() {
		ArrayList<Person> customers = driver.getPersonDB().getCustomerList();
		customerComboBoxItems.clear();
		customerComboBoxItems.add("<html><font color='red'>Add New Customer</font></html>");
		String item = "";
		for (Person customer : customers) {
			item = "\t" + customer.getId() + " \t - \t " + customer.getName();
			customerComboBoxItems.add(item);
		}
		customerComboBox.setSelectedIndex(customers.size());
		validate();
		repaint();
	}

	/** fill up product combo Box with price and quantity */
	public void fillUpProductComboBox() {
		ArrayList<StockItem> list = driver.getStockDB().getStockList();
		comboBoxItems.clear();
		itemsQuantity.clear();
		itemsPrice.clear();
		for (StockItem stockItem : list) {
			comboBoxItems.add(stockItem.getProduct().getProductName());
			itemsPrice.add(stockItem.getProduct().getRetailPrice());
			itemsQuantity.add(stockItem.getQuantity());
		}
		productComboBox.setSelectedIndex(list.size() - 1);
	}

	/** refresh Tab */
	private void refreshTab(int index) {
		priceField.setText(formatter.format(itemsPrice.get(index)));
		quantityTextField.setText("1");
		availableQuantityField.setText(Integer.toString(itemsQuantity.get(index)));
		grandTotalField.setText(formatter.format(grandTotal));
		productComboBox.setSelectedIndex(index);
	}

	private String textAlignment(String text1, String text2, String text3, String text4) {
		String s = text1;
		s += "\t" + text2 + "\t" + text3 + "\t" + text4;
		return s;
	}

	/** combo box selection */
	@Override
	public void itemStateChanged(ItemEvent event) {
		if (event.getStateChange() == ItemEvent.SELECTED) {
			refreshTab(productComboBox.getSelectedIndex());
		}
		if (event.getItemSelectable().equals(customerComboBox)) {
			if (customerComboBox.getItemAt((customerComboBox.getSelectedIndex())).equals(
					"<html><font color='red'>Add New Customer</font></html>")) {
				driver.getGui().getTabbedPane().setSelectedComponent(
						driver.getGui().getCustomerTab());
				driver.getGui().getCustomerTab().getNewCustomerButton().doClick();
			}
			/*
			 * else customer = driver.getPersonDB().getSupplierByName(supplierComboBox
			 * .getItemAt(supplierComboBox.getSelectedIndex()));
			 */
		}
	}

	/** button actions */
	@Override
	public void actionPerformed(ActionEvent e) {

		// ADD BUTTON
		if (e.getSource() == addButton) {

			// add product and quantity to the current order list
			int index = productComboBox.getSelectedIndex();

			Product selectedProduct = driver.getStockDB().getStockList().get(index).getProduct();

			int availableQuantity = itemsQuantity.get(index);

			// accepting only integer into quantityTextField
			try {
				int orderingQuantity = (Integer.parseInt(quantityTextField.getText()));

				if (orderingQuantity > 0) {

					// check if there is enough product available to buy
					if (orderingQuantity <= availableQuantity) {
						processButton.setEnabled(true);
						// set available quantity
						itemsQuantity.set(index, availableQuantity - orderingQuantity);

						// refresh subtotal price
						grandTotal += (selectedProduct.getRetailPrice() * orderingQuantity);

						// check if the selected product is already in the currentOrderList
						int orderListIndex = currentOrderList.indexOf(selectedProduct);
						if (orderListIndex >= 0) {
							orderingQuantity += currentOrderListQuantity.get(orderListIndex);
							currentOrderListQuantity.set(orderListIndex, orderingQuantity);

							listModel.setElementAt(textAlignment(comboBoxItems.get(index), Integer
									.toString(orderingQuantity), priceField.getText(), formatter
									.format(orderingQuantity * selectedProduct.getRetailPrice())),
									orderListIndex);
							orderingList.setSelectedIndex(orderListIndex);

						}
						// selected product is NOT on the currntOrderList
						else {
							currentOrderList.add(selectedProduct);
							currentOrderListQuantity.add(orderingQuantity);
							listModel.addElement(textAlignment(comboBoxItems.get(index),
									quantityTextField.getText(), priceField.getText(), formatter
											.format(orderingQuantity
													* selectedProduct.getRetailPrice())));
							validate();
							scrollBar.setValue(scrollBar.getMaximum());
							orderingList.setSelectedIndex(listModel.getSize() - 1);
						}

						removeButton.setEnabled(true);
						cancelButton.setEnabled(true);
						refreshTab(index);
					}
					else
						JOptionPane.showMessageDialog(null, "Not enough product in stock!");
				}
				else
					JOptionPane.showMessageDialog(null,
							"Product quantity must by positive whole number");

			}
			catch (NumberFormatException error) {
				JOptionPane.showMessageDialog(null,
						"Invalid entry, only positive whole numbers please!");
				// error.printStackTrace();
			}
		}

		// REMOVE BUTTON
		if (e.getSource() == removeButton) {
			int index = orderingList.getSelectedIndex();
			int i = 0; // index in combo list or stock list

			for (StockItem stockItem : driver.getStockDB().getStockList()) {
				if (stockItem.getProduct().getProductID() == currentOrderList.get(index)
						.getProductID()) {
					break;
				}
				i++;
			}

			Product selectedProduct = driver.getStockDB().getStockList().get(i).getProduct();

			int availableQuantity = itemsQuantity.get(i);

			// accepting only integer into quantityTextField
			try {
				int orderingQuantity = (Integer.parseInt(quantityTextField.getText()));

				if (orderingQuantity > 0) {
					processButton.setEnabled(true);
					// set available quantity
					itemsQuantity.set(i, availableQuantity + orderingQuantity);
					grandTotal -= (currentOrderList.get(index).getRetailPrice() * Integer
							.parseInt(quantityTextField.getText()));

					// check if the selected product is already in the currentOrderList
					int orderListIndex = currentOrderList.indexOf(selectedProduct);
					if (orderListIndex >= 0 && (currentOrderListQuantity.get(orderListIndex) > 1)) {
						orderingQuantity = currentOrderListQuantity.get(orderListIndex)
								- (Integer.parseInt(quantityTextField.getText()));
						currentOrderListQuantity.set(orderListIndex, orderingQuantity);

						listModel.setElementAt(textAlignment(comboBoxItems.get(index), Integer
								.toString(orderingQuantity), priceField.getText(), formatter
								.format(orderingQuantity * selectedProduct.getRetailPrice())),
								orderListIndex);
						orderingList.setSelectedIndex(orderListIndex);

					}
					else {
						currentOrderList.remove(index);
						currentOrderListQuantity.remove(index);
						listModel.remove(index);

					}
					removeButton.setEnabled(true);
					cancelButton.setEnabled(true);
				}
				else
					JOptionPane.showMessageDialog(null,
							"Product quantity must by positive whole number");

			}
			catch (NumberFormatException error) {
				JOptionPane.showMessageDialog(null,
						"Invalid entry, only positive whole numbers please!");
				// error.printStackTrace();
			}
			if (listModel.isEmpty()) {
				removeButton.setEnabled(false);
				processButton.setEnabled(false);
				cancelButton.setEnabled(false);
			}
			refreshTab(i);
		}

		// PROCESS BUTTON
		if (e.getSource() == processButton) {

			// make new stock list, fill the elements from the chosen product and quantities
			StockItem orderStockItem = new StockItem();
			orderStockItemList = new ArrayList<StockItem>();

			for (int i = 0; i < currentOrderList.size(); i++) {
				orderStockItem = new StockItem();
				orderStockItem.setProduct(currentOrderList.get(i));
				orderStockItem.setQuantity(currentOrderListQuantity.get(i));
				orderStockItemList.add(orderStockItem);
			}
			if (driver.getPersonDB().getCustomerList().size() > 0) {
				Customer customer = (Customer) driver.getPersonDB().getCustomerList().get(
						customerComboBox.getSelectedIndex() - 1);

				// Staff responsibleStaffForOrder =
				// (Staff)driver.getPersonDB().getStaffList().get(0);

				// make a new order and put it to the orderDB
				Order order = new Order(currentlyLoggedInStaff, customer, orderStockItemList,
						grandTotal, new java.util.Date());
				driver.getOrderDB().getCustomerOrderList().add(order);
				customer.getOrders().add(order);

				// reduce the products' quantity in stockDBControl
				int quantity;
				for (int index = 0; index < currentOrderList.size(); index++) {
					quantity = driver.getStockDB().getStockItem(
							currentOrderList.get(index).getProductID()).getQuantity();
					quantity -= currentOrderListQuantity.get(index);
					driver.getStockDB().getStockItem(currentOrderList.get(index).getProductID())
							.setQuantity(quantity);
				}

				driver.getGui().getCustomerTab().setAutomaticItemSelection(true);
				driver.getGui().getCustomerTab().addItemsToOrderCombobox();
				driver.getGui().getCustomerOrderHistorytab().setOrderList();
				showOrderDetails(order);

				// clear the tab and refresh
				listModel.clear();
				currentOrderList.clear();
				currentOrderListQuantity.clear();
				grandTotal = 0;
				removeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				processButton.setEnabled(false);
				fillUpCustomerComboBox();
				fillUpProductComboBox();
				refreshTab(0);
				driver.getGui().getStockControlTab().refreshStockControlTab();
				driver.getGui().getAccountingTab().refreshAccountingTab();
			}
			else {
				customerComboBoxLabel.setForeground(Color.red);
				JOptionPane.showMessageDialog(this,
						"Order cannot be created!\nCustomer field cannot be empty!");
			}
		}
		// CANCEL BUTTON
		if (e.getSource() == cancelButton) {
			do {
				int index = orderingList.getSelectedIndex();
				int i = 0; // index in combo list or stock list

				for (StockItem stockItem : driver.getStockDB().getStockList()) {
					if (stockItem.getProduct().getProductID() == currentOrderList.get(index)
							.getProductID()) {
						break;
					}
					i++;
				}

				grandTotal -= (currentOrderList.get(index).getRetailPrice() * currentOrderListQuantity
						.get(index));
				itemsQuantity.set(i, itemsQuantity.get(i) + currentOrderListQuantity.get(index));
				currentOrderList.remove(index);
				currentOrderListQuantity.remove(index);
				listModel.remove(index);

				if (listModel.isEmpty()) {
					removeButton.setEnabled(false);
					processButton.setEnabled(false);
				}
				if (index != 0)
					orderingList.setSelectedIndex(index - 1);
				else
					orderingList.setSelectedIndex(0);

				refreshTab(i);
			}
			while (listModel.getSize() != 0);
			cancelButton.setEnabled(false);
		}
	}

	/**
	 * Show the order details
	 */
	private void showOrderDetails(Order order) {
		String orderDetailsMessage = "New order was created at " + order.getDate();
		orderDetailsMessage += "\nStaff ID : " + order.getCurrentlyLoggedInStaff().getId();
		orderDetailsMessage += "\nCustomer ID : " + order.getPerson().getId() + " Customer name : "
				+ order.getPerson().getName();
		orderDetailsMessage += "\nItems in this order : ";
		for (StockItem stockItem : order.getOrderEntryList()) {
			orderDetailsMessage += "\n   " + stockItem.getQuantity() + " x "
					+ stockItem.getProduct().getProductName() + " \t Subtotal \t\u20ac"
					+ (stockItem.getProduct().getRetailPrice() * stockItem.getQuantity());
		}
		orderDetailsMessage += "\nThe total order value is \t\u20ac" + order.getGrandTotalOfOrder()
				+ "\n";
		JOptionPane.showMessageDialog(null, orderDetailsMessage, "Order ID : "
				+ (Order.getUniqueId() - 1), JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * Set the selected combo box item
	 * 
	 * @param productName
	 */
	private void setSelectedProductComboboxItem(String productName) {
		for (String str : comboBoxItems) {
			if (str.equals(productName))
				productComboBox.setSelectedItem(str);
		}
	}

	// list selection event
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getSource() == orderingList) {

			// check if order list is empty
			if (orderingList.getSelectedIndex() > -1) {

				// separate the text in the selected list item
				String[] values = orderingList.getSelectedValue().split("\\t");
				// call method to set the current combobox item according to the product name of the
				// selected list item
				setSelectedProductComboboxItem(values[0]);
			}
			removeButton.setEnabled(true);
			cancelButton.setEnabled(true);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == customerComboBox) {
			if (customerComboBox.getSelectedItem().equals(
					"<html><font color='red'>Add New Customer</font></html>")) {
				driver.getGui().getTabbedPane().setSelectedComponent(
						driver.getGui().getCustomerTab());
				driver.getGui().getCustomerTab().getNewCustomerButton().doClick();
			}
		}
	}

}
