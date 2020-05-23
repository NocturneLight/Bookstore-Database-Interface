import java.sql.*;
import java.util.*;
import java.time.*;
import java.lang.*;
import java.time.format.DateTimeFormatter;

public class Bookstore
{
	public static void main(String[] args) 
	{
		// Create variables or other objects here.
		UserInterface ui = new UserInterface();
		
		// Start the program.
		ui.runInterface();	
	}
}

class UserInterface
{
	// Create our variables here.
	private Database db = new Database("Bookstore", "chocobo");
	private Scanner scan;
	private String userChoice = "";
	private String userName = "";
	private String userPassword = "";
	private boolean loop = false;
	private boolean memberMenu = false;
	private boolean newCard = false;
	private Vector<String> memberInfo = new Vector<String>();
	private Vector<Vector<String>> cart = new Vector<Vector<String>>();
	
	// Create our functions here.
	void runInterface()
	{
		while (true)
		{
			// Set the value of loop at the start of the function 
			// to prevent errors during future iterations.
			this.loop = !this.loop ? true : true;
			
			// Clear the cart upon reaching the main menu to prevent runtime errors.
			this.cart.clear();
			
			if (!memberMenu)
			{
				// Print out the welcome screen.
				System.out.println("**********************************************************************************************************\n"
						 	 	 + "***												       ***\n"
						 	 	 + "***				   Welcome to the Online Bookstore				       ***\n"
						 	 	 + "***												       ***\n"
						 	 	 + "**********************************************************************************************************\n");
		
				// Print out the user's choices.
				System.out.println("1. Member Login\n" +
								   "2. New Member Registration\n" + 
								   "q. Quit\n");
			}
			else if (memberMenu)
			{
				// Print out the member menu.
				System.out.println("**********************************************************************************************************\n"
				 	 	         + "***												       ***\n"
				 	 	         + "***				   Welcome to the Online Bookstore				       ***\n"
				 	 	         + "***												       ***\n"
				 	 	         + "***                                          Member Menu                                               ***\n"
				 	 	         + "**********************************************************************************************************\n");
				
				// Print out the user's choices.
				System.out.println("1. Browse by Subject\n" +
								   "2. Search by Author/Title\n" +
								   "3. View/Edit Shopping Cart\n" +
								   "4. Check Order Status\n" +
								   "5. Check Out\n" +
								   "6. One Click Check Out\n" +
								   "7. View/Edit Personal Information\n" +
								   "8. Logout\n");
			}
			
			// Get their choice.
			getOption();
		
			// Perform an action that corresponds to their choice.
			PerformAction();
		}
	}
	
	void getOption()
	{
		while (loop)
		{
			// Tell the user to type in a character.
			System.out.print("Type in your option: ");
		
			// Store the user's input inside a variable.
			this.userChoice = readConsole();
			
			// Check the user's choice for invalid input.
			CheckChoice();
		}
	}
	
	String readConsole()
	{
		// Create variables exclusive to this function here.
		String choice = "";
		
		// Begin reading for input.
		this.scan = new Scanner(System.in);
				
		// Store the user's input inside a variable.
		choice = this.scan.next();
		
		// Then we return that variable.
		return choice;
	}
	
	String readConsoleLine()
	{
		// Create variables exclusive to this function here.
		String choice = "";
		
		// Begin reading for input.
		this.scan = new Scanner(System.in);
				
		// Store the user's input inside a variable.
		choice = this.scan.nextLine();
		
		// Then we return that variable.
		return choice;
	}
	
	void CheckChoice()
	{
		// End the loop if the user made a valid choice.
	    if (((this.userChoice.equals("1") || this.userChoice.equals("1.") || 
			  this.userChoice.equals("2") || this.userChoice.equals("2.") || 
			  this.userChoice.equals("q") || this.userChoice.equals("q.")) && !memberMenu) ||
	    	((this.userChoice.equals("1") || this.userChoice.equals("1.") ||
	    	  this.userChoice.equals("2") || this.userChoice.equals("2.") ||
	    	  this.userChoice.equals("3") || this.userChoice.equals("3.") || 
	    	  this.userChoice.equals("4") || this.userChoice.equals("4.") ||
	    	  this.userChoice.equals("5") || this.userChoice.equals("5.") ||
	    	  this.userChoice.equals("6") || this.userChoice.equals("6.") ||
	    	  this.userChoice.equals("7") || this.userChoice.equals("7.") || 
	    	  this.userChoice.equals("8") || this.userChoice.equals("8.")) && memberMenu))
		{
			this.loop = false;
		}
		// Otherwise, inform the user of their invalid input and continue
		// looping.
		else
		{
			System.out.println("Invalid input. Please try again.");
		}
	}
	
