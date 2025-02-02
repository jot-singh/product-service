# --Insert Categories
use products;
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Fruits', 'Fruits');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Vegetables', 'Vegetables');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Meat', 'Meat');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Fish', 'Fish');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Dairy', 'Dairy');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Bread', 'Bread');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Sweets', 'Sweets');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Drinks', 'Drinks');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'Alcohol', 'Alcohol');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'MOBILES', 'CATEGORY FOR MOBILES');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'LAPTOPS', 'CATEGORY FOR LAPTOPS');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'TV', 'CATEGORY FOR TV');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'CAMERA', 'CATEGORY FOR CAMERA');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'HOME APPLIANCES', 'CATEGORY FOR HOME APPLIANCES');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'BOOKS', 'CATEGORY FOR BOOKS');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'CLOTHES', 'CATEGORY FOR CLOTHES');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'FOOTWEAR', 'CATEGORY FOR FOOTWEAR');
INSERT INTO CATEGORIES(ID, NAME, DESCRIPTION)
VALUES (UUID(), 'WATCHES', 'CATEGORY FOR WATCHES');

-- Insert Prices
INSERT INTO prices(ID, price, CURRENCY)
VALUES (UUID(), 1.99, 'USD');
INSERT INTO prices(ID, price, CURRENCY)
VALUES (UUID(), 2.99, 'USD');
INSERT INTO prices(ID, price, CURRENCY)
VALUES (UUID(), 3.99, 'USD');
INSERT INTO prices(ID, price, CURRENCY)
VALUES (UUID(), 50000, 'INR');
INSERT INTO prices(ID, price, CURRENCY)
VALUES (UUID(), 60000, 'INR');


-- Insert Products
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Milk', 'Milk', 'Milk', (Select id from prices where
                                                                   price = 1.99 and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Drinks'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Apple', 'Apple', 'Apple', (Select id
                                         from prices
                                         where price = 1.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Fruits'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Orange', 'Orange', 'Orange', (Select id
                                         from prices
                                         where price = 2.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Fruits'));

INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Banana', 'Banana', 'Banana', (Select id
                                         from prices
                                         where price = 3.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Fruits'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Potato', 'Potato', 'Potato', (Select id
                                         from prices
                                         where price = 1.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Vegetables'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Tomato', 'Tomato', 'Tomato', (Select id
                                         from prices
                                         where price = 2.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Vegetables'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Cucumber', 'Cucumber', 'Cucumber', (Select id
                                         from prices
                                         where price = 3.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Vegetables'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Pork', 'Pork', 'Pork', (Select id
                                         from prices
                                         where price = 1.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Meat'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Beef', 'Beef', 'Beef', (Select id
                                         from prices
                                         where price = 2.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Meat'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'Chicken', 'Chicken', 'Chicken', (Select id
                                         from prices
                                         where price = 3.99
                                           and currency = 'USD'), (SELECT ID FROM CATEGORIES WHERE NAME = 'Meat'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'IPHONE 15', 'IPHONE 15', 'IPHONE 15', (Select id
                                         from prices
                                         where price = 50000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'MOBILES'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'IPHONE 16', 'IPHONE 16', 'IPHONE 16', (Select id
                                         from prices
                                         where price = 60000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'MOBILES'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'LENOVO', 'LENOVO', 'LENOVO', (Select id
                                         from prices
                                         where price = 50000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'LAPTOPS'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'DELL', 'DELL', 'DELL', (Select id
                                         from prices
                                         where price = 60000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'LAPTOPS'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'SAMSUNG', 'SAMSUNG', 'SAMSUNG', (Select id
                                         from prices
                                         where price = 50000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'TV'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'SONY', 'SONY', 'SONY', (Select id
                                         from prices
                                         where price = 60000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'TV'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'CANON', 'CANON', 'CANON', (Select id
                                         from prices
                                         where price = 50000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'CAMERA'));
INSERT INTO PRODUCTS(ID, NAME, TITLE, DESCRIPTION, price_id, CATEGORY_ID)
VALUES (UUID(), 'NIKON', 'NIKON', 'NIKON', (Select id
                                         from prices
                                         where price = 60000
                                           and currency = 'INR'), (SELECT ID FROM CATEGORIES WHERE NAME = 'CAMERA'));
