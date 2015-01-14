CREATE TABLE messages (
  id integer PRIMARY KEY, 
  msg text,
  location text,
  direction text
);

CREATE TABLE locations (
  id integer PRIMARY KEY autoincrement, 
  name text not null unique,
  radius text, 
  latitude text, 
  longitude text, 
  altitude text, 
  last_message_send text, 
  msg_in integer, 
  msg_out integer, 
  notify text,  
  FOREIGN KEY (msg_in) REFERENCES messages(id),
  FOREIGN KEY (msg_out) REFERENCES messages(id)
);

CREATE TABLE cellulars (
  id integer PRIMARY KEY, 
  name text
);

CREATE TABLE cellulars_locations (
  idCellularLocation integer, 
  idCelluar integer, 
  number text, 
  notify text, 
  PRIMARY KEY (idCellularLocation, idCelluar), 
  FOREIGN KEY (idCelluar) REFERENCES cellulars(id)
);


INSERT INTO locations (
  name, radius, latitude, longitude, altitude, last_message_send, msg_in, msg_out, notify
) 
VALUES (
  'HOME', '', '-23.22222', '-46.68689898', '', null, null, null, 'YES'
);



INSERT INTO messages (id, msg, location, direction) VALUES (1, 'To chegando em casa', 'HOME', 'IN');
INSERT INTO messages (id, msg, location, direction) VALUES (2, 'To saindo de casa', 'HOME', 'OUT');

UPDATE locations set msg_in = 1;
UPDATE locations set msg_out = 2;
UPDATE locations set last_message_send = 'IN';



INSERT INTO cellulars (id, name) VALUES (1, 'RODRIGO FROSCH');
INSERT INTO cellulars (id, name) VALUES (2, 'MÃE');
INSERT INTO cellulars_locations (idCellularLocation, idCelluar, number, notify) 
  VALUES (1, 1, '11986422469', 'YES');
INSERT INTO cellulars_locations (idCellularLocation, idCelluar, number, notify) 
  VALUES (2, 1, '11996422469', 'YES');