	void PerformAction()
	{
		// Perform the MemberLogin function.
		if ((this.userChoice.equals("1") || this.userChoice.equals("1.")) && !this.memberMenu)
		{
			this.memberMenu = MemberLogin();
			
			// Wipe the variables. We don't need them right now and personal information 
			// shouldn't be sitting in a variable.
			this.userName = "";
			this.userPassword = "";
		}
		// Update the user's details.
		else if ((this.userChoice.equals("2") || this.userChoice.equals("2.")) && !this.memberMenu)
		{
			// Go back through the MemberRegister function to update information.
			MemberRegister(true);
			
			// Go back to the main menu when the enter key is pressed.
			EnterToMainMenu();
		}
		// Exit the program.
		else if ((this.userChoice.equals("q") || this.userChoice.equals("q.")) && !this.memberMenu)
		{
			System.exit(0);
		}
		// List all the subjects, then display all titles under a subject the user chose.
		else if ((this.userChoice.equals("1") || this.userChoice.equals("1.")) && this.memberMenu)
		{
			// Get all subjects in database and store them in a variable.
			Vector<String> subjects = db.RetrieveSubjects(db);
			
			// For readability.
			System.out.println();
			
			// List the user's subject options.
			for (int i = 0; i < subjects.size(); i++)
			{
				System.out.println((i + 1) + ". " + subjects.get(i));
			}
			
			// Ask the user for a choice.
			System.out.print("\nEnter your choice: ");
			
			// Read the console for output and store it.
			this.userChoice = readConsole();
			
			// Check their choice.
			Vector<Vector<String>> tempCart = CheckSubjectChoice(this.userChoice, subjects, 1);
			
			// Add the books to the cart table in the database.
			db.StoreCartDatabase(tempCart, db);
			
			// Empty the vector of vectors so we don't store duplicates on accident.
			this.cart = new Vector<Vector<String>>();
		}
		// Search by Author, or Title.
		else if ((this.userChoice.equals("2") || this.userChoice.equals("2.")) && this.memberMenu)
		{
			// Inform the user of their possible choices.
			System.out.println("\n1. Author Search\n2. Title Search\n3. Go Back to Member Menu");
			
			// Ask the user to make a decision.
			System.out.print("Type in your option: ");
			
			// Store the user's input in the variable.
			this.userChoice = readConsole();
			
			// Check their choice. NOTE: Simply passing memberInfo because Java does not allow optional parameters. We do not need it here.
			Vector<Vector<String>> tempCart = CheckSubjectChoice(this.userChoice, this.memberInfo, 2);
			
			// Add the books to the cart table in the database.
			db.StoreCartDatabase(tempCart, db);
			
			// Empty the vector of vectors.
			this.cart = new Vector<Vector<String>>();
		}
		// Show the user their shopping cart and allow them to edit it.
		else if ((this.userChoice.equals("3") || this.userChoice.equals("3.")) && this.memberMenu)
		{
			// Retrieve cart items from the database.
			this.cart = db.RetrieveFromCart(db);

			// Display the items in the user's cart.
			// NOTE: We're handing it 3 memberInfo and a 0 because Java does not allow optional parameters. 
			// We don't really need memberInfo for mode 0.
			DisplayCart(this.memberInfo, this.memberInfo, this.cart, 0, 0);
			
			if (!this.cart.isEmpty())
			{
				while (true)
				{
					// Ask the user to make a choice.
					System.out.print("Type \"delete\" to delete an item,\n\"edit\" to edit your cart,\nor \"back\" to go back to the menu: ");
				
					// Store the user's choice in a variable.
					this.userChoice = readConsole();
				
					// For readability.
					System.out.println();
				
					// Delete from the cart.
					if (this.userChoice.toLowerCase().equals("delete") && !this.cart.isEmpty())
					{
						// Ask the user for a response.
						System.out.print("Enter the isbn of the item you wish to delete: ");
					
						// Store the response in a variable.
						this.userChoice = readConsole();
					
						// Search the cart in the database for the book and delete it.
						db.DeleteEditFromCart(this.userChoice, db, 0);
					}
					// Edit the cart.
					else if (this.userChoice.toLowerCase().equals("edit"))
					{
						// Ask the user to make a decision.
						System.out.print("Enter the isbn of the item you wish to edit: ");
						
						// Store their choice in a variable.
						this.userChoice = readConsole();
						
						// Search the cart in the database for the book and change the quantity.
						db.DeleteEditFromCart(this.userChoice, db, 1);
					}
					// Go back to the main menu.
					else if (this.userChoice.toLowerCase().equals("back"))
					{
						break;
					}
					else
					{
						System.out.println("Invalid input. Please try again.\n");
					}
				}
			}
			else
			{
				// Wait for user to press enter to go back to the main menu.
				EnterToMainMenu();
			}
		}
		// List orders and see details on an order of the user's choosing.
		else if ((this.userChoice.equals("4") || this.userChoice.equals("4.")) && this.memberMenu)
		{
			// Get all the user's orders.
			Vector<Vector<String>> orders = db.RetrieveAllUserOrders(db);
			
			// If there was nothing, inform them. Then go back to main menu when they press enter.
			if (orders.isEmpty())
			{
				System.out.println("You haven't placed any orders!");
				EnterToMainMenu();
			}
			else
			{
				// List all orders in place.
				DisplayOrdersPlaced(orders);
				
				while (true)
				{
					// Ask the user to enter an order number.
					System.out.print("Enter the ORDER NO to display its details or type \"quit\" to go back to the main menu: ");
				
					// Store the user's choice in a variable.
					this.userChoice = readConsole();
				
					// If they choose to quit, exit the loop to go back to the main menu.
					if (this.userChoice.toLowerCase().equals("q") || this.userChoice.toLowerCase().equals("quit"))
					{
						break;
					}
					// If the user typed in a response containing all numbers, check for an order number matching the user's response.
					else if (this.userChoice.matches("\\d+")) 
					{
						// A variable to determine whether to break through the while loop or not.
						boolean matchFound = false;
						
						// Check every order detail number of every order for a match.
						for (int i = 0; i < orders.size(); i++)
						{
							if (orders.get(i).get(1).equals(this.userChoice))
							{
								// When a match is found set the boolean to true so we can break through the while loop.
								matchFound = true;
								
								// Retrieve the order details.
								Vector<String> shipInfo = db.RetrieveInfo(db);
								Vector<String> billInfo = shipInfo;
								Vector<Vector<String>> orderDetails = db.RetrieveOrderDetails(this.userChoice, db);

								// Then display the order details.
								DisplayCart(shipInfo, billInfo, orderDetails, 0, 1);
								
								// Break from the for loop since we no longer need to be here.
								break;
							}
						}
						
						// If the boolean is true, break from the while loop.
						if (matchFound)
						{
							break;
						}
						// If it's false, inform the user that there was no match.
						else if (!matchFound)
						{
							System.out.println("No orders with that number found. Please try again.");
						}
					}
					// Inform the user that their response was invalid.
					else
					{
						System.out.println("Invalid choice. Please try again.");
					}
				}
			}
			
			// Wait for a press of the enter key before heading back to the main menu.
			EnterToMainMenu();
		}
		// Check out the user's items.
		else if ((this.userChoice.equals("5") || this.userChoice.equals("5.")) && this.memberMenu)
		{
			// Retrieve cart items from the database.
			this.cart = db.RetrieveFromCart(db);
			
			// Display a list of the user's orders. 
			// NOTE: We're handing it 2 memberInfo and a 0 because we must hand parameters something in Java.
			// It doesn't need them in mode 0. 
			DisplayCart(this.memberInfo, this.memberInfo, this.cart, 0, 0);
			
			// Exit the function prematurely if nothing is in the cart.
			if (this.cart.isEmpty())
			{
				// Inform the user that their cart is empty.
				System.out.println("Nothing to check out. Your cart is empty!");
				
				// Wait for an enter key press before returning.
				EnterToMainMenu();
				
				return;
			}
			
			// Loop until a proper answer is given.
			while (true)
			{
				// Ask the user for a decision.
				System.out.print("Proceed to check out (Y/N)? ");
			
				// Store the choice in a variable.
				this.userChoice = readConsole();
			
				// Exit the function if they pick no.
				if (this.userChoice.toLowerCase().equals("n") || this.userChoice.toLowerCase().equals("no"))
				{	
					return;
				}
				// Go to checkout.
				else if (this.userChoice.toLowerCase().equals("y") || this.userChoice.toLowerCase().equals("yes"))
				{		
					// Checkout their items and add the order and it's details to the database.
					CheckOut();
					
					// Remove all items from the user's cart now that the items are checked out.
					db.RemoveFromCart(db);
					
					// Wait for a press of the enter key to return to the main menu.
					EnterToMainMenu();
					
					return;
				}
				// Tell them their input is invalid if they picked none of the valid options.
				else
				{
					System.out.println("Invalid input. Please try again.");
				}
			}
		}
		else if ((this.userChoice.equals("6") || this.userChoice.equals("6.")) && this.memberMenu)
		{
			// Retrieve shipping and billing information from the database.
			Vector<String> shipInfo = db.RetrieveInfo(db);
			Vector<String> billInfo = shipInfo;
			
			// Get the contents of the cart from the database.
			this.cart = db.RetrieveFromCart(db);
			
			if (!this.cart.isEmpty())
			{
				// Generate an order number.
				int orderNumber = new Random().nextInt(99999) + 1;
			
				// Display the invoice.
				DisplayCart(shipInfo, billInfo, this.cart, orderNumber, 2);
			
				// Create a vector containing the information needed to go into the order table.
				Vector<String> order = new Vector<String>();
			
				// Add that information to the vector.
				order.add(billInfo.get(8));
				order.add(Integer.toString(orderNumber));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(shipInfo.get(2));
				order.add(shipInfo.get(3));
				order.add(shipInfo.get(4));
				order.add(shipInfo.get(5));

				// Store the order and order details in the database.
				db.AddOrderToDatabase(order, this.cart, db);

				// Delete all items from the user's cart.
				db.RemoveFromCart(db);
				
				// Wait for the press of an enter key before going back to the main menu.
				EnterToMainMenu();
			}
			else if (this.cart.isEmpty())
			{
				System.out.println("You have nothing to check out!");
				
				// Wait for the press of an enter key before going back to the main menu.
				EnterToMainMenu();		
			}
		}
		// Output the user's information to the console or edit their personal information.
		else if ((this.userChoice.equals("7") || this.userChoice.equals("7.")) && this.memberMenu)
		{
			// Inform the user that they need to reenter their user name and password.
			System.out.println("For verification purposes, re-enter your userID and password.");
			
			// Validate or invalidate the user and store the boolean in allow.
			boolean allow = MemberLogin();
			
			// If the user inputed the correct user name and password,
			// they may change their personal information.
			if (allow)
			{
				// Display the user's options.
				System.out.println("1. View Personal Information\n"
				                 + "2. Edit Personal Information");
				
				// Ask user for a choice.
				System.out.print("Type in your option: ");
			
				// Store the user's input inside a variable.
				this.userChoice = readConsole();
				
				// Store the user's info in memberInfo.
				this.memberInfo = db.RetrieveInfo(db);
				
				// Retrieve and output the user's personal information.
				if (this.userChoice.equals("1") || this.userChoice.equals("1."))
				{
					// Output the user's info to the console.
					DisplayInfo(this.memberInfo);
					
					// Wait for an enter key press before going back to the main menu.
					EnterToMainMenu();
				}
				// Change the user's information.
				else if (this.userChoice.equals("2") || this.userChoice.equals("2."))
				{
					// Clear memberInfo because we're about to need it.
					this.memberInfo = new Vector<String>();
					
					// Update the user's information.
					MemberRegister(false);
					
					// Wait for the press of the enter key to go back.
					EnterToMainMenu();
				}
				
				// Wipe the userName and userPassword variables now that we're finished with them.
				this.userName = "";
				this.userPassword = "";
			}
			// Otherwise, we block them and send them back to the beginning screen.
			else if (!allow)
			{
				System.out.println("Incorrect username or password.");
			}
		}
		// Exit the program. TODO Instead of exiting, make it go back to the login screen.
		else if ((this.userChoice.equals("8") || this.userChoice.equals("8.")) && memberMenu)
		{
			this.memberMenu = false;
			this.memberInfo = new Vector<String>();
			
			System.out.println("You have successfully logged out.");
			//System.exit(0);
		}
	}

