
-- DROP DATABASE IF EXISTS group2db;
-- CREATE DATABASE group2db;
-- COMMENT ON DATABASE group2db
-- 	IS 'CS 442 - Final Project - Banking Application and Shopping Domain';


-- Script assume that the database named group2db is created with owner set to postgres

-- For fresh start, you could completely delete group2db database if it exists and then create it again using pgAdmin

-- Run the following statements with user as postgres and database as group2db


DROP TABLE IF EXISTS Authentication       CASCADE;
DROP TABLE IF EXISTS Branch               CASCADE;
DROP TABLE IF EXISTS Customer             CASCADE;
DROP TABLE IF EXISTS AccountType          CASCADE;
DROP TABLE IF EXISTS Account              CASCADE;
DROP TABLE IF EXISTS Credit               CASCADE;
DROP TABLE IF EXISTS CustomerTransaction  CASCADE;
DROP TABLE IF EXISTS Item                 CASCADE;
DROP TABLE IF EXISTS ShoppingOrder        CASCADE;
DROP TABLE IF EXISTS Cart                 CASCADE;
DROP TABLE IF EXISTS OrderPayment         CASCADE;
DROP TABLE IF EXISTS OrderDetails         CASCADE;

DROP INDEX IF EXISTS Account_customerID_idx;
DROP INDEX IF EXISTS Account_accounttypeID_idx;
DROP INDEX IF EXISTS CustomerTransaction_customerID_idx;
DROP INDEX IF EXISTS CustomerTransaction_accountID_idx;
DROP INDEX IF EXISTS CustomerTransaction_accountID2_idx;
DROP INDEX IF EXISTS itemName_UNIQUE;
DROP INDEX IF EXISTS ShoppingOrder_customerID_idx;
DROP INDEX IF EXISTS Cart_itemID_idx;
DROP INDEX IF EXISTS OrderPayment_transactionID_idx;
DROP INDEX IF EXISTS OrderDetails_itemID_idx;

CREATE TABLE IF NOT EXISTS Authentication (
	userName VARCHAR(45) NOT NULL,
	password VARCHAR(45) NULL,
	PRIMARY KEY (userName));


CREATE TABLE IF NOT EXISTS Branch (
	branchID SERIAL NOT NULL,
	routingNumber CHAR(9) NOT NULL UNIQUE,
	branchName VARCHAR(45) NOT NULL,
	PRIMARY KEY (branchID));


