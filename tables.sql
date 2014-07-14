CREATE TABLE zipcodes (
    zip INT,
    city VARCHAR(30),
    PRIMARY KEY(zip),
	CHECK (zip>=0 AND zip<=99999)
);

CREATE TABLE employees (
    eno INT,
    ename VARCHAR(30),
    zip INT,
    hdate DATE,
    PRIMARY KEY(eno),
    FOREIGN KEY(zip) REFERENCES zipcodes,
	CHECK (zip>=0 AND zip<=99999),
	CHECK (eno>=0 AND eno<=9999)
);

CREATE TABLE parts (
    pno INT,
    pname VARCHAR(30),
    qoh INT,
    price NUMBER(6,2) NOT NULL,
    olevel INT,
    PRIMARY KEY(pno),
	CHECK (pno>=0 AND pno<=99999),
	CHECK (qoh>=0),
	CHECK (price>=0.0)
);

CREATE TABLE customers (
    cno INT,
    cname VARCHAR(30),
    street VARCHAR(30),
    zip INT,
    phone VARCHAR(12),
    PRIMARY KEY(cno),
    FOREIGN KEY(zip) REFERENCES zipcodes,
	CHECK (cno>=0 AND cno<=99999),
	CHECK (zip>=0 AND zip<=99999)
);

CREATE TABLE orders (
    ono INT,
    cno INT,
    eno INT,
    received DATE,
    shipped DATE,
    PRIMARY KEY(ono),
    FOREIGN KEY(cno) REFERENCES customers ON DELETE CASCADE,
    FOREIGN KEY(eno) REFERENCES employees ON DELETE CASCADE,
	CHECK (ono>=0 AND ono<=99999),
	CHECK (cno>=0 AND cno<=99999),
	CHECK (eno>=0 AND eno<=9999)
);

CREATE TABLE odetails (
    ono INT,
    pno INT,
    qty INT,
    PRIMARY KEY(ono, pno),
    FOREIGN KEY(ono) REFERENCES orders ON DELETE CASCADE,
    FOREIGN KEY(pno) REFERENCES parts ON DELETE CASCADE,
	CHECK (ono>=0 AND ono<=99999),
	CHECK (pno>=0 AND pno<=99999),
	CHECK (qty>0)
);


CREATE TABLE integritylevels (
    il INT,
    label VARCHAR(30) NOT NULL,
    PRIMARY KEY(il)
);
CREATE TABLE subjects (
    username VARCHAR(50),
    description VARCHAR(50) NOT NULL,
    il INT NOT NULL,
    istrusted CHAR(1) NOT NULL,
    PRIMARY KEY(username),
    FOREIGN KEY(il) REFERENCES integritylevels ON DELETE CASCADE
);
CREATE TABLE objects (
    oname VARCHAR(50),
    il INT NOT NULL,
    PRIMARY KEY(oname),
    FOREIGN KEY(il) REFERENCES integritylevels ON DELETE CASCADE
);

INSERT INTO integritylevels VALUES (1, 'Very Low');
INSERT INTO integritylevels VALUES (2, 'Low');
INSERT INTO integritylevels VALUES (3, 'Medium');
INSERT INTO integritylevels VALUES (4, 'High');
INSERT INTO integritylevels VALUES (5, 'Very High');

INSERT INTO subjects VALUES ('admin', 'CEO user account', 5, 't');
INSERT INTO subjects VALUES ('inventory', 'Inventory management script', 5, 'f');
INSERT INTO subjects VALUES ('customerservice', 'Customer service representative user account', 3, 'f');
INSERT INTO subjects VALUES ('hr', 'Human Resources representative user account', 4, 't');
INSERT INTO subjects VALUES ('audit', 'Auditing script, periodically scheduled', 1, 'f');
INSERT INTO subjects VALUES ('guest', 'Authorized guest user account', 2, 'f');

INSERT INTO objects VALUES ('zipcodes', 2);
INSERT INTO objects VALUES ('employees', 4);
INSERT INTO objects VALUES ('parts', 5);
INSERT INTO objects VALUES ('customers', 4);
INSERT INTO objects VALUES ('orders', 3);
INSERT INTO objects VALUES ('odetails', 4);