	void DisplayOrdersPlaced(Vector<Vector<String>> orders)
	{
		// Retrieve the user's first and last name.
		Vector<String> temp = db.RetrieveInfo(db);

		// The formatting.
		System.out.println("\nOrders placed by " + temp.get(0) + " " + temp.get(1));
		System.out.println("----------------------------------------------------------------------------------");
		System.out.printf("%-34s %-34s %-34s%n", "ORDER NO", "RECEIVED DATE", "SHIPPED DATE");
		System.out.println("----------------------------------------------------------------------------------");
		
		// A list of all of their orders.
		for (int i = 0; i < orders.size(); i++)
		{
			System.out.printf("%-34s %-34s %-34s%n", orders.get(i).get(1), orders.get(i).get(2), orders.get(i).get(3));
		}
		
		// The footer.
		System.out.println("----------------------------------------------------------------------------------");
	}
	
	void EnterToMainMenu()
	{
		// Loop until an appropriate response is given.
		while (true)
		{
			// Ask the user to press the enter key.
			System.out.println("Press the Enter Key to go back to the Main Menu.");
		
			// Store their response in a variable.
			String choice = readConsoleLine();
		
			if (!choice.isEmpty())
			{
				System.out.println("Invalid input. Please try again.");
			}
			else if (choice.isEmpty())
			{
				break;
			}
		}
	}

