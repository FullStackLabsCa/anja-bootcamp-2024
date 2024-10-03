create database trade_processor;

use trade_processor;

CREATE TABLE journal_entry (
    entry_id INT NOT NULL AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL,
    security_cusip VARCHAR(20) NOT NULL,
    direction ENUM('buy', 'sell') NOT NULL,
    quantity INT NOT NULL,
    posted_status ENUM('posted', 'not_posted') NOT NULL,
    transaction_time DATETIME NOT NULL,
    created_date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    trade_id VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (entry_id)
);

CREATE TABLE positions (
    position_id int not null auto_increment;
    account_number VARCHAR(20) NOT NULL,
    security_cusip VARCHAR(20) NOT NULL,
    positions INT NOT NULL,
    created_date_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL,
    UNIQUE KEY (account_number, security_cusip)
);

CREATE TABLE trade_payloads (
    id INT NOT NULL AUTO_INCREMENT,
    trade_id VARCHAR(20) NOT NULL UNIQUE,
    payload TEXT NOT NULL,
    validity_status ENUM('valid', 'invalid') NOT NULL,
    lookup_status ENUM('succeeded', 'failed') NOT NULL,
    posted_status ENUM('posted', 'not_posted') NOT NULL,
    PRIMARY KEY (id)
);