DROP TABLE IF EXISTS "journal_entry";
CREATE TABLE "journal_entry" (
  "quantity" int NOT NULL,
  "created_timestamp" timestamp NOT NULL,
  "id" bigint NOT NULL AUTO_INCREMENT,
  "transaction_timestamp" timestamp NOT NULL,
  "updated_timestamp" timestamp NOT NULL,
  "account_number" varchar(255) NOT NULL,
  "security_cusip" varchar(255) NOT NULL,
  "trade_id" varchar(255) NOT NULL,
  "direction" varchar(4) NOT NULL CHECK ("direction" IN ('BUY','SELL')),
  "posted_status" varchar(10) NOT NULL CHECK ("posted_status" IN ('POSTED','NOT_POSTED')),
  PRIMARY KEY ("id"),
  UNIQUE ("trade_id")
);

DROP TABLE IF EXISTS "positions";
CREATE TABLE "positions" (
  "version" int NOT NULL,
  "created_timestamp" timestamp NOT NULL,
  "holding" bigint NOT NULL,
  "updated_timestamp" timestamp NOT NULL,
  "account_number" varchar(255) NOT NULL,
  "security_cusip" varchar(255) NOT NULL,
  PRIMARY KEY ("account_number","security_cusip")
);

DROP TABLE IF EXISTS "securities_reference";
CREATE TABLE "securities_reference" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "cusip" varchar(255) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE ("cusip")
);

DROP TABLE IF EXISTS "trade_payloads";
CREATE TABLE "trade_payloads" (
  "created_timestamp" timestamp NOT NULL,
  "id" bigint NOT NULL AUTO_INCREMENT,
  "updated_timestamp" timestamp NOT NULL,
  "payload" varchar(255) NOT NULL,
  "trade_number" varchar(255) NOT NULL,
  "je_status" varchar(10) CHECK ("je_status" IN ('POSTED', 'NOT_POSTED')) NOT NULL,
  "lookup_status" varchar(12) CHECK ("lookup_status" IN ('PASS', 'FAIL', 'NOT_CHECKED')) NOT NULL,
  "validity_status" varchar(5) CHECK ("validity_status" IN ('VALID', 'INVALID')) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE ("trade_number")
);

INSERT INTO securities_reference (cusip)
VALUES
('AAPL'),
('GOOGL'),
('AMZN'),
('MSFT'),
('TSLA'),
('NFLX'),
('FB'),
('NVDA'),
('JPM'),
('VISA'),
('MA'),
('BAC'),
('DIS'),
('INTC'),
('CSCO'),
('ORCL'),
('WMT'),
('T'),
('VZ'),
('ADBE'),
('CRM'),
('PYPL'),
('PFE'),
('XOM'),
('UNH');