	void CheckOut()
	{
		while (true)
		{
			// Ask the user for a decision.
			System.out.print("Do you want to enter a new shipping address (Y/N)? ");
		
			// Store their choice in a variable.
			this.userChoice = readConsole();
		
			if (this.userChoice.equals("y") || this.userChoice.equals("yes"))
			{
				// Get the shipping address to be used.
				Vector<String> shipInfo = getPersonalInfo(0);
				
				// Get the billing address to be used.
				Vector<String> billInfo = db.RetrieveInfo(db);
				
				// Get the contents of the cart from the database.
				this.cart = db.RetrieveFromCart(db);
				
				// Generate an order number.
				int orderNumber = new Random().nextInt(99999) + 1;

				// Display the invoice.
				DisplayCart(shipInfo, billInfo, this.cart, orderNumber, 2);
				
				// Create a vector containing the information needed to go into the order table.
				Vector<String> order = new Vector<String>();

				// Add that information to the vector.
				order.add(billInfo.get(8));
				order.add(Integer.toString(orderNumber));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(shipInfo.get(2));
				order.add(shipInfo.get(3));
				order.add(shipInfo.get(4));
				order.add(shipInfo.get(5));
				
				// Store the order and order details in the database.
				db.AddOrderToDatabase(order, this.cart, db);
				
				// Then exit the loop.
				break;
			}
			else if (this.userChoice.equals("n") || this.userChoice.equals("no"))
			{
				// Retrieve shipping and billing information from the database.
				Vector<String> shipInfo = db.RetrieveInfo(db);
				Vector<String> billInfo = shipInfo;
				
				// Get the contents of the cart from the database.
				this.cart = db.RetrieveFromCart(db);
				
				// Generate an order number.
				int orderNumber = new Random().nextInt(99999) + 1;
				
				// Display the invoice.
				DisplayCart(shipInfo, billInfo, this.cart, orderNumber, 2);
				
				// Create a vector containing the information needed to go into the order table.
				Vector<String> order = new Vector<String>();
				
				// Add that information to the vector.
				order.add(billInfo.get(8));
				order.add(Integer.toString(orderNumber));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()));
				order.add(shipInfo.get(2));
				order.add(shipInfo.get(3));
				order.add(shipInfo.get(4));
				order.add(shipInfo.get(5));

				// TODO Store the order and order details in the database.
				db.AddOrderToDatabase(order, this.cart, db);
				
				// Then exit the loop.
				break;
			}
			else
			{
				System.out.println("Invalid input. Try again.");
			}
		}
	}
	
	Vector<String> getPersonalInfo(int mode)
	{
		// MODE 0: For getting ship address.
		// MODE 1: For updating member info.
		
		// Reset the newCard variable to false to prevent errors during runtime.
		this.newCard = false;
		
		// Create variables here.
		boolean realCard = false;
		Vector<String> info = new Vector<String>();
		String statements[] = {};
		
		// Create the appropriate statements array depending on the mode.
		if (mode == 0)
		{
			statements = new String[]
			{	
					"Enter your first name: ", 
					"Enter your last name: ", 
					"Enter your street: ", 
					"Enter your city: ", 
					"Enter your state: ", 
					"Enter your zipcode: ", 
					"Enter your card type (amex/visa): ", 
					"Enter your Credit Card Number: "
			};
		}
		else if (mode == 1)
		{
			statements = new String[]
			{	
					"Enter your first name: ", 
					"Enter your last name: ", 
					"Enter your street: ", 
					"Enter your city: ", 
					"Enter your state: ", 
					"Enter your zipcode: ",
					"Enter your phone number: ",
					"Enter your email address: ",
					"Enter your userID: ",
					"Enter your password: ",
					"Enter your card type (amex/visa): ", 
					"Enter your Credit Card Number: "
			};
		}

		// Get the user's information.
		for (int i = 0; i < statements.length; i++)
		{
			if ((i == 6 && mode == 0) || (i == 10 && mode == 1))
			{
				// Loop until a correct input is given.
				while (true)
				{
					// Ask the user to make a decision.
					System.out.println(mode == 0 ? "Do you want to enter a new credit card number (Y/N)? " 
												 : "Do you wish to store your credit card information (Y/N)? ");
					
					// Store their choice in a variable.
					this.userChoice = readConsole();
					
					// If they pick yes, newCard is true and then we break.
					if (this.userChoice.toLowerCase().equals("yes") || this.userChoice.toLowerCase().equals("y"))
					{
						this.newCard = true;
						
						break;
					}
					// If they pick no, newCard is false and then we break.
					else if (this.userChoice.toLowerCase().equals("no") || this.userChoice.toLowerCase().equals("n"))
					{
						this.newCard = false;
						
						break;
					}
					// If they gave an invalid option, keep looping until they give a valid one.
					else
					{
						System.out.print("Invalid input. Try again.");
					}
				}
			}
			
			// If we're at the point where the credit card information is asked, check which mode we're 
			// on and if we're on the correct index, and see if we're supposed to display the credit card information.
			if (((i == 10 || i == 11) && mode == 1 && !this.newCard) || ((i == 6 || i == 7) && mode == 0 && !this.newCard))
			{
				continue;
			}
			else if (this.newCard)
			{
				if (i == statements.length - 2)
				{
					while (true)
					{
						// Print the statement.
						System.out.print(statements[i]);
				
						// Store the user's response in a variable.
						this.userChoice = readConsoleLine();
					
						// Add it to the vector and break from the loop.
						if (this.userChoice.toLowerCase().equals("amex") || this.userChoice.toLowerCase().equals("discover")
						   || this.userChoice.toLowerCase().equals("mc") || this.userChoice.toLowerCase().equals("visa"))
						{
							info.add(this.userChoice);
							
							break;
						}
						// Inform the user of the error and continue looping.
						else
						{
							System.out.println("Invalid credit card type. Try again.");
						}
					}
				}
				else if (i == statements.length - 1)
				{
					while (!realCard)
					{
						// Print the statement.
						System.out.print(statements[i]);
				
						// Store the user's response in a variable.
						this.userChoice = readConsoleLine();

						// Get the truth value from card verification.
						realCard = this.userChoice.matches("\\d+") ? verifyCard(userChoice) : false;
					
						// If invalid, inform the user.
						if (!realCard)
						{
							System.out.println("Invalid credit card number. Try again.");
						}
						else if (realCard)
						{
							info.add(this.userChoice);
						}
					}
				}
			}
			else
			{	
				while (true)
				{
					// Print the statement.
					System.out.print(statements[i]);
			
					// Store the user's response in a variable.
					this.userChoice = readConsoleLine();
				
					if (((i == 0 || i == 1 || i == 4) && this.userChoice.length() < 21 && !this.userChoice.isEmpty())
					  || (i == 2 && this.userChoice.length() < 51 && !this.userChoice.isEmpty())
					  || (i == 3 && this.userChoice.length() < 31 && !this.userChoice.isEmpty())
					  || (i == 5 && this.userChoice.length() < 6 && !this.userChoice.isEmpty() && this.userChoice.matches("\\d+"))
					  || (i == 6 && this.userChoice.length() < 13) || (i == 7 && this.userChoice.length() < 41)
					  || (i == 8 && this.userChoice.length() < 21) || (i == 9 && this.userChoice.length() < 21))
					{
						// Add it to the vector.
						info.add(this.userChoice);
						
						// Then break from the while loop.
						break;
					}
					else
					{
						System.out.println("Input is either too short or too long. Please try again.");
					}
				}
			}
		}
		
		return info;
	}
	
	void DisplayCart(Vector<String> invShip, Vector<String> invBill, Vector<Vector<String>> orderInfo, int orderNumber, int mode)
	{
		// MODE 0: Display a shopping cart.
		// MODE 1: Display an invoice.
		
		// Create variables exclusive to this function here.
		float total = 0.0f;
		float completeCost = 0.0f;
		
		// If we're in mode 1...
		if (mode == 1 || mode == 2)
		{
			
			// Print out an invoice number.
			System.out.printf("%n%55s%n", (orderNumber > 0 ? "Invoice for Order No. " + orderNumber : "Details for Order No. " + orderInfo.get(0).get(0)));
			
			// Display the two address headers.
			System.out.printf("%-60s %-60s %n", "Shipping Address", "Billing Address");
			
			System.out.printf("%-60s %-60s %n", "Name: " + invShip.get(0) + " " + invShip.get(1), "Name: " + invBill.get(0) + " " + invBill.get(1));
			System.out.printf("%-60s %-60s %n", "Address: " + invShip.get(2), "Address: " + invBill.get(2));
			System.out.printf("%-60s %-60s %n", invShip.get(3), invBill.get(3));
			System.out.printf("%-60s %-60s %n", invShip.get(4) + " " + invShip.get(5), invBill.get(4) + " " + invBill.get(5));
			System.out.print("----------------------------------------------------------------------------------------------------------\n");
		}
		else if (mode == 0)
		{
			// Display the shopping cart header.		
			System.out.println("Current Cart Contents:\n");
		}
		
		System.out.printf("%-15s %-47.47s %-23.23s %-7.7s %-10.10s %n", "ISBN", "Title", "$", "Qty", "Total");
		System.out.print("----------------------------------------------------------------------------------------------------------\n");
					
		// If the cart is empty, inform the user of this.
		if (orderInfo.isEmpty())
		{
			System.out.println("--Your cart is empty.");
		}
		// Otherwise, display the contents of the cart.
		else
		{
			if (mode == 0 || mode == 2)
			{
				for (int i = 0; i < orderInfo.size(); i++)
				{
					System.out.printf("%-15s %-44.44s%-3s %-23.23s %-7.7s %-10.2f %n", 
						orderInfo.get(i).get(0), orderInfo.get(i).get(1), (orderInfo.get(i).get(1).length() <= 44) ? "   " : "...", 
						orderInfo.get(i).get(2), orderInfo.get(i).get(3), 
						total = Float.parseFloat(orderInfo.get(i).get(2)) * Float.parseFloat(orderInfo.get(i).get(3)));
					
					// Add the cost of each book in the cart to the completeCost variable.
					completeCost += total;
				}
			}
			else if (mode == 1)
			{
				for (int i = 0; i < orderInfo.size(); i++)
				{
					System.out.printf("%-15s %-44.44s%-3s %-23.23s %-7.7s %-10.2f %n", 
						orderInfo.get(i).get(1), orderInfo.get(i).get(2), (orderInfo.get(i).get(2).length() <= 44) ? "   " : "...", 
						orderInfo.get(i).get(3), orderInfo.get(i).get(4), 
						total = Float.parseFloat(orderInfo.get(i).get(3)) * Float.parseFloat(orderInfo.get(i).get(4)));
					
					// Add the cost of each book in the cart to the completeCost variable.
					completeCost += total;
				}
			}
		}
					
		// Display the shopping cart footer.
		System.out.println("----------------------------------------------------------------------------------------------------------");
		
		// Display total cost.
		System.out.printf("%-95s $%-14.2f %n", "Total = ", completeCost);
		
		// Display the invoice footer.
		System.out.println("----------------------------------------------------------------------------------------------------------");
	}
	
	Vector<Vector<String>> CheckSubjectChoice(String choice, Vector<String> subjects, int mode)
	{
		// Create variables exclusive to this function here.
		int maxSize = (mode == 1) ? subjects.size() : (mode == 2) ? 3 : -1;
		int numChoice = choice.matches("\\d+") ? Integer.parseInt(choice) : -1;
		Vector<Vector<String>> cartCur = new Vector<Vector<String>>();
		
		// Check if user input is within the bounds.
		if (numChoice > 0 && numChoice <= maxSize)
		{
			// Search by subject.
			if (mode == 1)
			{
				// Display books and fill the temporary cart with the books the user chose. NOTE: We handed it zero, because the user
				// makes no choice when searching by subject outside of picking the subject. So, we're putting it in "mode 0", essentially.
				cartCur = db.RetrieveBooksBy(choice, subjects, db, 0);

				// Return with that temporary cart.
				return cartCur;
			}
			// Search by Author/Title.
			else if (mode == 2)
			{
				// Look at the user's choice and execute the appropriate search method.
				if (choice.equals("1"))
				{
					// Author Search
					System.out.print("\nEnter a name or part of a name: ");
					
					// Store user's input in a variable.
					this.userChoice = readConsole();
					
					// For readability.
					System.out.println();
					
					// Display the number of books found and store any chosen in cartCur. 
					cartCur = db.RetrieveBooksBy(this.userChoice, subjects, db, numChoice);
				}
				else if (choice.equals("2"))
				{
					// Title Search
					System.out.print("\nEnter a title or a part of a title: ");
					
					// Store the user's choice in a variable.
					this.userChoice = readConsole();
					
					// For readability.
					System.out.println();
					
					// Display the number of books found and store any chosen in cartCur.
					cartCur = db.RetrieveBooksBy(this.userChoice, subjects, db, numChoice);
				}
				else if (choice.equals("3"))
				{
					// Go back to main menu.
					return cartCur;
				}
				else
				{
					System.out.println("Invalid choice.");
				}
			
			//RetrieveByAuthorTitle(String response, Database db)
			
			 
			}
		}
		// If not, inform the user.
		else
		{
			//Inform the user of the invalid input.
			System.out.println("Invalid choice given. Please try again.");
		}
		
		return cartCur;
	}
	
	boolean MemberLogin()
	{
		// Create variables exclusive to this function here.
		String userID = "";
		String userPassword = "";
		boolean match = false;
		
		// Ask the user for their userID.
		System.out.print("Enter userID: ");
		
		// Store the user's input inside a variable.
		userID = readConsoleLine();
		
		// Ask the user for their password.
		System.out.print("Enter password: ");
		
		// Store the user's input inside a second variable.
		userPassword = readConsoleLine();
		
		// Check the database for a matching user name and password.
		match = db.Login(userID, userPassword, db);
		
		// If match is true, set userName to the member's name. 
		this.userName = match ? userID : "";
		this.userPassword = match ? userPassword : "";
		
		return match;
	}
	
	void MemberRegister(boolean firstTime)
	{	
		if (firstTime)
		{
			// Enter the New Member Registration phase.
			System.out.println("\nWelcome to the Online Bookstore\n"
							 + "\tNew Member Registration");
		}
		
		// Get their personal information and store it in memberInfo.
		this.memberInfo = getPersonalInfo(1);
		
		// Then display their information back to them.
		DisplayInfo(memberInfo);
		
		if (firstTime)
		{
			// Finally, add that information to the database of users.
			db.AddToDatabase(memberInfo, "", "", db, this.newCard, false);
			
			// Tell the user they have successfully registered.
			System.out.println("\nYou have registered successfully!");
		}
		else if (!firstTime)
		{
			// Update the member's info in the database.
			db.AddToDatabase(this.memberInfo, this.userName, this.userPassword, this.db, this.newCard, true);

			// Tell the user there was no problem in updating their info.
			System.out.println("\nInformation updated successfully!");
		}
		
		// Then purge the memberInfo vector of the information now that
		// it is no longer needed.
		this.memberInfo = new Vector<String>();
	}
	
	void DisplayInfo(Vector<String> info)
	{
		// Create variables exclusive to this function here.
		String text[] = new String[] {};

		// Depending on the size of the info we were given, create the appropriate string array.
		if ((info.size() == 10) || (info.size() == 12 && info.get(10) == null && info.get(11) == null))
		{
			text = new String[] {"Name: ", "Address: ", "City: ", "Phone: ", "Email: ", "UserID: ", "Password: "};
		}
		else if (info.size() == 12)
		{
			text = new String[] {"Name: ", "Address: ", "City: ", "Phone: ", "Email: ", "UserID: ", "Password: ", "Credit Card Type: ", "Credit Card Number: "};
		}

		// For readability.
		System.out.println();
		
		for (int i = 0; i < text.length; i++)
		{	
			// Output first and last name.
			if (i == 0)
			{
				System.out.println(text[i] + info.get(i) + " " + info.get(i + 1));
			}
			// Output address. i + 1, because when were at i = 0, everything was offset by 1.
			else if (i == 1)
			{
				System.out.println(text[i] + info.get(i + 1));
			}
			// Output city. 
			else if (i == 2)
			{
				System.out.println(text[i] + info.get(i + 1) + " " + info.get(i + 2) + " " + info.get(i + 3));
			}
			// Skip iterations 7 and 8 if info doesn't contain credit card information.
			else if ((i == 7 || i == 8) && info.size() == 10)
			{
				continue;
			}
			// Everything else after i = 2 is now offset by 3, which is why we're doing i + 3.
			else
			{
				System.out.println(text[i] + info.get(i + 3));
			}
		}
		
		// For readability purposes.
		System.out.println();
	}
	
	boolean verifyCard(String ccNumber)
	{
		// Create variables exclusive to this function here.
		int totalSum = 0;
		boolean secondDigit = false;
		
		// Iterate through the credit card number in reverse.
		for (int i = ccNumber.length() - 1; i >= 0; i--)
		{
			// First, we convert the character in the credit card number to a digit.
			int digit = Integer.parseInt(ccNumber.substring(i, i + 1));
				
			// When we're at every second digit...
			if (secondDigit)
			{
				// Multiply the digit by 2.
				digit *= 2;
					
				// If the new number is a double digit number...
				if (digit > 9)
				{
					// Add the two digits together. NOTE: Since
					// we'll never reach 20, that's why we can just 
					// do "+ 1".
					digit = (digit % 10) + 1;
				}
			}
			
			// Add our digit to the sum.
			totalSum += digit;
				
			// Toggle our boolean value.
			secondDigit = !secondDigit;
		}
		
		return (totalSum % 10 == 0);
	}
};