CREATE TABLE IF NOT EXISTS Customer (
	customerID SERIAL,
	userName VARCHAR(45) NOT NULL,
	branchID INT NOT NULL,
	PRIMARY KEY (customerID),
	CONSTRAINT userName
		FOREIGN KEY (userName)
		REFERENCES Authentication (userName)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT branchID
		FOREIGN KEY (branchID)
		REFERENCES Branch (branchID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);
	
CREATE TABLE IF NOT EXISTS AccountType (
	accountTypeID INT NOT NULL,
	accountTypeName VARCHAR(45) NOT NULL UNIQUE,
	PRIMARY KEY (accountTypeID));

CREATE TABLE IF NOT EXISTS Account (
	accountID SERIAL NOT NULL,
	customerID INT NOT NULL,
	accountTypeID INT NOT NULL,
	pin INT NOT NULL,
	balance DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK(balance >= 0),
	PRIMARY KEY (accountID),
	CONSTRAINT customerID
		FOREIGN KEY (customerID)
		REFERENCES Customer (customerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT accountTypeID
		FOREIGN KEY (accountTypeID)
		REFERENCES AccountType (accountTypeID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);


CREATE INDEX Account_customerID_idx ON Account (customerID ASC);
CREATE INDEX Account_accounttypeID_idx ON Account (accountTypeID ASC);


CREATE TABLE IF NOT EXISTS Credit (
	cardNumber VARCHAR(16) NOT NULL UNIQUE,
	cvv2 VARCHAR(3) NOT NULL,
	accountID INT NOT NULL UNIQUE,
	approvedCredit DECIMAL(10,2) NOT NULL CHECK(approvedCredit > 0),
	availableCredit DECIMAL(10,2) NOT NULL CHECK(availableCredit >= 0),
	PRIMARY KEY (accountID),
	CONSTRAINT accountID
		FOREIGN KEY (accountID)
		REFERENCES Account (accountID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);


CREATE TABLE IF NOT EXISTS CustomerTransaction (
	transactionID SERIAL NOT NULL,
	customerID INT NOT NULL,
	fromAccountID INT NOT NULL CHECK(fromAccountID != 1), -- Portal User's Account
	toAccountID INT NOT NULL,
	amount DECIMAL(10,2) NOT NULL CHECK(amount > 0),
	timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (transactionID),
	CONSTRAINT customerID
		FOREIGN KEY (customerID)
		REFERENCES Customer (customerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT accountID
		FOREIGN KEY (fromAccountID)
		REFERENCES Account (accountID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT accountID2
		FOREIGN KEY (toAccountID)
		REFERENCES Account (accountID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);


CREATE INDEX CustomerTransaction_customerID_idx ON CustomerTransaction (customerID ASC);
CREATE INDEX CustomerTransaction_accountID_idx ON CustomerTransaction (fromAccountID ASC);
CREATE INDEX CustomerTransaction_accountID2_idx ON CustomerTransaction (toAccountID ASC);


CREATE TABLE IF NOT EXISTS Item (
	itemID SERIAL NOT NULL,
	itemName VARCHAR(400) NOT NULL UNIQUE,
	quantity INT NOT NULL DEFAULT 0,
	itemCost DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (itemCost > 0),
	PRIMARY KEY (itemID));

CREATE INDEX itemName_UNIQUE ON Item (itemName ASC);

CREATE TABLE IF NOT EXISTS ShoppingOrder (
	orderID SERIAL NOT NULL,
	customerID INT NOT NULL,
	orderPayment DECIMAL(10,2) NOT NULL,
	PRIMARY KEY (orderID),
	CONSTRAINT customerID
		FOREIGN KEY (customerID)
		REFERENCES Customer (customerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);

CREATE INDEX ShoppingOrder_customerID_idx ON ShoppingOrder (customerID ASC);


CREATE TABLE IF NOT EXISTS Cart (
	customerID INT NOT NULL,
	itemID INT NOT NULL,
	quantity INT NOT NULL CHECK (quantity > 0),
	PRIMARY KEY (customerID, itemID),
	CONSTRAINT customerID
		FOREIGN KEY (customerID)
		REFERENCES Customer (customerID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT itemID
		FOREIGN KEY (itemID)
		REFERENCES Item (itemID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);

CREATE INDEX Cart_itemID_idx ON Cart (itemID ASC);


CREATE TABLE IF NOT EXISTS OrderPayment (
	orderID INT NOT NULL,
	transactionID INT NOT NULL,
	amountPaid DECIMAL(10,2) NOT NULL CHECK(amountPaid > 0),
	PRIMARY KEY (orderID, transactionID),
	CONSTRAINT orderID
		FOREIGN KEY (orderID)
		REFERENCES ShoppingOrder (orderID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT transactionID
		FOREIGN KEY (transactionID)
		REFERENCES CustomerTransaction (transactionID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);

CREATE INDEX OrderPayment_transactionID_idx ON OrderPayment (transactionID ASC);


CREATE TABLE IF NOT EXISTS OrderDetails (
	orderID INT NOT NULL,
	itemID INT NOT NULL,
	quantity INT NOT NULL CHECK(quantity > 0),
	PRIMARY KEY (orderID, itemID),
	CONSTRAINT orderID
		FOREIGN KEY (orderID)
		REFERENCES ShoppingOrder (orderID)
		ON DELETE CASCADE
		ON UPDATE CASCADE,
	CONSTRAINT itemID
		FOREIGN KEY (itemID)
		REFERENCES Item (itemID)
		ON DELETE CASCADE
		ON UPDATE CASCADE);


CREATE INDEX OrderDetails_itemID_idx ON OrderDetails (itemID ASC);


-- Insert statements (Please preserve the order in executing these statements, dont modify!)

INSERT INTO Authentication(userName, password)
VALUES
	('portal', md5('password'));

INSERT INTO Authentication(userName, password)
VALUES
	('akanch4', md5('password')),
	('mgadge2', md5('password')),
	('nshah77', md5('password')),
	('rgeorg20', md5('password')),
	('pnair3', md5('password')),
	('apatil4', md5('password'));



INSERT INTO Branch(routingNumber, branchName)
VALUES
	('271070801', 'IL');

INSERT INTO Customer(userName, branchID)
VALUES
	('portal', 1);

INSERT INTO Customer(userName, branchID)
VALUES
	('akanch4', 1),
	('mgadge2', 1),
	('nshah77', 1),
	('rgeorg20', 1),
	('pnair3', 1),
	('apatil4', 1);



INSERT INTO AccountType(accountTypeID, accountTypeName)
VALUES
	(1, 'Checkings'),
	(2, 'Savings'),
	(3, 'Credit'),
	(4, 'Rewards');

-- Checkings for Portal User
INSERT INTO Account(customerID, accountTypeID, pin, balance)
VALUES
	(1, 1, 8888, 0.00);

-- Checkings for all users(except portal user)
INSERT INTO Account(customerID, accountTypeID, pin, balance)
VALUES	
	(2, 1, 8888, 100000.00),
	(3, 1, 8888, 100000.00),
	(4, 1, 8888, 100000.00),
	(5, 1, 8888, 100000.00),
	(6, 1, 8888, 100000.00),
	(7, 1, 8888, 100000.00);

-- Savings for all users(except portal user)
INSERT INTO Account(customerID, accountTypeID, pin, balance)
VALUES
	(2, 2, 8888, 1000.00),
	(3, 2, 8888, 1000.00),
	(4, 2, 8888, 1000.00),
	(5, 2, 8888, 1000.00),
	(6, 2, 8888, 1000.00),
	(7, 2, 8888, 100000.00);

-- Credit for all users(except portal user)
INSERT INTO Account(customerID, accountTypeID, pin, balance)
VALUES
	(2, 3, 0, 0.00),
	(3, 3, 0, 0.00),
	(4, 3, 0, 0.00),
	(5, 3, 0, 0.00),
	(6, 3, 0, 0.00),
	(7, 3, 0, 0.00);

-- Rewards for all users(except portal user)
INSERT INTO Account(customerID, accountTypeID, pin, balance)
VALUES
	(2, 4, 0, 0.00),
	(3, 4, 0, 0.00),
	(4, 4, 0, 0.00),
	(5, 4, 0, 0.00),
	(6, 4, 0, 0.00),
	(7, 4, 0, 0.00);

INSERT INTO Credit(cardNumber, cvv2, accountID, approvedCredit, availableCredit)
VALUES
	('8888888888888882','888',14, 1000.00, 1000.00),
	('8888888888888883','888',15, 1000.00, 1000.00),
	('8888888888888884','888',16, 1000.00, 1000.00),
	('8888888888888885','888',17, 1000.00, 1000.00),
	('8888888888888886','888',18, 1000.00, 1000.00),
	('8888888888888887','888',19, 1000.00, 1000.00);




INSERT INTO Item(itemName, itemCost, quantity)
VALUES
	('The Hobbit',                                                  21.45, 1000),
	('The Fellowship of the Ring(The Lord of the Rings #1)',        22.43, 1000),
	('The Two Towers(The Lord of the Rings #2)',                    23.45, 1000),
	('The Return of the King(The Lord of the Rings #3)',            24.45, 1000),
	('A Game of Thrones(A Song of Ice and Fire #1)',                89.45, 1000),
	('A Clash of Kings(A Song of Ice and Fire #2)',                 89.45, 1000),
	('A Storm of Swords(A Song of Ice and Fire #3)',                89.45, 1000),
	('A Feast for Crows(A Song of Ice and Fire #4)',                89.42, 1000),
	('A Dance with Dragons(A Song of Ice and Fire #5)',             99.45, 1000),
	('Harry Potter and the Sorcerer''s Stone (Harry Potter, #1)',   11.45, 1000),
	('Harry Potter and the Chamber of Secrets (Harry Potter, #2)',  12.45, 1000),
	('Harry Potter and the Prisoner of Azkaban (Harry Potter, #3)', 13.47, 1000),
	('Harry Potter and the Goblet of Fire (Harry Potter, #4)',      14.40, 1000),
	('Harry Potter and the Order of the Phoenix (Harry Potter #5)', 15.48, 1000),
	('Harry Potter and the Half-Blood Prince (Harry Potter, #6)',   16.49, 1000),
	('Harry Potter and the Deathly Hallows (Harry Potter, #7)',     17.42, 1000);

-- 1
-- 2
-- 3
-- 4
-- 5
-- 6

