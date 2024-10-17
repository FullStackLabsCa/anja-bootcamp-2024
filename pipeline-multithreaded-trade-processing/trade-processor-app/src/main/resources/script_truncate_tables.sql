use hibernate_trade_processor;
show tables;

select count(*) from trade_payloads;
select count(*) from journal_entry;
select count(*) from positions;

truncate table trade_payloads;
truncate table journal_entry;
truncate table positions;
