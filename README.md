Lab4: TicketMiner GUI and Reporting

An advanced TicketMiner version with GUI support, ticket inventory, reporting, discounts, receipt generation, and electronic ticket functionality.

Highlights
Adds a Swing-based GUI.
Tracks ticket inventory and revenue.
Supports discount strategies.
Generates receipts, summaries, and electronic tickets.
Includes source, library, and UML/design artifacts.

Overview
Lab4 is the most complete TicketMiner iteration in this prepared set. It adds a graphical interface and expands the system with inventory management, reporting, discounts, receipts, and ticket-generation features.
The project demonstrates larger-scale object-oriented design with multiple interacting services and model classes.

Why This Project
This project is a strong GitHub portfolio item because it shows growth from file parsing into a full Java application. It demonstrates object-oriented design, GUI work, reporting, and practical ticketing-system workflows.

Usage
Compile: Open the src folder and compile with javac *.java.
Run: Execute the GUI with java TicketMinerGUI if the classpath is configured for the included libraries.
Keep lib and UML folders with the project for dependencies and design reference.

Installation
Ensure you have the Java Development Kit (JDK) installed on your system.
Open a terminal in the lab folder before compiling so the program can find any required local text files.

Architecture
File Structure: The project contains src, lib, and UML folders.
Components: src contains the application classes, GUI, data store, event model, ticket inventory, discount strategies, receipt generation, and reports. lib stores external jars. UML stores design artifacts.

Future Improvements
Add a formal Maven or Gradle build.
Move generated logs and receipts into an ignored output directory.
Improve GUI layout and error handling.
Add end-to-end tests for purchase and reporting workflows.