class Database
{
	// Create variables here.
	Connection dbConnection;
	private String currentUser = "";
	private boolean backToMain = false;
	private final int NUM_COLUMNS_BOOK = 5;	
	
	// Our constructor.
	Database(String username, String password)  
	{
		// Surround in Try-Catch to parse errors if needed.
		try 
		{
			// Connect to the database.
			this.dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL", username, password);
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			// Exit the program since we have an error.
			System.exit(0);
		}
	}

	// Create our functions here.
	Vector<String> RetrieveSubjects(Database db) 
	{
		// Create variables exclusive to this function here.
		Vector<String> contents = new Vector<String>();
		String sqlQuery = "SELECT DISTINCT subject FROM BOOKS";
		ResultSet result;
		
		try 
		{
			// Set up the query.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlQuery);
			
			// Execute the query in the database.
			result = prepare.executeQuery();
			
			// Iterate through each row of the table.
			while (result.next())
			{
				// Add the first column's contents to the vector.
				contents.add(result.getString(1));
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Return our vector when finished.
		return contents;
	}
	
	Vector<Vector<String>> RetrieveOrderDetails(String orderNumber, Database db)
	{
		// Create variables exclusive to this function here.
		Vector<Vector<String>> orderDetails = new Vector<Vector<String>>();
		String sqlStatement1 = "SELECT * FROM ODETAILS WHERE ono = ?";
		String sqlStatement2 = "SELECT * FROM BOOKS WHERE isbn = ?";
		
		try 
		{
			// Prepare to set up the queries.
			PreparedStatement prepare1 = db.dbConnection.prepareStatement(sqlStatement1);
			PreparedStatement prepare2 = db.dbConnection.prepareStatement(sqlStatement2);
			
			// Insert values into the first query.
			prepare1.setString(1, orderNumber);
			
			// Execute the first query and store the result.
			ResultSet result1 = prepare1.executeQuery();
			
			// Go through each row and each column.
			while (result1.next()) 
			{
				// Create a vector to store book information.
				Vector<String> book = new Vector<String>();
				
				for (int i = 1; i < 5; i++)
				{
					if (i == 2)
					{
						// Insert the isbn into the second query.
						prepare2.setString(1, result1.getString(i));
						
						// Store the result.
						ResultSet result2 = prepare2.executeQuery();
						
						// Add the isbn to the book vector.
						book.add(result1.getString(i));
						
						while (result2.next())
						{
							book.add(result2.getString(3));
						}
					}
					else
					{
						// Add each field of the odetails table to the vector.
						book.add(result1.getString(i));
					}
				}
				
				// Add the book vector to the orderDetails vector.
				orderDetails.add(book);
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return orderDetails;
	}
	
	Vector<Vector<String>> RetrieveAllUserOrders(Database db)
	{
		// Create variables here.
		Vector<Vector<String>> orders = new Vector<Vector<String>>();
		String sqlStatement = "SELECT * FROM ORDERS WHERE USERID = ?";
		
		try 
		{
			// Prepare the query.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlStatement);
			
			// Insert value into query.
			prepare.setString(1, this.currentUser);
			
			// Store the raw results in a variable.
			ResultSet result = prepare.executeQuery();
			
			// Add the results to a vector of strings.
			while(result.next())
			{
				Vector<String> order = new Vector<String>();
				
				// Add each column to the order vector.
				for (int i = 1; i < 9; i++)
				{
					if (i == 3 || i == 4)
					{
						order.add(result.getDate(i).toString());
					}
					else
					{
						order.add(result.getString(i));
					}
				}
				
				// Add the row to the orders vector of vectors.
				orders.add(order);
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return orders;
	}
	
	void RemoveFromCart(Database db)
	{
		// Create variables exclusive to this function here.
		String sqlStatement = "DELETE FROM CART WHERE USERID = ?";
		
		try 
		{
			// Get ready to set up the query.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlStatement);
			
			// Insert value into the query.
			prepare.setString(1, this.currentUser);
			
			// Execute the query.
			prepare.executeQuery();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void AddOrderToDatabase(Vector<String> order, Vector<Vector<String>> cart, Database db)
	{
		// Create variables exclusive to this function here.
		String sqlStatement1 = "INSERT INTO ORDERS(userid, ono, recieved, shipped, shipAddress, shipCity, shipState, shipZip)"
							 + "VALUES(?, ?, TO_DATE(?, 'YYYY-MM-DD'), TO_DATE(?, 'YYYY-MM-DD'), ?, ?, ?, ?)";
		String sqlStatement2 = "INSERT INTO ODETAILS(ono, isbn, qty, price)"
							 + "VALUES(?, ?, ?, ?)";
		
		try 
		{
			// Get ready to set up the queries.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlStatement1);
			PreparedStatement prepare2 = db.dbConnection.prepareStatement(sqlStatement2);
			
			// Set up the first query.
			for (int i = 0, j = 1; i < order.size(); i++, j++)
			{
				if (i == 1 || i == 7)
				{
					prepare.setInt(j, Integer.parseInt(order.get(i)));
				}
				else if (i == 2 || i == 3)
				{
					prepare.setString(j, order.get(i));
				}
				else
				{
					prepare.setString(j, order.get(i));
				}
			}
	
			// Execute the first query.
			prepare.executeQuery();
			
			// Set up the second query for each book in the cart and then execute it.
			for (int i = 0; i < cart.size(); i++)
			{
				prepare2.setString(1, order.get(1));
				prepare2.setString(2, cart.get(i).get(0));
				prepare2.setString(3, cart.get(i).get(3));
				prepare2.setString(4, cart.get(i).get(2));
				
				prepare2.executeQuery();
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void AddToDatabase(Vector<String> memberInfo, String userName, String password, Database db, boolean storeCC, boolean mode)
	{
		// Create variables here.
		String sqlStatement = "";
		
		// Pick the corresponding statement depending on if we're adding or updating.
		if (!mode)
		{
			// A statement to insert a row of information to the database.
			sqlStatement = "INSERT INTO MEMBERS(fname, lname, address, city, state, zip, phone, email, userid, password, creditcardtype, creditcardnumber)"
						 + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		}
		else if (mode)
		{
			// A statement to update an already existing row.
			sqlStatement = "UPDATE MEMBERS SET "
						 + "fname = ?, lname = ?, address = ?, city = ?, state = ?, zip = ?, phone = ?, email = ?, userid = ?, password = ?, "
						 + "creditcardtype = ?, creditcardnumber = ? WHERE userID = ? AND password = ?";
		}
		
		// Assemble our query using the member information.
		try 
		{
			// First we hand prepareStatement the query we want to use.
			PreparedStatement prepareStatement = db.dbConnection.prepareStatement(sqlStatement);
			
			// Then we insert the contents of memberInfo into our query using the prepareStatement variable.
			prepareStatement.setString(1, memberInfo.get(0));
			prepareStatement.setString(2, memberInfo.get(1));
			prepareStatement.setString(3, memberInfo.get(2));
			prepareStatement.setString(4, memberInfo.get(3));
			prepareStatement.setString(5, memberInfo.get(4));
			prepareStatement.setInt(6, Integer.parseInt(memberInfo.get(5)));
			prepareStatement.setString(7, memberInfo.get(6));
			prepareStatement.setString(8, !memberInfo.get(7).isEmpty() ? memberInfo.get(7) : "N/A");
			prepareStatement.setString(9, !memberInfo.get(8).isEmpty() ? memberInfo.get(8) : "N/A");
			prepareStatement.setString(10, !memberInfo.get(9).isEmpty() ? memberInfo.get(9) : "N/A");
			
			// If the user wanted to store their credit card information, store that too.
			if (storeCC)
			{
				prepareStatement.setString(11, memberInfo.get(10));
				prepareStatement.setString(12, memberInfo.get(11));
			}
			// If the user didn't want to store their credit card info, set the values to null.
			else if (!storeCC)
			{
				prepareStatement.setNull(11, 1);
				prepareStatement.setNull(12, 1);
			}
			
			// If we're updating info, supply the user name and password for the WHERE portion.
			if (mode)
			{
				prepareStatement.setString(13, userName);
				prepareStatement.setString(14, password);
			}
			
			// Add the member to the database.
			// NOTE: In SQLDeveloper, make sure you've committed all changes.
			// If you don't, the program will just freeze at the below statement.
			prepareStatement.executeUpdate();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	boolean Login(String ID, String Pass, Database db)
	{
		// Create variables exclusive to this function here.
		String queryStatement = "SELECT * FROM MEMBERS WHERE USERID = ? AND PASSWORD = ?";
		boolean match = false;
		ResultSet returnValues;
		
		// Assemble the query.
		try 
		{
			// Give the query we want to execute to prepareStatement.
			PreparedStatement prepareStatement = db.dbConnection.prepareStatement(queryStatement);
			
			// Then insert the values.
			prepareStatement.setString(1, ID);
			prepareStatement.setString(2, Pass);
			
			// Execute the query.
			returnValues = prepareStatement.executeQuery();
			
			// Search the row for a matching ID and password.
			while (returnValues.next())
			{
				// If we get a matching username and password, set match to true.
				// Set currentUser to the user to who matched.
				if (returnValues.getString(9).equals(ID) && returnValues.getString(10).equals(Pass))
				{
					match = true;
					this.currentUser = ID; 
				}
				// If we find no match, set match to false.
				else
				{
					System.out.println("The username or password is incorrect. Please try again.");
					
					match = false;
				}
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If true, we get the member menu.
		// If false, we get the regular menu.
		return match;
	}
	
	Vector<String> RetrieveInfo(Database db)
	{	
		// Create variables exclusive to this function here.
		Vector<String> contents = new Vector<String>();
		String queryStatement = "SELECT * FROM MEMBERS WHERE USERID = ?";
		ResultSet returnValues;
		
		// Assemble the query.
		try 
		{
			// Give the query we want to execute to prepareStatement.
			PreparedStatement prepareStatement = db.dbConnection.prepareStatement(queryStatement);
			
			// Then insert the values.
			prepareStatement.setString(1, this.currentUser);
			
			// Execute the query.
			returnValues = prepareStatement.executeQuery();
			
			// Search the row for a matching ID and password.
			while (returnValues.next())
			{	
				// Add the contents of the column on this row to contents.
				for (int i = 1; i <= 12; i++)
				{
					contents.add(returnValues.getString(i));
				}
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send back the vector.
		return contents;
	}
	
	Vector<Vector<String>> RetrieveBooksBy(String response, Vector<String> subjects, Database db, int mode)
	{
		// Reset the state of backToMain to prevent errors. 
		this.backToMain = false;
		
		// Create variables here.
		int bookCount = 0;
		String columnLabel[] = {"ISBN: ", "Author: ", "Title: ", "Price: ", "Subject: "};
		Vector<Vector<String>> cart = new Vector<Vector<String>>();
		Vector<Vector<String>> temp = new Vector<Vector<String>>();
		Vector<String> isbn = new Vector<String>();
		
		// Assemble a query based on what mode we're in. 
		// MODE = 0: Search By Subject. MODE = 1: Search by Author. Mode = 2: Search by Title.
		String statementFrag1 = "SELECT * FROM BOOKS WHERE ";
		String statementFrag2 = (mode == 0) ? "subject" : (mode == 1) ? "LOWER(author)" : (mode == 2) ? "LOWER(title)" : null;
		String statementFrag3 = (mode == 0) ? " = ?" : " LIKE ?";
		String sqlStatement = statementFrag1 + statementFrag2 + statementFrag3;
		
		try 
		{
			// Set up the string containing the query we'll be executing.
			PreparedStatement prepStatement = db.dbConnection.prepareStatement(sqlStatement);
			
			// Insert the relevant information into the sql statement.
			prepStatement.setString(1, (mode == 0) ? subjects.get(Integer.parseInt(response) - 1) : "%" + response.toLowerCase() + "%");
			
			// Store returned query results in a variable.
			ResultSet result = prepStatement.executeQuery();
			
			// Get the number of hits.
			while (result.next())
			{
				// Create a temporary vector of strings.
				Vector<String> tempVec = new Vector<String>();
				
				// Increment the number of books found variable.
				bookCount++;
				
				// Store the information on the book in tempVec.
				for (int i = 1; i <= this.NUM_COLUMNS_BOOK; i++)
				{
					// Store the book info in tempVec for later output. 
					// If we're about to add the isbn, trim it so we'll be able to find the correct book later.
					tempVec.add((i == 1) ? result.getString(i).trim() : result.getString(i));
				}
				
				// Add the vector to the vector of vectors.
				temp.add(tempVec);
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Tell the user how many results were found. Sentence depends on what mode we're in.
		if (mode == 0)
		{
			System.out.println(bookCount + " book(s) found.\n");
		}
		else
		{
			System.out.println(bookCount + " book(s) available on this subject.");
		}
					
		// Then display each column of each row.
		for (int i = 0; i < temp.size(); i++)
		{
			for (int j = 0; j < temp.get(i).size(); j++)
			{
				// Display a book's information.
				System.out.println(columnLabel[j] + temp.get(i).get(j));
				
				// Store the book's isbn for future reference.
				if (j == 0)
				{
					isbn.add(temp.get(i).get(j).trim());
				}
			}

			// Skip for readability.
			System.out.println();
			
			// Are we showing 2 books right now?
			if (isbn.size() > 2)
			{
				cart.addAll(AddToCart(temp, isbn));

				if (this.backToMain)
				{	
					return cart;
				}
				
				// Clear the isbn vector for the next set of books.
				isbn = new Vector<String>();
			}
		}
		
		// Check one more time to allow the user to choose the last of the books.
		if (!isbn.isEmpty())
		{
			cart.addAll(AddToCart(temp, isbn));
		}
		
		return cart;
	}
	
	Vector<Vector<String>> AddToCart(Vector<Vector<String>> books, Vector<String> isbn)
	{
		// Create variables exclusive to this function here.
		UserInterface ui = new UserInterface();
		Vector<Vector<String>> cart = new Vector<Vector<String>>();
		
		// Let the user add the books to the cart if they choose.
		while (true)
		{
			// Ask the user for a response.
			System.out.print("\nEnter ISBN to add to Cart, \npress Enter to continue browsing, \ntype \"back\" to go back to the menu: ");
					
			// Store the response in a variable.
			String choice = ui.readConsoleLine();
					
			// Add to the cart.
			if (isbn.contains(choice))
			{
				System.out.print("Enter the quantity: ");
				
				String response = ui.readConsole();
				
				// If the user's typed isbn is in the isbn vector, add the book to the cart.
				for (int i = 0; i < books.size(); i++)
				{
					if (books.get(i).get(0).equals(choice))
					{	
						// Append the quantity value to the information on the book.
						books.get(i).add(response);
						
						// Add the book to the cart.
						cart.add(books.get(i));
					}
				}
			}
			// Exit the function and go back to the main menu.
			else if (choice.toLowerCase().equals("back"))
			{
				this.backToMain = true;
				
				return cart;
			}
			// Exit the function if enter is pressed.
			else if (choice.isEmpty())
			{
				return cart;
			}
			// For all other keys pressed, tell the user the input was invalid.
			else if (!choice.isEmpty())
			{
				System.out.println("Invalid input. Try again.");
			}
		}
	}
	
	void StoreCartDatabase(Vector<Vector<String>> tempCart, Database db)
	{
		// Create variables exclusive to this function here.
		String sqlStatement = "INSERT INTO CART(userid, isbn, qty) VALUES(?, ?, ?)";
		
		try 
		{
			// Get ready to assemble the query.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlStatement);
			
			// Insert all contents of the cart into the table.
			for (int i = 0; i < tempCart.size(); i++)
			{
				// Insert the values into the query.
				prepare.setString(1, this.currentUser);
				prepare.setString(2, tempCart.get(i).get(0));
				prepare.setString(3, tempCart.get(i).lastElement());
			
				// Execute the query.
				prepare.executeQuery();
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	Vector<Vector<String>> RetrieveFromCart(Database db)
	{
		// Create variables exclusive to this function here.
		Vector<String> book = new Vector<String>();
		Vector<Vector<String>> cart = new Vector<Vector<String>>();
		String sqlStatement = "SELECT * FROM CART WHERE USERID = ?";
		String sqlStatement2 = "SELECT * FROM BOOKS WHERE ISBN = ?";
		
		try 
		{
			// Get ready to assemble the queries.
			PreparedStatement prepare = db.dbConnection.prepareStatement(sqlStatement);
			PreparedStatement prepare2 = db.dbConnection.prepareStatement(sqlStatement2);
			
			// Insert values into the first query.
			prepare.setString(1, this.currentUser);
			
			// Execute the first query.
			ResultSet result = prepare.executeQuery();
			
			// Go through each row of the cart results.
			while (result.next())
			{
				// Take the isbn in the cart and insert it into the second query. TODO 
				prepare2.setString(1, result.getString(2));
						
				// Execute the second query.
				ResultSet result2 = prepare2.executeQuery();
						
				// Go through each row of the book results.
				while (result2.next())
				{
					// Get the isbn, title, price, and quantity and then add it to the book vector.
					book.add(result2.getString(1)); // Mysterious white space appears for 9 character isbn's. So we trim.
					book.add(result2.getString(3));
					book.add(Integer.toString(result2.getInt(4)));
					book.add(Integer.toString(result.getInt(3)));
							
					// Add the book vector to the cart vector.
					cart.add(book);
					
					// Clear the book vector for the next book.
					book = new Vector<String>();
				}
			}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cart;
	}
	
	void DeleteEditFromCart(String choice, Database db, int mode)
	{
		// MODE = 0: Delete a book from the cart.
		// MODE = 1: Change the quantity of an item.
		
		// Create the query based on what mode we're in.
		// NOTE: Because some isbn's are 9 digits, we must cast them to char(10) to properly delete or edit from the cart. 
		String sqlStatement = (mode == 0) ? "DELETE FROM CART WHERE isbn = CAST(? AS CHAR(10)) AND userid = ?" 
							: (mode == 1) ? "UPDATE CART SET QTY = ? WHERE isbn = CAST(? AS CHAR(10)) AND userid = ?" : null;

		// Create variables here.
		UserInterface ui = new UserInterface();
		String newQuantity = "";
		
		try 
		{
			// Get ready to prepare the query.
			PreparedStatement result = db.dbConnection.prepareStatement(sqlStatement);
			
			// Ask the user for a quantity.
			while (true && mode == 1)
			{
				System.out.print("Enter a quantity: ");
			
				newQuantity = ui.readConsole();
				
				if (newQuantity.matches("\\d+") && Integer.parseInt(newQuantity) < 100000)
				{
					break;
				}
				else
				{
					System.out.println("The quantity you provided either contains non-digits, is negative, or is higher than 99999. Please try again.");
				}
			}
			
			// Give the values to the query. Where they go depends on the mode.
			if (mode == 1) result.setString(1, newQuantity);
			result.setString((mode == 0) ? 1 : 2, choice);
			result.setString((mode == 0) ? 2 : 3, this.currentUser);
			
			// Then execute the query.
			result.executeQuery();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}		
};